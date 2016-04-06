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
import util.Logger;

public class DelDiagnosisAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Relation)o);
		new DBClinReason().saveAndCommit(patIllScript.getDiagnoses());
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELDIAGNOSIS_ACTION, patIllScript.getId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getDiagnoses()==null || patIllScript.getDiagnoses().isEmpty()){
			//todo error msg
			return;		
		}
		RelationDiagnosis rel = patIllScript.getDiagnosisById(Long.parseLong(id));
		patIllScript.getDiagnoses().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getDiagnoses());
		updateGraph(rel);
		new DelConnectionAction(patIllScript).deleteConnsByTargetId(rel.getId());
		notifyLog(rel);
		save(rel);
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.DelAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel){
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex vertex = graph.getVertexById(rel.getListItemId());
		if(vertex==null) return; //Should not happen
		vertex.setLearnerVertex(null);
		//remove complete edge param for all these edges:
		if(patIllScript.getTests()!=null){
			for(int i=0; i < patIllScript.getTests().size(); i++){
				graph.removeEdgeWeight(rel.getListItemId(), patIllScript.getTests().get(i).getListItemId());
			}
		}
		if(patIllScript.getMngs()!=null){
			for(int i=0; i < patIllScript.getMngs().size(); i++){
				graph.removeEdgeWeight(rel.getListItemId(), patIllScript.getMngs().get(i).getListItemId());
			}
		}
		if(patIllScript.getProblems()!=null){
			for(int i=0; i < patIllScript.getProblems().size(); i++){
				graph.removeEdgeWeight(patIllScript.getProblems().get(i).getListItemId(), rel.getListItemId());
			}
		}
		Logger.out(graph.toString(), Logger.LEVEL_TEST);
	}
}
