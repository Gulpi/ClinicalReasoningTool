package beans.scoring;

import java.sql.Timestamp;
import java.util.*;

import application.AppBean;
import beans.graph.Graph;
import beans.scripts.PatientIllnessScript;
import controller.GraphController;
import controller.NavigationController;
import database.DBScoring;
import properties.IntlConfiguration;
import util.CRTLogger;
import util.StringUtilities;

public class LearningBean {
	private static final int TIP_ACTIVE = 1; //learner should less often consult expert feedback
	private static final int TIP_NO = -1; //no tip displayed
	private static final int TIP_OK = 2; //learner did a good job, suggest more cases,...
	private static final int TIP_SUMMST = 3;
	//.... 
	private long id;
	private long patIllScriptId;
	private long userId;
	private String vpId;
	private boolean deleteFlag = false;
	private Timestamp creationDate = new Timestamp(System.currentTimeMillis());
	/**
	 * This is a value we calculate based on how many of the items the learner added by himself (without 
	 * checking the experts answer before.
	 * Later on we could also include the use of peer feedback instead of expert feedback
	 */
	private float activeLearning;
	/**
	 * How much scaffolding had to be displayed (same as continuousLearning?)
	 */
	private int scaffolding;
	
	/**
	 * We store the tip we display to the learner in the database, so we can vary the tips in future sessions.
	 */
	private int tip = -1;
	
	/**
	 * 1=no/bad summSt, 2 = good summSt, 3 = ....(we could be more precise with SQ)
	 */
	private int summSt;
	
	/**
	 * Where the items created during the scenario, or everything in the end....
	 */
	private int continuousLearning;
	
	public LearningBean(){}
	public LearningBean(LearningAnalyticsBean labean){
		initLearningBean(labean);
	}
	
	private void initLearningBean(LearningAnalyticsBean labean){
		this.patIllScriptId = labean.getPatIllScriptId();
		this.userId = labean.getUserId();
		this.vpId = labean.getVpId();
		calculateActiveLearning(labean);
		new DBScoring().saveAndCommit(this);
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public float getActiveLearning() {return activeLearning;}	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public int getScaffolding() {return scaffolding;}
	public void setScaffolding(int scaffolding) {this.scaffolding = scaffolding;}	
	public int getTip() {return tip;}
	public void setTip(int tip) {this.tip = tip;}	
	public int getSummSt() {return summSt;}
	public void setSummSt(int summSt) {this.summSt = summSt;}	
	public Timestamp getCreationDate() {return creationDate;}
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}	
	public boolean isDeleteFlag() {return deleteFlag;}
	public void setDeleteFlag(boolean deleteFlag) {this.deleteFlag = deleteFlag;}
	/**
	 * 2 high selfassessment, 1 medium, 0 low.
	 * @return
	 */
	public int getActiveLearningRange() {
		if(activeLearning>=ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_HIGH;
		if(activeLearning>=ScoreBean.SCORE_RANGE_LOW_PERC && activeLearning<ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_MEDIUM;
		return ScoreBean.SCORE_RANGE_LOW;
		}

	public void setActiveLearning(float activeLearning) {this.activeLearning = activeLearning;}
	
	/**
	 * We calculate the rate of items entered with/without feedbackOn
	 * 
	 */
	private void calculateActiveLearning(LearningAnalyticsBean labean){
		try{
			if(labean.getScoreContainer()==null || labean.getScoreContainer().getScores()==null){
				activeLearning = 0; //nothing entered
				return;
			}
		
			Iterator<ScoreBean> it = labean.getScoreContainer().getScores().iterator();
			int numWithFeedbackOn =0;
			while(it.hasNext()){
				if(it.next().getFeedbackOn()>0) numWithFeedbackOn++;
			}
			if(numWithFeedbackOn==0){
				this.activeLearning = 1;
				return;
			}
			this.activeLearning = labean.getScoreContainer().getScores().size()/numWithFeedbackOn;
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
		/*PatientIllnessScript patillscript = NavigationController.getInstance().getCRTFacesContext().getPatillscript();
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(labean.getVpId());
		Graph g = NavigationController.getInstance().getCRTFacesContext().getGraph();
		GraphController gc = new GraphController(g);
		g.*/
	}
	
	/**
	 * Based on the usage pattern we prioritize what we feedback to the learner and print one 
	 * tip on the chart page for the current VP. We also consider what we have displayed for the 
	 * recent VPs the learner has worked through. 
	 * @return
	 */
	public String printTip(){
		//TODO get last tips...
		if(activeLearning<ScoreBean.SCORE_RANGE_LOW){
			this.tip = TIP_ACTIVE;
		}
		if(this.summSt<ScoreBean.SCORE_RANGE_LOW){
			tip = TIP_SUMMST;
		}
		new DBScoring().saveAndCommit(this);

		return IntlConfiguration.getValue("charts.tip."+tip);

		
		
		
	}
	
	
}
