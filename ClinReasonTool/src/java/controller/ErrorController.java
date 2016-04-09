package controller;

import java.util.*;

import application.AppBean;
import beans.PatientIllnessScript;
import beans.error.*;
import beans.error.Error;
import beans.relation.Relation;
import beans.scoring.ScoreBean;
import database.DBClinReason;

/**
 * If the leaner has submitted a wrong final diagnosis, we check whether we can see a pattern that 
 * speaks in favor of one of the common errors.
 * @author ingahege
 *
 */
public class ErrorController {

	public List<Error> checkError(ScoreBean scoreBean, List<Relation> leanerFinals,List<Relation >expFinals){
		List<Error> errors = new ArrayList<Error>();
		PatientIllnessScript patIllScript = new NavigationController().getCRTFacesContext().getPatillscript();
		PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getParentId());
		
		if(leanerFinals==null || leanerFinals.isEmpty() || expFinals==null || expFinals.isEmpty()) return null;
		errors.add(isPrematureClosure(expIllScript, patIllScript));
		errors.add(isAvailabilityError());
		new DBClinReason().saveAndCommit(errors);
		
		return errors;
	}
	
	
	private Error isPrematureClosure(PatientIllnessScript expIllScript, PatientIllnessScript patIllScript){
		
			if(patIllScript.getSubmittedStage()< expIllScript.getSubmittedStage()) return new PrematureClosure();
			return null;
	}
	
	private Error isAvailabilityError(){
		//we have to check here the last x scripts/VPs of the user and whether it involved the diagnosis he has come up with 
		//here...
		return null;
	}
	
	//and so on....
}
