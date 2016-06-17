package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import beans.relation.Relation;
import beans.scripts.PatientIllnessScript;

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
	
	public static final int TIME_OK = 0;
	public static final int TIME_LATE = 1;
	public static final int TIME_EARLY = 2;
	
	private long id; 
	private long patIllScriptId; 
	private long parentId;
	private long userId;
	
	/**
	 * current score for this action, is constantly updated, when the user makes changes.
	 */
	private float scoreBasedOnExp = -1;
	/**
	 * We store the oroginal score here, which is not changed no matter which changes the learner makes 
	 * the score is 0 if added via a joker or user has seen the experts solution before adding it.
	 */
	private float orgScoreBasedOnExp = -1;
	/**
	 * percentage of other users who have chosen this item, or average score of peers....
	 */
	private float scoreBasedOnPeer = -1;
	private float scoreBasedOnIllScript = -1;
	/**
	 * We can calculate an overall score based on the components expert, peer, illScript,...
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
	
	public ScoreBean(){}
	public ScoreBean(PatientIllnessScript patIllScript, long scoredItem, int type){
		this.patIllScriptId = patIllScript.getId();
		this.scoredItem = scoredItem;
		this.type = type;
		this.stage = patIllScript.getCurrentStage();
		this.parentId = patIllScript.getParentId();
		this.userId = patIllScript.getUserId();		
	}
	
	public ScoreBean(PatientIllnessScript patIllScript, long scoredItem, int type, int stage){
		this.patIllScriptId = patIllScript.getId();
		this.scoredItem = scoredItem;
		this.type = type;
		this.stage = stage;
		this.parentId = patIllScript.getParentId();
		this.userId = patIllScript.getUserId();		
	}

	public ScoreBean(LearningAnalyticsBean laBean, int type){
		this.patIllScriptId = laBean.getPatIllScriptId();
		this.type = type;
		this.parentId = laBean.getParentId();
		this.userId = laBean.getUserId();		
	}
	
	public ScoreBean(long patIllScriptId, long parentId, long userId, int type){
		this.patIllScriptId = patIllScriptId;
		this.type = type;
		this.parentId = parentId;
		this.userId = userId;		
	}
	
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
	public void setScoreBasedOnPeer(float scoreBasedOnPeer) {this.scoreBasedOnPeer = scoreBasedOnPeer;}
	public float getScoreBasedOnIllScript() {return scoreBasedOnIllScript;}
	public void setScoreBasedOnIllScript(float scoreBasedOnIllScript) {this.scoreBasedOnIllScript = scoreBasedOnIllScript;}
	public long getScoredItem() {return scoredItem;}
	public void setScoredItem(long scoredItem) {this.scoredItem = scoredItem;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public int getWeight() {return weight;}
	public void setWeight(int weight) {this.weight = weight;}		
	public float getOverallScore() {return overallScore;}
	public void setOverallScore(float overallScore) {this.overallScore = overallScore;}	
	public int getTiming() {return timing;}
	public void setTiming(int timing) {this.timing = timing;}	
	public int getFeedbackOn() {return feedbackOn;}
	public void setFeedbackOn(int feedbackOn) {this.feedbackOn = feedbackOn;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public int getScoreBasedOnExpPerc() {return (int)(scoreBasedOnExp*100);}
	public int getOrgScoreBasedOnExpPerc() {return (int)(orgScoreBasedOnExp*100);}

	public long getExpItemId() {return expItemId;}
	public void setExpItemId(long expItemId) {this.expItemId = expItemId;}	
	public long getScoredRelId() {return scoredRelId;}
	public void setScoredRelId(long scoredRelId) {this.scoredRelId = scoredRelId;}	
	public float getOrgScoreBasedOnExp() {return orgScoreBasedOnExp;}
	public void setOrgScoreBasedOnExp(float orgScoreBasedOnExp) {this.orgScoreBasedOnExp = orgScoreBasedOnExp;}
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
			if(this.id == ((ScoreBean) o).getId()) return true;
			if(this.type==((ScoreBean) o).getType() && this.scoredItem==((ScoreBean) o).getScoredItem() && this.stage == ((ScoreBean) o).getStage()) return true;
		}
		return false;
	}
	
	public boolean isListScoreBean(){
		if(type==TYPE_PROBLEM_LIST  || type == TYPE_DDX_LIST || type==TYPE_MNG_LIST || type==TYPE_TEST_LIST || type==TYPE_EPI_LIST) return true;
		return false;
	}
	
}
