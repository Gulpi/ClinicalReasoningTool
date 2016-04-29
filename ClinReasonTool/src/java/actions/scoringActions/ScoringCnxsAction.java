package actions.scoringActions;

import java.util.*;

import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;

/**
 * We score all connections the learner has made at a stage
 * @author ingahege
 *
 */
public class ScoringCnxsAction {
private PatientIllnessScript patillscript;
	
	public ScoringCnxsAction(PatientIllnessScript patillscript){
		this.patillscript = patillscript;
	}
	
	public void scoreConnections(int stage){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		if(g.edgeSet()==null) return; //no one has made connections.
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_CNXS, stage);
		if(scoreBean!=null) return; //already scored....
		scoreBean = new ScoreBean(patillscript.getId(), -1, ScoreBean.TYPE_CNXS, stage);
		scoreContainer.addScore(scoreBean);
		calculateCnxsScoreBasedOnExpert(g.edgeSet(), scoreBean);	
		calculateCnxsScoreBasedOnPeers(g.edgeSet(), scoreBean);
		calculateOverallScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);
	}
	
	/**
	 * we compare the expert's edges with the learner's and count the correct ones, missing ones, and aditional ones.
	 * @param edges
	 * @param scoreBean
	 */
	private void calculateCnxsScoreBasedOnExpert(Set<MultiEdge> edges, ScoreBean scoreBean){
		if(edges==null || edges.isEmpty()) return;
		int missedNum = 0; 
		int addNum = 0; 
		int correctNum = 0;
		Iterator<MultiEdge> it = edges.iterator();
		while(it.hasNext()){
			MultiEdge edge = it.next();
			if(edge.getExpCnxId()>0 && edge.getLearnerCnxId()>0) correctNum++;
			if(edge.getExpCnxId()>0 && edge.getLearnerCnxId()<=0) missedNum++; //we do not consider the stage here!
			if(edge.getExpCnxId()<=0 && edge.getLearnerCnxId()>0) addNum++;			
		}
		float score = correctNum/(correctNum+missedNum);
		score = score - (addNum*ScoringController.ADD_CNX_RED_SCORE);
		if(score<0) score = 0;
		scoreBean.setScoreBasedOnExp(score);
	}
	
	private void calculateCnxsScoreBasedOnPeers(Set<MultiEdge> edges, ScoreBean scoreBean){
		
	}
	
	/** TODO we could consider all components and calculate based on these an overall score.
	 * For now we just take the expertsScore.
	 * @param scoreBean
	 */
	private void calculateOverallScore(ScoreBean scoreBean){
		scoreBean.setOverallScore(scoreBean.getScoreBasedOnExp());
	}
}
