package actions.scoringActions;

import beans.PatientIllnessScript;

/**
 * Scoring of lists, like ddxs, problems, etc..
 * @author ingahege
 *
 */
public class ScoringListAction {
	private PatientIllnessScript patillsscript;
	
	public ScoringListAction(PatientIllnessScript patillsscript){
		this.patillsscript = patillsscript;
	}
	
	public void scoreList(int listType){
		
	}
}
