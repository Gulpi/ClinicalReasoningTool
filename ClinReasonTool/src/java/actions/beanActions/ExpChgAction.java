package actions.beanActions;

import java.beans.Beans;

import beans.graph.*;
import beans.relation.Relation;
import controller.NavigationController;
import database.DBClinReason;

public class ExpChgAction{

	/**
	 * Called from the expert script creation in which for each item the stage can be changed directly. 
	 * @param itemId
	 * @param stage
	 */
	public void chgStage(String itemId, String stageStr){
		int stage = Integer.valueOf(stageStr);
		Graph g = NavigationController.getInstance().getAdminFacesContext().getGraph();
		long vertexId = Long.parseLong(itemId);
		MultiVertex vertex = g.getVertexById(vertexId);
		if(vertex!=null && vertex.getLearnerVertex()!=null){
			Relation rel = vertex.getLearnerVertex();
			rel.setStage(stage);
			new DBClinReason().saveAndCommit(rel);
		}
	}

}
