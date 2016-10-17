package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import application.AppBean;
import beans.LogEntry;
import beans.scripts.*;

/**
 * LearninAnalytics takes into account all users' actions, scores, and goals for one VP.
 * The LearningAnalytics Bean contains all scores for one script of a learner
 * @author ingahege
 *
 */
@ManagedBean(name = "analyticsbean", eager = true)
@SessionScoped
public class LearningAnalyticsBean extends Beans implements Serializable, Comparable{
	
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
	private String vpId;
	
	public LearningAnalyticsBean(){}
	public LearningAnalyticsBean(long patIllScripId, long userId, String vpId){
		this.patIllScriptId = patIllScripId;
		this.userId = userId;
		this.vpId = vpId;
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
	
	public ScoreBean getSummStScore(){
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoreByType(ScoreBean.TYPE_SUMMST);
	}
	/**
	 * we get the list score for problems at the last stage
	 * @return
	 */
	public ScoreBean getFinalProblemScore(){
		List<ScoreBean> listScores = getProblemScoreStages();
		if(listScores==null || listScores.isEmpty()) return null;
		return listScores.get(listScores.size()-1);
	}
	
	public ScoreBean getFinalDDXScore(){
		List<ScoreBean> listScores = getDDXScoreStages();
		if(listScores==null || listScores.isEmpty()) return null;
		return listScores.get(listScores.size()-1);
	}

	public ScoreBean getFinalTestScore(){
		List<ScoreBean> listScores = getTestScoreStages();
		if(listScores==null || listScores.isEmpty()) return null;
		return listScores.get(listScores.size()-1);
	}

	public ScoreBean getFinalMngScore(){
		List<ScoreBean> listScores = getMngScoreStages();
		if(listScores==null || listScores.isEmpty()) return null;
		return listScores.get(listScores.size()-1);
	}
	public List<PeerBean> getProblemPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_PROBLEM_LIST);
	}
	
	public List<ScoreBean> getDDXScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_DDX_LIST);
	}

	public List<PeerBean> getDDXPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_DDX_LIST);
	}
	
	public List<ScoreBean> getTestScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_TEST_LIST);
	}
	
	public List<PeerBean> getTestPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_TEST_LIST);
	}
	
	public List<ScoreBean> getMngScoreStages(){ 
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoresByType(ScoreBean.TYPE_MNG_LIST);
	}
	
	public List<PeerBean> getMngPeerStages(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_MNG_LIST);
	}
	public void levelOfEngagement(){
		//passive vs active user, non-user, regular users, glancers (<2min), interrupter 
		
	}
	
	public ScoreContainer getScoreContainer() {
		if(scoreContainer==null) scoreContainer = new ScoreContainer(patIllScriptId);
		return scoreContainer;
	}
	
	public ScoreBean getOverallScore(){
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoreByType(ScoreBean.TYPE_OVERALL_SCORE);
	}
	public long getUserId() {return userId;}
	public long getPatIllScriptId() {return patIllScriptId;}
	public String getVpId() {return vpId;}
	
	public int compareTo(Object o) {
		if(o instanceof LearningAnalyticsBean){
			LearningAnalyticsBean lab = (LearningAnalyticsBean) o;
			if(this.getPatIllScriptId() < lab.getPatIllScriptId()) return -1;
			if(this.getPatIllScriptId() > lab.getPatIllScriptId()) return 1;
			if(this.getPatIllScriptId() == lab.getPatIllScriptId()) return 0;			
		}		
		return 0;
	}
}
