package controller;

import java.util.*;
import org.apache.commons.lang3.StringUtils;

import actions.scoringActions.ScoringSummStAction;
import application.AppBean;
import beans.list.*;
import beans.relation.Relation;
import beans.relation.summary.*;
import beans.scripts.PatientIllnessScript;
import database.DBClinReason;
import database.DBList;
import util.*;
import net.casus.util.Utility;
import net.casus.util.nlp.spacy.*;
import net.casus.util.summarystatement.PerformantSpacyProcessor;


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
		
	public static List<SIUnit> getUnitList() {return unitList;}
	public static SIUnit getUnitById(long id){
		if(unitList==null) return null;
		for(int i=0;i<SummaryStatementController.getUnitList().size();i++){
			if(SummaryStatementController.getUnitList().get(i).getId()==id) return SummaryStatementController.getUnitList().get(i);	
		}
		return null;
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
	 * @DEPRECATED
	 */
	/*public static void analyzeSemanticQualsStatements(){
		List<SummaryStatement> statements = new DBClinReason().getSummaryStatementsByAnalyzed( false);
		if(statements==null || statements.isEmpty()) return; //no new statements to analyze
		for(int i=0; i<statements.size(); i++){
			SummaryStatement stst = statements.get(i);
			checkForSemanticQualifiers(stst, null);
		}
		new DBClinReason().saveAndCommit(statements);
	}*/
	
	/**
	 * We check the text of the summary statement concerning the use of semantic qualifiers.
	 * all hits are stored in the database.
	 * @param stst
	 */
	private static void checkForSemanticQualifiers(SummaryStatement stst, SpacyDocJson spacy){
		try{
			SpacyStructureStats spacyStructureStats = SpacyStructureStats.getInstance();
			if(stst==null || stst.getText()==null || stst.getText().trim().isEmpty() || stst.getLang()==null) return;
			List<SemanticQual> sqs = AppBean.getSemantiQualsByLang(stst.getLang());
			if(sqs==null) return;
			
			List<SummaryStatementSQ> hits= new ArrayList<SummaryStatementSQ>();
			for(int i=0; i < sqs.size(); i++){
				String s = sqs.get(i).getQualifier().toLowerCase().trim();
				if(stst.getText().toLowerCase().contains(s)){
					SummaryStatementSQ sssq = new SummaryStatementSQ(stst.getId(),sqs.get(i).getId(), s);
					sssq.setPosition(stst.getText().toLowerCase().indexOf(s));
					
					sssq.setSpacyMatch(isSQSpacyHit(sssq, spacy, spacyStructureStats));
					if(!hits.contains(sssq))
						hits.add(sssq);	
					else{ //we optimize hits, to avoid a double hit (e.g. for male and female)
						SummaryStatementSQ matchHit = hits.get(hits.indexOf(sssq));
						if(sssq.getText().contains(matchHit.getText())) { //sssq is the better match, so we update
							hits.remove(matchHit);
							hits.add(sssq);							
						}
						//if matchHit is the better match, we do not have to change anything
					}
				}
			}
			stst.setSqHitsAsList(hits);
			//stst.setAnalyzed(true);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	private static SpacyDocToken isSQSpacyHit(SummaryStatementSQ loop, SpacyDocJson spacy, SpacyStructureStats spacyStructureStats){
		String sq = null;
		try {
			sq = loop.getTextMatch();
		} catch (Exception e) {
		}
		String sqKey = loop.getText();
		if (sq != null) sq = sq.toLowerCase();
		if (sqKey != null) sqKey = sqKey.toLowerCase();
		if (sq == null) sq = sqKey;
		
		SpacyDocToken loopToken = spacy.getSpacyDocToken(loop.getPosition());
		boolean isHit = false;
		if (loopToken != null) {
			isHit =  spacyStructureStats.isHit(sqKey,loopToken);
		}
		if(isHit) return loopToken;
		return null;
	}
	
	
	/**
	 * When for a VP an expert statement has not yet been analyzed we do it before we continue analyzing 
	 * the lerner statement.
	 * @param st
	 * @param spacy
	 * @param loc
	 */
	private static void analyzeExpStatement(SummaryStatement st, SpacyDocJson spacy, Locale loc){
		
		if(st.isAnalyzed()){ //we have to make sure that the listItems in the SummStElems are loaded -> HACK!
			if(st.getItemHits()!=null && st.getItemHits().isEmpty());
			Iterator<SummaryStElem> it = st.getItemHits().iterator();
			while(it.hasNext()){
				SummaryStElem e = it.next();
				if(e.getListItemId()>0) e.setListItem(new DBList().selectListItemById(e.getListItemId()));
			}
			return; //exp statement already analyzed, nothing more to be done....
		}
		
		DBClinReason dcr = new DBClinReason();
		
		List<ListItem> items = getListItemsByLang(st.getLang());
		List<String> textAsList = StringUtilities.createStringListFromString(st.getText(), true);
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(st.getLang()), st);
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);		
			compareSimilarList(items, st, s, i, loc);
			if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, s, i, loc);		
			st.addUnit(compareSIUnits(s, i, st.getText().toLowerCase().indexOf(s), spacy, st.getId()));
		}
		checkForSemanticQualifiers(st, spacy);
		
		compareNumbers(st, spacy);
		checkForPerson(st, spacy);
		
		/*Iterator it = st.getItemHits().iterator();
		while(it.hasNext()){
			SummaryStElem e = (SummaryStElem) it.next();
			new DBClinReason().saveAndCommit(e);
			
		}*/
		dcr.saveAndCommit(st.getItemHits());
		dcr.saveAndCommit(st.getSqHits());
		dcr.saveAndCommit(st.getUnits());
		st.setAnalyzed(true);
		dcr.saveAndCommit(st);
	}
	
	/*public static SummaryStatement testSummStRating(String text, String vpId){
		SummaryStatement st = new SummaryStatement();
		st.setText(text);
		return testSummStRating(/st, vpId);
	}*/
	/**
	 * test summary statement rating using the list creation mechanism.
	 * (1) exp fdgs & ddx -> expPis.getSummSt().getDiagnosesHits() und expPis.getSummSt().getFindingsHits()
	 * (2) exp map items -> expPis.getProblems(), expPis.getDiagnoses(), expPis.getTests(), expPis.getMngs()
	 * (3) student fdgs & ddx (schon drin in den Spalten Findings und Diagnoses)
	 * (4,5) number of matches mit statement und map (schon drin) 
	 * (6,7) Number of additional fdgs, ddx / items -> können wir im Excel sehen
	 * @param contextIn
	 */
	/*public static SummaryStatement testSummStRating(SummaryStatement st, String vpId){		
		List<ListItem> items = getListItemsByLang(st.getLang());	
		if(st==null || st.getText()==null || items==null) return null;
		PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);		
		JsonTest jt = new DBClinReason().selectJsonTestBySummStId(st.getId()); //the json of the statement
		SpacyDocJson spacy = new SpacyDocJson(jt.getJson().trim());
		spacy.init();

		JsonTest jt2 = new DBClinReason().selectJsonTestBySummStId(expPis.getSummStId()); //the json of the statement
		SpacyDocJson spacyE = new SpacyDocJson(jt2.getJson().trim());
		spacyE.init();
		Locale loc = new Locale(st.getLang());
		analyzeExpStatement(expPis.getSummSt(), spacyE, loc);
		st.setSpacy_json(jt.getJson());
		//PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);		
		//analyzeExpStatement(expPis.getSummSt());

		if(!tempExpMaps.containsKey(vpId)) tempExpMaps.put(vpId, expPis);
		
		long startms = System.currentTimeMillis();
		CRTLogger.out("start: " + startms, CRTLogger.LEVEL_TEST);
		 
		List<String> textAsListOrg = StringUtilities.createStringListFromString(st.getText(), true);
		//String sumstTxtLowerCase = st.getText().toLowerCase();
		
		if(textAsListOrg==null) return st;
		
		compareList(items, st); 
		if(aList!=null) compareList(aList.get(st.getLang()), st);
		
		for(int i=0; i<textAsListOrg.size(); i++){
			String orgS = textAsListOrg.get(i);		
			
			if(isTermToAnalyze(orgS,st.getText().indexOf(orgS), spacy, true)){
				compareSimilarList(items, st, orgS, i, loc);
				st.addUnit(compareSIUnits(orgS, i, st.getText().indexOf(orgS), spacy));
				if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, orgS, i, loc);		
			}
		}
		
		checkForSemanticQualifiers(st, spacy); //count SQ
		compareNumbers(st, spacy);
		if(expPis!=null){
			calculateMatchesWithExpStatement(st.getItemHits(), expPis.getSummSt().getText());
			compareWithExpIllScript(st, expPis);
		}
		checkForPerson(st, spacy);
		checkAccuracy(st, expPis.getSummSt(),spacy, spacyE);
		//scoring starts:
		ScoringSummStAction scoreAct = new ScoringSummStAction();
		int sqScoreNew = scoreAct.calculateSemanticQualScoreBasic(st); //score SQ
		int sqScore = scoreAct.calculateSemanticQualScore(expPis.getSummSt(), st);
		st.setSqScore(sqScore);
		st.setSqScoreBasic(sqScoreNew);
		//CRTLogger.out("",1);
		scoreAct.calculateNarrowing(st, expPis.getSummSt());
		scoreAct.calculateTransformation(expPis.getSummSt(), st);
		scoreAct.calculatePersonScore(st);
		scoreAct.calculateGlobal(st);
		long endms = System.currentTimeMillis();
		st.analysisMs = endms - startms;
		CRTLogger.out("end: " + startms, CRTLogger.LEVEL_TEST);
		//new DBClinReason().saveAndCommit(st);
		return st;		
	}*/
	
	public SummaryStatement initSummStRating(PatientIllnessScript expScript, PatientIllnessScript learnerScript,  ScoringSummStAction scoreAct){
		if(learnerScript==null || learnerScript.getSummSt()==null || learnerScript.getSummSt().getText()==null) return null;
		return initSummStRating(expScript, learnerScript.getSummSt(), scoreAct);	
	}
	
	public SummaryStatement initSummStRating(PatientIllnessScript expScript, SummaryStatement st, ScoringSummStAction scoreAct){
		if(st==null) return null;
		try{
			//if(learnerScript==null || learnerScript.getSummSt()==null || learnerScript.getSummSt().getText()==null) return st;
			//st = learnerScript.getSummSt();
			if(st.isAnalyzed()){ //we do a re-calculation....
				resetSummSt(st);
			}
			SummaryStatement expSt = expScript.getSummSt();
			List<ListItem> items = getListItemsByLang(st.getLang());	
			//if(st==null || st.getText()==null || items==null) return null;
			//PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);	
			
			//creating the jsons for the student statement
			JsonTest jt = new DBClinReason().selectJsonTestBySummStId(st.getId()); //the json of the statement
			if(jt==null) jt = initJsonTest(st);
	
			SpacyDocJson spacy = new SpacyDocJson(jt.getJson().trim());
			if(spacy!=null) spacy.init();
	
			JsonTest jt2 = new DBClinReason().selectJsonTestBySummStId(expSt.getId()); //the json of the statement
			
			// Expert Info should be present? Better concept required
			if (jt2 == null || jt2.getJson()==null) jt2 = initJsonTest(expSt);
				/*long startms1 = System.currentTimeMillis();
				CRTLogger.out("spacy exp processing start: " + startms1, CRTLogger.LEVEL_PROD);
				
				SummaryStatement st2 = expScript.getSummSt();
				jt2 = new JsonTest();
				jt2.setJson("");
				
				try {
					PerformantSpacyProcessor impl = PerformantSpacyProcessor.getInstanceNoInit();
					String text_result = impl.getLangMappedSpacyJson( st2.getLang(), st2.getText());
					jt2.setJson(text_result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				long endms1 = System.currentTimeMillis();
				CRTLogger.out("spacy exp processing end: " + jt2.getJson() + "; " + (endms1-startms1), CRTLogger.LEVEL_PROD);
			}*/
			
			SpacyDocJson spacyE = new SpacyDocJson(jt2.getJson().trim());
			spacyE.init();
			Locale loc = new Locale(st.getLang());
			analyzeExpStatement(expSt, spacyE, loc);
			st.setSpacy_json(jt.getJson());
			
			long startms = System.currentTimeMillis();
			CRTLogger.out("start: " + startms, CRTLogger.LEVEL_TEST);
			 
			List<String> textAsListOrg = StringUtilities.createStringListFromString(st.getText(), true);
			
			if(textAsListOrg==null) return st;
			
			compareList(items, st); 
			if(aList!=null) compareList(aList.get(st.getLang()), st);
			
			for(int i=0; i<textAsListOrg.size(); i++){
				String orgS = textAsListOrg.get(i);		
				
				if(isTermToAnalyze(orgS,st.getText().indexOf(orgS), spacy, true)){
					compareSimilarList(items, st, orgS, i, loc);
					st.addUnit(compareSIUnits(orgS, i, st.getText().indexOf(orgS), spacy, st.getId()));
					if(aList!=null) compareSimilarList(aList.get(st.getLang()), st, orgS, i, loc);		
				}
			}
			
			checkForSemanticQualifiers(st, spacy); //count SQ
			compareNumbers(st, spacy);
			if(expSt!=null){
				calculateMatchesWithExpStatement(st.getItemHits(), expSt.getText());
				compareWithExpIllScript(st, expScript);
			}
			checkForPerson(st, spacy);
			checkAccuracy(st, expSt,spacy, spacyE);
	
			long endms = System.currentTimeMillis();
			st.analysisMs = endms - startms;
			CRTLogger.out("end: " + startms, CRTLogger.LEVEL_TEST);
			if (st.getSqHits() == null) {
				st.setSqHits(new HashSet<SummaryStatementSQ>());
			}
			new DBClinReason().saveAndCommit(st.getSqHits());
			
			if (st.getUnits() == null) {
				st.setUnits(new HashSet<SummaryStNumeric>());
			}
			new DBClinReason().saveAndCommit(st.getUnits());
			
			if (st.getItemHits() == null) {
				st.setItemHits(new HashSet<SummaryStElem>());
			}
			new DBClinReason().saveAndCommit(st.getItemHits());
			/*if (st.getItemHits() != null) {
				Iterator<SummaryStElem> it = st.getItemHits().iterator();
				while (it.hasNext()) {
					SummaryStElem loop = it.next();
					CRTLogger.out("loop: id:" + loop.getId() + "; listitemid:" + loop.getListItemId() + "; summstid:" + loop.getSummStId() + "; hash:" + loop.hashCode() , CRTLogger.LEVEL_PROD);
				}
			}*/
			
			st.setAnalyzed(true);
			new DBClinReason().saveAndCommit(st);
		}
		catch(Exception e){
			CRTLogger.out(Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
		
		return st;
	}
	
	/**
	 * init the spacy tree and return it as a JsonTest object.
	 * @param st
	 * @return
	 */
	private JsonTest initJsonTest(SummaryStatement st){
		long startms1 = System.currentTimeMillis();
		CRTLogger.out("spacy processing start: " + startms1, CRTLogger.LEVEL_PROD);
		
		JsonTest jt = new JsonTest(st.getId());
		jt.setJson("");
		try {
			PerformantSpacyProcessor impl = PerformantSpacyProcessor.getInstanceNoInit();
			String text_result = impl.getLangMappedSpacyJson(st.getLang(), st.getText());
			jt.setJson(text_result);
			new DBClinReason().saveAndCommit(jt);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long endms1 = System.currentTimeMillis();
		CRTLogger.out("spacy processing end: " + jt.getJson() + "; " + (endms1-startms1), CRTLogger.LEVEL_PROD);
		return jt;
	}
	
	/**
	 * We check whether a string at a given position should be further analyzed or not. We do not 
	 * further consider prepositions, conjunctions,...
	 * @param s
	 * @param idx
	 * @param spacy
	 * @return
	 */
	private static boolean isTermToAnalyze(String s,int idx,SpacyDocJson spacy, boolean strict){
		if(s==null || s.length()==0) return false;  
		if(strict && s.length()==1) return false; //stuff like "a" should generally not be analyzed, only when we look for units.
		SpacyDocToken tok = spacy.getSpacyDocToken(idx);
		if(tok==null) return true; 
		if(tok.getPos().equals(SpacyDocToken.LABEL_ADP)) return false; 
		if(tok.getPos().equals(SpacyDocToken.LABEL_DET)) return false; 
		if(tok.getPos().equals(SpacyDocToken.LABEL_PRON)) return false; 
		if(tok.getPos().equals(SpacyDocToken.LABEL_CCONJ)) return false;
		if(tok.getPos().equals(SpacyDocToken.LABEL_CONJ)) return false;
		
		return true;
	}
	
	/**
	 * we look whether we have a spacy token of PERSON and if so we double check whether it is not already
	 * in the found items or semantic qualifiers (spacy is not 100% accurate)
	 * @param st
	 * @param spacy
	 */
	private static void checkForPerson(SummaryStatement st, SpacyDocJson spacy){
		SpacyDocToken person = spacy.getTokenByType(SpacyDocToken.LABEL_PERSON);
		if(person==null) person = spacy.getTokenByType(SpacyDocToken.LABEL_PER);
		if(person==null) return;
		//check if person is not in the list of SQ (can happen,since spay is not 100% accurate)
		boolean isSQ = false;
		if(st.getSqHits()!=null){
			Iterator it = st.getSqHits().iterator();
			while(it.hasNext()){
			//for(int i=0;i<st.getSqHits().size();i++){
				SummaryStatementSQ sq = (SummaryStatementSQ) it.next();
				if(sq.getSpacyMatch()!=null && sq.getSpacyMatch().equals(person)) isSQ = true;
				else if(sq.getText()!=null && sq.getText().equalsIgnoreCase(person.getToken())) isSQ = true;
			}
		}
		//added only if not already present in list (can happen,since spay is not 100% accurate)
		if(!isSQ) st.addItemHit(new SummaryStElem(person, st.getText().indexOf(person.getToken()), st.getId()));		
	}
	
	/**
	 * We look whether we can find any of the SI units in the list in the element of the summary statement. 
	 * @param text
	 * @return
	 */
	private static SummaryStNumeric compareSIUnits(String text, int pos, int idx, SpacyDocJson spacy, long sumStId){
		if(text==null) return null;	
		String spacyType = spacy.getEntityTypeByIdx(idx);
		
		for(int i=0; i< unitList.size(); i++){
			if(text.equalsIgnoreCase(unitList.get(i).getName())) return new SummaryStNumeric(unitList.get(i), pos, idx, spacyType, sumStId);
			
			if(text.contains("/")){ //identify something like g/dl and count it once
				String text2 = text.replace('/', ' ');
				List<String> s = StringUtilities.createStringListFromString(text2, true);
				if(s!=null){
					for(int j=0;j < s.size();j++){
						String st = s.get(j);
						if(/*isTermToAnalyze(st,idx, spacy, false) &&*/ st.equalsIgnoreCase(unitList.get(i).getName())) 
							return new SummaryStNumeric(unitList.get(i),text, pos, idx, spacyType, sumStId);
					}
				}
			}
			if(text.contains(unitList.get(i).getName())){ //identify something like 14mg or 79%
				String text3 = text.replace(unitList.get(i).getName(), "");
				if(text3!=null && StringUtilities.isNumeric(text3))
					return new SummaryStNumeric(unitList.get(i), text, pos, idx, spacyType, sumStId);
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
				else st.addUnit(new SummaryStNumeric(null, s.get(i), i, -1, null, st.getId()));
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
								st.addUnit(new SummaryStNumeric(null, s2[j], i, -1, null, st.getId()));
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
		Iterator<SummaryStElem> it = st.getItemHits().iterator();
		while(it.hasNext()){
		//for (int i=0; i<st.getItemHits().size(); i++){
			SummaryStElem se = it.next(); //st.getItemHits().get(i).getListItem();
			if(se !=null && se.getListItem()!=null){
				if(pis.getRelationByListItemIdAndType(se.getListItem().getListItemId(), Relation.TYPE_PROBLEM)!=null)
					se.setExpertScriptMatch(Relation.TYPE_PROBLEM);
				else if(pis.getRelationByListItemIdAndType(se.getListItem().getListItemId(), Relation.TYPE_DDX)!=null) 
					se.setExpertScriptMatch(Relation.TYPE_DDX);
				else if(pis.getRelationByListItemIdAndType(se.getListItem().getListItemId(), Relation.TYPE_TEST)!=null) 
					se.setExpertScriptMatch(Relation.TYPE_TEST);
				else if(pis.getRelationByListItemIdAndType(se.getListItem().getListItemId(), Relation.TYPE_MNG)!=null) 
					se.setExpertScriptMatch(Relation.TYPE_MNG);
			}
		}
	}
	
	/**
	 * compare string s with the items list and their synonyms for similar entries.
	 * @param items
	 * @param st
	 * @param loc
	 * @param s
	 */
	private static boolean compareSimilarList(List items, SummaryStatement st, String orgS, int pos, Locale loc){	
		String replS = StringUtilities.replaceChars(orgS.toLowerCase());
		CRTLogger.out("", 1);
		//we do not compare anything for ver short strings (including age) 
		if(replS==null || replS.trim().length()<=2) return false;
		
		for(int j=0;j<items.size(); j++){ //comparison with adapted Mesh list:
			ListItem li = (ListItem) items.get(j);	

			boolean isSimilar = StringUtilities.similarStrings(replS, li.getReplName(), replS, li.getReplName(), loc, true);
			
			if(isSimilar){
				
				st.addItemHit(li, /*pos,*/ st.getText().indexOf(orgS));
				return true;
			}
			
			if(li.getSynonyma()!=null){
				Iterator<Synonym> it = li.getSynonyma().iterator();
				while(it.hasNext() && !isSimilar){
					Synonym syn = it.next();
					isSimilar = StringUtilities.similarStrings(replS, syn.getReplName(), replS, syn.getReplName(), loc, true);
					if(isSimilar){
						st.addItemHit(li, syn, /*pos,*/ st.getText().indexOf(orgS));	
					
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
		String st_text_lower = st.getText();
		if (st_text_lower != null) st_text_lower = st_text_lower.toLowerCase();
		
		for(int j=0;j<items.size(); j++){ //look for two or more word items, e.g. "productive cough"
			ListItem li = (ListItem) items.get(j);

			if (li.getName().contains(" ") && st_text_lower.contains(li.getNameLower())){
				//get start and end position of match in text
				//int startPos = StringUtilities.getStartPosOfStrInText(li.getName().toLowerCase(),  st.getText().toLowerCase());
				st.addItemHit(li, /*startPos,*/ st_text_lower.indexOf(li.getNameLower()));		
			}
			else if(li instanceof ListItem){ //also look for synonyms with two or more words
				ListItem li2 = (ListItem) li;
				if(li2.getSynonyma()!=null){
					Iterator<Synonym> it = li2.getSynonyma().iterator();
					while(it.hasNext()){
						Synonym sy = it.next();
						if(sy.getName().contains(" ") && st_text_lower.contains(sy.getNameLower())){
							//int startPos = StringUtilities.getStartPosOfStrInText(sy.getName().toLowerCase(),  st.getText().toLowerCase());
							st.addItemHit(li2, sy, /*startPos,*/ st_text_lower.indexOf(sy.getNameLower()));
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
	private static void calculateMatchesWithExpStatement(Set<SummaryStElem> matchingWords, String expText){
		if(matchingWords==null || matchingWords.isEmpty()) return;
		//int matchCounter = 0;
		String[] expMatchesArr = expText.split(" ");
		if(expMatchesArr==null || expMatchesArr.length==0) return;
		
		for(int i=0;i<expMatchesArr.length;i++){
			Iterator it2 = matchingWords.iterator();
			innerLoop: while(it2.hasNext()){//for(int j=0; j<matchingWords.size(); j++){
				SummaryStElem elem = (SummaryStElem) it2.next();
				String expMatchRepl = StringUtilities.replaceChars(expMatchesArr[i].toLowerCase());
				//compare main listItem:
				
				// expMatchRepl && getReplName() already lower case
				if (elem.getListItem()!=null && elem.getListItem().getName()!=null && StringUtilities.similarStrings(elem.getListItem().getReplName(), expMatchRepl, elem.getListItem().getReplName(), expMatchRepl, elem.getListItem().getLanguage(), true)){
					//matchCounter++;
					elem.setExpertMatchBool(true);
					elem.setExpertMatchIdx(expText.indexOf(expMatchesArr[i]));
					break innerLoop;
				}

				//compare synonyms:
				ListItem li  = (ListItem) elem.getListItem();
				if (li!=null && li.getSynonyma()!=null && !li.getSynonyma().isEmpty()){
					Iterator it = li.getSynonyma().iterator();
					while(it.hasNext()){
						Synonym s= (Synonym) it.next();
						
						// expMatchRepl && getReplName() already lower case
						if(StringUtilities.similarStrings(s.getReplName(), expMatchRepl, s.getReplName(), expMatchRepl, elem.getListItem().getLanguage(), true)){
							//matchCounter++;
							elem.setExpertMatchBool(true);
							elem.setExpertMatchIdx(expText.indexOf(expMatchesArr[i]));
							break innerLoop;
						}
					}
				}
			}
		}
	}

	/*public static Map<String, PatientIllnessScript> getTempExpMaps() {
		return tempExpMaps;
	}*/

	/*public static void setTempExpMaps(Map<String, PatientIllnessScript> tempExpMaps) {
		SummaryStatementController.tempExpMaps = tempExpMaps;
	}*/
	
	public static void setSIUnitAndTransformList(){
		unitList = new DBList().selectSIUnits();
		if(unitList!=null){ //we remove an empty entry which we need for relating numbers for which we do not have a unit 
			for (int i=0; i<unitList.size();i++){
				if(unitList.get(i).getId()==0){
					unitList.remove(unitList.get(i));
					break;
				}
			}
		}
		transformRules = new DBList().selectTransformRules();
	}
	
	/**
	 * @param learnerSt
	 * @param expertSt
	 * @param lSpacy
	 * @param eSpacy
	 */
	private static void checkAccuracy(SummaryStatement learnerSt, SummaryStatement expertSt, SpacyDocJson lSpacy, SpacyDocJson eSpacy){
		//minimum requirements:
		if(learnerSt.getText()==null || learnerSt.getText().length()<=10 || learnerSt.getItemHits()==null){
			learnerSt.setAccuracyScore(0);
			return;
		}
		try{
			//we look for contradicting semantic qualifiers for expertMatch hits:
			Set<SummaryStElem> hits = learnerSt.getItemHits();
			if(hits!=null && !hits.isEmpty()){
				Iterator<SummaryStElem> it = hits.iterator();
				while(it.hasNext()){
				//for (int i=0;i<hits.size(); i++){
					SummaryStElem elem = it.next(); //hits.get(i);
					if(elem.isExpertMatchBool()){
						SpacyDocToken tokenL = lSpacy.getSpacyDocToken(elem.getStartIdx());
						SpacyDocToken tokenE = eSpacy.getSpacyDocToken(elem.getExpertMatchIdx());
						if(tokenL!=null && tokenL.getChildren()!=null){
							for(int j=0; j<tokenL.getChildren().size();j++){
								SpacyDocToken learnerChild = tokenL.getChildren().get(j);
								SummaryStatementSQ learnerSQ = learnerSt.getSQBySpacyToken(learnerChild);
								if(learnerSQ!=null) //child is a SQ:
									if(tokenE!=null && tokenE.getChildren()!=null){
										for(int k=0;k<tokenE.getChildren().size();k++){
											SpacyDocToken expChild = tokenE.getChildren().get(k);
											SummaryStatementSQ expSQ = expertSt.getSQBySpacyToken(expChild);
											//expertChild is an SQ and we found contrasting SQ for the same token:
											if(expSQ!=null && oppositeSQ(learnerSQ.getSqId(), expSQ.getSqId(), learnerSt.getLang())){ 
												learnerSt.setAccuracyScore(0);
												learnerSQ.setExpHasOpposite(true);
												return;
											}
										}
									}
								}
							}
					}
				}
			}
			int acuracy = checkPersonAgeAccuracy(learnerSt, expertSt, lSpacy, eSpacy);
			//we have not found anything wrong, so we can only assume that it is correct:
			learnerSt.setAccuracyScore(acuracy);	
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	/**
	 * We check whether persons are in the statements and if so, if the related number/date is the same (presumably this is 
	 * then the age of the patient.
	 * @param learnerSt
	 * @param expSt
	 * @param lSpacy
	 * @param eSpacy
	 * @return
	 */
	private static int checkPersonAgeAccuracy(SummaryStatement learnerSt, SummaryStatement expSt, SpacyDocJson lSpacy, SpacyDocJson eSpacy){
		SummaryStElem personL = learnerSt.getPerson();
		SummaryStElem personE = expSt.getPerson();
		if(personL==null || personE==null) return 1; //no persons to reference to
		SpacyDocToken persTokL = lSpacy.getSpacyDocToken(personL.getStartIdx());
		SpacyDocToken persTokE = eSpacy.getSpacyDocToken(personE.getStartIdx());
		if(persTokL==null || persTokE==null) return 1; //no person tokens found
		SpacyDocToken ageL = persTokL.getTokenByLabelInChildsOrParents(SpacyDocToken.LABEL_DATE);
		SpacyDocToken ageE = persTokE.getTokenByLabelInChildsOrParents(SpacyDocToken.LABEL_DATE);
		if (ageL == null || ageE == null) return 1; //no related age found either in learner or in expert statement
		if(!ageL.getToken().equals(ageE.getToken())) 
			return 0; //age mismatch
		
		return 1;

	}
		
	/**
	 * Check whether the two SQ ids belong to contrasting SQ (e-g. left vs. right or upper vs. lower). 
	 * @param id1
	 * @param id2
	 * @param lang
	 * @return
	 */
	private static boolean oppositeSQ(long id1, long id2, String lang){
		SemanticQual sq1 = AppBean.getSemantiQualsByLangAndId(lang, id1);
		SemanticQual sq2 = AppBean.getSemantiQualsByLangAndId(lang, id2);
		if(sq1==null || sq2==null) return false;
		if(sq1.isContrast(sq2)) return true;
		return false;
	}
	
	/**
	 * To analyze already entered statements we select them and run the new algorithm. 
	 * CAVE: Before running, make sure that all statements that should be analyzed, have been set to analyzed=0.
	 */
	/*public static void reanalyzeStatements(){
		List<SummaryStatement> stmts = new DBClinReason().getSummaryStatementsByAnalyzed(false);
		if(stmts==null || stmts.isEmpty()) return;
		for(int i=0; i<stmts.size();i++){
			SummaryStatement st = stmts.get(i);
			if(st.getType()==1){ //only consider learner statements
				PatientIllnessScript learnerScript = new DBClinReason().selectPatIllScriptById(st.getPatillscriptId());
				PatientIllnessScript expScript = new DBClinReason().selectExpertPatIllScriptByVPId(learnerScript.getVpId());	
				if(learnerScript!=null && expScript!=null && expScript.getSummSt()!=null){
					ScoringSummStAction ssa = new ScoringSummStAction();
					initSummStRating(expScript, learnerScript, ssa);
					ssa.doScoring(st, expScript.getSummSt());
				}
			}			
		}		
	}*/
	
	/**
	 * We remove the previously found hits for semantic qualifiers, units, and other hits from the database.
	 * @param st
	 */
	private void resetSummSt(SummaryStatement st){
		if(st.getItemHits() != null) new DBClinReason().deleteAndCommit(st.getItemHits());
		if(st.getSqHits()!=null) new DBClinReason().deleteAndCommit(st.getSqHits());
		if(st.getUnits()!=null) new DBClinReason().deleteAndCommit(st.getUnits());
		st.setItemHits(null);
		st.setUnits(null);
		st.setSqHits(null);
		//TODO reload JsonTest!!!
	}
	
	/*public static SummaryStatement testSummStRating(){
		//Locale loc = new Locale("en");
		long id = 28393;
		String vpId = "914015_2";
		SummaryStatement st = new DBClinReason().loadSummSt(id, null);
		st =  testSummStRating(st, vpId);
		return st;
	}*/

	
	/*public static void testSummStRating(){
		long[] ids = new long[]{76369,79314,79911,81723,82533,82822,71816,72350,74804,75134,75854,70659,141433,142950,144100,146816,147601,148031,148583,135566,68937,77182,77671,147862,86813,94126,97276,88512,89467,95367,95717,95836,84120,85748,87773,90543,93832,94734,160048,162578,162935,167338,168254,169244,164301,166480,171792,174917,170190,172691,173526,176050,176400,177502,84249,91616,171644,176843,176917,258230,259325,156766,165312,190176,198217,183259,179448,180132,182374,185591,224568,215194,233237,255923,198217,42082,42717,44101,45738,48497,27947,28393,43129,44826,26314,34810,35997,31273,32010,33510,41025,43009,142289,137492,157934,214134,245001,33280,31830,34218,7852,50936,51612,56138,56689,57139,57773,60295,60369,52550,52864,53807,53831,54771,61789,62884,117537,119306,119568,121026,118336,121097,122299,122367,122466,192059};
		Long[] idsL = new Long[ids.length];
		for (int i=0; i<ids.length; i++){
			idsL[i] = new Long(ids[i]);
		}
		List<SummaryStatement> list = new DBClinReason().getSummaryStatementsById(idsL);
		Collections.sort(list);
		for(int i=0; i<list.size(); i++){
			SummaryStatement s = list.get(i);
			CRTLogger.out("stid = " + s.getId(), 1);
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
			
			sb.append(st.analysisMs+"\r");
			//sb2.append(st.getNarrowingScoreNew()+"\r");
			//sb3.append(st.getNarr1Score()+"\r");
			//sb4.append(st.getNarr2Score()+"\r");
			sb3.append(st.getTransformNum()+"\r");
			//sb4.append(st.getUnitNum()+"\r");
			//sb3.append(st.getTransformationScore()+"\r");
			sb2.append(st.getTransformScorePerc()+"\r");
			sb4.append(st.getGlobalScore()+"\r");
			sb5.append(st.tempExpTransfNum +"\r");
			//sb.append(st.getSqHits()+"\r");
			//sb2.append(st.getSqScore()+"\r");
			//sb3.append(st.getSqScoreBasic()+"\r"); //used for pilot study
			//sb3.append(st.getSqScorePerc()+"\r");
			//sb3.append(st.getAccuracyScore()+"\r");
			//sb5.append(st.getSQSpacyHits()+"\r");
			//sb4.append(st.getGlobalScore()+"\r");
			//sb5.append(st.getPersonName()+"\r");
			//sb6.append(st.getPersonScore()+"\r");
			
		}
		CRTLogger.out(sb.toString(), 1);
		CRTLogger.out(sb2.toString(), 1); 
		CRTLogger.out(sb3.toString(), 1);
		CRTLogger.out(sb4.toString(), 1);
		CRTLogger.out(sb5.toString(), 1);
		//CRTLogger.out(sb6.toString(), 1);
	}*/
	
}
