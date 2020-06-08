package controller;

import beans.list.ListItem;
import beans.relation.*;
import beans.scripts.PatientIllnessScript;
import database.DBClinReason;
import util.*;

public class ScriptTranslationController {
	
	/**
	 * new language has already been set!
	 */
	private PatientIllnessScript patIllScript; 

	public ScriptTranslationController(){}
	public ScriptTranslationController(PatientIllnessScript pis){
		this.patIllScript = pis;
	}
	
	/**
	 * duplicate the script/map and translate all items (if possible) into the new language. 
	 * @param newLang
	 * @param vpId
	 */
	public void translateScript(){
		try{
			if(patIllScript.getProblems()!=null){
				for(int i=0;i<patIllScript.getProblems().size();i++){
					translateProblem(patIllScript.getProblems().get(i));
				}
			}
			if(patIllScript.getDiagnoses()!=null){
				for(int i=0;i<patIllScript.getDiagnoses().size();i++){
					translateDiagnosis(patIllScript.getDiagnoses().get(i));
				}
			}
			if(patIllScript.getTests()!=null){
				for(int i=0;i<patIllScript.getTests().size();i++){
					translateTest(patIllScript.getTests().get(i));
				}
			}
			if(patIllScript.getMngs()!=null){
				for(int i=0;i<patIllScript.getMngs().size();i++){
					translateMng(patIllScript.getMngs().get(i));
				}
			}	
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}


	/**
	 * We translate a problem/finding by looking for the MeSH code in the new language
	 * @param rel
	 */
	private void translateProblem(RelationProblem rel){		
		ListItem li = ScriptCopyController.getListItem(rel.getProblem().getFirstCode(), this.patIllScript.getLocale());		
		if (li!=null){ //we found a match in the list
			rel.setProblem(li);
			CRTLogger.out(rel.getProblem().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			rel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(rel);

		}
	}
	
	/**
	 * We translate a ddx by looking for the MeSH code in the new language
	 * @param rel
	 */
	private void translateDiagnosis(RelationDiagnosis rel){		
		ListItem li = ScriptCopyController.getListItem(rel.getDiagnosis().getFirstCode(), this.patIllScript.getLocale());		
		if (li!=null){ //we found a match in the list
			rel.setDiagnosis(li);
			CRTLogger.out(rel.getDiagnosis().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			rel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(rel);

		}
	}
	
	/**
	 * We translate a test by looking for the MeSH code in the new language
	 * @param rel
	 */
	private void translateTest(RelationTest rel){		
		ListItem li = ScriptCopyController.getListItem(rel.getTest().getFirstCode(), this.patIllScript.getLocale());		
		if (li!=null){ //we found a match in the list
			rel.setTest(li);
			CRTLogger.out(rel.getTest().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			rel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(rel);

		}
	}
	
	/**
	 * We translate a test by looking for the MeSH code in the new language
	 * @param rel
	 */
	private void translateMng(RelationManagement rel){		
		ListItem li = ScriptCopyController.getListItem(rel.getManagement().getFirstCode(), this.patIllScript.getLocale());		
		if (li!=null){ //we found a match in the list
			rel.setManagement(li);
			CRTLogger.out(rel.getManagement().getName() + "->" + li.getName(), CRTLogger.LEVEL_PROD);
			rel.setListItemId(li.getItem_id());
			new DBClinReason().saveAndCommit(rel);

		}
	}
}