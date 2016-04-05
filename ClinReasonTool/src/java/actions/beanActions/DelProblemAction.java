package actions.beanActions;

import java.util.*;

import beans.IllnessScriptInterface;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.*;
import controller.NavigationController;
import database.DBClinReason;

public class DelProblemAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelProblemAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object rel) {
		new DBClinReason().deleteAndCommit(rel);
		new DBClinReason().saveAndCommit(patIllScript.getProblems()); //orderNrs have changed, so we have to save all
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELPROBLEM_ACTION, patIllScript.getSessionId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getProblems()==null || patIllScript.getProblems().isEmpty()){
			//todo error msg
			return;		
		}
		RelationProblem rel = patIllScript.getProblemById(Long.parseLong(id));
		patIllScript.getProblems().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getProblems());		
		notifyLog(rel);
		updateGraph(rel);
		new DelConnectionAction(patIllScript).deleteConnsByStartId(rel.getId());
		save(rel);
		
	}
	
	public void updateGraph(Relation rel){
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex vertex = graph.getVertexById(rel.getListItemId());
		if(vertex==null) return; //Should not happen
		vertex.setLearnerVertex(null);
		//remove complete edge param for all these edges:
		if( patIllScript.getDiagnoses()!=null){
			for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
				graph.removeEdgeWeight(rel.getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		}
		System.out.println(graph.toString());
	}
}