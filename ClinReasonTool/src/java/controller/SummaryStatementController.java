package controller;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import application.AppBean;
import beans.list.ListInterface;
import beans.relation.SummaryStatement;
import beans.relation.SummaryStatementSQ;
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
	private static Map<String, List<ListInterface>> listItems;

	
	/**
	 * returns a List of ListItems for the given language or null if nothing is found.
	 * @param lang
	 * @return
	 */
	public static List<ListInterface> getListItemsByLang(String lang) {
		if(listItems==null || lang==null) return null;
		return (List<ListInterface>) listItems.get(lang);
	}
	
	/**
	 * adds a list of ListItems to the Map (key=language). 
	 * @param myListItems
	 * @param lang
	 */
	public static void addListItems(List<ListInterface> myListItems, String lang) {
		if(SummaryStatementController.listItems == null) 
			SummaryStatementController.listItems = new TreeMap<String, List<ListInterface>>();
		if(!listItems.containsKey(lang))
			listItems.put(lang, myListItems);
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
					hits.add(new SummaryStatementSQ(stst.getId(),sqs.get(i).getId(), s));			
				}
			}
			if(hits!=null && !hits.isEmpty()) new DBClinReason().saveAndCommit(hits);
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
	public List<String> getMatchingWordsFromMesh(String learnerText, String lang, JsonCreator jc){
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
	}
	
	/**
	 * returns the listItem or the parent listItem (which we have to select from the database first) if the 
	 * listItem is a synonym
	 * @param li
	 * @return
	 */
	private ListInterface getListItem(ListInterface li){
		if(!li.getItemType().equalsIgnoreCase("Syn"))
			return li; //.getItemType();
		//for synonyms we have to check the parent list item:
		ListInterface parent = new DBList().selectListItemById(li.getListItemId());
		/*if(parent!=null)*/ return parent; //.getItemType();
		//return null; //should not happen
		
	}		
}
