package actions.scoringActions;

import beans.PatientIllnessScript;
import beans.scoring.ScoreBean;

/**
 * Scoring is done based on the Graph we have created for this learner and a parentId (VPId).
 * @author ingahege
 *
 */
public interface ScoringAction {
	public static final float SCORE_EXP_SAMEAS_LEARNER = 1;
	public static final float SCORE_NOEXP_BUT_LEARNER = 0; //we score with 0 points, BUT the action itself will be honored in the LA part
	public static final float MIN_PEERS = 20;
	
	ScoreBean scoreAction(long listItemId, PatientIllnessScript learnerPatIllScript);
}
