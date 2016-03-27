package beans;

import java.beans.Beans;
import java.sql.Timestamp;

/**
 * This is a single score for a specific action in an patientIllnessScript, such as an added problem or the summary 
 * statement. 
 * @author ingahege
 *
 */
public class ScoreBean extends Beans{

	private long id; 
	private long patIllnessScriptId; 
	private float score;
	private long scoredItem; //e.g. the problemRelationId, summStId
	private Timestamp creationDate; 
	private int itemType; //e.g. problem, ddx,...
	private int weight = 1; //per default all items have the same weight.
	
	public ScoreBean(){}
	public ScoreBean(long patIllId, long scoredItem, float score, int type){
		this.patIllnessScriptId = patIllId;
		this.scoredItem = scoredItem;
		this.score = score;
		this.itemType = type;
		
	}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getPatIllnessScriptId() {return patIllnessScriptId;}
	public void setPatIllnessScriptId(long patIllnessScriptId) {this.patIllnessScriptId = patIllnessScriptId;}
	public float getScore() {return score;}
	public void setScore(float score) {this.score = score;}
	public long getScoredItem() {return scoredItem;}
	public void setScoredItem(long scoredItem) {this.scoredItem = scoredItem;}
	public int getItemType() {return itemType;}
	public void setItemType(int itemType) {this.itemType = itemType;}
	public int getWeight() {return weight;}
	public void setWeight(int weight) {this.weight = weight;}	
	
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
