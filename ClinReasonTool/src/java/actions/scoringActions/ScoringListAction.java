package actions.scoringActions;

import java.util.List;

import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import controller.NavigationController;

/**
 * Scoring of lists, like ddxs, problems, etc..
 * @author ingahege
 *
 */
public class ScoringListAction {
	private PatientIllnessScript patillsscript;
	
	public ScoringListAction(PatientIllnessScript patillsscript){
		this.patillsscript = patillsscript;
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
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> mvertices = g.getVerticesByType(relType);
		if(mvertices==null) return;
		
	}
}
