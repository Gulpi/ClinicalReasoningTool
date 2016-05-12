package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import database.DBClinReason;

/**
 * At which stage on which tab (for which items) has the user seen/consulted feedback...This is 
 * important for the learning analytics (scoring?), so that we know whether a learner has performed an action based 
 * on feedback he already had (or on his own)
 * We create a FeedbackBean object every time the learner clicks on the feedback button or switches to another task (tab)
 * during feedbackOn status.
 * @author ingahege
 *
 */
public class FeedbackBean extends Beans implements Serializable{
	public static final int FEEDBACK_NONE = 0;
	public static final int FEEDBACK_EXP = 1;
	public static final int FEEDBACK_PEER = 2;
	public static final int FEEDBACK_EXP_PEER = 3;
	public static final int FEEDBACK_CHG = 4; //user changes an item based on what the expert entered
	/**
	 * an internal id...
	 */
	private long id;
	private int stage; 
	/**
	 * did the user see the feedback concerning problems, ddx, etc (see static definitions in Relation)
	 */
	private int itemType;
	private long patIllScriptId; 
	private int type; 
	/**
	 * is it on (true) or off (false)
	 */
	private boolean value; 
	private Timestamp creationDate;
	
	public FeedbackBean(){}
	public FeedbackBean(int stage, int type, int itemType, long patIllScriptId){
		this.stage = stage;
		this.type = type; 
		this.itemType = itemType;
		this.patIllScriptId = patIllScriptId;
	}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public int getItemType() {return itemType;}
	public void setItemType(int itemType) {this.itemType = itemType;}
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public boolean isValue() {return value;}
	public void setValue(boolean value) {this.value = value;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;} 
	
	public void save(){
		new DBClinReason().saveAndCommit(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof FeedbackBean){
			FeedbackBean fb = (FeedbackBean) o;
			if(fb.getId()==this.id) return true;
			if(fb.getStage()==this.stage && fb.getItemType()==this.itemType && fb.type==this.type && fb.getPatIllScriptId()==this.patIllScriptId) return true;
		}
		return false;
	}
			
}
