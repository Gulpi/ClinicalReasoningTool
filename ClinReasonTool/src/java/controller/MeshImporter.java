package controller;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import database.DBClinReason;
import beans.list.*;
import util.*;

/**
 * We import a Mesh ACSII file into the database
 * TODO: exclude similar synonyma already upon import, exclude synonyma containing a |, include mechanism 
 * to assign unique ids to synonyma.
 * 
 * use ids > 240.000 - 240.500 to add new entries manually.
 * @author ingahege
 *
 */
public class MeshImporter {
	static String file = "/Users/ingahege/ownCloud/documents/Inga/marie_curie/WP2_concept/mesh/d2016.bin";
	static String file_DE = "/Users/ingahege/ownCloud/documents/Inga/marie_curie/WP2_concept/mesh/deutsch/MeSH-2016.xml";
	static String file_campus = "/Users/ingahege/ownCloud/Shared/instruct (2)/CASUS/CampusCasus/campus_list_items_not_in_crt_list.txt";

	
	public static void main(String[] lang){
		//if(lang.equals("en")) createRecord();
		//if(lang.equals("de")) importMeshDE();
		//importCampusList();
		//XAPIController.testXAPI();
		ScriptCopyController.main(null);
	}
	
	/*private static void importCampusList(){
		try{
			LineNumberReader lbr = new LineNumberReader(new FileReader(file_campus));
			new HibernateSession().initHibernate();
			List<ListItem> meshList = new DBList().selectListItemByLang("de");
			String line;
			String matchStr ="";
			List<String> l = new ArrayList<String>();
			while((line=lbr.readLine())!=null){
				l.add(line.trim());
			}
			String line2;
			List<String> jsonList = new ArrayList<String>();
			while((line2=lbr.readLine())!=null){
				jsonList.add(line2.trim());
			}
			//if(l==null) return;
			List<String> entriesFound = new ArrayList<String>();
			for(int i=0; i<l.size(); i++){
				String s = l.get(i);
				boolean isSimilar = false;
				innerLoop:
				for(int j=0; j<meshList.size();j++){
					
					isSimilar = StringUtilities.similarStrings(s, meshList.get(j).getName(), new Locale("de"));
					if(isSimilar){
						entriesFound.add(s + " , " + meshList.get(j).getName() );
						matchStr = meshList.get(j).getName();
						//CRTLogger.out(s + " , " + meshList.get(j).getName() + " = "+ isSimilar,  CRTLogger.LEVEL_TEST);
						break innerLoop;
					}
					else{
						if(meshList.get(j).getSynonyma()!=null){
							Iterator it =  meshList.get(j).getSynonyma().iterator();
							while(it.hasNext()){
								Synonym syn = (Synonym) it.next();
								if(syn.getName().equals("Rose Natal Grass") && s.equals("Blutungsneigung-Nase"))
									System.out.println("");
								isSimilar = StringUtilities.similarStrings(s, syn.getName(), new Locale("de"));
								if(isSimilar){
									entriesFound.add(s + " , " + syn.getName() );
									matchStr = syn.getName();
									//CRTLogger.out(s + " , " + meshList.get(j).getName() + " = "+ isSimilar,  CRTLogger.LEVEL_TEST);
									break innerLoop;
								}
							}
						}
					}
				}
				ListItem2 li2= new ListItem2();
				li2.setName(s);
				li2.setMatched(isSimilar);
				if(isSimilar && matchStr!=null)
					li2.setMatchedItem(matchStr);
				new DBList().saveAndCommit(li2);
				if(!isSimilar){
					CRTLogger.out(s + " no match found",  CRTLogger.LEVEL_TEST);
				}
				else isSimilar = false;

			}
			if(entriesFound!=null){
				
				for(int k =0; k<entriesFound.size(); k++){
					CRTLogger.out("matches: ", CRTLogger.LEVEL_TEST );
					CRTLogger.out(entriesFound.get(k), CRTLogger.LEVEL_TEST);
				}
			}
			lbr.close();
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
		
	}*/
	
	
	private static void importMeshDE(){
		
		try{
			LineNumberReader lbr = new LineNumberReader(new FileReader(file_DE));
			String line;
			List<String[]> l = new ArrayList<String[]>();
			String[] str = new String[3];
			boolean readNextLine1 = false;
			boolean readNextLine2 = false;
			while((line=lbr.readLine())!=null){
				
				if(line.contains("<DescriptorUI>")){ 
					if(str[0]==null || str[0].equals("")) str[0] = StringUtils.substringBetween(line, "<DescriptorUI>", "</DescriptorUI>");
				}
				if(line.contains("<TreeNumberList>"))readNextLine1 = true;
				if(readNextLine1 && line.contains("<TreeNumber>")){
					str[1] = StringUtils.substringBetween(line, "<TreeNumber>", "</TreeNumber>");
					readNextLine1 = false;					
				}					
				
				if(line.contains("<TermUI>ger"))readNextLine2 = true;
				if(readNextLine2 && line.contains("<String>")){
					str[2] = StringUtils.substringBetween(line, "<String>", "</String>");
					readNextLine2 = false;
					CRTLogger.out(str[0]+", " + str[1] + ", " + str[2], CRTLogger.LEVEL_TEST);
					l.add(str);
					str = new String[3];
				}
			}
			importListItemDE(l);
			lbr.close();
		}
		catch (Exception e){
			CRTLogger.out("MeshImporter_de: Exception= " + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);

		}
	}
	
	/**
	 * str[0] = meshID
	 * str[1] = <TreeNumber>
	 * str[2] = german Term
	 * @param strList
	 */
	private static void importListItemDE(List<String[]> strList){
		DBClinReason dbcr = new DBClinReason();
		List<ListItem> deItems = new ArrayList<ListItem>();
		ListItem deItem = null;
		//List<Synonym> deSynonyma = new ArrayList<Synonym>();
		String currentId="";
		for(int i=0; i<strList.size(); i++){
			String[] str = strList.get(i);
			if(str[0]!=null){ //then it a new ListItem
				currentId = str[0];
				deItem = new ListItem("de", "MESH", str[2]); //dbcr.selectListItemByMeshId(str[0]);
				deItem.setLevel(StringUtils.countMatches(str[1], ".")+1);
				deItem.setItemType(StringUtils.substring(str[1], 0, 1));
				deItem.setMesh_id(str[0]);
				deItem.setFirstCode(str[1]);
				deItems.add(deItem);
				dbcr.saveAndCommit(deItem);
			}
			else{
				Synonym syn = new Synonym(new Locale("de"), str[2]);
				syn.setListItemId(deItem.getItem_id());
				dbcr.saveAndCommit(syn);
			}
		}
	}
	
	/*private static void createRecord(){
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
			lbr.close();
		}
		catch(Exception e){
			System.out.println("MeshImporter: Exception= " + StringUtilities.stackTraceToString(e));
		}
	}*/
	
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
		li.setLanguage(new Locale("en"));
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
