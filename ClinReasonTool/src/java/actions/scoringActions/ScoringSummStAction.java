package actions.scoringActions;

import application.AppBean;
import beans.scripts.*;
import beans.relation.SummaryStatement;
import beans.scoring.LearningAnalyticsBean;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;
//import it.uniroma1.lcl.adw.*;
//import it.uniroma1.lcl.adw.comparison.*;
import util.CRTLogger;

/**
 * We have multiple summary statement scores (one for each stage)...
 * @author ingahege
 *
 */
public class ScoringSummStAction {
	/**
	 * if the summary statement is shorter, we do not even consider to score it
	 */
	private static final int MIN_LENGTH_SUMST = 10;
	
	
	/**
	 * scoring triggered from chart page, if no summary statement has been created so far, it will 
	 * be created now (obviously with score=0, because otherwise it would have been scored before.)
	 * @param labean
	 * @return
	 */
	public ScoreBean scoreAction(LearningAnalyticsBean labean){
		PatientIllnessScript patillscript = NavigationController.getInstance().getCRTFacesContext().getPatillscript();
		if(patillscript!=null && patillscript.getId()==labean.getPatIllScriptId())
			return scoreAction(patillscript, patillscript.getCurrentStage());
		return null;
	}
	/**
	 * call when learner saves the summary statement? 
	 * call when learner enters the next stage.
	 * @param patIllScript
	 */
	public ScoreBean scoreAction(PatientIllnessScript patIllScript, int stage){
		if(patIllScript.isExpScript()) return null;
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		ScoreContainer scoreContainer = NavigationController.getInstance().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_SUMMST, stage);
		boolean isChg = true;
		//if(scoreBean!=null) return;
		if(scoreBean==null){
			scoreBean = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_SUMMST, stage);
			isChg = false;
		}
		if(expScript!=null && expScript.getSummSt()!=null){ //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript.getSummSt(), patIllScript.getSummSt(), isChg);				
		}
		//if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
		//calculateAddActionScoreBasedOnPeers(edge, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	
		return scoreBean;
	}
	
	/**
	 * Either correct (1) or wrong (0)
	 * @param scoreBean
	 * @param courseOfTimeExp
	 * @param courseOfTimeLearner
	 */
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, SummaryStatement expSt, SummaryStatement learnerSt, boolean isChg){
		if(learnerSt==null || learnerSt.getText()==null || learnerSt.getText().trim().equals(""))
			scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
		else{
			if(learnerSt.getText().length()>MIN_LENGTH_SUMST){
				scoreBean.setScoreBasedOnExp(ScoringController.SCORE_EXP_SAMEAS_LEARNER, isChg);
				//we need to do the scoring here, for now we just set the score to 1
			}
		}		
		if(expSt!=null && learnerSt!=null)
			scoreBean.setTiming(learnerSt.getStage(), expSt.getStage());

		ScoringController.getInstance().setFeedbackInfo(scoreBean, isChg, false);

	}

	
	/**
	 * Calculate seperately from any other comparison the appropriate use of semantic qualifiers in the 
	 * summary statement.
	 */
	public double calculateSimilarityADW(String studtext, String exptext){
		//Durning 2012: 1 point for correctly used term, -1 for a wrong term.
/*		ADW adw = new ADW();
		DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;
		//ItemType it = ItemType.SURFACE;

		double similarity = adw.getPairSimilarity("text1", "text2", disMethod,  new WeightedOverlap(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity weightedoverlap: " + similarity, CRTLogger.LEVEL_TEST);

		double similarity2 = adw.getPairSimilarity("text1", "text2", disMethod, new Cosine(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity Cosine: " + similarity2, CRTLogger.LEVEL_TEST);
		
		double similarity3 = adw.getPairSimilarity("text1", "text2", disMethod, new Jaccard(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity Jaccard: " + similarity3, CRTLogger.LEVEL_TEST);
		*/
		return 0; // similarity;
	}
	
	private void calculateSQUse(){
		
	}
}
