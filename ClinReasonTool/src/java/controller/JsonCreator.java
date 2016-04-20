package controller;

import java.io.*;
import java.util.*;

import database.DBList;
import model.ListItem;
import model.Synonym;
import util.StringUtilities;

/**
 * Creates Json files based on lists
 * Format: {"label": "Calcimycin", "value": "3"},
 * @author ingahege
 *
 */
public class JsonCreator {

	public static final String TYPE_PROBLEM = "C";
	public static final String TYPE_DDX = "C";
	public static final String TYPE_TEST = "E";
	public static final String TYPE_EPI = "F";
	public static final String TYPE_DRUGS = "D";
	public static final String TYPE_MNG = "E";
	public static final String TYPE_PERSONS = "M";
	public static final String TYPE_MANUALLY_ADDED = "MA";
	private static final String fileNameOneListEN = "jsonp_en.json"; //TODO we need the path to the HTML folder!!!
	private static final String fileNameOneListDE = "jsonp_de.json";
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
		List<ListItem> items = new DBList().selectListItemsByTypesAndLang(loc, new String[]{TYPE_PROBLEM, TYPE_TEST,TYPE_DRUGS, TYPE_EPI, TYPE_MANUALLY_ADDED, TYPE_PERSONS});
		if(items==null || items.isEmpty()) return; //then something went really wrong!
		try{
			File f = null;
			if (loc.getLanguage().equals(new Locale("en").getLanguage())) f = new File(fileNameOneListEN);
			else if (loc.getLanguage().equals(new Locale("de").getLanguage())) f = new File(fileNameOneListDE);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			int lines = 0;
			StringBuffer sb = new StringBuffer("[");
			for(int i=0; i<items.size(); i++){
				ListItem item = items.get(i);
				if(doAddItem(item)){
					sb.append("{\"label\": \""+item.getName()+"\", \"value\": \""+item.getItem_id()+"\"},\n");
					lines++;
					lines += addSynonyma(item, sb);
				}
			}
			sb.replace(sb.length()-2, sb.length(), "]");
			pw.print(sb.toString());
			pw.flush();
		    pw.close();
		    System.out.println("lines exported: " + lines);
		}
		catch( Exception e){
			System.out.println(StringUtilities.stackTraceToString(e));
		}
	}
	
	/**
	 * Make some improvements of the list, we do not add a certain item_level depending on the category etc.
	 * maybe do more here....
	 * @param item
	 * @return
	 */
	private boolean doAddItem(ListItem item){
		//D:
		if(item.getItemType().equals("D") && item.getLevel()>=10) return false;
		if(item.getFirstCode()==null) return true;
		if(item.getFirstCode().startsWith("D20.0") || item.getFirstCode().startsWith("D20.1")) return false;
		if(item.getFirstCode().startsWith("D20.3") || item.getFirstCode().startsWith("D20.4")) return false;
		if(item.getFirstCode().startsWith("D20.7") || item.getFirstCode().startsWith("D20.8")) return false;
		if(item.getFirstCode().startsWith("D20.9")) return false;
		if(item.getFirstCode().startsWith("D27.")) return false;
		if(item.getFirstCode().startsWith("D26.2")) return false;
		//F
		if(item.getFirstCode().startsWith("F") && !item.getFirstCode().startsWith("F01.145")) return false;
		//C
		if(item.getFirstCode().startsWith("C22")) return false; //Animal diseases
		
		if(item.getName().startsWith("1") || item.getName().startsWith("2") || item.getName().startsWith("3")) return false;
		if(item.getName().startsWith("4-") || item.getName().startsWith("4,")) return false;
		if(item.getName().startsWith("5") || item.getName().startsWith("6") || item.getName().startsWith("7")) return false;
		if(item.getName().startsWith("8") || item.getName().startsWith("9")) return false;
		if(item.getName().contains("[")) return false;
		return true;
	}
	
	/*private void setIdsForSyn(){
		DBClinReason dbcr = new DBClinReason();
		List<Synonym> l = dbcr.selectSynonyma();
		long counter = 142620;
		for(int i=0; i<l.size(); i++){
			l.get(i).setId(counter);
			dbcr.saveAndCommit(l.get(i));
			counter++;
		}
	}*/
	
	/**
	 * We check all Synonyma for the given ListItem and add them to the list if they are not too similar to the main 
	 * listItem or any already added synonyma of this ListItem.
	 * @param item
	 * @param pw
	 * @return
	 */
	private int addSynonyma(ListItem item, StringBuffer sb/*PrintWriter pw, boolean lastEntry*/){
		if(item.getSynonyma()==null) return 0;
		List<Synonym> addedSyn = new ArrayList<Synonym>();
		Iterator<Synonym> it = item.getSynonyma().iterator();
		int counter = 0;
		while(it.hasNext()){	
			Synonym syn = it.next();
			boolean isSimilar = StringUtilities.similarStrings(item.getName(), syn.getName(), item.getLanguage());
			if(!isSimilar/*distance > MIN_SIMILARITY_DISTANCE*/){ //then it has enough difference to add 
				//now check similarity to already added synonyma: 
				boolean doAdd = true;
				if(addedSyn!=null || !addedSyn.isEmpty()){
					for(int i=0; i < addedSyn.size(); i++){
						boolean isSimilar2 = StringUtilities.similarStrings(syn.getName(), addedSyn.get(i).getName(), syn.getLocale());
						if(isSimilar2/*distance2<=MIN_SIMILARITY_DISTANCE*/){ //then we have found a similar item
							//System.out.println("not added: " + syn + " - " + addedSyn.get(i).getName());
							doAdd = false;
							break;
						}
					}
				}
				addedSyn.add(syn);
				if(doAdd){
					sb.append("{\"label\": \""+syn.getName()+"\", \"value\": \""+Synonym.SYN_VERTEXID_PREFIX+syn.getId()+"\"},\n");
					counter++;
				}
			}
		}
		return counter;
	}

}
