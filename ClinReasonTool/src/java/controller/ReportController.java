package controller;

import java.util.List;

import beans.scripts.PatientIllnessScript;
import database.DBClinReason;

/**
 * Handles all stuff around displaying the learner's scripts, either on an individual or on an overall basis.
 * 
 * @author ingahege
 *
 */
public class ReportController {
	static private ReportController instance = new ReportController();
	static public ReportController getInstance() { return instance; }

	public List<PatientIllnessScript> getLearnerScriptsForVPId(String vpId){
		if(vpId==null) return null;
		return new DBClinReason().selectLearnerPatIllScriptsByVPId(vpId);
	}
}
