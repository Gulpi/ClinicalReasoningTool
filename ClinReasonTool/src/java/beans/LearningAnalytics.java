package beans;

import java.util.*;

/**
 * LearninAnalytics takes into account all users' actions, scores, and goals. 
 * @author ingahege
 *
 */
public class LearningAnalytics {
	
	private long userId; 
	private List<ScoreContainer> allScores; //all items that have been scored
	private List<LogEntry> logs; //all actions the user has performed, this includes any clicks on links, etc...
	private List<PatientIllnessScript> scripts; //all scripts of the user 
	//TODO we also need the goals and current goal
	//TODO we also need time on task? 
	//TODO badges, certificates, achievments
	//TODO errors (dependning on confidence, complexity)
	//TODO any emotions? can we messure those somehow? ask? 
	//TODO include when an action was performed? E.g. problems created at end or thruout session? 
	
	public void levelOfSRL(){
		//based on goal setting, note taking, use of the tool, what do we return (fuzzy)? consequences? 
	}
	
	public void overallPerformance(){
		//calculate something linke an overall score?
	}
	
	public void levelOfDiagnoses(){ //etc....
		//something like that to return detailed performance for each CR step (based on MOT model?) 
		//should also correspond to the goals
	}
	public void levelOfProblems(){ //etc....
		//less challenging than diagnoses creation, just careful reading, so this is a different level. 
	}
	
	/**if we could determine this, would be great, maybe we can store the LA details on a regularly basis 
	 * and compare them over time and see if there is no progress at all and ths learner has worked with the 
	 * tool. 
	 * @return
	 */
	public boolean isLearnerStuck(){ 
		return false; //if true, where (if we can tell that) 
	}
	
	
}
