package actions.beanActions;

import java.beans.Beans;

import beans.graph.*;
import beans.relation.Relation;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;
import database.DBClinReason;
import database.DBEditing;

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
	
	/**
	 * Expert changes the stage of a connection...
	 * @param itemId
	 * @param stageStr
	 */
	public void chgEdgeStage(String itemId, String stageStr){
		int stage = Integer.valueOf(stageStr);
		Graph g = NavigationController.getInstance().getAdminFacesContext().getGraph();
		long cnxId = Long.parseLong(itemId);
		MultiEdge edge = g.getEdgeByCnxId(PatientIllnessScript.TYPE_EXPERT_CREATED, cnxId);
		if(edge!=null && edge.getLearnerCnx()!=null){
			edge.getLearnerCnx().setStage(stage);
			new DBEditing().saveAndCommit(edge.getLearnerCnx());
		}
	}

}
