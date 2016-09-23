package controller;

import java.util.*;

import application.AppBean;
import beans.LogEntry;
import beans.scripts.*;
import beans.error.*;
import beans.error.MyError;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import database.DBClinReason;

/**
 * If the leaner has submitted a wrong final diagnosis, we check whether we can see a pattern that 
 * speaks in favor of one of the common errors.
 * @author ingahege
 *
 */
public class ErrorController {

	public List<MyError> checkError(List<RelationDiagnosis> learnerFinals,List<RelationDiagnosis> expFinals){
		List<MyError> errors = new ArrayList<MyError>();
		PatientIllnessScript patIllScript = new NavigationController().getCRTFacesContext().getPatillscript();
		PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		
		if(learnerFinals==null || learnerFinals.isEmpty() || expFinals==null || expFinals.isEmpty()) return null;
		checkPrematureClosure(expIllScript, patIllScript);
		checkAvailabilityError();
		//new DBClinReason().saveAndCommit(errors);
		
		return errors;
	}
	
	
	/**
	 * ddx has been submitted too early
	 * TODO: we could check in addition whether user has missed important findings (or these come at a later stage) that 
	 * would lead to the correct diagnoses.
	 * @param expIllScript
	 * @param patIllScript
	 */
	private void checkPrematureClosure(PatientIllnessScript expIllScript, PatientIllnessScript patIllScript){		
		if(patIllScript.getSubmittedStage() < expIllScript.getSubmittedStage()){
			PrematureClosure pcl =  new PrematureClosure(patIllScript.getId(), patIllScript.getCurrentStage());
			if(patIllScript.addError(pcl)) //we save only if this is a new error that has not previously occured at this stage
				new DBClinReason().saveAndCommit(pcl);	
			notifyLog(pcl, patIllScript.getId());
		}
	}
	
	/**
	 * we have to check here the last x scripts/VPs of the user and whether it involved the diagnosis he has come up with here...
	 * @return
	 */
	private void checkAvailabilityError(){
		PatIllScriptContainer cont = new NavigationController().getCRTFacesContext().getScriptContainer();
		if(cont==null || cont.getScriptsOfUser()==null) return; //no previous scripts
		List<PatientIllnessScript> lastScripts = cont.getLastCompletedScripts(AvailabilityBias.NUM_SCRIPTS, null);
	}
	
	//and so on....
	
	private void notifyLog(MyError err, long patIllScriptId){
		LogEntry le = new LogEntry(LogEntry.ERROR_ACTION, patIllScriptId, err.getType());
		le.save();		
	}
}
