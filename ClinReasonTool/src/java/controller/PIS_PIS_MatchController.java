package controller;

import beans.*;

/**
 * This class handles the matching of PatientIllnessScripts with another PatientIllnessScript 
 * at certain stages during a VP session. 
 * @author ingahege
 *
 */
public class PIS_PIS_MatchController extends IS_MatchController{
	
	private PatientIllnessScript pisLearner;
	private PatientIllnessScript pisCompare; //can be a authors IS, another learner or even peer average IS? 
	
	
	public PIS_PIS_MatchController(PatientIllnessScript isLearner, PatientIllnessScript isCompare){
		pisLearner = isLearner; 
		pisCompare = isCompare;
	}
	//what to return?
	public void compareProblems(int stage){
		//comparison of two lists, how to score, what to return?
	}
	
	//what to return?
	public void compareDDX(int stage){
		//we have to compare two lists and also take into account the diagnoses flags ("doNotMiss" or "Lethal") 
		//Score should be more reduced if important diagnoses at a given stage are missed. 
	}
	
	//what to return? true/false
	public void compareFinalDiagnoses(int stage){
		//consider level of confidence for scoring (especially if wrong)
	}
	
	//what to return? 
	public void compareSummaryStatements(int stage){
		//do magic here....
		//we could compare the students summary with the PIS entries at that stage of the author: 
		// most important problems included? age? other important enabling conditions? use of SQ?
	}

}
