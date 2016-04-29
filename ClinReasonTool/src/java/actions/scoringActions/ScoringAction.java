package actions.scoringActions;

import beans.PatientIllnessScript;
import beans.scoring.ScoreBean;

/**
 * Scoring is done based on the Graph we have created for this learner and a parentId (VPId).
 * @author ingahege
 *
 */
public interface ScoringAction {
	
	void scoreAction(long listItemId, PatientIllnessScript learnerPatIllScript);
}
