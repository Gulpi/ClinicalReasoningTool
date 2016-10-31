package controller;

import java.util.List;

import application.AppBean;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import beans.scripts.PatientIllnessScript;

/**
 * Handles all kinds of feedback displayed to the learner (except the direct scoring, which is handled by the ScoringController): 
 * - mouseover texts over checkmarks (itemFeedback)
 * - ...
 * @author ingahege
 *
 */
public class FeedbackController {
	static private FeedbackController instance = new FeedbackController();
	static public FeedbackController getInstance() { return instance; }
	public static final int SLIGHT_RED_BOX_CUTOFF = 3; //if the difference between learner and exp item num is >=3 we make the box slightly red
	public static final int RED_BOX_CUTOFF = 5; //if the difference between learner and exp item num is >=5 we make the box slightly red
	

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
		ScoreContainer scoreContainer = NavigationController.getInstance().getMyFacesContext().getScoreContainer();
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
	
	/**
	 * We calculate the difference of number of items the expert has added and number of items the learner has added.
	 * If the difference becomes too big, we change the color of the box to indicate that the learner should do something.
	 * expnum-learnernum >=0 && <=3 -> 0
	 * expnum-learnernum >3 && <=5 -> 1
	 * expnum-learnernum >5 -> 2
	 * @param stage
	 * @param items
	 * @param type
	 * @return 0 (normal), 1 (slight red), 2 (dark red)
	 */
	public int getItemsDiffExpForStage(int stage, List items, int type){
		if(stage==1) return 0; //we do not do anything in the beginning
		Graph g = NavigationController.getInstance().getCRTFacesContext().getGraph();
		//we use stage-1 to avoid that already on opening the next card a box is turned to red: 
		List<MultiVertex> expVertices = g.getVerticesByTypeAndStageRangeExp(type, 1, stage-1);
		//expert has no items:
		if(expVertices==null || expVertices.isEmpty()) return 0;
		//learner has no items:
		if(items==null ||items.isEmpty()){
			if(expVertices.size()<= SLIGHT_RED_BOX_CUTOFF) return 0;
			if(expVertices.size()> RED_BOX_CUTOFF) return 2;
			return 1;
		}
		//learner has same or more items:
		if(expVertices.size() - items.size()<=0) return 0;
		//learner has less items:
		if(expVertices.size() - items.size() <= SLIGHT_RED_BOX_CUTOFF) return 0;
		if(expVertices.size() - items.size() > RED_BOX_CUTOFF) return 2;
		return 1;		
	}
	
	/** We make the summary statement box light red if expert has added a summary statement and the learner is past 
	 * the stage at which the expert did it. 
	 * @param stage
	 * @param vpId
	 * @return
	 */
	public int getSumStDiffForStage(int stage, String vpId){
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(vpId);
		//if for some reason we cannot get the expertscript just consider the stage:
		if(expScript==null && stage>=4) return 1;
		if(expScript==null && stage<4) return 0;
		if(expScript.getSummSt()==null) return 0; //expert has no summary statement
		if(stage > expScript.getSummSt().getStage()) return 1;
		return 0;
	}
}
