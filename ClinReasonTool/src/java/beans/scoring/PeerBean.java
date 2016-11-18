package beans.scoring;

import java.beans.Beans;
import java.sql.Timestamp;

import application.AppBean;
import properties.IntlConfiguration;

/**
 * A PeerBean models the peers responses for a single action or scoring.
 * @author ingahege
 *
 */
public class PeerBean extends Beans{

	private long id; //internal id, to avoid composite id
	/**
	 * @deprecated 
	 **/
	private long parentId;
	private String vpId;
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
	 * @deprecated
	 */
	private float overallScoreSum;
	
	/**
	 * average of org scores (without seeing feedback)
	 */
	private float orgScoreBasedOnExp;
	
	/**
	 * average of scores (no matter whether exp has been seen)
	 */
	private float scoreBasedOnExp; 
	/**
	 * Primarily needed for the list scoring actions which are based on each stage.
	 * We could also calculate an median stage when an item was added....
	 */
	private int stage; 
	/**
	 * @deprecated 
	 **/	
	public long getParentId() {return parentId;}
	/**
	 * @deprecated 
	 **/
	public void setParentId(long parentId) {this.parentId = parentId;}
	
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public int getAction() {return action;}
	public String getActionStr() {
		if(action<0) return "-";
		return IntlConfiguration.getValue("reports.action."+action);
	}
	public void setAction(int action) {this.action = action;}
	public long getItemId() {return itemId;}
	public void setItemId(long itemId) {this.itemId = itemId;}
	public Timestamp getModificationDate() {return modificationDate;}
	public void setModificationDate(Timestamp modificationDate) {this.modificationDate = modificationDate;}
	public int getPeerNum() {return peerNum;}
	public void setPeerNum(int peerNum) {this.peerNum = peerNum;}		
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	public float getOverallScoreSum() {return overallScoreSum;}
	public void setOverallScoreSum(float overallScoreSum) {this.overallScoreSum = overallScoreSum;}	
	public float getOrgScoreBasedOnExp() {return orgScoreBasedOnExp;}
	public void setOrgScoreBasedOnExp(float orgScoreBasedOnExp) {this.orgScoreBasedOnExp = orgScoreBasedOnExp;}
	public float getScoreBasedOnExp() {return scoreBasedOnExp;}
	public void setScoreBasedOnExp(float scoreBasedOnExp) {this.scoreBasedOnExp = scoreBasedOnExp;}	
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	
	public PeerBean(){}
	public PeerBean(int action, String vpId, int peerNum, /*float overallScoreSum,*/ int stage, float expScore, float orgExpScore){
		this.action = action; 
		//this.parentId = parentId;
		this.peerNum = peerNum;
		//this.overallScoreSum = overallScoreSum;
		this.vpId = vpId;
		this.orgScoreBasedOnExp = orgExpScore;
		this.scoreBasedOnExp = expScore;
		this.stage = stage;
	}
	
	public PeerBean(int action, String vpId, int peerNum, long itemId){
		this.action = action; 
		//this.parentId = parentId;
		this.peerNum = peerNum;
		this.itemId = itemId;
		this.vpId = vpId;
	}
	
	/**
	 * This is a constructor that creates a dummy peerBean (for charts). In rare cases for new 
	 * Vps, there might not be yet a peerBean....
	 * @param action
	 * @param vpId
	 */
	public PeerBean(int action, String vpId, int stage){
		this.action = action; 
		this.vpId = vpId;
		this.stage = stage;
		scoreBasedOnExp = 0;
		orgScoreBasedOnExp = 0;
		overallScoreSum = 0;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null && o instanceof PeerBean){
			PeerBean pb = (PeerBean)o;
			if(pb.getAction()==action && pb.getItemId()==itemId && pb.getVpId().equals(vpId)) return true;
		}
		return false;
	}
	
	public void incrPeerNum(){ 
		peerNum++;
		modificationDate = new Timestamp(System.currentTimeMillis());
	}
	
	public void incrScoreSums(float overallscore, float expScore, float orgExpScore){
		incrScoreSum(overallscore);
		incrExpScoreSum(expScore);
		incrOrgExpScoreSum(orgExpScore);
	}
	
	private void incrScoreSum(float score){
		if(score>=0) this.overallScoreSum += score;
		if(this.overallScoreSum<0) this.overallScoreSum = 0;
	}
	
	private void incrExpScoreSum(float score){
		if(score>=0) this.scoreBasedOnExp += score;
		if(this.scoreBasedOnExp<0) this.scoreBasedOnExp = 0;
	}
	
	private void incrOrgExpScoreSum(float score){
		if(score>=0) this.orgScoreBasedOnExp += score;
		if(this.orgScoreBasedOnExp<0) this.orgScoreBasedOnExp = 0;
	}
	
	/**
	 * Average score of peers for this action (only useful for list scoring)
	 * @return
	 */
	public float getPeerPercentage(){
		if(peerNum<=0) return 0;
		return (float) overallScoreSum/peerNum;
	}

	/**
	 * Average score of peers for this action (only useful for list scoring)
	 * @return
	 */
	public int getPeerPercentagePerc(){
		if(peerNum<=0) return 0;
		return (int) (overallScoreSum/peerNum * 100);
	}
	
	public int getExpPeerPercentagePerc(){
		if(peerNum<=0) return 0;
		int sum = (int) (this.scoreBasedOnExp/peerNum * 100);
		return (int) (this.scoreBasedOnExp/peerNum * 100);
	}
	
	public int getOrgExpPeerPercentagePerc(){
		if(peerNum<=0) return 0;
		return (int) (this.orgScoreBasedOnExp/peerNum * 100);
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
	
	public String getVpName(){
		return AppBean.getVPNameByVPId(vpId);
	}
}
