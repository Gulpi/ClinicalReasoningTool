package controller;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import actions.scoringActions.ScoringSummStAction;
import application.AppBean;
import beans.list.*;
import beans.relation.Relation;
import beans.relation.summary.*;
import beans.scripts.PatientIllnessScript;
import beans.test.JsonTest;
import database.DBClinReason;
import database.DBList;
import model.SemanticQual;
import util.*;
import net.casus.util.nlp.spacy.SpacyDocJson;
import net.casus.util.nlp.spacy.SpacyDocToken;


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
	public static List<TransformRule> transformRules;
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
			checkForSemanticQualifiers(stst, null);
		}
		new DBClinReason().saveAndCommit(statements);
	}
	
	/**
	 * We check the text of the summary statement concerning the use of semantic qualifiers.
	 * all hits are stored in the database.
	 * @param stst
	 */
	public static void checkForSemanticQualifiers(SummaryStatement stst, SpacyDocJson spacy){
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
			//if(hits!=null && !hits.isEmpty() && stst.getId()>0)
			//	new DBClinReason().saveAndCommit(hits);
			stst.setSqHits(hits);
			stst.setAnalyzed(true);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	
	private static void analyzeExpStatement(SummaryStatement st){
		if(st.getItemHits()!=null && !st.getItemHits().isEmpty()) return; //already done....
		JsonTest jt = new DBClinReason().selectJsonTestBySummStId(st.getId()); //the json of the statement
		SpacyDocJson spacy = new SpacyDocJson(jt.getJson().trim());
		spacy.init();
		List<ListItem> items = getListItemsByLang(st.getLang());
		List<String> textAsList = StringUtilities.createStringListFromString(st.getText(), true);
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(st.getLang()), st);
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);		
			compareSimilarList(items, st, s, i);
			if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, s, i);		
			st.addUnit(compareSIUnits(s, i, st.getText().toLowerCase().indexOf(s), spacy));
		}
		checkForSemanticQualifiers(st, null);
		compareNumbers(st, spacy);
		checkForPerson(st, spacy);
	}
	
	public static SummaryStatement testSummStRating(/*Locale loc, */String text, String vpId){
		SummaryStatement st = new SummaryStatement();
		st.setText(text);
		return testSummStRating(/*loc,*/ st, vpId);
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
	public static SummaryStatement testSummStRating(/*Locale loc, */SummaryStatement st, String vpId){
		
		List<ListItem> items = getListItemsByLang(st.getLang());
		JsonTest jt = new DBClinReason().selectJsonTestBySummStId(st.getId()); //the json of the statement
		SpacyDocJson spacy = new SpacyDocJson(jt.getJson().trim());
		spacy.init();
		
		st.setSpacy_json(jt.getJson());
		PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);		
		analyzeExpStatement(expPis.getSummSt());
		if(!tempExpMaps.containsKey(vpId)) tempExpMaps.put(vpId, expPis);
		
		if(st==null || st.getText()==null || items==null) return null; 
		List<String> textAsList = StringUtilities.createStringListFromString(st.getText(), true);
		
		if(textAsList==null) return st;
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(st.getLang()), st);
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);		
			compareSimilarList(items, st, s, i);
			st.addUnit(compareSIUnits(s, i, st.getText().toLowerCase().indexOf(s), spacy));
			if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, s, i);			
		}
		
		checkForSemanticQualifiers(st, spacy); //count SQ
		compareNumbers(st, spacy);
		if(expPis!=null){
			calculateMatchesWithExpStatement(st.getItemHits(), expPis.getSummSt().getText());
			compareWithExpIllScript(st, expPis);
		}
		checkForPerson(st, spacy);
		//scoring starts:
		ScoringSummStAction scoreAct = new ScoringSummStAction();
		int sqScore = scoreAct.calculateSemanticQualScoreNew(st); //score SQ
		st.setSqScore(sqScore);
		//CRTLogger.out("",1);
		scoreAct.calculateNarrowing(st, expPis.getSummSt());
		scoreAct.calculateTransformation(expPis.getSummSt(), st);
		/*CRTLogger.out("id= " + st.getId(), 1);
		CRTLogger.out("  " + st.getNarr1Score(), 1);
		CRTLogger.out(" " + st.getNarr2Score(), 1);*/
		return st;		
	}
	
	private static void checkForPerson(SummaryStatement st, SpacyDocJson spacy){
		SpacyDocToken person = spacy.getTokenByType(SpacyDocToken.LABEL_PERSON);
		if(person==null) return;
		st.addItemHit(new SummaryStElem(person));		
	}
	
	/**
	 * We look whether we can find any of the SI units in the list in the element of the summary statement. 
	 * @param text
	 * @return
	 */
	private static SummaryStNumeric compareSIUnits(String text, int pos, int idx, SpacyDocJson spacy){
		if(text==null) return null;	
		String spacyType = spacy.getEntityTypeByIdx(idx);
		CRTLogger.out("", 1);
		//if(spacyType.equals(SpacyDocToken.LABEL_DATE)) return null; //we do not
		for(int i=0; i< unitList.size(); i++){
			if(text.equalsIgnoreCase(unitList.get(i).getName())) return new SummaryStNumeric(unitList.get(i), pos, idx, spacyType);
			
			if(text.contains("/")){ //identify something like g/dl and count it once
				String text2 = text.replace('/', ' ');
				List<String> s = StringUtilities.createStringListFromString(text2, true);
				if(s!=null){
					for(int j=0;j < s.size();j++){
						String st = s.get(j);
						if(st.equalsIgnoreCase(unitList.get(i).getName())) 
							return new SummaryStNumeric(unitList.get(i),text, pos, idx, spacyType);
					}
				}
			}
			if(text.contains(unitList.get(i).getName())){ //identify something like 14mg or 79%
				String text3 = text.replace(unitList.get(i).getName(), "");
				if(text3!=null && StringUtilities.isNumeric(text3))
					return new SummaryStNumeric(unitList.get(i), text, pos, idx, spacyType);
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
	private static void compareNumbers(SummaryStatement st, SpacyDocJson spacy){
		if(st==null || st.getText()==null) return;
		List<String> s = StringUtilities.createStringListFromString(st.getText(), false);
		
		for(int i=0; i<s.size(); i++){
			//identify something like 45 or 4.5
			String s1 = s.get(i);
			if(StringUtilities.isNumeric(s.get(i))){ 
				//we already have found something like mg/dl one position after the number, so we assume that the number 
				//belongs to the unit
				if(st.getUnitAtPos(i+1)!=null){ 
					SummaryStNumeric sn = st.getUnitAtPos(i+1);
					sn.setName(s.get(i));
					sn.setPos(i); //update the start position!
					
				}
				else st.addUnit(new SummaryStNumeric(null, s.get(i), i, -1, null));
			}
			else if(s1.contains("-")){ //identify something like "25-year old"...
				String[] s2 = StringUtils.split(s1, "-");
				if(s2!=null){
					for(int j=0;j<s2.length;j++){
						if(StringUtilities.isNumeric(s2[j]))
							if(st.getUnitAtPos(i+1)!=null){ 
								SummaryStNumeric sn = st.getUnitAtPos(i+1);
								sn.setName(s2[j]);
								sn.setPos(i); //update the start position!
								
							}
							else
								st.addUnit(new SummaryStNumeric(null, s2[j], i, -1, null));
					}
				}
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
	private static boolean compareSimilarList(List items, SummaryStatement st, String s, int pos){	
		for(int j=0;j<items.size(); j++){ //comparison with adapted Mesh list:
			ListItem li = (ListItem) items.get(j);	
			if(li.getName().equalsIgnoreCase("Leucocytosis"))
				CRTLogger.out("", 1);
			Locale loc = new Locale(st.getLang());

			boolean isSimilar = StringUtilities.similarStrings(s, li.getName(), loc, true);
			
			if(isSimilar){
				
				st.addItemHit(li, pos, st.getText().indexOf(s));
				return true;
			}
			
			if(li.getSynonyma()!=null){
				Iterator<Synonym> it = li.getSynonyma().iterator();
				while(it.hasNext() && !isSimilar){
					Synonym syn = it.next();
					isSimilar = StringUtilities.similarStrings(s, syn.getName(), loc, true);
					if(isSimilar){
						st.addItemHit(li, syn, pos, st.getText().indexOf(s));	
					
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

			if (li.getName().contains(" ") && st.getText().toLowerCase().contains(li.getName().toLowerCase())){
				//get start and end position of match in text
				int startPos = StringUtilities.getStartPosOfStrInText(li.getName().toLowerCase(),  st.getText().toLowerCase());
				st.addItemHit(li, startPos, st.getText().toLowerCase().indexOf(li.getName().toLowerCase()));		
			}
			else if(li instanceof ListItem){ //also look for synonyms with two or more words
				ListItem li2 = (ListItem) li;
				if(li2.getSynonyma()!=null){
					Iterator<Synonym> it = li2.getSynonyma().iterator();
					while(it.hasNext()){
						Synonym sy = it.next();
						if(sy.getName().contains(" ") && st.getText().toLowerCase().contains(sy.getName().toLowerCase())){
							int startPos = StringUtilities.getStartPosOfStrInText(sy.getName().toLowerCase(),  st.getText().toLowerCase());
							st.addItemHit(li2, sy, startPos, st.getText().toLowerCase().indexOf(sy.getName().toLowerCase()));
						}
					}
				}
			}		
		}
	}
	
	
	//todo expert statements could be prepared and all listEntries already stored in the database. 
	/**
	 * go through all found matching words and compare them with the expert statement
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
				if (StringUtilities.similarStrings(elem.getListItem().getName(), expMatchesArr[i], elem.getListItem().getLanguage(), true)){
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
						if(StringUtilities.similarStrings(s.getName(), expMatchesArr[i], elem.getListItem().getLanguage(), true)){
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
	
	public static void setSIUnitAndTransformList(){
		unitList = new DBList().selectSIUnits();
		transformRules = new DBList().selectTransformRules();
	}
	
	/*public static SummaryStatement testSummStRating(){
		//Locale loc = new Locale("en");
		long id = 54771;
		String vpId = "447006_2";
		SummaryStatement st = new DBClinReason().loadSummSt(id, null);
		st =  testSummStRating(st, vpId);
		return st;
	}*/
	
	public static void testSummStRating(){
		long[] ids = new long[]{76369,79314,79911,81723,82533,82822,71816,72350,74804,75134,75854,70659,141433,142950,144100,146816,147601,148031,148583,135566,68937,77182,77671,147862,86813,94126,97276,88512,89467,95367,95717,95836,84120,85748,87773,90543,93047,93832,94734,160048,162578,162935,167338,168254,169244,164301,166480,171792,174917,170190,172691,173526,176050,176400,177502,84249,91616,171644,176843,176917,258230,259325,156766,165312,190176,198217,183259,179448,180132,182374,185591,224568,215194,233237,255923,198217,42082,42717,44101,45738,48497,27947,28393,43129,44826,26314,34810,35997,31273,32010,33510,41025,43009,142289,137492,157934,214134,245001,33280,31830,34218,7852,50936,51612,56138,56689,57139,57773,60295,60369,52550,52864,53807,53831,54771,61789,62884,117537,119306,119568,121026,118336,121097,122299,122367,122466,192059};
		Long[] idsL = new Long[ids.length];
		for (int i=0; i<ids.length; i++){
			idsL[i] = new Long(ids[i]);
		}
		List<SummaryStatement> list = new DBClinReason().getSummaryStatementsById(idsL);
		Collections.sort(list);
		for(int i=0; i<list.size(); i++){
			SummaryStatement s = list.get(i);
			
			PatientIllnessScript pis = new DBClinReason().selectPatIllScriptById(s.getPatillscriptId());
			testSummStRating(s, pis.getVpId());			
		}
		StringBuffer sb = new StringBuffer(); 
		StringBuffer sb2 = new StringBuffer(); 
		StringBuffer sb3 = new StringBuffer(); 
		StringBuffer sb4 = new StringBuffer(); 
		StringBuffer sb5 = new StringBuffer(); 
		StringBuffer sb6 = new StringBuffer();
		for(int i=0;i<list.size();i++){
			SummaryStatement st = list.get(i);
			
			//sb.append(st.getNarrowingScore()+"\r");
			//sb2.append(st.getNarrowingScoreNew()+"\r");
			sb3.append(st.getTransformNum()+ " - "+st.getUnitHitsToString()+"\r");
			sb.append(st.getTransformationScore()+"\r");
			sb2.append(st.getTransformScorePerc()+"\r");
			sb4.append(st.getNarrowingScore()+"\r");
			sb5.append(st.getPersonName()+"\r");
			sb6.append(st.getItemHits()+"\r");
			
		}
		CRTLogger.out(sb.toString(), 1);
		CRTLogger.out(sb2.toString(), 1);
		CRTLogger.out(sb3.toString(), 1);
		CRTLogger.out(sb4.toString(), 1);
		CRTLogger.out(sb5.toString(), 1);
		CRTLogger.out(sb6.toString(), 1);
	}
	
}
