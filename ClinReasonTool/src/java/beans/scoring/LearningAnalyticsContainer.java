package beans.scoring;

import java.io.Serializable;
import java.util.*;

/**
 * This is a container of learningAnalytic objects for all scripts of a user 
 * Here we analyze the development over time and scripts....
 * We could do all kinds of analytics, look for problems, ddx the user misses often, whether he is stuck at a certain point
 * (e.g. always low scores for ddx category),...
 * @author ingahege
 *
 */
public class LearningAnalyticsContainer implements Serializable{
	
	/**
	 * key = patIllScriptId
	 */
	private Map<Long,LearningAnalyticsBean> analytics = new HashMap<Long,LearningAnalyticsBean>(); 
	private long userId; 
	
	public LearningAnalyticsContainer(){}
	public LearningAnalyticsContainer(long userId){
		this.userId = userId;
	}
	
	public LearningAnalyticsBean getLearningAnalyticsBeanByPatIllScriptId(long patIllScriptId, long parentId){
		if(analytics==null || analytics.isEmpty()) initLearningAnalyticsBean(patIllScriptId, parentId);
		return analytics.get(new Long(patIllScriptId));
	}
	
	/**
	 * Init the LearningAnalyticsBean for this script, scores are loaded when creating the Bean.
	 * @param patIllScriptId
	 * @param parentId
	 */
	private void initLearningAnalyticsBean(long patIllScriptId, long parentId){
		analytics.put(new Long(patIllScriptId), new LearningAnalyticsBean(patIllScriptId, userId, parentId));
	}
	
	public void addLearningAnalyticsBean(long patIllScriptId, long parentId){
		if(analytics.containsKey(new Long(patIllScriptId))) return;
		initLearningAnalyticsBean(patIllScriptId, parentId);
	}
	
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
