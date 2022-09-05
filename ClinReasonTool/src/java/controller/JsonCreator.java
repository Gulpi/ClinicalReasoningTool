package controller;

import java.io.*;
import java.util.*;

import javax.servlet.ServletContext;

import org.jfree.util.Log;

import application.AppBean;
import database.DBList;
import beans.CRTFacesContext;
import beans.list.*;
import properties.IntlConfiguration;
import util.CRTLogger;
import util.StringUtilities;

/**
 * Creates Json files based on lists
 * Format: {"label": "Calcimycin", "value": "3"},
 * Nursing items are either marked with "1" in the nursing column or added as additional item with "U" as type.
 * (U1.xxx are nursing diagnoses)
 * @author ingahege
 *
 */
public class JsonCreator {
	
	//private boolean createOneList = true; //if false, we create multiple lists for problems, ddx, etc.
	private static ServletContext context;	
	
	public synchronized void initJsonExport(ServletContext contextIn){
		context = contextIn;
		
		String lang = null; //language can be set with a query parameter, e.g. when calling through list admin page
		try{
			lang = AjaxController.getInstance().getRequestParamByKey("lang");
		}
		catch(Exception e){}
		
		if(lang!=null){
			exportList("standard",new Locale(lang));
			exportList("nursing",new Locale(lang));
		}
		else{
			// loop thru all types
			String list_types = AppBean.getProperty("lists.types","standard,context,nursing");
			List<String> list_types_list = net.casus.util.StringUtilities.getStringListFromString(list_types, ",");
			Iterator<String> list_types_list_it = list_types_list.iterator();
			while(list_types_list_it.hasNext()) {
				String loop = list_types_list_it.next();
				//Log.info("JsonCreator","list_type:" + loop);
				
				// loop thru all languages
				String languages = AppBean.getProperty("lists.languages." + loop,"en,de,pl,sv,es,pt,fr,uk");
				List<String> languages_list = net.casus.util.StringUtilities.getStringListFromString(languages, ",");
				Iterator<String> languages_list_it = languages_list.iterator();
				while(languages_list_it.hasNext()) {
					exportList(loop,new Locale(languages_list_it.next()));
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected List exportList(String type,Locale loc) {
		return exportGenericList(type,loc);
	}
	
	public void setContext(ServletContext context){
		this.context = context;
	}
	
	/**
	 * helper could be outsourced to utilities...
	 * 
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	static public String[] getArray(String prefix, String type, String[] default_value) {
		String dbTypes = AppBean.getProperty(prefix + type,null);
		if (dbTypes == null || dbTypes.length()==0) {
			return default_value;
		}

		return net.casus.util.StringUtilities.getStringArrayFromString(dbTypes, ",");
	}
	
	/**
	 * helper could be outsourced to utilities...
	 * 
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	static protected List<String> getStringList(String prefix, String type, List<String> default_value) {
		String list_string = AppBean.getProperty(prefix + type,null);
		if (list_string == null || list_string.length()==0) {
			return default_value;
		}

		return net.casus.util.StringUtilities.getStringListFromString(list_string, ",");
	}
	
	/**
	 * helper could be outsourced to utilities...
	 * 
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	static protected int getInt(String prefix, String type, int default_value) {
		String professionType = AppBean.getProperty(prefix + type,null);
		if (professionType == null || professionType.length()==0) {
			return 0;
		}

		return net.casus.util.StringUtilities.getIntegerFromString(professionType, default_value);
	}
	
	/**
	 * helper could be outsourced to utilities...
	 * 
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	static protected boolean getBoolean(String prefix, String type, boolean default_value) {
		String preprocess = AppBean.getProperty(prefix + type,null);
		if (preprocess == null || preprocess.length()==0) {
			return false;
		}

		return net.casus.util.StringUtilities.getBooleanFromString(preprocess, default_value);
	}
	
	/**
	 * We export the list of the given language from the database into a JSON file for use in the user interface 
	 * We also store the list items in the SummaryController for using it for the statement analysis and assessment.
	 * @param loc
	 */
	public File getGenericJsonFileByLoc(String type, String lang ){
		String file = AppBean.getProperty("lists." + type,"jsonp_#{locale}.json");
		List<String> getStringList = getStringList("lists.languages.", type, null);
		if (getStringList != null && getStringList.contains(lang)) {
			file = net.casus.util.StringUtilities.replace(file, "#{locale}", lang != null ? lang : "en");
		}
		else {
			file = net.casus.util.StringUtilities.replace(file, "#{locale}", lang != null ? lang : AppBean.getProperty("lists.default_languages." + type,"en"));
		}
		
		if (context != null) {
			return new File(context.getRealPath(AppBean.getProperty("lists.base","src/html/") + file));
		}
		else {
			String name = file;
			int idx = name.lastIndexOf('/');
			name = name.substring(idx+1);
			return new File(name);
		}
	}
	
	/**
	 * We export the list of the given language from the database into a JSON file for use in the user interface 
	 * We also store the list items in the SummaryController for using it for the statement analysis and assessment.
	 * @param loc
	 */
	public List exportGenericList(String type, Locale loc){
		List<ListItem> items = new DBList().selectListItemsByTypesAndLang(loc, getArray("lists.dbtypes.", type, null), getInt("lists.professionType.", type, 0), getInt("lists.professionVariant.", type, -1));
		//we collect all items here, to sort it alphabetically
		List itemsAndSyns = new ArrayList();
		
		if(items==null || items.isEmpty()) return null; //then something went really wrong!
		try{
			File f = getGenericJsonFileByLoc(type,loc != null ? loc.getLanguage() : "en");
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			int lines = 0;
			StringBuffer sb = new StringBuffer("[");

			if (this.getBoolean("lists.preprocess.", type, false)) {
				for(int i=0; i<items.size(); i++){
					ListItem item = items.get(i);
					//add items for SummaryStatement Rating
					if(doAddItem(item)){
						lines += addItemAndSynonymaNew(item, sb, itemsAndSyns);
						SummaryStatementController.addListItem(item, loc.getLanguage());
					}
					
					else if(item.getFirstCode().startsWith("A")){
						SummaryStatementController.addListItemsA(item, loc.getLanguage());
					}
					
					else if(item.getFirstCode().startsWith("Z")){ //we add countries...
						SummaryStatementController.addListItem(item, loc.getLanguage());
					}				
				}
				Collections.sort(itemsAndSyns);
			}
			else {
				Collections.sort(items);
				itemsAndSyns = items;
			}
			//SummaryStatementController.addListItems(itemsAndSyns, loc.getLanguage());
			
			for(int i=0; i<itemsAndSyns.size();i++){
				ListInterface li = (ListInterface) itemsAndSyns.get(i);				
				sb.append("{\"label\": \""+li.getName()+"\", \"value\": \""+li.getIdForJsonList()+"\"},\n");
			}
			boolean allowOwnEntries = AppBean.getProperty("AllowOwnEntries", false);
			if (allowOwnEntries) {
				allowOwnEntries = this.getBoolean("lists.allowOwnEntries.", type, false);
			}
			if(allowOwnEntries) {
				sb.append(getOwnEntry(loc));
			}
			
			sb.replace(sb.length()-2, sb.length(), "]");
			pw.print(sb.toString());
			pw.flush();
		    pw.close();
		    CRTLogger.out("lines exported: " + lines, CRTLogger.LEVEL_PROD);
		    return itemsAndSyns;
		}
		catch( Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
			return null;
		}
	}
	
	private String getOwnEntry(Locale loc){
		String ownEntry = IntlConfiguration.getValue("list.ownEntry", loc);
		return "{\"label\": \""+ownEntry+"\", \"value\": \"-99\"},\n";
	}
	
	/**
	 * Make some improvements of the list, we do not add a certain item_level depending on the category etc.
	 * maybe do more here....
	 * @param item
	 * @return
	 */
	private boolean doAddItem(ListItem item){
		if(item.isIgnored()) return false;
		//D:
		if(item.getItemType().equals("D") && item.getLevel()>=10) return false;
		if(item.getItemType().equals("A")) return false;
		if(item.getFirstCode()==null) return true;
		if(item.getFirstCode().startsWith("D20.0") || item.getFirstCode().startsWith("D20.1")) return false;
		if(item.getFirstCode().startsWith("D20.3") || item.getFirstCode().startsWith("D20.4")) return false;
		if(item.getFirstCode().startsWith("D20.7") || item.getFirstCode().startsWith("D20.8")) return false;
		if(item.getFirstCode().startsWith("D20.9")) return false;
		if(item.getFirstCode().startsWith("D26.2")) return false;
		if(item.getFirstCode().startsWith("C22")) return false; //Animal diseases		
		if(item.getName().startsWith("1") || item.getName().startsWith("2") || item.getName().startsWith("3")) return false;
		if(item.getName().startsWith("4-") || item.getName().startsWith("4,")) return false;
		if(item.getName().startsWith("5") || item.getName().startsWith("6") || item.getName().startsWith("7")) return false;
		if(item.getName().startsWith("8") || item.getName().startsWith("9")) return false;
		if(item.getName().contains("[")) return false;
		return true;
	}
	
	
	private int addItemAndSynonymaNew(ListItem item, StringBuffer sb, List itemsAndSyns/*PrintWriter pw, boolean lastEntry*/){
		List<ListInterface> toAddItems = new ArrayList<ListInterface>();
		if(item.getSynonyma()==null || item.getSynonyma().isEmpty()){ //no synonyma, only one main item:
			toAddItems.add(item);
		}
		
		//now we compare the synonyma
		else{
			Iterator<Synonym> it = item.getSynonyma().iterator();
			toAddItems.add(item);
			while(it.hasNext()){	
				Synonym syn = it.next();
				if(!syn.isIgnored()){
					boolean isSimilar = false;
					for(int i=0;i<toAddItems.size(); i++){						
						isSimilar = StringUtilities.similarStrings(toAddItems.get(i).getName(), syn.getName(), item.getLanguage(), false);
						if(isSimilar){
							ListInterface bestItem = bestTerm(toAddItems.get(i),syn);
							if(!bestItem.equals(toAddItems.get(i))){
								toAddItems.remove(i);
								toAddItems.add(i, bestItem);
							}
							break;
						}
					}
					if(!isSimilar && !toAddItems.contains(syn))
						toAddItems.add(syn);
				}
			}
								
		}
		itemsAndSyns.addAll(toAddItems);
		return toAddItems.size();
	}										
	
	private ListInterface bestTerm(ListInterface currBestTerm, ListInterface newTerm){
		if(!currBestTerm.getName().contains(" ")) return currBestTerm; //current term is one word
		if(!newTerm.getName().contains(" ")) return newTerm; //new term is one word -> better term
		if(currBestTerm.getName().contains(",") && !newTerm.getName().contains(",")) return newTerm;
		return currBestTerm;
	}
	
	/**
	 * called from context bean:
	 * configurable in properties:
	 * 
	 * lists.<mode>.<type> and overridable by:
	 * lists.<mode>.<type>.<lang>
	 * 
	 * 
	 * @param mode
	 * @param type
	 * @param lang
	 * @return
	 */
	static public String getDisplayListName(String mode, String type, String lang) {
		String result = AppBean.getProperty("lists." + mode + "." + type,"");
		result = AppBean.getProperty("lists." + mode + "." + type + (lang!=null&&lang.length()>0 ? "." + lang : ""),result);
		
		List<String> getStringList = getStringList("lists.languages.", type, null);
		if (getStringList != null && getStringList.contains(lang)) {
			result = net.casus.util.StringUtilities.replace(result, "#{locale}", lang != null ? lang : "en");
		}
		else {
			result = net.casus.util.StringUtilities.replace(result, "#{locale}", lang != null ? lang : AppBean.getProperty("lists.default_languages." + type,"en"));
		}
		
		return result;
	}
}
