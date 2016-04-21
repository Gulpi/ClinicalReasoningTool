package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.SessionScoped;

import beans.LogEntry;
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
	private int currStage = new NavigationController().getCRTFacesContext().getPatillscript().getCurrentStage();

	
	public FeedbackContainer(long patIllScriptId){
		this.patIllScriptId = patIllScriptId;
	}

	/**
	 * Has the learner seen the expert's feedback/solution for this/these item(s) 
	 * @param itemType
	 * @return
	 */
	public boolean isExpFeedbackOn(int itemType) {
		if(getFeedbackBean(FeedbackBean.FEEDBACK_EXP, itemType)==null) return false;
		return true;
	}

	/**
	 * Has the learner seen the peer's solutions for this/these item(s) 
	 * @param itemType
	 * @return
	 */
	public boolean isPeerFeedbackOn(int itemType) {
		if(getFeedbackBean(FeedbackBean.FEEDBACK_PEER, itemType)==null) return false;
		return true;
	}
	
	private FeedbackBean getFeedbackBean(int feedbackType, int itemType){
		if(feedbackBeans==null) return null;
		List<FeedbackBean> beans = feedbackBeans.get(new Integer(currStage)); 
		if(beans==null || beans.isEmpty()) return null;
		for(int i=0; i<beans.size(); i++){
			FeedbackBean fb = beans.get(i);
			if(fb.getType()==feedbackType && fb.getItemType() == itemType) return fb;
		}
		return null;		
	}
	
	
	public void toogleExpFeedback(String toggle, String taskStr){
		if(toggle==null) return;
		if(toggle.equals("0")) expFeedbackOff();
		else setExpFeedback(taskStr);
	}
	
	/**
	 * If no feedbackBean has been created for this task at this stage, we create on, save it, and store it in the 
	 * container.
	 * @param taskStr
	 */
	public void setExpFeedback(String taskStr){
		if(taskStr==null) return;
		int task = Integer.parseInt(taskStr);
		FeedbackBean fb = getFeedbackBean(FeedbackBean.FEEDBACK_EXP, task);
		if(fb!=null) return; //already set
		if(task==5){ //concept map -> 1-4 are true
			for(int i=0; i<5; i++){
				fb = new FeedbackBean(currStage, FeedbackBean.FEEDBACK_EXP, i, patIllScriptId);
				
				if(addFeedbackBean(fb)) fb.save();
			}
		}
		else{
			fb = new FeedbackBean(currStage, FeedbackBean.FEEDBACK_EXP, task, patIllScriptId);
			//fb.save();
			if(addFeedbackBean(fb)) fb.save();
		}

	}
		
	private boolean addFeedbackBean(FeedbackBean fb){
		if(feedbackBeans==null) feedbackBeans = new HashMap<Integer, List<FeedbackBean>>();
		if(feedbackBeans.get(new Integer(currStage))==null){
			List<FeedbackBean> l = new ArrayList<FeedbackBean>();
			l.add(fb);
			feedbackBeans.put(new Integer(currStage), l);
			return true;
		}
		else{
			List l= feedbackBeans.get(new Integer(currStage));
			if(l.contains(fb)) return false;
			feedbackBeans.get(new Integer(currStage)).add(fb);
			return true;
		}
	}

	
	/**
	 * Not much to do here, we do NOT toogle the values in the database, because learner has already seen the feedback,
	 * but we store this action in the log
	 */
	private void expFeedbackOff(){
		notifyLog(FeedbackBean.FEEDBACK_NONE);
	}

	/**
	 * create and save a log entry for the feedback on/off action
	 * @param feedbackOn
	 */
	private void notifyLog(int feedbackOn){	
		int action = LogEntry.FEEDBACK_ON_ACTION;
		if(feedbackOn==0) action = LogEntry.FEEDBACK_OFF_ACTION;
		LogEntry log = new LogEntry(action , patIllScriptId, (long) feedbackOn);
		log.save();
	}
	
	public void initFeedbackContainer(){
		feedbackBeans = new DBScoring().selectFeedbackBeansByPatIllScriptId(this.patIllScriptId);
		//feedbackBeans = new DBClinReason().selectScoreBeansByPatIllScriptId(this.patIllScriptId);
		CRTLogger.out("FeedbackContainer init done", CRTLogger.LEVEL_TEST);
	}
}
