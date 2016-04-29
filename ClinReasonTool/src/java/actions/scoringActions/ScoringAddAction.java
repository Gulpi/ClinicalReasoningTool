package actions.scoringActions;

import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import controller.ScoringController;
import database.DBScoring;
import model.Synonym;

/**
 * We score any action in which the learner adds an item (problem, ddx, text, mng item) to the list/concept map.
 * @author ingahege
 *
 */
public class ScoringAddAction implements ScoringAction{

	/**
	 * We try to compare the users entry with the experts list of problems and score based on whether the 
	 * problem is in the list, the learner has used the correct term, etc. 
	 * We also have to take into account whether the learner has used any feedback before this action 
	 * @param patIllScript
	 * @param rel
	 */
	public void scoreAction(long vertexId, PatientIllnessScript patIllScript){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex mvertex = g.getVertexById(vertexId);
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(mvertex.getType(), vertexId);
		if(scoreBean!=null) return; //then this item has already been scored: 
		scoreBean = new ScoreBean(patIllScript.getId(), mvertex.getVertexId(), mvertex.getType(), patIllScript.getCurrentStage());
		if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(mvertex, scoreBean, patIllScript);				
					
		if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
			calculateAddActionScoreBasedOnPeers(mvertex, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		calculateOverallScore(scoreBean); 
		new DBScoring().saveAndCommit(scoreBean);			
		//}
	}
	
	/** TODO we could consider all components and calculate based on these an overall score.
	 * For now we just take the expertsScore.
	 * @param scoreBean
	 */
	private void calculateOverallScore(ScoreBean scoreBean){
		scoreBean.setOverallScore(scoreBean.getScoreBasedOnExp());
	}
	
	/**
	 * User has added an item to the list, we score it based on the experts list
	 * 1 = exact same problem is in experts' list (dark green check)
	 * 0.x = synonyma is in the experts' list (light green check -> learner can change to better term) -> see weight in synonym object
	 * 0 = problem is not at all in the experts' list (no check) 
	 * TODO consider position? probably not possible on add, but on move! 
	 */	
	private void calculateAddActionScoreBasedOnExpert(MultiVertex mvertex, ScoreBean scoreBean, PatientIllnessScript patIllScript){
		//PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getParentId());
		
		Relation expRel = mvertex.getExpertVertex();
		Relation learnerRel = mvertex.getLearnerVertex();
		if(learnerRel!=null && expRel!=null) scoreBean.setTiming(learnerRel.getStage(), expRel.getStage());
		new ScoringController().setFeedbackInfo(scoreBean);
		if(expRel!=null /*&& expRel.getSynId()<=0*/){ //expert has chosen this item (not synonym)
			if(learnerRel!=null && learnerRel.getSynId()<=0) scoreBean.setScoreBasedOnExp(ScoringController.SCORE_EXP_SAMEAS_LEARNER);
			if(learnerRel!=null && learnerRel.getSynId()>0){//learner has chosen a synonym:
				Synonym learnerSyn = learnerRel.getSynonym();
				scoreBean.setScoreBasedOnExp(learnerSyn.getRatingWeight());
			}
		}
		else //expert has not picked this item (nor a synonym)
			scoreBean.setScoreBasedOnExp(ScoringController.SCORE_NOEXP_BUT_LEARNER);		
	}
	

	
	private void calculateAddActionScoreBasedOnPeers(MultiVertex mvertex, ScoreBean scoreBean, int overallPeers){
		Relation learnerRel = mvertex.getLearnerVertex();
		float peerRatio =  mvertex.getPeerNums()/overallPeers; 
		//we need a good algorithm here to score this based on current and overall peer nums
		//CAVE: do we have to consider the stage? 
		
	}	
}
