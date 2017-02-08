package beans.user;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Special settings for a VP session, e.g. different modes for feedback or scaffolding,...
 * Currently this needs to be provided by the parent VP system, otherwise default setting is used (e.g. expert feedback
 * always accessible)
 * @author ingahege
 *
 */
@ManagedBean(name = "sessSett", eager = true)
@SessionScoped
public class SessionSetting {
	public static final int EXPFEEDBACKMODE_END = 1; //show expert feedback only on last card
	public static final int EXPFEEDBACKMODE_NEVER = 2; //show no expert feedback at all
	
	private long id;
	/**
	 * When shall the expert script be displayed...
	 */
	private int expFeedbackMode = 0; //0 or -1 is default
	/**
	 * When shall the peer feedback be displayed...
	 */
	private int peerFeedbackMode = 0; //0 or -1 is default
	
	private String vpId;
	private long userId;
	/**
	 * if the hint for the expert feedback has been displayed once, we set this to true and do not display it again. 
	 * Not stored in the database....
	 */
	private boolean expHintDisplayed = false;
	
	public SessionSetting(){}
	
	public SessionSetting(String vpId, long userId){
		this.userId = userId;
		this.vpId = vpId;
	}
	
	public int getExpFeedbackMode() {return expFeedbackMode;}
	public void setExpFeedbackMode(int expFeedbackMode) {this.expFeedbackMode = expFeedbackMode;}
	public int getPeerFeedbackMode() {return peerFeedbackMode;}
	public void setPeerFeedbackMode(int peerFeedbackMode) {this.peerFeedbackMode = peerFeedbackMode;}
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	private boolean isExpHintDisplayed() {return expHintDisplayed;}
	private void setExpHintDisplayed(boolean hintDisplayed) {this.expHintDisplayed = hintDisplayed;}
	
	/**
	 * return true if exp feedback shall be accessible, otherwise false. Depends on current stage in session and 
	 * feedback mode.
	 * @param actStage
	 * @param maxStage
	 * @return
	 */
	public boolean displayExpFeedback(int actStage, int maxStage){
		if(expFeedbackMode == EXPFEEDBACKMODE_NEVER) return false; //never show exp feedback
		if(expFeedbackMode<=0) return true; //default setting, feedback always accessible
		if(expFeedbackMode==EXPFEEDBACKMODE_END){ //only show at end:
			if(actStage>=maxStage){
				return true;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Decide whether a hint to the expert feedback button shall be displayed or not. Display if feedback is only available 
	 * at the end of a session if learner is on last stage/card.
	 * @param actStage
	 * @param maxStage
	 * @return
	 */
	public boolean displayExpFeedbackHint(int actStage, int maxStage){
		if(isExpHintDisplayed()) return false; //hint has already been displayed before....
		if(expFeedbackMode == EXPFEEDBACKMODE_NEVER) return false;
		if(expFeedbackMode<=0) return false;
		if(expFeedbackMode==EXPFEEDBACKMODE_END && actStage==maxStage){
			setExpHintDisplayed(true);
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SessionSetting){
			SessionSetting sessSett = (SessionSetting) o;
			if(sessSett.getUserId()==this.userId && sessSett.getVpId().equals(this.vpId)) return true;
			if(sessSett.getId()==this.id) return true;
			return false;
		}
		return false;
	}
}
