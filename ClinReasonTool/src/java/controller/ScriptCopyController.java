package controller;

import java.util.*;
import beans.relation.*;
import beans.scripts.PatientIllnessScript;
import beans.scripts.VPScriptRef;
import database.*;
import beans.list.*;
import util.CRTLogger;

/**
 * Copies a script/map created by an expert including translation into a different language (e.g. from de to en). 
 * @author ingahege
 *
 */
public class ScriptCopyController {
	
	private static PatientIllnessScript newScript;
	private static PatientIllnessScript orgScript;
	private static Properties idTable = new Properties();
	
	/**
	 * for testing purposes only!
	 * @param args
	 */
	public static void main(String[] args ){
		String lang;
		String newVPId;
		String orgVPId;
		
		if(args!=null && args.length>=2){
			orgVPId = args[0];
			lang = args[1].trim();
			newVPId = args[2].trim();
		}
		else{
			lang = "en";
			orgVPId = "169509_2";
			newVPId = "169514";
		}
		new HibernateSession().initHibernate();		
		initCopyAndTranslate(orgVPId, newVPId, lang);
	}
	
	public static void initCopyAndTranslate(String orgVPId, String newVPId, String lang){
		if(orgVPId==null) return;
		if(orgVPId.indexOf("_")<=0) orgVPId = orgVPId+"_2";
		orgScript = new DBClinReason().selectExpertPatIllScriptByVPId(orgVPId);
		if(orgScript == null|| orgScript.getType()!=PatientIllnessScript.TYPE_EXPERT_CREATED) return;
		if(!orgScript.getLocale().getLanguage().equalsIgnoreCase(lang))
			copyAndTranslateScript(lang, newVPId);
	}
	
	private static void copyAndTranslateScript(String newLang, String vpId){
		PatientIllnessScript newScript = createNewScript(newLang, vpId);
		if(newScript==null) return;
		createVPScriptRef(newScript);
		if(orgScript.getProblems()!=null){
			for(int i=0;i<orgScript.getProblems().size();i++){
				copyAndTranslateProblem(orgScript.getProblems().get(i));
			}
		if(orgScript.getDiagnoses()!=null){
			for(int i=0;i<orgScript.getDiagnoses().size();i++){
				copyAndTranslateDDX(orgScript.getDiagnoses().get(i));
			}
			}
		if(orgScript.getTests()!=null){
			for(int i=0;i<orgScript.getTests().size();i++){
				copyAndTranslateTests(orgScript.getTests().get(i));
			}
		}
		if(orgScript.getMngs()!=null){
			for(int i=0;i<orgScript.getMngs().size();i++){
				copyAndTranslateManagements(orgScript.getMngs().get(i));
			}
		}						
			copyConnections();	
			copySummStatement();
		}
	}
	
	/**
	 * We create and save the VPScriptRef object (which contains the VP name and id).
	 * @param newScript
	 */
	private static void createVPScriptRef(PatientIllnessScript newScript){
		VPScriptRef orgRef = (VPScriptRef) new DBClinReason().getVPScriptRef(orgScript.getVpId());
		if(orgRef==null) return;
		VPScriptRef newRef = new VPScriptRef(newScript.getVpId(), orgRef.getVpName(), orgRef.getSystemId(), "-1");
		new DBClinReason().saveAndCommit(newRef);
		
	}
	
	/**
	 * Creates a new map and stores it in the database, where necessary parameters from the orgScript are transferred. 
	 * @param orgScript
	 * @param newLang
	 * @param vpId
	 * @return
	 */
	private static PatientIllnessScript createNewScript(String newLang, String vpId){
		//todo check whether for the given VP Id a script has already been created!
		newScript = new PatientIllnessScript(orgScript.getUserId(), vpId, new Locale(newLang), 2);
		newScript.setFinalDDXType(orgScript.getFinalDDXType());
		newScript.setType(PatientIllnessScript.TYPE_EXPERT_CREATED);
		newScript.setCurrentStage(orgScript.getCurrentStage());
		newScript.setSubmittedStage(orgScript.getSubmittedStage());
		newScript.setMaxSubmittedStage(orgScript.getMaxSubmittedStage());
		new DBClinReason().saveAndCommit(newScript);
		return newScript;
	}
	
	private static Relation copyAndTranslateProblem(RelationProblem rel){
		RelationProblem newRel = (RelationProblem) copyBasicData(new RelationProblem(), rel);
		
		ListItem li = getListItem(rel.getProblem().getFirstCode());
		
		if (li!=null){ //we found a match in the list
			newRel.setProblem(li);
			CRTLogger.out(rel.getProblem().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			newRel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(newRel);
			idTable.put(new Long(rel.getId()), newRel.getId());
			return newRel;
		}
		idTable.put(new Long(rel.getId()), new Long(-1));
		return null;
	}
	
	private static Relation copyAndTranslateDDX(RelationDiagnosis rel){
		RelationDiagnosis newRel = (RelationDiagnosis) copyBasicData(new RelationDiagnosis(), rel);	
		ListItem li = getListItem(rel.getDiagnosis().getFirstCode());
		newRel.setFinalDiagnosis(rel.getFinalDiagnosis());
		newRel.setMnm(rel.getMnm());
		newRel.setWorkingDDX(rel.getWorkingDDX());
		newRel.setRuledOut(rel.getRuledOut());
		newRel.setPrevalence(rel.getPrevalence());
		
		if (li!=null){ //we found a match in the list
			newRel.setDiagnosis(li);
			CRTLogger.out(rel.getDiagnosis().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			newRel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(newRel);
			idTable.put(new Long(rel.getId()), newRel.getId());
			return newRel;
		}
		idTable.put(new Long(rel.getId()), new Long(-1));
		return null;
	}
	
	private static Relation copyAndTranslateTests(RelationTest rel){
		RelationTest newRel = (RelationTest) copyBasicData(new RelationTest(), rel);	
		ListItem li = getListItem(rel.getTest().getFirstCode());
		
		if (li!=null){ //we found a match in the list
			newRel.setTest(li);
			CRTLogger.out(rel.getTest().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			newRel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(newRel);
			idTable.put(new Long(rel.getId()), newRel.getId());
			return newRel;
		}
		idTable.put(new Long(rel.getId()), new Long(-1));
		return null;
	}
	
	private static Relation copyAndTranslateManagements(RelationManagement rel){
		RelationManagement newRel = (RelationManagement) copyBasicData(new RelationManagement(), rel);	
		ListItem li = getListItem(rel.getManagement().getFirstCode());
		
		if (li!=null){ //we found a match in the list
			newRel.setListItemId(li.getItem_id());
			newRel.setManagement(li);
			CRTLogger.out(rel.getManagement().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);

			new DBClinReason().saveAndCommit(newRel);
			idTable.put(new Long(rel.getId()), newRel.getId());
			return newRel;
		}
		idTable.put(new Long(rel.getId()), new Long(-1));
		return null;
	}
	
	private static void copyConnections(){
		if(idTable==null || idTable.isEmpty()) return;
		if(orgScript.getConns()==null || orgScript.getConns().isEmpty()) return; //no connections to copy!
		Iterator<Connection> it = orgScript.getConns().values().iterator();
		while(it.hasNext()){
			Connection orgCnx =it.next();
			//(long startId, long targetId, long illScriptId, int startType, int targetType, int stage)
			Connection newCnx = new Connection();
			newCnx.setIllScriptId(newScript.getId());
			newCnx.setStartType(orgCnx.getStartType());
			newCnx.setTargetType(orgCnx.getTargetType());
			newCnx.setStage(orgCnx.getStage());
			newCnx.setTargetEpIdx(orgCnx.getTargetEpIdx());
			newCnx.setStartEpIdx(orgCnx.getStartEpIdx());
			newCnx.setWeight(orgCnx.getWeight());
			Long newStartId = (Long) idTable.get(new Long(orgCnx.getStartId()));
			Long newTargetId = (Long) idTable.get(new Long(orgCnx.getTargetId()));
			if(newStartId!=null && newTargetId!=null && newStartId.longValue()>0 && newTargetId.longValue()>0){
				newCnx.setStartId(newStartId.longValue());
				newCnx.setTargetId(newTargetId.longValue());
				new DBClinReason().saveAndCommit(newCnx);
			}
					
		}
		
	}
	
	private static Relation copyBasicData(Relation newRel, Relation orgRel){
		newRel.setDestId(newScript.getId());
		newRel.setX(orgRel.getX());
		newRel.setY(orgRel.getY());
		newRel.setStage(orgRel.getStage());
		newRel.setOrder(orgRel.getOrder());
		//CAVE prefixes need to be translated, too
		return newRel;
	}
	
	/**
	 * select the ListItem with the given code in the language of the new map
	 * @param code
	 * @return
	 */
	private static ListItem getListItem(String code){	
		ListItem li = new DBList().selectListItemByCode(code, newScript.getLocale());
		if(li==null){
			CRTLogger.out("No ListItem found for code " + code, CRTLogger.LEVEL_PROD);
			return null;
		}		 
		return li;
	}
	
	/**
	 * We cannot translate the summary statement, but we copy it, so that a translation in the user interface is easier. 
	 */
	private static void copySummStatement(){
		if(orgScript.getSummStId()>0){
			SummaryStatement summSt = orgScript.getSummSt(); //could that be null? lazy loading?
			
			if(summSt!=null){
				SummaryStatement newStatement = new SummaryStatement();
				newStatement.setText(summSt.getText());
				newStatement.setLang(newScript.getLocale().getLanguage());
				newStatement.setPatillscriptId(newScript.getId());
				newStatement.setType(PatientIllnessScript.TYPE_EXPERT_CREATED);
				newStatement.setStage(summSt.getStage());
				new DBClinReason().saveAndCommit(newStatement);
				newScript.setSummStId(newStatement.getId());
				new DBClinReason().saveAndCommit(newScript);
			}
		}
	}
}
