package beans.scoring;

import java.beans.Beans;
import java.sql.Timestamp;

/**
 * A PeerBean models the peers responses for a single action or scoring.
 * @author ingahege
 *
 */
public class PeerBean extends Beans{

	private long id; //internal id, to avoid composite id
	private long parentId;
	private int action; //see scoreBean definitions.
	private long itemId = -1;
	private Timestamp modificationDate;
	/**
	 * How many peers have selected this item or have been scored on a list action
	 */
	private int peerNum = 0;
	/**
	 * for all list scoring we store the sum of all scores of all peers here...
	 * (for average score divide by the peerNum!)
	 */
	private float scoreSum;
	
	/**
	 * Primarily needed for the list scoring actions which are based on each stage.
	 * We could also calculate an median stage when an item was added....
	 */
	private int stage; 
	
	public long getParentId() {return parentId;}
	public void setParentId(long parentId) {this.parentId = parentId;}
	public int getAction() {return action;}
	public void setAction(int action) {this.action = action;}
	public long getItemId() {return itemId;}
	public void setItemId(long itemId) {this.itemId = itemId;}
	public Timestamp getModificationDate() {return modificationDate;}
	public void setModificationDate(Timestamp modificationDate) {this.modificationDate = modificationDate;}
	public int getPeerNum() {return peerNum;}
	public void setPeerNum(int peerNum) {this.peerNum = peerNum;}		
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	public float getScoreSum() {return scoreSum;}
	public void setScoreSum(float scoreSum) {this.scoreSum = scoreSum;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	
	public PeerBean(){}
	public PeerBean(int action, long parentId, int peerNum, float scoreSum, int stage){
		this.action = action; 
		this.parentId = parentId;
		this.peerNum = peerNum;
		this.scoreSum = scoreSum;
	}
	
	public PeerBean(int action, long parentId, int peerNum, long itemId){
		this.action = action; 
		this.parentId = parentId;
		this.peerNum = peerNum;
		this.itemId = itemId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null && o instanceof PeerBean){
			PeerBean pb = (PeerBean)o;
			if(pb.getAction()==action && pb.getItemId()==itemId && pb.getParentId()==parentId) return true;
		}
		return false;
	}
	
	public void incrPeerNum(){ 
		peerNum++;
		modificationDate = new Timestamp(System.currentTimeMillis());
	}
	
	public void incrScoreSum(float score){
		if(score>=0) this.scoreSum += score;
		if(this.scoreSum<0) this.scoreSum = 0;
	}
	
	/**
	 * Average score of peers for this action (only useful for list scoring)
	 * @return
	 */
	public float getPeerPercentage(){
		if(peerNum<=0) return 0;
		return (float) scoreSum/peerNum;
	}

	/**
	 * Average score of peers for this action (only useful for list scoring)
	 * @return
	 */
	public int getPeerPercentagePerc(){
		if(peerNum<=0) return 0;
		return (int) (scoreSum/peerNum * 100);
	}
	/**
	 * How many peers have added this item in comparison to the overall number of peers who have created 
	 * an illnessScript
	 * @param overallNum
	 * @return
	 */
	public float getPeerPercentage(int overallNum){
		if(peerNum<=0 || overallNum<=0) return 0;
		return (float) peerNum/overallNum;
	}
}
