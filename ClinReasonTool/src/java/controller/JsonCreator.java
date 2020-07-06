package controller;

import java.io.*;
import java.util.*;

import javax.servlet.ServletContext;

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
 * @author ingahege
 *
 */
public class JsonCreator {

	public static final String TYPE_PROBLEM = "C";
	//public static final String TYPE_DDX = "C";
	public static final String TYPE_TEST = "E";
	public static final String TYPE_EPI = "F";
	public static final String TYPE_DRUGS = "D";
	//public static final String TYPE_MNG = "E";
	public static final String TYPE_PERSONS = "M";
	public static final String TYPE_MANUALLY_ADDED = "MA";
	public static final String TYPE_HEALTHCARE = "N";
	public static final String TYPE_CONTEXT = "I";
	public static final String TYPE_B = "B";
	public static final String TYPE_G = "G";
	public static final String TYPE_ANATOMY = "A";

	public static final String fileNameOneListEN = "src/html/jsonp_en.json"; //TODO we need the path to the HTML folder!!!
	public static final String fileNameOneListDE = "src/html/jsonp_de.json";
	public static final String fileNameOneListPL = "src/html/jsonp_pl.json";
	public static final String fileNameOneListSV = "src/html/jsonp_sv.json";
	public static final String fileNameOneListES = "src/html/jsonp_es.json";
	public static final String fileNameOneListPT = "src/html/jsonp_pt.json";
	private boolean createOneList = true; //if false, we create multiple lists for problems, ddx, etc.
	private static ServletContext context;	

	
	public synchronized void initJsonExport(ServletContext contextIn){
		context = contextIn;
		//language can be set with a query parameter
		String lang = null;
		try{
			lang = AjaxController.getInstance().getRequestParamByKey("lang");
		}
		catch(Exception e){}
		if(lang!=null && (lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("en"))){
			exportOneList(new Locale(lang));
		}
		else if(createOneList){			
			exportOneList(new Locale("en"));
			exportOneList(new Locale("de"));
			exportOneList(new Locale("pl"));
			exportOneList(new Locale("sv"));
			exportOneList(new Locale("es"));
			exportOneList(new Locale("pt"));
		}
		
	}
	
	public void setContext(ServletContext context){
		this.context = context;
	}
	
	/**
	 * We export the list of the given language from the database into a JSON file for use in the user interface 
	 * We also store the list items in the SummaryController for using it for the statement analysis and assessment.
	 * @param loc
	 */
	public List exportOneList(Locale loc){
		//setIdsForSyn();
		List<ListItem> items = new DBList().selectListItemsByTypesAndLang(loc, new String[]{TYPE_ANATOMY, TYPE_PROBLEM, TYPE_TEST,TYPE_DRUGS, TYPE_EPI, TYPE_MANUALLY_ADDED, TYPE_PERSONS, TYPE_HEALTHCARE, TYPE_CONTEXT, TYPE_B, TYPE_G});
		//we collect all items here, to sort it alphabetically
		List itemsAndSyns = new ArrayList();
		
		if(items==null || items.isEmpty()) return null; //then something went really wrong!
		try{
			File f = getMeshJsonFileByLoc(loc);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			int lines = 0;
			StringBuffer sb = new StringBuffer("[");
			for(int i=0; i<items.size(); i++){
				ListItem item = items.get(i);
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
			//SummaryStatementController.addListItems(itemsAndSyns, loc.getLanguage());
			for(int i=0; i<itemsAndSyns.size();i++){
				ListInterface li = (ListInterface) itemsAndSyns.get(i);				
				sb.append("{\"label\": \""+li.getName()+"\", \"value\": \""+li.getIdForJsonList()+"\"},\n");
			}
			boolean allowOwnEntries = AppBean.getProperty("AllowOwnEntries", false);
			if(allowOwnEntries) sb.append(getOwnEntry(loc));
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
	
	public static File getMeshJsonFileByLang(String lang){
		if (context != null) {
			if (lang.equals("de")) return new File(context.getRealPath(fileNameOneListDE));
			if (lang.equals("pl")) return new File(context.getRealPath(fileNameOneListPL));
			if (lang.equals("sv")) return new File(context.getRealPath(fileNameOneListSV));
			if (lang.equals("es")) return new File(context.getRealPath(fileNameOneListES));
			if (lang.equals("pt")) return new File(context.getRealPath(fileNameOneListPT));
			return new File(context.getRealPath(fileNameOneListEN));
		}
		else {
			String name = fileNameOneListEN;
			if (lang.equals("de"))  name = fileNameOneListDE;
			if (lang.equals("pl"))  name = fileNameOneListPL;
			if (lang.equals("sv"))  name = fileNameOneListSV;
			if (lang.equals("es"))  name = fileNameOneListES;
			if (lang.equals("pt"))  name = fileNameOneListPT;
			
			int idx = name.lastIndexOf('/');
			name = name.substring(idx+1);
			return new File(name);
		}
	
	}
	public static File getMeshJsonFileByLoc(Locale loc){
		return getMeshJsonFileByLang(loc.getLanguage());
	}
}
