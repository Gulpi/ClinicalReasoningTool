package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import beans.relation.Relation;

/**
 * This is a single score for a specific action in an patientIllnessScript, such as an added problem or the summary 
 * statement. 
 * @author ingahege
 *
 */
@ManagedBean(name = "score", eager = true)
@SessionScoped
public class ScoreBean extends Beans implements Serializable{

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
	
	public static final int TIME_OK = 0;
	public static final int TIME_LATE = 1;
	public static final int TIME_EARLY = 2;
	
	private long id; 
	private long patIllScriptId; 
	private float scoreBasedOnExp = -1;
	/**
	 * percentage of other users who have chosen this item, or average score of peers....
	 */
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
	private int stage = -1;
	
	public ScoreBean(){}
	public ScoreBean(long patIllId, long scoredItem, int type, int stage){
		this.patIllScriptId = patIllId;
		this.scoredItem = scoredItem;
		//this.score = score;
		this.type = type;
		this.stage = stage;
		
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
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	
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
			if(this.type==((ScoreBean) o).getType() && this.scoredItem==((ScoreBean) o).getScoredItem() && this.stage == ((ScoreBean) o).getStage()) return true;
		}
		return false;
	}
	
	public boolean isListScoreBean(){
		if(type==TYPE_PROBLEM_LIST  || type == TYPE_DDX_LIST || type==TYPE_MNG_LIST || type==TYPE_TEST_LIST || type==TYPE_EPI_LIST) return true;
		return false;
	}
	
}
