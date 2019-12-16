package controller;

import java.util.*;

import javax.servlet.ServletContext;

import actions.scoringActions.ScoringSummStAction;
import application.AppBean;
//import beans.list.ListInterface;
import beans.list.ListItem;
import beans.list.Synonym;
import beans.relation.Relation;
import beans.relation.SummaryStElem;
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
	
	/**
	 * We split the text written by the learner and compare each word (length>3) with the MesH list entries. 
	 * If we find a match, we store it in a String array and return it. 
	 * @param learnerText
	 * @param lang
	 * @return
	 */
	/*public List<String> getMatchingWordsFromMesh(String learnerText, String lang, JsonCreator jc){
		try{
			learnerText = learnerText.replace("\n", " ");
			String[] sumStSplit = StringUtils.splitByWholeSeparator(learnerText, " ");
			List<String> matchingTerms = new ArrayList<String>();
			List<ListInterface> list = getListItemsByLang(lang);
			if(list==null) return matchingTerms;
			for(int i = 0; i < sumStSplit.length; i++){
				String word = sumStSplit[i];
				if(word!=null && word.length()>3){ //only compare words with more than 2 letters:
					innerloop: for(int j=0;j<list.size();j++){
						String s2 = list.get(j).getName();
						//for transformation we do not consider categories M, I, E
						//if(s2.equalsIgnoreCase("Rale"))
						//	System.out.println("");
						if(list.get(j).getItemType().equalsIgnoreCase("C") || list.get(j).getItemType().equalsIgnoreCase("Syn")){
						   boolean isSimilar = word.equalsIgnoreCase(s2);
						   if(!isSimilar) isSimilar = StringUtilities.similarStrings(word, s2,new Locale(lang), 4, 10);
						   if(isSimilar){
							   ListInterface parent = getListItem(list.get(j));
							   if(parent!=null && parent.getItemType().equalsIgnoreCase("C")){
								   String termWithCat = parent.getName();
								   if(!matchingTerms.contains(termWithCat)){
									   matchingTerms.add(termWithCat); //make sure we have each term only once!
									   break innerloop;
								   }
							   }
						   }
						}
					}
				}
				
			}
			return matchingTerms;
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return null;
		}
	}*/
	
	/**
	 * returns the listItem or the parent listItem (which we have to select from the database first) if the 
	 * listItem is a synonym
	 * @param li
	 * @return
	 */
	/*private ListInterface getListItem(ListInterface li){
		if(!li.getItemType().equalsIgnoreCase("Syn"))
			return li; //.getItemType();
		//for synonyms we have to check the parent list item:
		ListInterface parent = new DBList().selectListItemById(li.getListItemId());
		/*if(parent!=null)*/ /*return parent; //.getItemType();
		//return null; //should not happen
		
	}	*/	
	
	public static SummaryStatement testSummStRating(){
		Locale loc = new Locale("en");
		String text = "Mr Henry Rodin, 22 y.o. car mechanics. Comes from hiking trip. since two days ago has experienced cough, dyspnea, flushing, chest pain on right side and weakness. Productive cough w/green exudate. Findings: Tachypnea - 45/min Tachycardia - 116/min Poor sat - 79%Fever - 39,6 BP - 109/55  Auscultation: Bronchial breathing and coarse rales over the upper right quadrant. A pleural friction rub is present This area is dull to percussion. Chest x-ray: Consolidation of right upper lobe Diagnosis: Lobar pneumoniaGive oxygen, paracetamole, fluids and antibiotics.";
		String vpId = "408797_2"; 
		return testSummStRating(loc, text, vpId);
	}
	
	/**
	 * test summary statement rating using the list creation mechanism.
	 * @param contextIn
	 */
	public static SummaryStatement testSummStRating(Locale loc, String text, String vpId){
		
		List<ListItem> items = getListItemsByLang(loc.getLanguage());
		PatientIllnessScript expPis = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);		
		
		if(text==null || text.isEmpty() || items==null) return null; 
		List<String> textAsList = StringUtilities.createStringListFromString(text);
		SummaryStatement st = new SummaryStatement();
		st.setText(text);
		st.setLang(loc.getLanguage());
		if(textAsList==null) return st;
		for(int i=0; i<textAsList.size(); i++){
			String s = textAsList.get(i);
			
			compareList(items, st); 
			if(aList!=null) compareList(aList.get(loc.getLanguage()), st);
			compareSimilarList(items, st, loc, s);
			if(aList!=null) compareSimilarList(aList.get(loc.getLanguage()), st, loc, s);			
		}
		
		checkForSemanticQualifiers(st); //count SQ
		if(expPis!=null){
			calculateMatchesWithExpStatement(st.getItemHits(), expPis.getSummSt().getText());
			compareWithExpIllScript(st, expPis);
		}
		int sqScore = new ScoringSummStAction().calculateSemanticQualScoreNew(st); //score SQ
		st.setSqScore(sqScore);
		new ScoringSummStAction().calculateNarrowing(st);
		
		/*CRTLogger.out("SQ1: " + st.getSqHits()!=null ? st.getSqHits().toString() : "null", CRTLogger.LEVEL_PROD); //coumn SQ1
		CRTLogger.out("fdgs: " + st.getFindingHits()!=null ? st.getFindingHits() : "null", CRTLogger.LEVEL_PROD); //coumn findings
		CRTLogger.out("anatomy " + st.getAnatomyHits()!=null ? st.getAnatomyHits() : "null", CRTLogger.LEVEL_PROD); //column anatomy
		CRTLogger.out("exp: " + st.getExpMatches()!=null ? st.getExpMatches() : "null", CRTLogger.LEVEL_PROD); //column ExpMatchSumSt
		CRTLogger.out("diagn: " + st.getDiagnosesHits()!=null ? st.getDiagnosesHits() : "null", CRTLogger.LEVEL_PROD); //column diagnoses
		CRTLogger.out("test: " + st.getTestHits()!=null ? st.getTestHits() : "null", CRTLogger.LEVEL_PROD); //column tests
		CRTLogger.out("ther: " + st.getTherHits()!=null ? st.getTherHits() : "null", CRTLogger.LEVEL_PROD); //column therapies
		CRTLogger.out("all: " + st.getItemHits()!=null ? st.getItemHits().toString() : "null", CRTLogger.LEVEL_PROD);
		CRTLogger.out("otger: " + st.getOtherHits()!=null ? st.getOtherHits().toString() : "null", CRTLogger.LEVEL_PROD); //column other
		CRTLogger.out("exp script: " + st.getExpScriptMatches()!=null ? st.getExpScriptMatches() : "null", CRTLogger.LEVEL_PROD); //column ExpMatchScript
		CRTLogger.out("" + sqScore, CRTLogger.LEVEL_PROD);*/
		return st;		
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
	private static boolean compareSimilarList(List items, SummaryStatement st, Locale loc, String s){	
		for(int j=0;j<items.size(); j++){ //comparison with adapted Mesh list:
			ListItem li = (ListItem) items.get(j);	
			//if(li.getListItemId()==1246 && s.equalsIgnoreCase("Hypoxemia"))
			//	CRTLogger.out("", 1);

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
					
				//}
				/*if(elem.getSynonymStr()!=null){
					//Synonym s = (Synonym) elem.getListItem();
					//ListItem li = elem.getListItem(); //new DBList().selectListItemById(s.getListItemId());
					if(li!=null && StringUtilities.similarStrings(li.getName(), expMatchesArr[i], elem.getListItem().getLanguage())){
						//matchCounter++;
						elem.setExpertMatch(true);
						break innerLoop;
					}
				}*/
					/*if(li!=null && li.getSynonyma()!=null){
						Iterator it2 = li.getSynonyma().iterator();
						while(it2.hasNext()){
							Synonym s2= (Synonym) it2.next();
							if(StringUtilities.similarStrings(s2.getName(), expMatchesArr[i], elem.getListItem().getLanguage())){
								matchCounter++;
								elem.setExpertMatch(true);
								break innerLoop;
							}
						}
					}
				}*/
			}
		}
	}
}
