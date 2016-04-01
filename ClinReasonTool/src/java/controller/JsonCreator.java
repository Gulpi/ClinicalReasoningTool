package controller;

import java.io.*;
import java.util.*;

import beans.graph.SimpleVertex;
import database.DBClinReason;
import model.ListItem;
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
	public static final String TYPE_DRUGS = "D";
	public static final String TYPE_MNG = "E";
	private static final String fileNameOneList = "jsonp.json"; //TODO we need the path to the HTML folder!!!
	//A=Anatomy,B=Organisms, F=Psychiatry/Psychology, G=Phenomena and Processes, H=Disciplines/Occupations
	
	//configurations: TODO get from property file
	private boolean createOneList = true; //if false, we create multiple lists for problems, ddx, etc.
	private boolean includeSynonyma = true;
	private String language="en";
	public static final int MIN_SIMILARITY_DISTANCE = 3; //if we have a level 1 similarity the item is not included
	
	
	public synchronized void initJsonExport(){
		if(createOneList) exportOneList();
	}
	
	//TODO also load item which have a corresponding (secondary code)
	private void exportOneList(){
		List<ListItem> items = new DBClinReason().selectListItemsByTypes(new String[]{TYPE_PROBLEM, TYPE_TEST,TYPE_DRUGS});
		if(items==null || items.isEmpty()) return; //then something went really wrong!
		try{
			File f = new File(fileNameOneList);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			int lines = 0;
			for(int i=0; i<items.size(); i++){
				ListItem item = items.get(i);
				StringBuffer sb = new StringBuffer();
				if(i==0) sb.append("[");
				sb.append("{\"label\": \""+item.getName()+"\", \"value\": \""+item.getItem_id()+"\"}");
				if(i<items.size()-1) sb.append(",");
				lines++;
				lines += addSynonyma(item, pw);
				
				if(i==items.size()-1) sb.append("]");
					
				//System.out.println(sb.toString());
				pw.println(sb.toString());
				//TODO synonyma
			}
			//pw.println("]");
			pw.flush();
		    pw.close();
		    System.out.println("lines exported: " + lines);
		}
		catch( Exception e){
			System.out.println(StringUtilities.stackTraceToString(e));
		}
	}
	
	/**
	 * We check all Synonyma for the given ListItem and add them to the list if they are not too similar to the main 
	 * listItem or any already added synonyma of this ListItem.
	 * @param item
	 * @param pw
	 * @return
	 */
	private int addSynonyma(ListItem item, PrintWriter pw){
		if(item.getSynonyma()==null) return 0;
		List<String> addedSyn = new ArrayList<String>();
		Iterator<String> it = item.getSynonyma().iterator();
		int counter = 1;
		while(it.hasNext()){	
			String syn = it.next();
			//boolean doAdd = false;
			int distance = StringUtilities.compareStrings(item.getName(), syn);
			if(distance > MIN_SIMILARITY_DISTANCE){ //then it has enough difference to add 
				//now check similarity to already added synonyma: 
				boolean doAdd = true;
				if(addedSyn!=null || !addedSyn.isEmpty()){
					for(int i=0; i < addedSyn.size(); i++){
						int distance2 = StringUtilities.compareStrings(syn, addedSyn.get(i));
						if(distance2<=MIN_SIMILARITY_DISTANCE){ //then we have found a similar item
							System.out.println("not added: " + syn + " - " + addedSyn.get(i));
							doAdd = false;
							break;
						}
					}
				}
				addedSyn.add(syn);
				if(doAdd){
					String s = "{\"label\": \""+syn+"\", \"value\": \""+SimpleVertex.SYN_VERTEXID_PREFIX+item.getItem_id()+"_"+counter+"\"},";
					counter++;
					pw.println(s);
				}
			}
		}
		return counter;
		//return sb.toString();
	}

}
