package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import beans.relation.Relation;

/**
 * This is a single score for a specific action in an patientIllnessScript, such as an added problem or the summary 
 * statement. 
 * @author ingahege
 *
 */
public class ScoreBean extends Beans implements Serializable{

	public static final int TYPE_ADD_PROBLEM = Relation.TYPE_PROBLEM;
	public static final int TYPE_ADD_DDX = Relation.TYPE_DDX;
	public static final int TYPE_ADD_TEST = Relation.TYPE_TEST;
	public static final int TYPE_ADD_MNG = Relation.TYPE_MNG;
	public static final int TYPE_ADD_EPI = Relation.TYPE_EPI;
	
	public static final int TYPE_PROBLEM_LIST = 7;
	public static final int TYPE_DDX_LIST = 8;
	public static final int TYPE_TEST_LIST = 9;
	public static final int TYPE_MNG_LIST = 10;
	public static final int TYPE_FINAL_DDX = 11;
	public static final int TIME_OK = 0;
	public static final int TIME_LATE = 1;
	public static final int TIME_EARLY = 2;
	
	private long id; 
	private long patIllScriptId; 
	private float scoreBasedOnExp = -1;
	private float scoreBasedOnPeer = -1;
	private float scoreBasedOnIllScript = -1;
	/**
	 * We can calculate an overall score based on the components expert, peer, illScript,...
	 */
	private float overallScore;
	private long scoredItem; //e.g. the problemRelationId, summStId
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
	
	public ScoreBean(){}
	public ScoreBean(long patIllId, long scoredItem, int type){
		this.patIllScriptId = patIllId;
		this.scoredItem = scoredItem;
		//this.score = score;
		this.type = type;
		
	}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public float getScoreBasedOnExp() {return scoreBasedOnExp;}
	public void setScoreBasedOnExp(float scoreBasedOnExp) {this.scoreBasedOnExp = scoreBasedOnExp;}
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
	
	public void setTiming(int learnerStage, int expStage){
		if(learnerStage>expStage) setTiming(TIME_LATE);
		if(learnerStage==expStage) setTiming(TIME_OK);
		if(learnerStage<expStage) setTiming(TIME_EARLY);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof ScoreBean){
			if(this.id == ((ScoreBean) o).getId()) return true;
		}
		return false;
	}
	
}
