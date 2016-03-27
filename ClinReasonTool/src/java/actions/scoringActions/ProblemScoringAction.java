package actions.scoringActions;

import javax.faces.context.FacesContextWrapper;

import beans.CRTFacesContext;
import beans.PatientIllnessScript;
import beans.ScoreContainer;
import beans.relation.RelationProblem;
//import errors.WrongProblemError;

public class ProblemScoringAction {

	private ScoreContainer scoreContainer;
	/**
	 * We try to compare the users entry with the experts list of problems and score based on whether the 
	 * problem is in the list, the learner has used the correct term, etc. 
	 * We also have to take into account whether the learner has used any feedback before this action 
	 * @param patIllScript
	 * @param rel
	 */
	public void scoreAddProblemAction(RelationProblem prob, PatientIllnessScript learnerScript, PatientIllnessScript expertScript){
		setScoreBean();
		//we update the score? score the problem, return score for feedback
		//if(patIllScript.getExpertPatIllScript()!=null){
			//then we can compare the learners entry...
		//}
		//TODO we have to have different scoring modes? 
		scoreAddProblemActionBasedOnExpertList(prob, expertScript);
		
		scoreOverallProblemList(); //update this to also consider problems the learner has not (yet) come up with  
	}
	
	/**
	 * User has added a problem to the list, we score it based on the experts list
	 * 1 = exact same problem is in experts' list (dark green check)
	 * 0.5 = synonyma is in the experts' list (light green check -> learner can change to better term) 
	 * 0 = problem is not at all in the experts' list (no check) 
	 * TODO consider position? 
	 */
	private void scoreAddProblemActionBasedOnExpertList(RelationProblem prob, PatientIllnessScript expertScript){
		
		
	}
	
	/**
	 * This can only be done at the end of the session or when diagnoses are committed? 
	 * We cannot do this from the beginning on, because this might change. 
	 * But, we can do it thruout the process and update it each time a problem has been added or changed (not deleted?)
	 * Algorithm? Based on single scores and overall number of problems
	 * Include when problem was created? (or only in LA piece)?  
	 * 0 = learner has not created any list
	 * 0.? something in between
	 * 1 = experts' list contains no additional problems, learner has captured all (or at least synonyma)
	 * This is probably not for feedback, just for LA- relevant scoring
	 */
	private void scoreOverallProblemList(){
		
	}
	
	
	
	/*public void calcScoreForDelProblem(){
		
	}*/
	
	private void setScoreBean(){
		CRTFacesContext fc =  (CRTFacesContext) FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		this.scoreContainer = fc.getScoreContainer();
	}
	

}
