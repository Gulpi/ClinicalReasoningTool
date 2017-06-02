package actions.scoringActions;

import application.AppBean;
import beans.scripts.*;
import beans.relation.SummaryStatement;
import beans.scoring.LearningAnalyticsBean;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import controller.SummaryStatementController;
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
	public static final int RUBRIC_SQ = 1;
	public static final int RUBRIC_ACCURACY = 2;
	public static final int RUBRIC_NARROWDDX = 3;
	public static final int RUBRIC_INFOTRAMSFORM = 4;
	public static final int RUBRIC_GLOBALRATE = 5;
	
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
		SummaryStatementController.checkForSemanticQualifiers(patIllScript.getSummSt());

		if(patIllScript.isExpScript()) return null;
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		ScoreContainer scoreContainer = NavigationController.getInstance().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_SUMMST, stage);
		boolean isChg = true;
		//if(scoreBean!=null) return;
		if(scoreBean==null){
			scoreBean = new ScoreBean(patIllScript, patIllScript.getSummStId(), ScoreBean.TYPE_SUMMST, stage);
			if(patIllScript.getSummSt()!=null) scoreBean.setDetails(patIllScript.getSummSt().getText());
			isChg = false;
		}
		//we analyze the text concerning semantic qualifiers:
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
			
			//not enough useful text is there:
			if(learnerSt.getText().length()<=MIN_LENGTH_SUMST){
				scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
			}
			
			//we have some text to analyze, but currently we only have the check for semantic qualifiers...
			else{
				int sqScore = calculateSemanticQualScore(expSt, learnerSt); //0, 1, or 2
				if(sqScore==2) scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE, isChg);
				//for the moment we give half score if we find a few or no sematic qualifiers:
				else scoreBean.setScoreBasedOnExp(ScoringController.HALF_SCORE, isChg);
			}
			
		}		
		if(expSt!=null && learnerSt!=null)
			scoreBean.setTiming(learnerSt.getStage(), expSt.getStage());

		ScoringController.getInstance().setFeedbackInfo(scoreBean, isChg, false);

	}
	
	/**
	 * score = 0, if no semantic qualifiers used or less than 30% of the expert
	 * score = 1, if more than 30% and less than 60% of the expert
	 * score = 2, if more than 60% of the expert
	 * @param expSt
	 * @param learnerSt
	 * @return
	 */
	private int calculateSemanticQualScore(SummaryStatement expSt, SummaryStatement learnerSt){
		if(learnerSt.getSqHits()==null || learnerSt.getSqHits().isEmpty()) return 0;
		if(expSt.getSqHits()==null || expSt.getSqHits().isEmpty()) return 2; //should not happen, would be a bad statement
		if(learnerSt.getSqHits().size() / expSt.getSqHits().size() >=0.6 ) return 2;
		if(learnerSt.getSqHits().size() / expSt.getSqHits().size() >=0.3 ) return 1;
		return 0;
		
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
	
	/*private void calculateSQUse(SummaryStatement stst){
		SummaryStatementController.checkForSemanticQualifiers(stst);
	}*/
}
