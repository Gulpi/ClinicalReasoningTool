package actions.scoringActions;

import application.AppBean;
import beans.scripts.*;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;

public class ScoringCourseOfTimeAction {
	
	private boolean isChg = false; //true if scoring is repeated after user has changed an item

	/*public ScoringCourseOfTimeAction(boolean isChg){
		this.isChg = isChg;
	}*/
	public void scoreAction(PatientIllnessScript patIllScript){
		if(patIllScript.isExpScript()) return;
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_COURSETIME, -1);
		if(scoreBean!=null) isChg = true;
		scoreBean = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_COURSETIME);
		if(expScript!=null && expScript.getCourseOfTime()>=0) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript.getCourseOfTime(), patIllScript.getCourseOfTime());				
		
		//does this make sense here?
		//if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
		//calculateAddActionScoreBasedOnPeers(edge, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	
	}
	
	/**
	 * Either correct (1) or wrong (0)
	 * @param scoreBean
	 * @param courseOfTimeExp
	 * @param courseOfTimeLearner
	 */
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, int courseOfTimeExp, int courseOfTimeLearner){
		if(courseOfTimeExp==courseOfTimeLearner) scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE, isChg);
		else scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
	}
	
}
