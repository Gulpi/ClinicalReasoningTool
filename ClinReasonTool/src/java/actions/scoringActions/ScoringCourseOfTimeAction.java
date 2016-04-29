package actions.scoringActions;

import application.AppBean;
import beans.PatientIllnessScript;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;

public class ScoringCourseOfTimeAction {


	public void scoreAction(PatientIllnessScript patIllScript){
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getParentId());
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_COURSETIME, -1);
		if(scoreBean!=null) return;
		scoreBean = new ScoreBean(patIllScript.getId(), -1, ScoreBean.TYPE_COURSETIME, patIllScript.getCurrentStage());
		if(expScript!=null && expScript.getCourseOfTime()>=0) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript.getCourseOfTime(), patIllScript.getCourseOfTime());				
		
		//does this make sense here?
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
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, int courseOfTimeExp, int courseOfTimeLearner){
		if(courseOfTimeExp==courseOfTimeLearner) scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE);
		else scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE);
	}
	
	private void calculateOverallScore(ScoreBean scoreBean){
		scoreBean.setOverallScore(scoreBean.getScoreBasedOnExp());
	}
}
