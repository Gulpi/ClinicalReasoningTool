package beans.scoring;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import actions.scoringActions.ScoringOverallAction;
import application.AppBean;
import beans.CRTFacesContext;
import beans.scripts.PatIllScriptContainer;
import controller.NavigationController;
import database.DBScoring;
import util.CRTLogger;

/**
 * This is a container of learningAnalytic objects for all scripts of a user 
 * Here we analyze the development over time and scripts....
 * We could do all kinds of analytics, look for problems, ddx the user misses often, whether he is stuck at a certain point
 * (e.g. always low scores for ddx category),...
 * @author ingahege
 */
@ManagedBean(name = "analytics", eager = true)
@SessionScoped
public class LearningAnalyticsContainer implements Serializable{
	
	/**
	 * key = patIllScriptId
	 */
	private Map<Long,LearningAnalyticsBean> analytics = new HashMap<Long,LearningAnalyticsBean>(); 
	private long userId; 
	
	//public LearningAnalyticsContainer(){}
	public LearningAnalyticsContainer(long userId){
		this.userId = userId;
		initLearningAnalyticsContainer();
	}
	
	/**
	 * select all ScoreBeans of the user and add these to LearningAnalyticsBean objects (in the ScoreContainers)
	 */
	private void initLearningAnalyticsContainer(){
		List<ScoreBean> scores = new DBScoring().selectScoreBeansByUserId(userId);
		if(scores==null) return;
		Iterator<ScoreBean> it = scores.iterator();
		while(it.hasNext()){
			ScoreBean score = it.next();
			LearningAnalyticsBean lab =  getLearningAnalyticsBeanByPatIllScriptId(score.getPatIllScriptId(), score.getParentId());
			if(lab!=null) lab.getScoreContainer().addScore(score);
			else{
				CRTLogger.out("", CRTLogger.LEVEL_ERROR);
			}
		}
		
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
	
	

	public List<ScoreBean> getProblemScores(){ return getListScores(ScoreBean.TYPE_PROBLEM_LIST); }
	public List<ScoreBean> getDDXScores(){ 
		//TODO: we have to combine it with the final diagnosis score!
		return getListScores(ScoreBean.TYPE_DDX_LIST);
	}
	public List<ScoreBean> getTestScores(){ return getListScores(ScoreBean.TYPE_TEST_LIST);}
	public List<ScoreBean> getMngScores(){ return getListScores(ScoreBean.TYPE_MNG_LIST);}
	/**
	 * Get a combined score of summary statement similarity and use of semantic qualifier
	 * @return
	 */
	public List<ScoreBean> getSumScores(){
		if(analytics==null) return null; 
		List<ScoreBean> l = new ArrayList<ScoreBean>();
		Iterator<LearningAnalyticsBean> it = analytics.values().iterator();
		while(it.hasNext()){
			LearningAnalyticsBean laBean = it.next();
			if(laBean!=null){
				//can only be one, but for security reasons this method returns a list...
				ScoreBean score = laBean.getSummStScore();
				if(score!=null) l.add(score);
			}
		}
		return l;
	}
	
	/**
	 * returns all PeerBean problem list objects for the learner's scripts
	 * @return
	 */
	public List<PeerBean> getProblemPeerScores(){ return getPeerScores(ScoreBean.TYPE_PROBLEM_LIST);}
	public List<PeerBean> getDDXPeerScores(){ return getPeerScores(ScoreBean.TYPE_DDX_LIST);}
	public List<PeerBean> getTestPeerScores(){ return getPeerScores(ScoreBean.TYPE_TEST_LIST);}
	public List<PeerBean> getMngPeerScores(){ return getPeerScores(ScoreBean.TYPE_MNG_LIST);}
	public List<PeerBean> getSumPeerScores(){ return getPeerScores(ScoreBean.TYPE_SUMMST);}
	public List<PeerBean> getOverallPeerScores(){ return getPeerScores(ScoreBean.TYPE_OVERALL_SCORE);}
	
	private List<PeerBean> getPeerScores(int type){
		if(AppBean.getPeers()==null) return null;
		PatIllScriptContainer cont = new NavigationController().getCRTFacesContext().getScriptContainer();
		return AppBean.getPeers().getPeerBeansByAction(type, cont);
	}
	/**
	 * We take the list score of the last stage for every scripts of the learner
	 * @return list of ScoreBean objects
	 */
	private List<ScoreBean> getListScores(int type){
		if(analytics==null) return null; 
		List<ScoreBean> l = new ArrayList<ScoreBean>();
		Iterator<LearningAnalyticsBean> it = analytics.values().iterator();
		while(it.hasNext()){
			ScoreContainer scoreContainer = it.next().getScoreContainer();
			if(scoreContainer!=null){
				ScoreBean score = scoreContainer.getListScoreBeanOfLastStage(type);
				if(score!=null) l.add(score);
			}
		}
		return l;
	}
	
	public List<ScoreBean> getOverallScores(){
		Iterator<LearningAnalyticsBean> it = analytics.values().iterator();
		List<ScoreBean> l = new ArrayList<ScoreBean>();
		
		while(it.hasNext()){
			LearningAnalyticsBean lab = it.next();
			
			if(lab!=null){
				ScoreBean score = lab.getOverallScore();
				if(score==null){
					score = new ScoringOverallAction().scoreAction(lab);
				}
				if(score!=null) l.add(score);
			}
		}
		return l;
	}
}
