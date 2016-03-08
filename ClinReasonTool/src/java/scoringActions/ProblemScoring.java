package scoringActions;

import beans.PatientIllnessScript;
import beans.relation.Relation;
import errors.WrongProblemError;

public class ProblemScoring {

	/**
	 * We try to compare the users entry with the experts list of problems and score based on whether the 
	 * problem is in the list, the learner has used the correct term, etc. 
	 * We also have to take into account whether the learner has used any feedback before this action 
	 * @param patIllScript
	 * @param rel
	 */
	public void calcScoreForAddProblem(PatientIllnessScript patIllScript, Relation rel) throws WrongProblemError{
		//we update the score? score the problem, return score for feedback
		if(patIllScript.getExpertPatIllScript()!=null){
			//then we can compare the learners entry...
		}
	}
	
	
	public void calcScoreForDelProblem(){
		
	}
	
}
