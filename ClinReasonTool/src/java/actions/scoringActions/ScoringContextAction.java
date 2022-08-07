package actions.scoringActions;

import application.AppBean;
import beans.ContextContainer;
import beans.context.*;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import database.DBScoring;
import net.casus.util.Utility;
import util.CRTLogger;

public class ScoringContextAction{

	public void scoreAddActorAction(Actor act) {
		try {
			if(act.getType()==2) return; //expert -> no scoring
			ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
			ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_ADD_ACTOR, act.getListItemId());
			if(scoreBean!=null) return; //already scored
			scoreBean = new ScoreBean(ScoreBean.TYPE_ADD_ACTOR, act.getUserId(), act.getVpId()+"_2", act.getListItemId());
			float score =  calculateScore(act);
			scoreBean.setOrgScoreBasedOnExp(score);
			scoreBean.setScoreBasedOnExp(score);
			scoreBean.setStage(act.getStage());
			scoreContainer.addScore(scoreBean);
			new DBScoring().saveAndCommit(scoreBean);
		}
		catch(Exception e) {
			CRTLogger.out(Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	
	}
	
	/**
	 * We check whether the expert has the same or similar item in the container
	 * @param act
	 */
	private float calculateScore(Actor act) {
		float score = (float)0.0;
		ContextContainer expContainer = AppBean.getExpertContexts(act.getVpId());//should not happen, but we could re-try here
		if(expContainer==null || expContainer.getActors()==null) return -1; //should not happen
		for(int i=0; i<expContainer.getActors().size();i++) {
			Actor expActor = expContainer.getActors().get(i);
			if(expActor.getListItemId()==act.getListItemId()) return (float) 1.0; //same listItem added by expert -> full score
			if(expActor.getSynId()==act.getSynId()) return (float) 1.0; //same synonym added by expert -> full score
			
			//we might add other scorings here....
		}		
		return score;
		
	}
	
	public void scoreAddContextAction(Context c) {
		try {
			if(c.getType()==2) return; //expert -> no scoring
			ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
			ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_ADD_CONTEXT, c.getListItemId());
			if(scoreBean!=null) return; //already scored
			scoreBean = new ScoreBean(ScoreBean.TYPE_ADD_CONTEXT, c.getUserId(), c.getVpId()+"_2", c.getListItemId());
			float score =  calculateScore(c);
			scoreBean.setOrgScoreBasedOnExp(score);
			scoreBean.setScoreBasedOnExp(score);
			scoreBean.setStage(c.getStage());
			scoreContainer.addScore(scoreBean);
			new DBScoring().saveAndCommit(scoreBean);
		}
		catch(Exception e) {
			CRTLogger.out(Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	/**
	 * We check whether the expert has the same or similar item in the container
	 * @param act
	 */
	private float calculateScore(Context c) {
		float score = (float)0.0;
		ContextContainer expContainer = AppBean.getExpertContexts(c.getVpId());//should not happen, but we could re-try here
		if(expContainer==null || expContainer.getCtxts()==null) return -1; //should not happen
		for(int i=0; i<expContainer.getCtxts().size();i++) {
			Context expContext = expContainer.getCtxts().get(i);
			if(expContext.getListItemId()==c.getListItemId()) return (float) 1.0; //same listItem added by expert -> full score
			if(expContext.getSynId()==c.getSynId()) return (float) 1.0; //same synonym added by expert -> full score
			
			//we might add other scorings here....
		}		
		return score;
		
	}


}
