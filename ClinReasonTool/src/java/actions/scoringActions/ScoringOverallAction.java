package actions.scoringActions;

import java.util.*;

import application.AppBean;
import beans.scoring.LearningAnalyticsBean;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import beans.scripts.IllnessScriptInterface;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;
import database.DBScoring;

/**
 * We score the overall performance for a VP with weighting based on Durning et al (2012).
 * @author ingahege
 *
 */
public class ScoringOverallAction {

	private static final int WEIGHT_PROB = 3;
	private static final int WEIGHT_DDX = 3;
	private static final int WEIGHT_SUMMST = 1;
	private static final int WEIGHT_FINALDDX = 1;
	private static final int WEIGHT_TESTS = 2; //Durning: "supporting data"
	private static final int WEIGHT_MNG = 3; //not covered by Durning
	
	public void scoreAction(PatientIllnessScript patIllScript, int stage){
		if(patIllScript.isExpScript()) return;
		LearningAnalyticsBean la_bean = new NavigationController().getCRTFacesContext().getLearningAnalytics();		
		/*if(la_bean==null) return;
		ScoreBean scoreBean = la_bean.getOverallScore();
		if(scoreBean!=null) return; //overall score already calculated
		scoreBean = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_OVERALL_SCORE, stage);
		float score = calculateScore(la_bean);
		scoreBean.setScoreBasedOnExp(score, false);
		la_bean.getScoreContainer().addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	*/
		scoreAction(la_bean);
	}
	
	/**
	 * We call this method if for some reason there is no overallScore for the given VP/script. 
	 * @param la_bean
	 */
	public ScoreBean scoreAction(LearningAnalyticsBean la_bean){
		if(la_bean==null || la_bean.getScoreContainer()==null) return null;
		ScoreBean scoreBean = la_bean.getOverallScore();
		if(scoreBean!=null) return scoreBean; //overall score already calculated

		scoreBean = new ScoreBean(la_bean, ScoreBean.TYPE_OVERALL_SCORE);
		float score = calculateScore(la_bean);
		scoreBean.setScoreBasedOnExp(score, false);
		la_bean.getScoreContainer().addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	
		return scoreBean;
	}
	
	/**
	 * We call this method if for some reason there is no overallScore for the given VP/script. 
	 * @param la_bean
	 */
	/*public ScoreBean scoreAction(ScoreContainer sc, long parentId, long userId, long patIllScriptId){
		ScoreBean scoreBean = new ScoreBean(patIllScriptId, parentId, userId, ScoreBean.TYPE_OVERALL_SCORE);
		float score = calculateScore(sc);
		scoreBean.setScoreBasedOnExp(score);
		new DBScoring().saveAndCommit(scoreBean);	
		return scoreBean;
	}*/
	
	/**
	 * Based on the weights defined above we calculate an overall score for this VP.
	 * @param laBean
	 * @return
	 */
	/*private float calculateScore(ScoreContainer sc){
		ScoreBean scoreProbBean = sc.getFinalProblemScore();
		ScoreBean scoreDDXBean = laBean.getFinalDDXScore();
		ScoreBean scoreTestBean = laBean.getFinalTestScore();
		ScoreBean scoreMngBean = laBean.getFinalMngScore();
		ScoreBean scoreSummStBean = laBean.getSummStScore();
		return calculateScore(scoreProbBean, scoreDDXBean, scoreTestBean, scoreMngBean, scoreSummStBean);

	}*/
	
	/**
	 * Based on the weights defined above we calculate an overall score for this VP.
	 * @param laBean
	 * @return
	 */
	private float calculateScore(LearningAnalyticsBean laBean){
		ScoreBean scoreProbBean = laBean.getFinalProblemScore();
		ScoreBean scoreDDXBean = laBean.getFinalDDXScore();
		ScoreBean scoreTestBean = laBean.getFinalTestScore();
		ScoreBean scoreMngBean = laBean.getFinalMngScore();
		ScoreBean scoreSummStBean = laBean.getSummStScore();
		return calculateScore(scoreProbBean, scoreDDXBean, scoreTestBean, scoreMngBean, scoreSummStBean);

	}
	
	/**
	 * Based on the weights defined above we calculate an overall score for this VP.
	 * @param laBean
	 * @return
	 */
	private float calculateScore(ScoreBean scoreProbBean, ScoreBean scoreDDXBean, ScoreBean scoreTestBean, ScoreBean scoreMngBean, ScoreBean scoreSummStBean){
		float scoreProb = 0; 
		float scoreDDX = 0;
		float scoreTest = 0;
		float scoreMng = 0;
		float scoreSummSt = 0;
		if(scoreProbBean!=null) scoreProb = scoreProbBean.getOrgScoreBasedOnExp();
		if(scoreDDXBean!=null) scoreDDX = scoreDDXBean.getOrgScoreBasedOnExp();
		if(scoreTestBean!=null) scoreTest = scoreTestBean.getOrgScoreBasedOnExp();
		if(scoreMngBean!=null) scoreMng = scoreMngBean.getOrgScoreBasedOnExp();
		if(scoreSummStBean!=null) scoreSummSt = scoreSummStBean.getOrgScoreBasedOnExp();
		
		float finalScore = (scoreProb * WEIGHT_PROB + scoreDDX * WEIGHT_DDX + scoreTest * WEIGHT_TESTS + scoreMng * WEIGHT_MNG + scoreSummSt * WEIGHT_SUMMST) /5;
		return finalScore;
	}
}
