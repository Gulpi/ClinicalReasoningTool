package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import beans.relation.RelationProblem;
import beans.scoring.ScoreBean;
import controller.GraphController;
import controller.NavigationController;
import controller.ScoringController;

public abstract class ChgAction implements Scoreable{
	/**
	 * A log entry for the change action is created and saved in a Log object
	 */
	abstract void notifyLog(Beans rel, long newId);
	
	abstract void save(Beans rel);
	abstract void changeRelation(MultiVertex v, Relation rel);
	/**
	 * called from lists, where we only have the the current id, we change it to the new id, which is stored in the
	 * scoreBean.
	 * @param oldProbIdStr
	 */
	protected void changeItem(long oldItemId, PatientIllnessScript patIllScript, int type){
		Relation probToChg = patIllScript.getRelationByIdAndType(oldItemId, type);
		ScoreBean score = new ScoringController().getScoreBeanForItem(type, probToChg.getListItemId());
		//change in RelationProblem & Vertex:
		
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex expVertex = g.getVertexByIdAndType(score.getExpItemId(), type);
		MultiVertex learnerVertexOld = g.getVertexByIdAndType(probToChg.getListItemId(), type);
		if(!expVertex.equals(learnerVertexOld)){ //then it is NOT a synonyma, but a hierarchy node
				new GraphController(g).transferEdges(learnerVertexOld, expVertex);		
				g.removeVertex(learnerVertexOld);
				if(expVertex.getLearnerVertex()==null) expVertex.setLearnerVertex(probToChg);
		}
		changeRelation(expVertex, probToChg);	
		//we re-score the item:
		new ScoringAddAction(true).scoreAction(expVertex.getVertexId(), patIllScript, false, type);
	}

}
