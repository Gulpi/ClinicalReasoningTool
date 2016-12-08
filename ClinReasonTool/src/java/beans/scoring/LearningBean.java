package beans.scoring;

import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;
import database.DBScoring;
import properties.IntlConfiguration;
import util.*;
import java.util.Random;

/**
 * A LearningBean helps to analyze learner behavior within the current VP session and displays a tip based on 
 * the analysis. We store the LearningBean in the database, so we know which tip was displayed to the learner. 
 * @author ingahege
 *
 */
/**
 * @author ingahege
 *
 */
@ManagedBean(name = "learningBean", eager = true)
@RequestScoped
public class LearningBean {
	private static final int TIP_ACTIVE = 2; //learner should less often consult expert feedback
	private static final int TIP_NO = -1; //no tip displayed
	private static final int TIP_OK = 1; //learner did a good job, suggest more cases,...
	private static final int TIP_SUMMST = 3; //create a summary statement
	private static final int TIP_SCAFFOLD = 4; //work more continuously throughout the VP
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
	private float activeLearning = -1;
	/**
	 * How much scaffolding had to be displayed (same as continuousLearning?)
	 */
	private int scaffolding = -1;
	
	/**
	 * We store the tip we display to the learner in the database, so we can vary the tips in future sessions.
	 */
	private int tip = -1;
	
	/**
	 * 0=no/bad summSt, 1 = medium summSt, 2 = good summSt....(we could be more precise with SQ)
	 */
	private int summSt;
	
	/**
	 * 0-2 nased on the overall ScoreBean. We need this here to decide whether to display a "well done" message 
	 * or something else....
	 */
	private int overallScore;
	
	/**
	 * Where the items created during the scenario, or everything in the end....
	 */
	//private int continuousLearning;
	
	public LearningBean(){}
	public LearningBean(LearningAnalyticsBean labean){
		initLearningBean(labean);
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
	public int getOverallScore() {return overallScore;}
	public void setOverallScore(int overallScore) {this.overallScore = overallScore;}
	public void setActiveLearning(float activeLearning) {this.activeLearning = activeLearning;}
	
	/**
	 * calculate the parameters for displaying the tip.
	 * @param labean
	 */
	private void initLearningBean(LearningAnalyticsBean labean){
		this.patIllScriptId = labean.getPatIllScriptId();
		this.userId = labean.getUserId();
		this.vpId = labean.getVpId();
		calculateActiveLearning(labean);
		calculateSummSt(labean);
		setOverallScore(labean);
		//TODO get last tips...
		determineTip();
		new DBScoring().saveAndCommit(this);
		new DBScoring().saveAndCommit(this);
	}
	
	/**
	 * Based on the parameters in this LearningBean and previously displayed tips we determine the 
	 * current tip to be displayed. If we cannot find anything we display an encouraging message....
	 * 
	 */
	private void determineTip(){
		if(tip>=0) return;
		//for the moment we choose by random....
		List<Integer> potentialTips = new ArrayList<Integer>();
		//ok
		if(activeLearning>=0 && activeLearning<ScoreBean.SCORE_RANGE_LOW){
			potentialTips.add(new Integer(TIP_ACTIVE));
		}
		//ok
		if(this.summSt>=0 && this.summSt<=ScoreBean.SCORE_RANGE_LOW){
			potentialTips.add(new Integer(TIP_SUMMST));
		}
		//not yet active
		if(this.scaffolding>=0 && this.scaffolding<=ScoreBean.SCORE_RANGE_LOW){
			potentialTips.add(new Integer(TIP_SCAFFOLD));
		}
		
		//then everything seems to be ok...or we could not calculate -> for the moment we display nothing then
		if(potentialTips.isEmpty()){ 
			if(overallScore>=ScoreBean.SCORE_RANGE_HIGH){
				tip = TIP_OK; //learner was doing quite good
			}
			else 
				tip = TIP_NO; //not so well, we might later on display something here....
			
			return;
		}
		int i = new Random().nextInt(potentialTips.size());
		if(i>=0 && i<potentialTips.size()) tip = potentialTips.get(i);		
	}
	

	/**
	 * 2 high selfassessment, 1 medium, 0 low.
	 * @return
	 */
	public int getActiveLearningRange() {
		if(activeLearning>=ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_HIGH;
		if(activeLearning>=ScoreBean.SCORE_RANGE_LOW_PERC && activeLearning<ScoreBean.SCORE_RANGE_MEDIUM_PERC) return ScoreBean.SCORE_RANGE_MEDIUM;
		return ScoreBean.SCORE_RANGE_LOW;
	}

	/**
	 * get the overall score Range from the ScoreContainer....
	 * @param labean
	 */
	private void setOverallScore(LearningAnalyticsBean labean){
		try{
			if(labean.getScoreContainer()==null || labean.getScoreContainer().getScores()==null){
				overallScore = ScoreBean.SCORE_RANGE_LOW;
				return;
			}
			ScoreBean scoreBean = labean.getScoreContainer().getScoreByType(ScoreBean.TYPE_OVERALL_SCORE);
			if(scoreBean!=null){
				overallScore = scoreBean.getScoreRange();
			}
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	/**
	 * We calculate the rate of items entered with/without feedbackOn
	 * 
	 */
	private void calculateActiveLearning(LearningAnalyticsBean labean){
		try{
			if(labean.getScoreContainer()==null || labean.getScoreContainer().getScores()==null){
				activeLearning = ScoreBean.SCORE_RANGE_LOW; //nothing entered
				return;
			}
		
			Iterator<ScoreBean> it = labean.getScoreContainer().getScores().iterator();
			int itemsNum = 0;
			int numWithFeedbackOn =0;
			while(it.hasNext()){
				ScoreBean sb = it.next();
				if(sb.getFeedbackOn()>0 && sb.isAddItemScoreBean()) numWithFeedbackOn++;
				if (sb.isAddItemScoreBean()) itemsNum++;
			}
			if(numWithFeedbackOn==0){
				this.activeLearning = 1;
				return;
			}
			this.activeLearning = (float) numWithFeedbackOn/itemsNum;
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
	 * We check the summary statement score and if summary statement is insufficient, we set it to 0, so it
	 * might be displayed as a tip 
	 * @param labean
	 */
	private void calculateSummSt(LearningAnalyticsBean labean){
		try{
			ScoreBean sumStScore = labean.getLastSummStScore();
			if(sumStScore==null || sumStScore.getScoreBasedOnExp()<=ScoreBean.SCORE_RANGE_LOW_PERC){
				this.summSt = 0;
			}
			//TODO: more interpretation needs to be done, once we have more analysis of the summary statement:
			else this.summSt = sumStScore.getScoreRange();
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);

		}
	}
	
	/**
	 * Based on the usage pattern we prioritize what we feedback to the learner and print one 
	 * tip on the chart page for the current VP. We also consider what we have displayed for the 
	 * recent VPs the learner has worked through. 
	 * @return
	 */
	public String getPrintTip(){
		if(tip<0) determineTip();
		return IntlConfiguration.getValue("charts.tip."+tip);		
	}
	
	public boolean equals(Object o){
		if(o instanceof LearningBean){
			LearningBean lb = (LearningBean) o;
			if(lb.getPatIllScriptId()==this.patIllScriptId) return true;
		}
		return false;
	}
	
	
	
}
