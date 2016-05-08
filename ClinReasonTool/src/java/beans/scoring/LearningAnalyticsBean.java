package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import application.AppBean;
import beans.LogEntry;
import beans.PatientIllnessScript;

/**
 * LearninAnalytics takes into account all users' actions, scores, and goals for one VP.
 * @author ingahege
 *
 */
@ManagedBean(name = "analytics", eager = true)
@SessionScoped
public class LearningAnalyticsBean extends Beans implements Serializable{
	//categories based on the MOT model
	public static final int CATEGORY_PROBLEM_IDENT = 1; 		//MOT: Determine objectives of encounter
	public static final int SUBCATEGORY_EARLYCUE_IDENT = 2; 	//MOT: identify early cues
	public static final int CATEGORY_DDX_IDENT = 3;				//MOT: Categorize for purpose of action
	public static final int CATEGORY_SEMANTIC_TRANSF = 4; 		//MOT: semantic transformation
	public static final int CATEGORY_METACOGNITION = 5; 		//MOT: Metacognition
	public static final int CATEGORY_THERAP_INTERVENTION = 6; 	//MOT: Therapeutic Interventions
	public static final int CATEGORY_INVESTIGATION = 7; 		//MOT: Investigations
	public static final int CATEGORY_REPRESENTATION = 8; 		//MOT: Final representation of the problem (summary statement)
	public static final int CATEGORY_SRL = 9;					//self-regulated learning
	public static final int CATEGORY_PATTERN_RECOGNITION = 10;	//analytic reasoning or pattern recognition prevalent?
	public static final int CATEGORY_PRIORIZATION = 11;			//important aspects identified and ranked high? e.g. MnM
	public static final int CATEGORY_FINAL_DDX_IDENT = 12; 		//MOT: ?
	public static final int CATEGORY_ERRORS = 13;
	public static final int CATEGORY_ENGAGEMENT = 14; 			//e.g. number of actions, time on task,...at which stage have actions been performed?
	public static final int CATEGORY_EMOTIONS = 15;				//fatigue, boredom, anxiety, frustration
	
	
	private long userId; 
	/**
	 * All scores for the VP this LearningAnalyticsBean belongs to.
	 */
	private ScoreContainer scoreContainer; //all items that have been scored
	private List<LogEntry> logs; //all actions the user has performed, this includes any clicks on links, etc...
	//private PatientIllnessScript patIllScript; //all scripts of the user 
	//TODO we also need the goals and current goal
	//TODO we also need time on task? 
	//TODO badges, certificates, achievements
	//TODO errors (depending on confidence, complexity)
	//TODO any emotions? can we measure those somehow? ask? 
	//TODO include when an action was performed? E.g. problems created at end or throughout session? 
	private long patIllScriptId;
	private long parentId;
	
	public LearningAnalyticsBean(){}
	public LearningAnalyticsBean(long patIllScripId, long userId, long parentId){
		this.patIllScriptId = patIllScripId;
		this.userId = userId;
		this.parentId = parentId;
		scoreContainer = new ScoreContainer(patIllScripId);
		scoreContainer.initScoreContainer();
	}
	
	public void levelOfSRL(){
		//based on goal setting, note taking, use of the tool, what do we return (fuzzy)? consequences? 
	}
	
	public void overallPerformance(){
		//calculate something like an overall score?
	}
	
	public void levelOfDiagnoses(){ //etc....
		//something like that to return detailed performance for each CR step (based on MOT model?) 
		//should also correspond to the goals
	}
	public List<ScoreBean> getProblemScoreStages(){ 
		//less challenging than diagnoses creation, just careful reading, so this is a different level. 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_PROBLEM_LIST);
	}
	
	public List<PeerBean> getProblemPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndParentId(parentId, ScoreBean.TYPE_PROBLEM_LIST);
	}
	
	public List<ScoreBean> getDDXScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_DDX_LIST);
	}

	public List<PeerBean> getDDXPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndParentId(parentId, ScoreBean.TYPE_DDX_LIST);
	}
	
	public List<ScoreBean> getTestScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_TEST_LIST);
	}
	
	public List<PeerBean> getTestPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndParentId(parentId, ScoreBean.TYPE_TEST_LIST);
	}
	
	public List<ScoreBean> getMngScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_MNG_LIST);
	}
	
	public List<PeerBean> getMngPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndParentId(parentId, ScoreBean.TYPE_MNG_LIST);
	}
	public void levelOfEngagement(){
		//passive vs active user, non-user, regular users, glancers (<2min), interrupter 
		
	}
	
	public ScoreContainer getScoreContainer() {
		if(scoreContainer==null) scoreContainer = new ScoreContainer(patIllScriptId);
		return scoreContainer;
	}
	
}
