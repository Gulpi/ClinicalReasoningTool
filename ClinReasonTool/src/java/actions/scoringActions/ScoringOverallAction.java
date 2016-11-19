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
	
/*	public void scoreAction(PatientIllnessScript patIllScript, int stage){
		if(patIllScript.isExpScript()) return;
		LearningAnalyticsBean la_bean = new NavigationController().getCRTFacesContext().getLearningAnalytics();		

		scoreAction(la_bean);
	}*/
	
	/**
	 * We call this method if for some reason there is no overallScore for the given VP/script. 
	 * we call it also if it has been calculated, to make sure that we have considered the latest stage.
	 * @param la_bean
	 */
	public ScoreBean scoreAction(LearningAnalyticsBean la_bean){
		if(la_bean==null || la_bean.getScoreContainer()==null) return null;
		ScoreBean scoreBean = la_bean.getOverallScore();
		//if the scoring has been done at the end of the VP, we do not do it again.
		//if(scoreBean.getStage>= )
		if(scoreBean==null){ //return scoreBean; //overall score already calculated
			scoreBean = new ScoreBean(la_bean, ScoreBean.TYPE_OVERALL_SCORE);
			la_bean.getScoreContainer().addScore(scoreBean);
		}
		calculateScore(la_bean, scoreBean);
		//scoreBean.setScoreBasedOnExp(score, false);
		//la_bean.getScoreContainer().addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	
		return scoreBean;
	}
	
	
	/**
	 * Based on the weights defined above we calculate an overall score for this VP.
	 * @param laBean
	 * @return
	 */
	private void calculateScore(LearningAnalyticsBean laBean, ScoreBean overallScore){
		ScoreBean scoreProbBean = laBean.getOverallProblemScore();
		ScoreBean scoreDDXBean = laBean.getOverallDDXScore();
		ScoreBean scoreTestBean = laBean.getOverallTestScore();
		ScoreBean scoreMngBean = laBean.getOverallMngScore();
		ScoreBean scoreSummStBean = laBean.getLastSummStScore();
		calculateScore(scoreProbBean, scoreDDXBean, scoreTestBean, scoreMngBean, scoreSummStBean, overallScore);

	}
	
	/**
	 * Based on the weights defined above we calculate an overall score for this VP.
	 * @param laBean
	 * @return
	 */
	private void calculateScore(ScoreBean scoreProbBean, ScoreBean scoreDDXBean, ScoreBean scoreTestBean, ScoreBean scoreMngBean, ScoreBean scoreSummStBean, ScoreBean overallScore){
		float scoreOrgProb = 0; 
		float scoreOrgDDX = 0;
		float scoreOrgTest = 0;
		float scoreOrgMng = 0;
		float scoreOrgSummSt = 0;
		float scoreProb= 0; 
		float scoreDDX = 0;
		float scoreTest = 0;
		float scoreMng = 0;
		float scoreSummSt = 0;
		if(scoreProbBean!=null){
			scoreOrgProb = scoreProbBean.getOrgScoreBasedOnExp();
			scoreProb = scoreProbBean.getScoreBasedOnExp();
		}
		if(scoreDDXBean!=null){
			scoreOrgDDX = scoreDDXBean.getOrgScoreBasedOnExp();
			scoreDDX = scoreDDXBean.getScoreBasedOnExp();
		}
		if(scoreTestBean!=null){
			scoreOrgTest = scoreTestBean.getOrgScoreBasedOnExp();
			scoreTest = scoreTestBean.getScoreBasedOnExp();
		}
		if(scoreMngBean!=null){
			scoreOrgMng = scoreMngBean.getOrgScoreBasedOnExp();
			scoreMng = scoreMngBean.getScoreBasedOnExp();
		}
		if(scoreSummStBean!=null){
			scoreOrgSummSt = scoreSummStBean.getOrgScoreBasedOnExp();
			scoreSummSt = scoreSummStBean.getScoreBasedOnExp();
		}
		
		float finalOrgScore = (scoreOrgProb + scoreOrgDDX  + scoreOrgTest  + scoreOrgMng  + scoreOrgSummSt ) /5;
		float finalScore = (scoreProb  + scoreDDX  + scoreTest  + scoreMng  + scoreSummSt ) /5;

		//float finalOrgScore = (scoreOrgProb * WEIGHT_PROB + scoreOrgDDX * WEIGHT_DDX + scoreOrgTest * WEIGHT_TESTS + scoreOrgMng * WEIGHT_MNG + scoreOrgSummSt * WEIGHT_SUMMST) /5;
		//float finalScore = (scoreProb * WEIGHT_PROB + scoreDDX * WEIGHT_DDX + scoreTest * WEIGHT_TESTS + scoreMng * WEIGHT_MNG + scoreSummSt * WEIGHT_SUMMST) /5;

		overallScore.setOrgScoreBasedOnExp(finalOrgScore);
		overallScore.setScoreBasedOnExp(finalScore);
		//return finalScore;
	}
}
