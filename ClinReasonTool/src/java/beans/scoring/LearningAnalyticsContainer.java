package beans.scoring;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import actions.scoringActions.ScoringOverallAction;
import actions.scoringActions.ScoringSummStAction;
import application.AppBean;
import beans.scripts.PatIllScriptContainer;
import beans.scripts.PatientIllnessScript;
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

	/**
	 * LearningBean objects of all patillscripts of the learner. We need these to determine the 
	 * tip to display in the current learningBean.
	 */
	//private List<LearningBean> learningBeans;
	//private LearningBean currLearningBean;
	
	//public LearningAnalyticsContainer(){}
	public LearningAnalyticsContainer(long userId){
		this.userId = userId;
		initLearningAnalyticsContainer();
	}
	
	public long getUserId() {return userId;}

	/**
	 * select all ScoreBeans of the user and add these to LearningAnalyticsBean objects (in the 
	 * ScoreContainers), also set all LearningBean objects
	 */
	private void initLearningAnalyticsContainer(){
		List<ScoreBean> scores = new DBScoring().selectScoreBeansByUserId(userId);
		//learningBeans =  new DBScoring().selectLearningBeansByUserId(userId);
		/*int counter = 0;
		if(lbs!=null){
			Iterator<LearningBean> it = lbs.iterator();
			while(it.hasNext()){
				LearningBean lb = it.next();
				if(counter<3)lastScriptIds.add(new Long(lb.getPatIllScriptId()));
				counter++;
				LearningAnalyticsBean lab =  getLearningAnalyticsBeanByPatIllScriptId(lb.getPatIllScriptId(), lb.getVpId());
				if(lab==null) lab = initLearningAnalyticsBean(lb.getPatIllScriptId(), lb.getVpId());
				lab.setLearningBean(lb);
			
			}
		}*/
		if(scores==null) return;
		Iterator<ScoreBean> it = scores.iterator();
		while(it.hasNext()){
			ScoreBean score = it.next();
			LearningAnalyticsBean lab =  getLearningAnalyticsBeanByPatIllScriptId(score.getPatIllScriptId(), score.getVpId());
			if(lab==null) lab = initLearningAnalyticsBean(score.getPatIllScriptId(), score.getVpId());
			if(lab!=null) lab.getScoreContainer().addScore(score);
			else{
				CRTLogger.out("", CRTLogger.LEVEL_ERROR);
			}
		}
		
	}
	
	public LearningAnalyticsBean getLearningAnalyticsBeanByPatIllScriptId(long patIllScriptId, String vpId){
		if(analytics==null || analytics.isEmpty()) return null;
		return analytics.get(new Long(patIllScriptId));
	}
	
	/**
	 * Init the LearningAnalyticsBean for this script, scores are loaded when creating the Bean.
	 * @param patIllScriptId
	 * @param parentId
	 */
	private LearningAnalyticsBean initLearningAnalyticsBean(long patIllScriptId, String vpId){
		LearningAnalyticsBean lab = new LearningAnalyticsBean(patIllScriptId, userId, vpId);
		if(analytics==null) initLearningAnalyticsContainer();
		analytics.put(new Long(patIllScriptId), lab);
		return lab;
	}
	
	public void addLearningAnalyticsBean(long patIllScriptId, String vpId){
		if(analytics.containsKey(new Long(patIllScriptId))) return;
		initLearningAnalyticsBean(patIllScriptId, vpId);
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
	
	public List<ScoreBean> getProblemScores(){ 
		return getListScores(ScoreBean.TYPE_PROBLEM_LIST); 
	}
	
	public List<ScoreBean> getVPProblemScores(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getProblemScoreStages();
	}
	public List<PeerBean> getVPProblemPeers(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getProblemPeerStages();
	}
	
	public List<PeerBean> getVPDDXPeers(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getDDXPeerStages();
	}
	public List<PeerBean> getVPTestPeers(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getTestPeerStages();
	}	
	public List<PeerBean> getVPMngPeers(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getMngPeerStages();
	}
	public List<ScoreBean> getVPDDXScores(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getDDXScoreStages();
	}
	
	public List<ScoreBean> getVPTestScores(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getTestScoreStages();
	}
	
	public List<ScoreBean> getVPMngScores(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getMngScoreStages();
	}
	public List<ScoreBean> getDDXScores(){ 
		//TODO: we have to combine it with the final diagnosis score!
		return getListScores(ScoreBean.TYPE_DDX_LIST);
	}
	
	/**
	 * overall score for a VP
	 * @return
	 */
	public ScoreBean getOverallScore(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		ScoreBean overallScore = labean.getOverallScore();
		//if(overallScore==null){ //then we calculate it based on the current stage
			//we update it in any case:
			overallScore = new ScoringOverallAction().scoreAction(labean);
		//}
		return overallScore;
	}
	
	public PeerBean getOverallPeerScore(){	
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		return labean.getOverallPeerScore();
	}
	
	public ScoreBean getSumScore(){ 		
		LearningAnalyticsBean labean = this.getLearningAnalyticsBeanByPatIllScript();
		if(labean==null) return null;
		ScoreBean sumStScore = labean.getLastSummStScore();
		if(sumStScore==null) 
			sumStScore = new ScoringSummStAction().scoreAction(labean);
		return sumStScore;
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
				ScoreBean score = laBean.getLastSummStScore();
				if(score!=null) l.add(score);
			}
		}
		return l;
	}
	
	/**
	 * returns all PeerBean problem list objects for the learner's scripts
	 * @return
	 */
	public List<PeerBean> getProblemPeerScores(){ return getPeerScoresLastStage(ScoreBean.TYPE_PROBLEM_LIST, getProblemScores());}
	public List<PeerBean> getDDXPeerScores(){ return getPeerScoresLastStage(ScoreBean.TYPE_DDX_LIST, getDDXScores());}
	public List<PeerBean> getTestPeerScores(){ return getPeerScoresLastStage(ScoreBean.TYPE_TEST_LIST, getTestScores());}
	public List<PeerBean> getMngPeerScores(){ return getPeerScoresLastStage(ScoreBean.TYPE_MNG_LIST, getMngScores());}
	public List<PeerBean> getSumPeerScores(){ return getPeerScoresLastStage(ScoreBean.TYPE_SUMMST, getSumScores());}
	
	public List<PeerBean> getOverallPeerScores(){ 
		if(AppBean.getPeers()==null) return null;
		//PatIllScriptContainer cont = NavigationController.getInstance().getCRTFacesContext().getScriptContainer();
		return AppBean.getPeers().getPeerBeansByAction(ScoreBean.TYPE_OVERALL_SCORE, getOverallScores());
		//return getPeerScores(ScoreBean.TYPE_OVERALL_SCORE);
	}
	
	private List<PeerBean> getPeerScoresLastStage(int type, List<ScoreBean> scores){
		if(AppBean.getPeers()==null) return null;
		//PatIllScriptContainer cont = new NavigationController().getCRTFacesContext().getScriptContainer();
		
		return AppBean.getPeers().getPeerBeansByActionLastStage(type, scores);
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
				if(score!=null && score.getScoreBasedOnExp()>=0) l.add(score);
			}
		}
		return l;
	}
	
	private LearningAnalyticsBean getLearningAnalyticsBeanByPatIllScript(){
		PatientIllnessScript patillscript = NavigationController.getInstance().getCRTFacesContext().getPatillscript();
		if(patillscript==null) return null;
		return getLearningAnalyticsBeanByPatIllScriptId(patillscript.getId(), patillscript.getVpId());
	}
	
	public LearningAnalyticsBean getLearningAnalyticsBean(){return getLearningAnalyticsBeanByPatIllScript();}
	
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
				if(score!=null &&score.getScoreBasedOnExp()>=0) l.add(score);
			}
		}
		return l;
	}
	
	/**
	 * We provide the VP names in a json string so that we can use the names as tooltips for the  charts.
	 * (more helpful for learner than the Vp ids...)
	 * @deprecated
	 * @return
	 */
	public String getVPNamesToJson(){
		StringBuffer sb = new StringBuffer("{");
		PatIllScriptContainer sc = NavigationController.getInstance().getCRTFacesContext().getScriptContainer();
		if(sc==null || sc.getScriptsOfUser()==null) return "";
		for(int i=0; i<sc.getScriptsOfUser().size(); i++){
			//sb.append("{\"id\":\""+sc.getScriptsOfUser().get(i).getVpId()+"\",");
			//sb.append("\"name\":\""+AppBean.getVPNameByParentId(sc.getScriptsOfUser().get(i).getVpId())+"\"},");
			sb.append("\""+sc.getScriptsOfUser().get(i).getVpId()+"\":\"");
			sb.append(AppBean.getVPNameByVPId(sc.getScriptsOfUser().get(i).getVpId())+"\",");
			
		}
		if(sb.length()>1) sb.replace(sb.length()-1, sb.length(), ""); //remove the last ","
		sb.append("}");
		return sb.toString();
	}
}
