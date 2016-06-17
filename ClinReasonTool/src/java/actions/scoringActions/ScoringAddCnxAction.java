package actions.scoringActions;

import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;

/**
 * @author ingahege
 */
public class ScoringAddCnxAction implements ScoringAction{

	public void scoreAction(long cnxId, PatientIllnessScript patIllScript, boolean isJoker){
		if(patIllScript.getType()==IllnessScriptInterface.TYPE_EXPERT_CREATED) return;
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiEdge edge = g.getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnxId);
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_ADD_CNX, cnxId);
		if(scoreBean!=null) return; //then this item has already  been scored: 
		scoreBean = new ScoreBean(patIllScript, cnxId, ScoreBean.TYPE_ADD_CNX);
		if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(edge, scoreBean, patIllScript, isJoker);				
					
		if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
			calculateAddActionScoreBasedOnPeers(edge, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		calculateOverallScore(scoreBean); 
		new DBScoring().saveAndCommit(scoreBean);			
		
	}
	
	/**
	 * @param mvertex
	 * @param scoreBean
	 * @param patIllScript
	 */
	private void calculateAddActionScoreBasedOnExpert(MultiEdge edge, ScoreBean scoreBean, PatientIllnessScript patIllScript, boolean isJoker){
		//PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getParentId());		
		//scoreBean.setTiming(edge.getStage(), expRel.getStage()); Timing not important fro cnxs
		new ScoringController().setFeedbackInfo(scoreBean, false,false);
		if(edge.getExpCnxId()>0 ){ //expert has als made an explicit connection
			scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE, false);	
		}
		else //expert has not picked this cnx
			scoreBean.setScoreBasedOnExp(ScoringController.SCORE_NOEXP_BUT_LEARNER, false);		
	}
	
	private void calculateAddActionScoreBasedOnPeers(MultiEdge edge, ScoreBean scoreBean, int peerNum){
		//TODO
	}

	/** TODO we could consider all components and calculate based on these an overall score.
	 * For now we just take the expertsScore.
	 * @param scoreBean
	 */
	private void calculateOverallScore(ScoreBean scoreBean){
		scoreBean.setOverallScore(scoreBean.getScoreBasedOnExp());
	}


}
