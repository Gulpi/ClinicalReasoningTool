package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.SessionScoped;

import beans.LogEntry;
import beans.scripts.ExpViewPatientIllnessScript;
import controller.NavigationController;
import database.DBClinReason;
import database.DBScoring;
import util.CRTLogger;

/**
 * Contains all elements we need for the different types of Feedback, such as the illnessScripts,
 * patientIllnessScript of the expert for this VP, or any peer stuff we want to display.
 * <b>Important:</b> The feedback buttons are set to false each time the user clicks to the next stage....
 * @author ingahege
 *
 */
@SessionScoped
public class FeedbackContainer implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * key= stage, 
	 * value= which feedback has been consulted for this stage
	 * TODO: this might be insufficient if we have more feedback types, then we have to change it to a map with arrays
	 * as values....
	 */
	private Map<Integer, List<FeedbackBean>> feedbackBeans;
	//private Map<Integer, Integer> peerFeedbackOn;
	private long patIllScriptId;
	private long userId;
	//private int currStage = 1;

	
	public FeedbackContainer(long patIllScriptId, long userId){
		this.patIllScriptId = patIllScriptId;
		//if()
		//stage = new NavigationController().getCRTFacesContext().getPatillscript().getCurrentStage();
	}

	/**
	 * Has the learner seen the expert's feedback/solution for this/these item(s) 
	 * @param itemType
	 * @return
	 */
	public boolean isExpFeedbackOn(int itemType, int currStage) {
		if(getFeedbackBean(FeedbackBean.FEEDBACK_EXP, currStage)==null) return false;
		return true;
	}
	

	public long getUserId() {return userId;}

	/**
	 * Has the learner seen the peer's solutions for this/these item(s) 
	 * @param itemType
	 * @return
	 */
	/*public boolean isPeerFeedbackOn(int itemType, int currStage) {
		if(getFeedbackBean(FeedbackBean.FEEDBACK_PEER, itemType, currStage)==null) return false;
		return true;
	}*/
	
	/*private FeedbackBean getFeedbackBean(int feedbackType, int itemType, int currStage){
		if(feedbackBeans==null) return null;
		List<FeedbackBean> beans = feedbackBeans.get(new Integer(currStage)); 
		if(beans==null || beans.isEmpty()) return null;
		for(int i=0; i<beans.size(); i++){
			FeedbackBean fb = beans.get(i);
			if(fb.getType()==feedbackType && fb.getItemType() == itemType) return fb;
		}
		return null;		
	}*/
	
	private FeedbackBean getFeedbackBean(int feedbackType, int currStage){
		if(feedbackBeans==null) return null;
		List<FeedbackBean> beans = feedbackBeans.get(new Integer(currStage)); 
		if(beans==null || beans.isEmpty()) return null;
		for(int i=0; i<beans.size(); i++){
			FeedbackBean fb = beans.get(i);
			if(fb.getType()==feedbackType) return fb;
		}
		return null;		
	}
	
	
	/*public void toogleExpFeedback(String toggle, String taskStr, int currStage){
		if(toggle==null) return;
		if(toggle.equals("0")) expFeedbackOff();
		else setExpFeedback(taskStr,currStage);
	}*/
	
	/**
	 * Learner has activated/deactivated the feedback within a box. This does not include the display of missing items.
	 * Currently we only make a log entry....
	 * @param toggle
	 * @param taskStr
	 * @param currStage
	 */
	public void toogleExpBoxFeedback(String toggle, String taskStr){
		if(toggle==null) return;
		notifyExpToggleLog(Integer.parseInt(toggle), Integer.parseInt(taskStr));
	}
	
	public void tooglePeerBoxFeedback(String toggle, String taskStr, int currStage){
		if(toggle==null) return;
		notifyPeerToggleLog(Integer.parseInt(toggle), Integer.parseInt(taskStr));
	}
	
	
	public void toogleExpFeedback(String toggleStr, int currStage){
		if(toggleStr==null) return;
		int toggle = Integer.parseInt(toggleStr);
		notifyExpToggleLog(toggle, -1);
		if(toggle ==0) return; //nothing to do
		
		FeedbackBean fb = getFeedbackBean(FeedbackBean.FEEDBACK_EXP, currStage);
		if(fb!=null) return; //already set
		fb = new FeedbackBean(currStage, FeedbackBean.FEEDBACK_EXP, patIllScriptId);
		if(addFeedbackBean(fb, currStage)) fb.save();
	}
	
	/**
	 * If no feedbackBean has been created for this task at this stage, we create one, save it, and store it in the 
	 * container.
	 * @param taskStr
	 */
	/*public void setExpFeedback(String taskStr, int currStage){
		if(taskStr==null) return;
		int task = Integer.parseInt(taskStr);
		FeedbackBean fb = getFeedbackBean(FeedbackBean.FEEDBACK_EXP, task, currStage);
		if(fb!=null) return; //already set
		if(task==5){ //concept map -> 1-4 are true
			for(int i=0; i<5; i++){
				fb = new FeedbackBean(currStage, FeedbackBean.FEEDBACK_EXP, i, patIllScriptId);
				
				if(addFeedbackBean(fb, currStage)) fb.save();
			}
		}
		else{ //Currently we do not have to do anything here for 1-4 because we do not display the missing items.
			fb = new FeedbackBean(currStage, FeedbackBean.FEEDBACK_EXP, task, patIllScriptId);
			//fb.save();
			if(addFeedbackBean(fb, currStage)) fb.save();
		}

	}*/
		
	private boolean addFeedbackBean(FeedbackBean fb, int currStage){
		if(feedbackBeans==null) feedbackBeans = new HashMap<Integer, List<FeedbackBean>>();
		if(feedbackBeans.get(new Integer(currStage))==null){
			List<FeedbackBean> l = new ArrayList<FeedbackBean>();
			l.add(fb);
			feedbackBeans.put(new Integer(currStage), l);
			return true;
		}
		else{
			List<FeedbackBean> l= feedbackBeans.get(new Integer(currStage));
			if(l.contains(fb)) return false;
			feedbackBeans.get(new Integer(currStage)).add(fb);
			return true;
		}
	}


	/**
	 * create and save a log entry for the feedback on/off action
	 * @param feedbackOn
	 */
	private void notifyExpToggleLog(int feedbackOn, int type){	
		int action = LogEntry.FEEDBACK_ON_ACTION;
		if(feedbackOn==0) action = LogEntry.FEEDBACK_OFF_ACTION;
		LogEntry log = new LogEntry(action , patIllScriptId, type);
		log.save();
	}
	
	/**
	 * create and save a log entry for the feedback on/off action
	 * @param feedbackOn
	 */
	private void notifyPeerToggleLog(int feedbackOn, int type){	
		int action = LogEntry.PEERFEEDBACK_ON_ACTION;
		if(feedbackOn==0) action = LogEntry.PEERFEEDBACK_OFF_ACTION;
		LogEntry log = new LogEntry(action , patIllScriptId, type);
		log.save();
	}
	
	public void initFeedbackContainer(){
		feedbackBeans = new DBScoring().selectFeedbackBeansByPatIllScriptId(this.patIllScriptId);
		//feedbackBeans = new DBClinReason().selectScoreBeansByPatIllScriptId(this.patIllScriptId);
		//CRTLogger.out("FeedbackContainer init done", CRTLogger.LEVEL_TEST);
	}
}
