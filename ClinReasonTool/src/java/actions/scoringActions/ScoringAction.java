package actions.scoringActions;

/**
 * Scoring is done based on the Graph we have created for this learner and a parentId (VPId).
 * @author ingahege
 *
 */
public interface ScoringAction {
	void scoreAddAction(long id);
}
