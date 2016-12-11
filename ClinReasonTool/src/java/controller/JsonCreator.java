package controller;

import java.io.*;
import java.util.*;

import application.AppBean;
import database.DBList;
import model.ListInterface;
import model.ListItem;
import model.Synonym;
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

	public static final String fileNameOneListEN = "jsonp_en.json"; //TODO we need the path to the HTML folder!!!
	public static final String fileNameOneListDE = "jsonp_de.json";
	//A=Anatomy,B=Organisms, F=Psychiatry/Psychology, G=Phenomena and Processes, H=Disciplines/Occupations
	
	//configurations: TODO get from property file
	private boolean createOneList = true; //if false, we create multiple lists for problems, ddx, etc.
	private boolean includeSynonyma = true;
	
	
	public synchronized void initJsonExport(){
		if(createOneList){
			exportOneList(new Locale("en"));
			exportOneList(new Locale("de"));
		}
		
	}
	
	//TODO also load item which have a corresponding (secondary code)
	private void exportOneList(Locale loc){
		//setIdsForSyn();
		List<ListItem> items = new DBList().selectListItemsByTypesAndLang(loc, new String[]{TYPE_PROBLEM, TYPE_TEST,TYPE_DRUGS, TYPE_EPI, TYPE_MANUALLY_ADDED, TYPE_PERSONS, TYPE_HEALTHCARE, TYPE_CONTEXT, TYPE_B, TYPE_G});
		//we collect all items here, to sort it alphabetically
		List itemsAndSyns = new ArrayList();
		
		if(items==null || items.isEmpty()) return; //then something went really wrong!
		try{
			File f = getMeshJsonFileByLoc(loc);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			int lines = 0;
			StringBuffer sb = new StringBuffer("[");
			for(int i=0; i<items.size(); i++){
				ListItem item = items.get(i);
				if(doAddItem(item)){
					lines += addItemAndSynonymaNew(item, sb, itemsAndSyns);
				}
			}
			Collections.sort(itemsAndSyns);
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
		}
		catch( Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
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
		if(item.getFirstCode()==null) return true;
		if(item.getFirstCode().startsWith("D20.0") || item.getFirstCode().startsWith("D20.1")) return false;
		if(item.getFirstCode().startsWith("D20.3") || item.getFirstCode().startsWith("D20.4")) return false;
		if(item.getFirstCode().startsWith("D20.7") || item.getFirstCode().startsWith("D20.8")) return false;
		if(item.getFirstCode().startsWith("D20.9")) return false;
		//if(item.getFirstCode().startsWith("D27.")) return false;
		if(item.getFirstCode().startsWith("D26.2")) return false;
		//F
		//if(item.getFirstCode().startsWith("F") && !item.getFirstCode().startsWith("F01.145")) return false;
		//C
		if(item.getFirstCode().startsWith("C22")) return false; //Animal diseases
		//if(item.getFirstCode().startsWith("C02.782.147.")) return false; 
		//if(item.getFirstCode().startsWith("C02.782.791")) return false;
		//if(item.getFirstCode().startsWith("C02.782.310.") ||item.getFirstCode().startsWith("C02.782.600.")) return false; 
		//if(item.getFirstCode().startsWith("C02.782.815")) return false;
		//if(item.getFirstCode().startsWith("C02.782.930")) return false;



		
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
						isSimilar = StringUtilities.similarStrings(toAddItems.get(i).getName(), syn.getName(), item.getLanguage());
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
		/*for(int i=0; i<toAddItems.size();i++){
			sb.append("{\"label\": \""+toAddItems.get(i).getName()+"\", \"value\": \""+toAddItems.get(i).getIdForJsonList()+"\"},\n");
		}*/

		return toAddItems.size();
	}					
					
			
	
	private ListInterface bestTerm(ListInterface currBestTerm, ListInterface newTerm){
		
		if(!currBestTerm.getName().contains(" ")) return currBestTerm; //current term is one word
		if(!newTerm.getName().contains(" ")) return newTerm; //new term is one word -> better term
		if(currBestTerm.getName().contains(",") && !newTerm.getName().contains(",")) return newTerm;
		return currBestTerm;
	}
	
	public static File getMeshJsonFileByLoc(Locale loc){
		if (loc.getLanguage().equals(new Locale("de").getLanguage())) return new File(fileNameOneListDE);
		/*if (loc.getLanguage().equals(new Locale("en").getLanguage())) */ return new File(fileNameOneListEN);
	}
	
	public static String getMeshJsonFileNameByLoc(Locale loc){
		if (loc.getLanguage().equals(new Locale("de").getLanguage())) return fileNameOneListDE;
		/*if (loc.getLanguage().equals(new Locale("en").getLanguage())) */ return fileNameOneListEN;
	}

}
