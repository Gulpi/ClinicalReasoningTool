package actions.scoringActions;

import java.util.*;

import beans.scripts.*;
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
	private boolean isChg = false; //true if scoring is repeated after user has changed an item
	
	public ScoringCnxsAction(PatientIllnessScript patillscript/*, boolean isChg*/){
		this.patillscript = patillscript;
		//this.isChg = isChg;
	}
	
	public void scoreConnections(int stage){
		if(patillscript.isExpScript()) return;
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		if(g.edgeSet()==null) return; //no one has made connections.
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_CNXS, stage);
		if(scoreBean!=null) isChg = true;//return; //already scored....
		scoreBean = new ScoreBean(patillscript, -1, ScoreBean.TYPE_CNXS, stage);
		scoreContainer.addScore(scoreBean);
		calculateCnxsScoreBasedOnExpert(g.edgeSet(), scoreBean);	
		calculateCnxsScoreBasedOnPeers(g.edgeSet(), scoreBean);
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
		float score = 0; 
		if(correctNum+missedNum>0) score = correctNum/(correctNum+missedNum);
		score = score - (addNum*ScoringController.ADD_CNX_RED_SCORE);
		if(score<0) score = 0;
		scoreBean.setScoreBasedOnExp(score, isChg);
	}
	
	private void calculateCnxsScoreBasedOnPeers(Set<MultiEdge> edges, ScoreBean scoreBean){
		
	}
	
}
