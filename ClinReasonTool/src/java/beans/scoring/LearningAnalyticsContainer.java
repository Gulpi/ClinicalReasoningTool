package beans.scoring;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import actions.scoringActions.ScoringOverallAction;
import application.AppBean;
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
	public static final int CATEGORY_OVERALL = 16;
	
	private static final long serialVersionUID = 1L;
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
	
	/**
	 * overall scores of the learner for all VPs she/he has worked on so far. For calculation algorithm see 
	 * ScoringOverallAction
	 * @return
	 */
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
