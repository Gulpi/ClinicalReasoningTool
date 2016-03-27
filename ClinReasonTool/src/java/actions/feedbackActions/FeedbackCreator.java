package actions.feedbackActions;

/**
 * The Actions implementing this interface provide some kind of immediate feedback. CAVE: 
 * This is independent from scoring, which can also be done just in the background without any 
 * feedback to the learner.
 * @author ingahege
 *
 */
public interface FeedbackCreator {
	public void triggerFeedbackAction();

}
