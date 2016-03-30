package actions.scoringActions;

import javax.faces.context.FacesContextWrapper;

import beans.CRTFacesContext;
import beans.ScoreBean;
import beans.ScoreContainer;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import beans.relation.RelationProblem;
//import errors.WrongProblemError;
import controller.NavigationController;
import controller.ScoringController;
import database.DBClinReason;

public class ProblemScoringAction implements ScoringAction{

	//private ScoreContainer scoreContainer;
	/**
	 * We try to compare the users entry with the experts list of problems and score based on whether the 
	 * problem is in the list, the learner has used the correct term, etc. 
	 * We also have to take into account whether the learner has used any feedback before this action 
	 * @param patIllScript
	 * @param rel
	 */
	public void scoreAddAction(long listItemId, long patIllScriptId){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		calculateAddActionScoreBasedOnExpert( listItemId, g, patIllScriptId);
		//we update the score? score the problem, return score for feedback
		//if(patIllScript.getExpertPatIllScript()!=null){
			//then we can compare the learners entry...
		//}
		//TODO we have to have different scoring modes? 
		//scoreAddProblemActionBasedOnExpertList(prob, expertScript);
		
		scoreOverallProblemList(); //update this to also consider problems the learner has not (yet) come up with  
	}
	
	/**
	 * User has added an item to the list, we score it based on the experts list
	 * 1 = exact same problem is in experts' list (dark green check)
	 * 0.5 = synonyma is in the experts' list (light green check -> learner can change to better term) 
	 * 0 = problem is not at all in the experts' list (no check) 
	 * TODO consider position? 
	 */	
	private void calculateAddActionScoreBasedOnExpert(long itemId, Graph graph, long patIllScriptId){		
		ScoreBean scoreBean = new ScoreBean(patIllScriptId, itemId, Relation.TYPE_PROBLEM);
		MultiVertex vertex = graph.getVertexById(itemId);
		if(graph.getExpertPatIllScriptId()>0){ 
			if (vertex.isExpertVertex()) scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE);
			else{//look for synonyma:
				
			}
		}
		if(graph.getIllScriptIds()!=null && !graph.getIllScriptIds().isEmpty()){
			//todo score ....
		}
		
		//else no scoring possible because no experteIllScript exists
		new DBClinReason().saveAndCommit(scoreBean); //we store a scoreBean even if scoring is not possible.
		
	}
	
	/**
	 * User has added a problem to the list, we score it based on the experts list
	 * 1 = exact same problem is in experts' list (dark green check)
	 * 0.5 = synonyma is in the experts' list (light green check -> learner can change to better term) 
	 * 0 = problem is not at all in the experts' list (no check) 
	 * TODO consider position? 
	 */
	/*private void scoreAddProblemActionBasedOnExpertList(RelationProblem prob, PatientIllnessScript expertScript){
		
		
	}*/
	
	/**
	 * This can only be done at the end of the session or when diagnoses are committed? 
	 * We cannot do this from the beginning on, because this might change. 
	 * But, we can do it thruout the process and update it each time a problem has been added or changed (not deleted?)
	 * Algorithm? Based on single scores and overall number of problems
	 * Include when problem was created? (or only in LA piece)?  
	 * 0 = learner has not created any list
	 * 0.? something in between
	 * 1 = experts' list contains no additional problems, learner has captured all (or at least synonyma)
	 * This is probably not for feedback, just for LA- relevant scoring
	 */
	private void scoreOverallProblemList(){
		
	}
	
	
	
	/*public void calcScoreForDelProblem(){
		
	}*/
}
