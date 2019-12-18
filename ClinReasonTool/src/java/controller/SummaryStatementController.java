package controller;

import java.util.*;

import actions.scoringActions.ScoringSummStAction;
import application.AppBean;
import beans.list.*;
import beans.relation.Relation;
import beans.relation.SummaryStElem;
import beans.relation.SummaryStNumeric;
import beans.relation.SummaryStatement;
import beans.relation.SummaryStatementSQ;
import beans.scripts.PatientIllnessScript;
import database.DBClinReason;
import database.DBList;
import model.SemanticQual;
import util.*;

/**
 * handles everything concerning summary statements and semantic qualifiers.
 * @author ingahege
 *
 */
public class SummaryStatementController {
	static private SummaryStatementController instance = new SummaryStatementController();
	static public SummaryStatementController getInstance() { return instance; }
	/**
	 * caching of the MesH entries (value) for each language (key). We need this for the scoring of the 
	 * summary statements (more suitable than the JSON file, because we have additional information (such as 
	 * category...) 
	 */
	private static Map<String, List<ListItem>> listItems;
	private static Map<String, List<ListItem>> aList; //Mesh items with code "A" - have it separate because not considered for scripts.
	private static List<SIUnit> unitList;
	//key = vp_id
	private static Map<String, PatientIllnessScript>  tempExpMaps= new TreeMap();
	
	/**
	 * returns a List of ListItems for the given language or null if nothing is found.
	 * @param lang
	 * @return
	 */
	public static List<ListItem> getListItemsByLang(String lang) {
		if(listItems==null || lang==null) return null;
		return (List<ListItem>) listItems.get(lang);
	}
	
	/**
	 * adds a list of ListItems to the Map (key=language). 
	 * @param myListItems
	 * @param lang
	 */
	public static void addListItems(List<ListItem> myListItems, String lang) {
		if(SummaryStatementController.listItems == null) 
			SummaryStatementController.listItems = new TreeMap<String, List<ListItem>>();
		if(!listItems.containsKey(lang))
			listItems.put(lang, myListItems);
	}
	
	public static void addListItem(ListItem myListItem, String lang) {
		if(SummaryStatementController.listItems == null) 
			SummaryStatementController.listItems = new TreeMap<String, List<ListItem>>();
		if(!listItems.containsKey(lang))
			listItems.put(lang, new ArrayList());
		
		listItems.get(lang).add(myListItem);
	}
	
	/**
	 * Adds a listItem or synonym to the aList containing all mesh items with code A (anatomy)
	 * @param listItem
	 * @param lang
	 */
	public static void addListItemsA(ListItem listItem, String lang) {
		try{
			if(SummaryStatementController.aList == null) 
				SummaryStatementController.aList = new TreeMap<String, List<ListItem>>();
			if(!aList.containsKey(lang)){
				aList.put(lang, new ArrayList<ListItem>());
			}
			aList.get(lang).add(listItem);
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}


	public static Map<String, List<SemanticQual>> loadSemanticQuals(){
		DBList dbl = new DBList();
		HashMap<String, List<SemanticQual>> map = new HashMap<String, List<SemanticQual>>();
		map.put("en", dbl.selectSemanticQuals("en"));
		map.put("de", dbl.selectSemanticQuals("de"));
		return map;
	}
	
	/**
	 * We check for new expert summary statements and analyze them concerning the use of semantic qualifiers. 
	 * After analysis we save the statements and set the analyzed flag to true.
	 */
	public static void analyzeSemanticQualsStatements(){
		List<SummaryStatement> statements = new DBClinReason().getSummaryStatementsByAnalyzed(/*PatientIllnessScript.TYPE_EXPERT_CREATED,*/ false);
		if(statements==null || statements.isEmpty()) return; //no new statements to analyze
		for(int i=0; i<statements.size(); i++){
			SummaryStatement stst = statements.get(i);
			checkForSemanticQualifiers(stst);
		}
		new DBClinReason().saveAndCommit(statements);
	}
	
	/**
	 * We check the text of the summary statement concerning the use of semantic qualifiers.
	 * all hits are stored in the database.
	 * @param stst
	 */
	public static void checkForSemanticQualifiers(SummaryStatement stst){
		try{
			if(stst==null || stst.getText()==null || stst.getText().trim().isEmpty() || stst.getLang()==null) return;
			List<SemanticQual> sqs = AppBean.getSemantiQualsByLang(stst.getLang());
			if(sqs==null) return;
			//List<SemanticQual> hits = new ArrayList<SemanticQual>();
			List<SummaryStatementSQ> hits= new ArrayList<SummaryStatementSQ>();
			for(int i=0; i < sqs.size(); i++){
				String s = sqs.get(i).getQualifier().toLowerCase().trim();
				if(stst.getText().toLowerCase().contains(s)){
					SummaryStatementSQ sssq = new SummaryStatementSQ(stst.getId(),sqs.get(i).getId(), s);
					sssq.setPosition(stst.getText().toLowerCase().indexOf(s));
					//sssq.setTextMatch(StringUtilities.getWordFromText(stst.getText(), s));
					hits.add(sssq);			
				}
			}
			if(hits!=null && !hits.isEmpty() && stst.getId()>0)
				new DBClinReason().saveAndCommit(hits);
			stst.setSqHits(hits);
			stst.setAnalyzed(true);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
		
	
	public static SummaryStatement testSummStRating(){
		Locale loc = new Locale("en");
		String text = "22 year old man, comes inn ill and distressed. Patient could not continue the trekking tour. Felt weak. Had a cold 2 weeks ago. Has fever, tachypnea, tachycardia, leukocytosis (4/4 SIRS) Consolidation right upper lobe. Lobal pneumonia is diagnosed. Start oxygen,penicillin, fluids, and paracetamol. ";
		String vpId = "408797_2"; 
		return testSummStRating(loc, text, vpId);
	}
	
	private static void analyzeExpStatement(SummaryStatement st){
		if(st.getItemHits()!=null && !st.getItemHits().isEmpty()) return; //already done....
		List<ListItem> items = getListItemsByLang(st.getLang());
		List<String> textAsList = StringUtilities.createStringListFromString(st.getText(), true);
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(st.getLang()), st);
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);		
			compareSimilarList(items, st, s);
			if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, s);			
		}
		checkForSemanticQualifiers(st);
	}
	
	/**
	 * test summary statement rating using the list creation mechanism.
	 * (1) exp fdgs & ddx -> expPis.getSummSt().getDiagnosesHits() und expPis.getSummSt().getFindingsHits()
	 * (2) exp map items -> expPis.getProblems(), expPis.getDiagnoses(), expPis.getTests(), expPis.getMngs()
	 * (3) student fdgs & ddx (schon drin in den Spalten Findings und Diagnoses)
	 * (4,5) number of matches mit statement und map (schon drin) 
	 * (6,7) Number of additional fdgs, ddx / items -> können wir im Excel sehen
	 * @param contextIn
	 */
	public static SummaryStatement testSummStRating(Locale loc, String text, String vpId){
		
		List<ListItem> items = getListItemsByLang(loc.getLanguage());
		PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);		
		analyzeExpStatement(expPis.getSummSt());
		if(!tempExpMaps.containsKey(vpId)) tempExpMaps.put(vpId, expPis);
		
		if(text==null || text.isEmpty() || items==null) return null; 
		List<String> textAsList = StringUtilities.createStringListFromString(text, true);
		SummaryStatement st = new SummaryStatement();
		st.setText(text);
		st.setLang(loc.getLanguage());
		if(textAsList==null) return st;
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(loc.getLanguage()), st);
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);		
			compareSimilarList(items, st, s);
			st.addUnit(compareSIUnits(s, i));
			if(aList!=null) compareSimilarList(aList.get(loc.getLanguage()), st, s);			
		}
		
		checkForSemanticQualifiers(st); //count SQ
		compareNumbers(st);
		if(expPis!=null){
			calculateMatchesWithExpStatement(st.getItemHits(), expPis.getSummSt().getText());
			compareWithExpIllScript(st, expPis);
		}
		ScoringSummStAction scoreAct = new ScoringSummStAction();
		int sqScore = scoreAct.calculateSemanticQualScoreNew(st); //score SQ
		st.setSqScore(sqScore);
		scoreAct.calculateNarrowing(st, expPis.getSummSt());
		scoreAct.calculateTransformation(expPis.getSummSt(), st);
		return st;		
	}
	
	/**
	 * We look whether we can find any of the SI units in the list in the element of the summary statement. 
	 * @param text
	 * @return
	 */
	private static SummaryStNumeric compareSIUnits(String text, int pos){
		if(text==null) return null;	
		
		for(int i=0; i< unitList.size(); i++){
			if(text.equalsIgnoreCase(unitList.get(i).getName())) return new SummaryStNumeric(unitList.get(i), pos);
			
			if(text.contains("/")){ //identify something like g/dl and count it once
				String text2 = text.replace('/', ' ');
				List<String> s = StringUtilities.createStringListFromString(text2, true);
				if(s!=null){
					for(int j=0;j < s.size();j++){
						String st = s.get(j);
						if(st.equalsIgnoreCase(unitList.get(i).getName())) 
							return new SummaryStNumeric(unitList.get(i),text, pos);
					}
				}
			}
			if(text.contains(unitList.get(i).getName())){ //identify something like 14mg or 79%
				String text3 = text.replace(unitList.get(i).getName(), "");
				if(text3!=null && StringUtilities.isNumeric(text3))
					return new SummaryStNumeric(unitList.get(i), text, pos);
			}
		}
		return null;
	}
	
	/**
	 * We look for numbers in the original text, before removing commas etc.
	 * @param text
	 * @param pos
	 * @return
	 */
	private static void compareNumbers(SummaryStatement st){
		if(st==null || st.getText()==null) return;
		List<String> s = StringUtilities.createStringListFromString(st.getText(), false);
		
		for(int i=0; i<s.size(); i++){
			//identify something like 45 or 4.5
			String s1 = s.get(i);
			if(StringUtilities.isNumeric(s.get(i))){ 
				//String s1 = s.get(i);
				st.addUnit(new SummaryStNumeric(null, s.get(i), i));
			}
		}
	}
	
	/**
	 * We compare the hits we have with the illness Script elements of the expert and store if and where we have a hit.
	 * @param st
	 * @param vpId
	 */
	private static void compareWithExpIllScript(SummaryStatement st, PatientIllnessScript pis){
		if(st.getItemHits()==null || pis==null) return; 
		
		for (int i=0; i<st.getItemHits().size(); i++){
			ListItem li = st.getItemHits().get(i).getListItem();
			if(pis.getRelationByListItemIdAndType(li.getListItemId(), Relation.TYPE_PROBLEM)!=null)
				st.getItemHits().get(i).setExpertScriptMatch(Relation.TYPE_PROBLEM);
			else if(pis.getRelationByListItemIdAndType(li.getListItemId(), Relation.TYPE_DDX)!=null) 
				st.getItemHits().get(i).setExpertScriptMatch(Relation.TYPE_DDX);
			else if(pis.getRelationByListItemIdAndType(li.getListItemId(), Relation.TYPE_TEST)!=null) 
				st.getItemHits().get(i).setExpertScriptMatch(Relation.TYPE_TEST);
			else if(pis.getRelationByListItemIdAndType(li.getListItemId(), Relation.TYPE_MNG)!=null) 
				st.getItemHits().get(i).setExpertScriptMatch(Relation.TYPE_MNG);
		}
	}
	
	/**
	 * compare string s with the items list and their synonyms for similar entries.
	 * @param items
	 * @param st
	 * @param loc
	 * @param s
	 */
	private static boolean compareSimilarList(List items, SummaryStatement st, String s){	
		for(int j=0;j<items.size(); j++){ //comparison with adapted Mesh list:
			ListItem li = (ListItem) items.get(j);	
			Locale loc = new Locale(st.getLang());

			boolean isSimilar = StringUtilities.similarStrings(s, li.getName(), loc);
			
			if(isSimilar){
				st.addItemHit(li);
				return true;
			}
			
			if(li.getSynonyma()!=null){
				Iterator<Synonym> it = li.getSynonyma().iterator();
				while(it.hasNext() && !isSimilar){
					Synonym syn = it.next();
					isSimilar = StringUtilities.similarStrings(s, syn.getName(), loc);
					if(isSimilar){
						st.addItemHit(li, syn);						
						return true;
					}
				}
			}
		}				
		return false;
	
	}
	/**
	 * compare list entries directly with the text to identify matches with items of two or more words. 
	 * @param items
	 * @param st
	 */
	private static void compareList(List items, SummaryStatement st){
		for(int j=0;j<items.size(); j++){ //look for two or more word items, e.g. "productive cough"
			ListItem li = (ListItem) items.get(j);
			/*if(li.getItem_id()==4655){
				CRTLogger.out("", 1);
			}*/
			if (li.getName().contains(" ") && st.getText().toLowerCase().contains(li.getName().toLowerCase())){
				st.addItemHit(li);		
				//return true;
			}
			else if(li instanceof ListItem){ //also look for synonyms with two or more words
				ListItem li2 = (ListItem) li;
				if(li2.getSynonyma()!=null){
					Iterator it = li2.getSynonyma().iterator();
					while(it.hasNext()){
						Synonym sy = (Synonym) it.next();
						if(sy.getName().contains(" ") && st.getText().toLowerCase().contains(sy.getName().toLowerCase())){
							st.addItemHit(li2, sy);
							//return true;
						}
					}
				}
			}		
		}
		//return false;
	}
	
	//todo expert statements could be prepared and all listEntries already stored in the database. 
	/**
	 * go through all found matching words and comapre them with the expert statement
	 * @param matchingWords
	 * @param expMatches
	 * @return
	 */
	private static void calculateMatchesWithExpStatement(List<SummaryStElem> matchingWords, String expMatches){
		if(matchingWords==null || matchingWords.isEmpty()) return;
		//int matchCounter = 0;
		String[] expMatchesArr = expMatches.split(" ");
		if(expMatchesArr==null || expMatchesArr.length==0) return;
		for(int i=0;i<expMatchesArr.length;i++){
			innerLoop: for(int j=0; j<matchingWords.size(); j++){
				SummaryStElem elem = matchingWords.get(j);
				//compare main listItem:
				if (StringUtilities.similarStrings(elem.getListItem().getName(), expMatchesArr[i], elem.getListItem().getLanguage())){
					//matchCounter++;
					elem.setExpertMatch(true);
					break innerLoop;
				}

				//compare synonyms:
				ListItem li  = (ListItem) elem.getListItem();
				if (li.getSynonyma()!=null && !li.getSynonyma().isEmpty()){
					Iterator it = li.getSynonyma().iterator();
					while(it.hasNext()){
						Synonym s= (Synonym) it.next();
						if(StringUtilities.similarStrings(s.getName(), expMatchesArr[i], elem.getListItem().getLanguage())){
							//matchCounter++;
							elem.setExpertMatch(true);
							break innerLoop;
						}
					}
				}
			}
		}
	}

	public static Map<String, PatientIllnessScript> getTempExpMaps() {
		return tempExpMaps;
	}

	public static void setTempExpMaps(Map<String, PatientIllnessScript> tempExpMaps) {
		SummaryStatementController.tempExpMaps = tempExpMaps;
	}
	
	public static void setSIUnitList(){
		unitList = new DBList().selectSIUnits();
	}
	
}
