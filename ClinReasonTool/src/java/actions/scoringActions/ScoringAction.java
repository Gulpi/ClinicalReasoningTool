package actions.scoringActions;

import beans.scripts.*;
import beans.scoring.ScoreBean;

/**
 * Scoring is done based on the Graph we have created for this learner and a parentId (VPId).
 * @author ingahege
 *
 */
public interface ScoringAction {
	public static final int NO_SCORING_POSSIBLE = -2;

	void scoreAction(long listItemId, PatientIllnessScript learnerPatIllScript, boolean isJoker, int type);
}
