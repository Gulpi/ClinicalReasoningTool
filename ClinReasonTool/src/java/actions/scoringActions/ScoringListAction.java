package actions.scoringActions;

import java.util.List;

import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;

/**
 * Scoring of lists, like ddxs, problems, etc.. Currently we calculate for expScore:
 * correctItems/(correctItems+missedItems) - (additionalItems * 0.05).
 * @author ingahege
 *
 */
public class ScoringListAction {
	private PatientIllnessScript patillscript;
	
	public ScoringListAction(PatientIllnessScript patillscript){
		this.patillscript = patillscript;
	}
	/**
	 * This can only be done at the end of the session or when diagnoses are committed? 
	 * We cannot do this from the beginning on, because this might change. 
	 * But, we can do it throughout the process and update it each time a problem has been added or changed (not deleted?)
	 * Algorithm? Based on single scores and overall number of problems
	 * Include when problem was created? (or only in LA piece)?  
	 * 0 = learner has not created any list
	 * 0.? something in between
	 * 1 = experts' list contains no additional problems, learner has captured all (or at least synonyma)
	 * This is probably not for feedback, just for LA- relevant scoring
	 */
	public void scoreList(int listType, int relType){
		if(patillscript.isExpScript()) return;
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> mvertices = g.getVerticesByType(relType);
		if(mvertices==null) return; //neither learner nor expert has added items
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(listType, patillscript.getCurrentStage());
		if(scoreBean!=null) return; //already scored....
		if(scoreBean==null){
			scoreBean = new ScoreBean(patillscript, -1, listType);
			scoreContainer.addScore(scoreBean);
		}
		
		calculateListScoreBasedOnExpert(mvertices, scoreBean, scoreContainer);	
		//calculateStrictListScoreBasedOnExpert(mvertices, scoreBean, scoreContainer);	
		calculateListScoreBasedOnPeers(mvertices, scoreBean);
		new DBScoring().saveAndCommit(scoreBean);
	}
	
	/**
	 * algorithm: correctItems/(correctItems+missedItems) - (additionalItems * 0.05).
	 * TODO: we currently do not include the correct order of the items....
	 *  we also calculate a strict score for the list, that does not consider any items that have been added as joker or 
	 * after seen the expert solution. Also no changes are considered, but the original score is taken as a basis.
	 * @param mvertices
	 * @param scoreBean
	 */
	private void calculateListScoreBasedOnExpert(List<MultiVertex> mvertices, ScoreBean scoreBean, ScoreContainer scoreContainer){
		int missedNum = 0; 
		int addNum = 0; 
		int correctNum = 0;
		float correctScore = 0;
		for(int i=0; i< mvertices.size(); i++){
			MultiVertex vertex = mvertices.get(i);
			if(vertex.isExpertVertex() && vertex.isLearnerVertex()){
				correctNum++;
				//for strict scoring we have to consider the score the learner got for this item:
				ScoreBean sb = scoreContainer.getScoreBeanByTypeAndItemId(vertex.getType(), vertex.getVertexId());
				if(sb!=null)
					correctScore +=sb.getOrgScoreBasedOnExp();
			}
			//learner has missed an item if the expert has added it and the learner is already at the stage 
			//the expert has added it:
			if(vertex.isExpertVertex() && !vertex.isLearnerVertex() && vertex.getExpertVertex().getStage()<=patillscript.getCurrentStage()) 
				missedNum++;
			
			if(!vertex.isExpertVertex() && vertex.isLearnerVertex()) addNum++;			
		}
		float score = (float)correctNum/(correctNum+missedNum);
		score = score - (addNum*ScoringController.ADD_LISTITEM_RED_SCORE);
		if(score<0) score = 0;
		scoreBean.setScoreBasedOnExp(score, false);
		float strictScore = (float)correctScore/(correctScore+missedNum);
		strictScore = strictScore - (addNum*ScoringController.ADD_LISTITEM_RED_SCORE);
		scoreBean.setOrgScoreBasedOnExp(strictScore);
	}
	
	/**
	 * we calculate a strict score for the list, that does not consider any items that have been added as joker or 
	 * after seen the expert solution. Also no changes are considered, but the original score is taken as a basis.
	 * @param mvertices
	 * @param scoreBean
	 */
	/*private void calculateStrictListScoreBasedOnExpert(List<MultiVertex> mvertices, ScoreBean scoreBean, ScoreContainer scoreContainer){
		int missedNum = 0; 
		int addNum = 0; 
		int correctNum = 0;
		float correctScore = 0;
		for(int i=0; i< mvertices.size(); i++){
			MultiVertex vertex = mvertices.get(i);
			if(vertex.isExpertVertex() && vertex.isLearnerVertex()){
			//get the score for the item:
				ScoreBean sb = scoreContainer.getScoreBeanByTypeAndItemId(vertex.getType(), vertex.getVertexId());
				if(sb!=null)
					correctScore +=sb.getOrgScoreBasedOnExp();
			}
			//learner has missed an item if the expert has added it and the learner is already at the stage 
			//the expert has added it:
			if(vertex.isExpertVertex() && !vertex.isLearnerVertex() && vertex.getExpertVertex().getStage()<=patillscript.getCurrentStage()) 
				missedNum++;
			
			if(!vertex.isExpertVertex() && vertex.isLearnerVertex()) addNum++;		//learner has added additional items	
		}
		float score = (float)correctNum/(correctNum+missedNum);
		score = score - (addNum*ScoringController.ADD_LISTITEM_RED_SCORE);
		if(score<0) score = 0;
		scoreBean.setOrgScoreBasedOnExp(score);
	}*/
	
	private void calculateListScoreBasedOnPeers(List<MultiVertex> mvertices, ScoreBean scoreBean){
		//TODO
	}
	
	
}
