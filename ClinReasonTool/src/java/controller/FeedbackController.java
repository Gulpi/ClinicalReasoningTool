package controller;

import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;

/**
 * Handles all kinds of feedback displayed to the learner (except the direct scoring, which is handled by the ScoringController): 
 * - mouseover texts over checkmarks (itemFeedback)
 * - ...
 * @author ingahege
 *
 */
public class FeedbackController {
	
	/**
	 * Give feedback for an added item for the mouseover texts over checkmarks. 
	 * @param type
	 * @param itemId
	 * @return
	 */
	public String getItemFeedback(int type, long itemId){
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		if (scoreContainer==null) return ""; //no icon
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(type,itemId);
		if(scoreBean==null) return "";
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex learnerVertex = g.getVertexByIdAndType(itemId, type);
		//give feedback that learner has chosen a similar item (more general, more specific)
		if(scoreBean.getScoreBasedOnExp()<ScoringController.SCORE_EXP_SAMEAS_LEARNER && scoreBean.getExpItemId()>0){			
			MultiVertex expVertex = g.getVertexByIdAndType(scoreBean.getExpItemId(), type);
			if(expVertex!=null) return "Your answer is correct, however, expert has chosen " + expVertex.getLabel() +". If you want to change it to the expert's choice, please click.";
		}
		//give feedback for synonyma
		if(learnerVertex!=null && learnerVertex.getLearnerVertex()!=null && learnerVertex.getLearnerVertex().getIsSynonyma() && learnerVertex.getExpertVertex()!=null){
			return "Your answer is correct, however, expert has chosen " + learnerVertex.getExpertVertex().getLabel() +". If you want to change it to the expert's choice, please click.";
		}
		if(scoreBean.getScoreBasedOnExp()==ScoringController.SCORE_EXP_SAMEAS_LEARNER) return "Expert has chosen same item";
		if(scoreBean.getScoreBasedOnExp()==ScoringController.SCORE_NOEXP_BUT_LEARNER) return "wrong...";

		return "";
	}
	
	/**
	 * return the label the expert has entered (if different from the learner's)
	 * @param type
	 * @param itemId
	 * @return
	 */
	public String getExpItemLabel(int type, long itemId){
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		if (scoreContainer==null) return ""; //no icon
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(type,itemId);
		if(scoreBean==null || scoreBean.getExpItemId()<=0) return "";
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex expVertex = g.getVertexByIdAndType(scoreBean.getExpItemId(), type);
		if(expVertex!=null) return expVertex.getLabel();
		return "";
	}
	
	/**
	 * Is the learner allowed to change the item? Yes, if we have an expertItemId to which we can change
	 * @param type
	 * @param itemId
	 * @return
	 */
	public boolean isChgAllowed(int type, long itemId){
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		if (scoreContainer==null) return false; //no icon
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(type,itemId);
		if(scoreBean==null) return false;
		if(scoreBean.getExpItemId()>0) return true;
		return false;
		//if(scoreBean.getScoreBasedOnExp()>0 && scoreBean.getScoreBasedOnExp()<1) return true;
	}
}
