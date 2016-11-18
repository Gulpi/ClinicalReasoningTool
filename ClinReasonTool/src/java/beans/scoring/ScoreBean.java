package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import application.AppBean;
import beans.relation.Relation;
import beans.scripts.PatIllScriptContainer;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;

/**
 * This is a single score for a specific action in an patientIllnessScript, such as an added problem or the summary 
 * statement. 
 * @author ingahege
 *
 */
@ManagedBean(name = "score", eager = true)
@SessionScoped
public class ScoreBean extends Beans implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_ADD_PROBLEM = Relation.TYPE_PROBLEM; //1
	public static final int TYPE_ADD_DDX = Relation.TYPE_DDX; //2
	public static final int TYPE_ADD_TEST = Relation.TYPE_TEST; //3
	public static final int TYPE_ADD_MNG = Relation.TYPE_MNG; //4
	public static final int TYPE_ADD_EPI = Relation.TYPE_EPI; //6
	public static final int TYPE_ADD_CNX = Relation.TYPE_CNX; //5
	
	public static final int TYPE_PROBLEM_LIST = 7;
	public static final int TYPE_DDX_LIST = 8;
	public static final int TYPE_TEST_LIST = 9;
	public static final int TYPE_MNG_LIST = 10;
	public static final int TYPE_FINAL_DDX = 11;
	public static final int TYPE_CNXS = 12;
	public static final int TYPE_COURSETIME = 13;
	public static final int TYPE_SUMMST = 14;
	public static final int TYPE_EPI_LIST = 15;
	public static final int TYPE_SCRIPT_CREATION = 16; //we use this for PeerBean creation 
	public static final int TYPE_OVERALL_SCORE = 17;
	public static final int TYPE_FINAL_DDX_LIST = 18;
	
	public static final int TIME_OK = 0;
	public static final int TIME_LATE = 1;
	public static final int TIME_EARLY = 2;
	
	public static final int SCORE_RANGE_LOW = 0;
	public static final int SCORE_RANGE_MEDIUM = 1;
	public static final int SCORE_RANGE_HIGH = 2;

	/**
	 * if a score is <0.3 the score is low
	 */
	public static final double SCORE_RANGE_LOW_PERC = 0.3; 
	/**
	 * if a score is between SCORE_RANGE_LOW_PERC and 0.7 the score is medium. above that the score is high
	 */
	public static final double SCORE_RANGE_MEDIUM_PERC = 0.7;
	
	
	private long id; 
	private long patIllScriptId; 
	/**
	 * @deprecated
	 **/
	private long parentId;
	private String vpId;
	private long userId;
	
	/**
	 * current score for this action, is constantly updated, when the user makes changes.
	 */
	private float scoreBasedOnExp = 0;
	/**
	 * We store the original score here, which is not changed no matter which changes the learner makes 
	 * the score is 0 if added via a joker or user has seen the experts solution before adding it.
	 */
	private float orgScoreBasedOnExp = 0;
	/**
	 * percentage of other users who have chosen this item, or average score of peers....
	 */
	private float scoreBasedOnPeer = 0;
	//private float scoreBasedOnIllScript = -1;
	/**
	 * We can calculate an overall score based on the components expert, peer, illScript,...
	 * @deprecated (we have more specific scores instead)
	 */
	private float overallScore;
	/**
	 * itemId which was scored (NOT relationId)
	 */
	private long scoredItem; 
	/**
	 *  //e.g. the problemRelationId, summStId
	 *  just in case we need it, we store this id here as well
	 */
	private long scoredRelId;
	private Timestamp creationDate; 
	/**
	 * Was the item added at the same stage (or later/earlier) as the expert has added it?
	 */
	private int timing = -1;
	
	/**
	 * has any type of feedback be seen before adding this item?
	 * 0= no, 1=expert, 2=peer, 3 = exp&peer
	 */
	private int feedbackOn;
	/**
	 * e.g. problem, ddx, but also problemList... see definitions above
	 */
	private int type; 
	private int weight = 1; //per default all items have the same weight.
	private int stage = -1;
	/**
	 * if learner has not chosen same item as expert, but we can map it, we store the expertListItem id here
	 */
	private long expItemId; 
	
	/**
	 * if an item is related to an expert item, we store the distance to it here. 
	 * (-) learner is less specific than expert
	 * (+) learner is more specific than expert
	 */
	private int distance = -99;
	
	private boolean deleteFlag = false;
	
	public ScoreBean(){}
	public ScoreBean(PatientIllnessScript patIllScript, long scoredItem, int type){
		this.patIllScriptId = patIllScript.getId();
		this.scoredItem = scoredItem;
		this.type = type;
		this.stage = patIllScript.getCurrentStage();
		//this.parentId = patIllScript.getParentId();
		this.userId = patIllScript.getUserId();	
		this.vpId = patIllScript.getVpId();
	}
	
	public ScoreBean(PatientIllnessScript patIllScript, long scoredItem, int type, int stage){
		this.patIllScriptId = patIllScript.getId();
		this.scoredItem = scoredItem;
		this.type = type;
		this.stage = stage;
		//this.parentId = patIllScript.getParentId();
		this.userId = patIllScript.getUserId();		
		this.vpId = patIllScript.getVpId();
	}

	public ScoreBean(LearningAnalyticsBean laBean, int type){
		this.patIllScriptId = laBean.getPatIllScriptId();
		this.type = type;
		this.vpId = laBean.getVpId();
		this.userId = laBean.getUserId();		
	}

	
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public float getScoreBasedOnExp() {return scoreBasedOnExp;}
	public void setScoreBasedOnExp(float scoreBasedOnExp) {this.scoreBasedOnExp = scoreBasedOnExp;}
	public void setScoreBasedOnExp(float scoreBasedOnExp, boolean isChg) {
		this.scoreBasedOnExp = scoreBasedOnExp;
		if(!isChg) this.orgScoreBasedOnExp = scoreBasedOnExp;
	}
	public float getScoreBasedOnPeer() {return scoreBasedOnPeer;}
	public void setScoreBasedOnPeer(float scoreBasedOnPeer) {
		this.scoreBasedOnPeer = scoreBasedOnPeer;
		//calculateOverallScore();
	}
	//public float getScoreBasedOnIllScript() {return scoreBasedOnIllScript;}
	//public void setScoreBasedOnIllScript(float scoreBasedOnIllScript) {this.scoreBasedOnIllScript = scoreBasedOnIllScript;}
	public long getScoredItem() {return scoredItem;}
	public void setScoredItem(long scoredItem) {this.scoredItem = scoredItem;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public int getWeight() {return weight;}
	public void setWeight(int weight) {this.weight = weight;}	
	
	/**
	 * @deprecated
	 */
	public float getOverallScore() {return overallScore;}
	/**
	 * @deprecated
	 */
	public void setOverallScore(float overallScore) {this.overallScore = overallScore;}	
	public int getTiming() {return timing;}
	public void setTiming(int timing) {this.timing = timing;}	
	public int getFeedbackOn() {return feedbackOn;}
	public void setFeedbackOn(int feedbackOn) {this.feedbackOn = feedbackOn;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public int getScoreBasedOnExpPerc() {return (int)(scoreBasedOnExp*100);}
	public int getOrgScoreBasedOnExpPerc() {return (int)(orgScoreBasedOnExp*100);}
	public int getDistance() {return distance;}
	public void setDistance(int distance) {this.distance = distance;}
	public long getExpItemId() {return expItemId;}
	public void setExpItemId(long expItemId) {this.expItemId = expItemId;}	
	public long getScoredRelId() {return scoredRelId;}
	public void setScoredRelId(long scoredRelId) {this.scoredRelId = scoredRelId;}	
	public float getOrgScoreBasedOnExp() {return orgScoreBasedOnExp;}
	public void setOrgScoreBasedOnExp(float orgScoreBasedOnExp) {
		this.orgScoreBasedOnExp = orgScoreBasedOnExp;
		//calculateOverallScore();
	}
	
	public boolean isDeleteFlag() {return deleteFlag;}
	public void setDeleteFlag(boolean deleteFlag) {this.deleteFlag = deleteFlag;}
	
	public void setTiming(int learnerStage, int expStage){
		if(learnerStage>expStage) setTiming(TIME_LATE);
		if(learnerStage==expStage) setTiming(TIME_OK);
		if(learnerStage<expStage) setTiming(TIME_EARLY);
	}
	
	public long getParentId() {return parentId;}
	public void setParentId(long parentId) {this.parentId = parentId;}	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof ScoreBean){
			ScoreBean sb = (ScoreBean) o;
			if(this.id == sb.getId()) return true;
			if(this.type==sb.getType() && this.scoredItem==sb.getScoredItem() && this.stage == sb.getStage() && this.userId == sb.getUserId()) return true;
		}
		return false;
	}
	
	public boolean isListScoreBean(){
		if(type==TYPE_PROBLEM_LIST  || type == TYPE_DDX_LIST || type==TYPE_MNG_LIST || type==TYPE_TEST_LIST || type==TYPE_EPI_LIST) return true;
		return false;
	}

	/** TODO we could consider all components and calculate based on these an overall score.
	 * For now we just take the expertsScore.
	 * @deprecated
	 * @param scoreBean
	 */
	private void calculateOverallScore(){
		setOverallScore(getScoreBasedOnExp());
	}
	
	public String getVpName(){
		return AppBean.getVPNameByVPId(vpId);
	}
	
	/**
	 * We return a category for the score, either high score, medium score, or low score.
	 * @return
	 */
	public int getScoreRange(){
		if(scoreBasedOnExp>=ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_HIGH;
		if(scoreBasedOnExp>=ScoreBean.SCORE_RANGE_LOW_PERC && scoreBasedOnExp<ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_MEDIUM;
		return ScoreBean.SCORE_RANGE_LOW;
	}

	/**
	 * We return a category for the org score, either high score, medium score, or low score.
	 * @return
	 */
	public int getOrgScoreRange(){
		if(orgScoreBasedOnExp>=ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_HIGH;
		if(orgScoreBasedOnExp>=ScoreBean.SCORE_RANGE_LOW_PERC && orgScoreBasedOnExp<ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_MEDIUM;
		return ScoreBean.SCORE_RANGE_LOW;
	}
	
}
