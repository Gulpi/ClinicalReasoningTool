package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import application.AppBean;
import beans.LogEntry;
import beans.scripts.*;
import controller.NavigationController;
import database.DBScoring;

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
	private LearningBean learningBean;
	
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
	
	public ScoreBean getLastSummStScore(){
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoreByType(ScoreBean.TYPE_SUMMST);
	}
	/**
	 * we get the list score for problems over all stages. 
	 * @return
	 */
	public ScoreBean getOverallProblemScore(){
		return getOverallListScores(getProblemScoreStages());
	}

	/**
	 * Returns -1 if no score has been calculated, 
	 * 0 = score is very low
	 * 1 = score is low
	 * 2 = score is medium
	 * 3 = score is high 
	 * 4 = score is very high 
	 * @TODO get thresholds from existing data in database  
	 * @return
	 */
	public int getOverallProblemScoreCat(){
		ScoreBean sb = getOverallListScores(getProblemScoreStages());
		if(sb==null) return -1;
		if(sb.getOrgScoreBasedOnExp()<=0.1) return 0;
		if(sb.getOrgScoreBasedOnExp()<=0.2) return 1;
		if(sb.getOrgScoreBasedOnExp()<=0.3) return 2;
		if(sb.getOrgScoreBasedOnExp()<=0.4) return 3;
		return 4;		
	}
	
	/**
	 * @return the overall problem score (all cards without expert consultation)
	 */
	public float getProblemScore(){
		ScoreBean sb = getOverallListScores(getProblemScoreStages());
		if(sb==null) return -1;
		return sb.getOrgScoreBasedOnExp();
	}
	
	public ScoreBean getOverallDDXScore(){
		return getOverallListScores(getDDXScoreStages());
		/*List<ScoreBean> listScores = getDDXScoreStages();
		if(listScores==null || listScores.isEmpty()) return null;
		return listScores.get(listScores.size()-1);*/
	}
	
	/**
	 * @return the overall ddx score (all cards without expert consultation)
	 */
	public float getDDXScore(){
		ScoreBean sb = getOverallListScores(getDDXScoreStages());
		if(sb==null) return -1;
		return sb.getOrgScoreBasedOnExp();
	}
	
	/**
	 * Returns -1 if no score has been calculated, 
	 * 0 = score is very low
	 * 1 = score is low
	 * 2 = score is medium
	 * 3 = score is high 
	 * 4 = score is very high 
	 * @TODO get thresholds from existing data in database  
	 * @return
	 */
	public int getOverallDDXScoreCat(){
		ScoreBean sb = getOverallListScores(getDDXScoreStages());
		if(sb==null) return -1;
		if(sb.getOrgScoreBasedOnExp()<=0.1) return 0;
		if(sb.getOrgScoreBasedOnExp()<=0.2) return 1;
		if(sb.getOrgScoreBasedOnExp()<=0.3) return 2;
		if(sb.getOrgScoreBasedOnExp()<=0.4) return 3;
		return 4;		
	}

	public ScoreBean getOverallTestScore(){
		return getOverallListScores(getTestScoreStages());
	}

	/**
	 * @return the overall test score (all cards without expert consultation)
	 */
	public float getTestScore(){
		ScoreBean sb = getOverallListScores(getTestScoreStages());
		if(sb==null) return -1;
		return sb.getOrgScoreBasedOnExp();
	}
	
	/**
	 * Returns -1 if no score has been calculated, 
	 * 0 = score is very low
	 * 1 = score is low
	 * 2 = score is medium
	 * 3 = score is high 
	 * 4 = score is very high 
	 * @TODO get thresholds from existing data in database  
	 * @return
	 */
	public int getOverallTestScoreCat(){
		ScoreBean sb = getOverallListScores(getTestScoreStages());
		if(sb==null) return -1;
		if(sb.getOrgScoreBasedOnExp()<=0.1) return 0;
		if(sb.getOrgScoreBasedOnExp()<=0.2) return 1;
		if(sb.getOrgScoreBasedOnExp()<=0.3) return 2;
		if(sb.getOrgScoreBasedOnExp()<=0.4) return 3;
		return 4;		
	}
	public ScoreBean getOverallMngScore(){
		return getOverallListScores(getMngScoreStages());
	}
	
	/**
	 * @return the overall test score (all cards without expert consultation)
	 */
	public float getMngScore(){
		ScoreBean sb = getOverallListScores(getMngScoreStages());
		if(sb==null) return -1;
		return sb.getOrgScoreBasedOnExp();
	}

	/**
	 * Returns -1 if no score has been calculated, 
	 * 0 = score is very low
	 * 1 = score is low
	 * 2 = score is medium
	 * 3 = score is high 
	 * 4 = score is very high 
	 * @TODO get thresholds from existing data in database  
	 * @return
	 */
	public int getOverallMngScoreCat(){
		ScoreBean sb = getOverallListScores(getMngScoreStages());
		if(sb==null) return -1;
		if(sb.getOrgScoreBasedOnExp()<=0.1) return 0;
		if(sb.getOrgScoreBasedOnExp()<=0.2) return 1;
		if(sb.getOrgScoreBasedOnExp()<=0.3) return 2;
		if(sb.getOrgScoreBasedOnExp()<=0.4) return 3;
		return 4;		
	}
	
	
	/**
	 * We go through the given list and calculate the overall score and orgscore by summing up all scores and dividing 
	 * it by the number of scores. This way someone who enters all items at the final stage does not get a score of 1, 
	 * but 1/number of stages. 
	 * @param listScores
	 * @return a new ScoreBean with the orgScore and score....
	 */
	private ScoreBean getOverallListScores(List<ScoreBean> listScores){
		if(listScores==null || listScores.isEmpty()) return null;
		Iterator<ScoreBean> it = listScores.iterator();
		float overallScoreCount = 0;
		float overallScoreOrgCount = 0;
		while(it.hasNext()){
			ScoreBean probScore = it.next();
			overallScoreCount += probScore.getScoreBasedOnExp();
			overallScoreOrgCount += probScore.getOrgScoreBasedOnExp();
		}
		ScoreBean result = new ScoreBean();
		float overallScore = overallScoreCount/(float)listScores.size();
		float overallScoreOrg = overallScoreOrgCount/(float) listScores.size();
		result.setScoreBasedOnExp(overallScore);
		result.setOrgScoreBasedOnExp(overallScoreOrg);
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
	/*** following four methods return the last peer score calculated for the given types ****/
	public PeerBean getProblemlistPeerScore()
	{
		return AppBean.getPeers().getPeerBeanOfLastStage(ScoreBean.TYPE_PROBLEM_LIST, getProblemPeerStages());
	}
	public PeerBean getDDXlistPeerScore(){return AppBean.getPeers().getPeerBeanOfLastStage(ScoreBean.TYPE_DDX_LIST, getDDXPeerStages());}
	public PeerBean getTestlistPeerScore(){return AppBean.getPeers().getPeerBeanOfLastStage(ScoreBean.TYPE_TEST_LIST, getTestPeerStages());}
	public PeerBean getMnglistPeerScore(){return AppBean.getPeers().getPeerBeanOfLastStage(ScoreBean.TYPE_MNG_LIST, getMngPeerStages());}
	
	public ScoreContainer getScoreContainer() {
		if(scoreContainer==null) scoreContainer = new ScoreContainer(patIllScriptId);
		return scoreContainer;
	}
	
	public ScoreBean getOverallScore(){
		if(scoreContainer==null) return null; 
		return scoreContainer.getScoreByType(ScoreBean.TYPE_OVERALL_SCORE);
	}
	
	public PeerBean getOverallPeerScore(){
		if(AppBean.getPeers()==null) return null;
		return AppBean.getPeers().getPeerBeanByVpIdActionAndStage(ScoreBean.TYPE_OVERALL_SCORE, -1, vpId);
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
	
	/**
	 * Try to select the learningBean from the database, if not there, we create a new one and save it.
	 * @return
	 */
	public LearningBean getLearningBean(){
		if(learningBean==null){
			PatientIllnessScript patillscript = NavigationController.getInstance().getMyFacesContext().getPatillscript();
			if(patillscript!=null && patillscript.getSubmitted()){
				learningBean = new DBScoring().selectLearningBeanByScriptId(patillscript.getId());
				if(learningBean==null) learningBean = new LearningBean(this);
			}			
		}
		return learningBean;
	}
	public void setLearningBean(LearningBean learningBean) {this.learningBean = learningBean;}
		
}
