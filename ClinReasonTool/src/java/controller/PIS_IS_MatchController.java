package controller;

import beans.*;

/**
 * This controller handles the comparison of PatientIllnessScripts with IllnessScripts. This can be done for scoring 
 * after a diagnosis has been submitted.
 * @author ingahege
 *
 */
public class PIS_IS_MatchController extends IS_MatchController{

	private PatientIllnessScript pisLearner;
	//private IllnessScript isCompare; //can be a authors IS, another learner or even peer average IS? 
	
	
	public PIS_IS_MatchController(PatientIllnessScript isLearner/*, IllnessScript isCompare*/){
		pisLearner = isLearner; 
		//this.isCompare = isCompare;
	}
}
