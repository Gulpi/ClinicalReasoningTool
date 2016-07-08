package actions.scoringActions;

import application.AppBean;
import beans.scripts.*;
import beans.relation.SummaryStatement;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;
import it.uniroma1.lcl.adw.*;
import it.uniroma1.lcl.adw.comparison.*;
import util.CRTLogger;

/**
 * We have multiple summary statement scores (one for each stage)...
 * @author ingahege
 *
 */
public class ScoringSummStAction {

	
	/**
	 * call when learner saves the summary statement? 
	 * call when learner enters the next stage.
	 * @param patIllScript
	 */
	public void scoreAction(PatientIllnessScript patIllScript, int stage){
		if(patIllScript.getType()==IllnessScriptInterface.TYPE_EXPERT_CREATED) return;
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_SUMMST, stage);
		if(scoreBean!=null) return;
		scoreBean = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_SUMMST, stage);
		if(expScript!=null && expScript.getSummSt()!=null) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript.getSummSt(), patIllScript.getSummSt());				
					
		//if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
		//calculateAddActionScoreBasedOnPeers(edge, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		calculateOverallScore(scoreBean); 
		new DBScoring().saveAndCommit(scoreBean);	
	}
	
	/**
	 * Either correct (1) or wrong (0)
	 * @param scoreBean
	 * @param courseOfTimeExp
	 * @param courseOfTimeLearner
	 */
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, SummaryStatement expSt, SummaryStatement learnerSt){
		if(learnerSt==null || learnerSt.getText()==null || learnerSt.getText().trim().equals(""))
			scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, false);
		else{
			//TODO....
		}
	}
	
	private void calculateOverallScore(ScoreBean scoreBean){
		scoreBean.setOverallScore(scoreBean.getScoreBasedOnExp());
	}
	
	/**
	 * Calculate seperately from any other comparison the appropriate use of semantic qualifiers in the 
	 * summary statement.
	 */
	public double calculateSimilarityADW(String studtext, String exptext){
		//Durning 2012: 1 point for correctly used term, -1 for a wrong term.
		ADW adw = new ADW();
		DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;
		//ItemType it = ItemType.SURFACE;

		double similarity = adw.getPairSimilarity("text1", "text2", disMethod,  new WeightedOverlap(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity weightedoverlap: " + similarity, CRTLogger.LEVEL_TEST);

		double similarity2 = adw.getPairSimilarity("text1", "text2", disMethod, new Cosine(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity Cosine: " + similarity2, CRTLogger.LEVEL_TEST);
		
		double similarity3 = adw.getPairSimilarity("text1", "text2", disMethod, new Jaccard(), LexicalItemType.SURFACE, LexicalItemType.SURFACE); 
		CRTLogger.out("similarity Jaccard: " + similarity3, CRTLogger.LEVEL_TEST);
		
		return similarity;
	}
	
	private void calculateSQUse(){
		
	}
}
