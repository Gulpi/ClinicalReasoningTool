package controller;

import java.io.*;
import java.util.*;

//import net.casus.util.*;
import database.DBClinReason;
import model.ListItem;
import util.*;

/**
 * We import a Mesh ACSII file into the database
 * @author ingahege
 *
 */
public class MeshImporter {

	public static void main(String[] args){
		String file = "/Users/ingahege/ownCloud/documents/Inga/marie_curie/WP2_concept/mesh/d2016.bin";
		createRecord(file);
	}
	
	private static void createRecord(String file){
		Map<String, List<String>> m = new TreeMap<String, List<String>>();
		String line;
		try{
			LineNumberReader lbr = new LineNumberReader(new FileReader(file));
			while((line=lbr.readLine())!=null){
				if(line.equals("*NEWRECORD")){ 
					ListItem li = createListItem(m);
					saveListItem(li);
					m = new TreeMap<String, List<String>>();
				}
				importLine(line, m);
	
			}
		}
		catch(Exception e){
			System.out.println("MeshImporter: Exception= " + StringUtilities.stackTraceToString(e));
		}
	}
	
	private static void importLine(String line, Map m){			
		if(line!=null && !line.equals("") && line.contains("=")){
			int idx = line.indexOf("=");
			String key = line.substring(0,idx).trim();
			String value = line.substring(idx+1).trim();
			//if(m==null) m = new TreeMap<String, List<String>>();
			if(m.get(key)==null){
				List valList = new ArrayList<String>();
				valList.add(value);
				m.put(key, valList);
			}
			else{
				List valList = (List<String>)m.get(key);
				valList.add(value);
				m.put(key, valList);			
			}
		}		
	}
	
	/**
	 * We create an ItemList object to store it in the database
	 * @param m
	 */
	private static ListItem createListItem(Map<String, List<String>> m){
		ListItem li = new ListItem(); 
		li.setName(m.get("MH").get(0));
		li.setMesh_id(m.get("UI").get(0));
		String firstCode = ""; 
		if(m.get("MN")!=null && m.get("MN").get(0)!=null) firstCode = m.get("MN").get(0);
		if(m.get("MN")!=null && m.get("MN").size()>1){ //other codes:
			Set mns = new TreeSet();
			for(int i=1; i<m.get("MN").size(); i++){
				mns.add(m.get("MN").get(i));
			}
			li.setOtherCodes(mns);
		}
		int level = StringUtilities.countInString(firstCode,".")+1;
		if(firstCode.equals("")) level = 0;
		li.setFirstCode(firstCode);
		li.setLevel(level);
		if(firstCode!=null && !firstCode.trim().equals("")) li.setItemType(String.valueOf(firstCode.charAt(0)));
		List<String> cats = m.get("PA");
		if(cats!=null){
			String catString="";
			for(int i=0; i<cats.size(); i++){
				if(i<cats.size()-1) catString += cats.get(i)+",";
				else catString += cats.get(i);
			}
			li.setCategory(catString);
		}
		if(m.get("MS")!=null && m.get("MS").get(0)!=null) li.setNote(m.get("MS").get(0));
		if(m.get("AN")!=null && m.get("AN").get(0)!=null) li.setItem_description(m.get("AN").get(0));
		if(m.get("EC")!=null && m.get("EC").get(0)!=null) li.setMesh_ec(m.get("EC").get(0));

		li.setSource("MESH");
		li.setLanguage("en");
		if(m.get("ENTRY")!=null){ //synonyma:
			Set entries = new TreeSet();
			for(int i=0; i<m.get("ENTRY").size(); i++){
				entries.add(m.get("ENTRY").get(i));
			}
			li.setSynonyma(entries);
		}
		return li;
	}
	
	private static void saveListItem(ListItem li){
		new DBClinReason().saveAndCommit(li);
	}
}
