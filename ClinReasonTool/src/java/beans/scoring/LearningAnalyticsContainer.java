package beans.scoring;

import java.util.*;

/**
 * Here we analyze the development over time and scripts....
 * We could do all kinds of analytics, look for problems, ddx the user misses often, whether he is stuck at a certain point
 * (e.g. always low scores for ddx category),...
 * @author ingahege
 *
 */
public class LearningAnalyticsContainer {

	List<LearningAnalyticsBean> analyticsOfUser; 
	/**if we could determine this, would be great, maybe we can store the LA details on a regularly basis 
	 * and compare them over time and see if there is no progress at all and ths learner has worked with the 
	 * tool. 
	 * @return
	 */
	public boolean isLearnerStuck(){ 
		return false; //if true, where (if we can tell that) 
	}
	
	public void identifyAreaOfWeakness(){}
	public void identifyAreaOfStrenght(){}
	
}
