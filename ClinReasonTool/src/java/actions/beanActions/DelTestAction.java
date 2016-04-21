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
import util.CRTLogger;

public class DelTestAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelTestAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Relation)o);
		new DBClinReason().saveAndCommit(patIllScript.getTests());
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELTEST_ACTION, patIllScript.getId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getTests()==null || patIllScript.getTests().isEmpty()){
			//todo error msg
			return;		
		}
		RelationTest rel = patIllScript.getTestById(Long.parseLong(id));
		patIllScript.getTests().remove(rel);
		updateGraph(rel);
		new ActionHelper().reOrderItems(patIllScript.getTests());
		//TODO:
		//new DelConnectionAction(patIllScript).deleteConnsByTargetId(rel.getId());
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
		if( patIllScript.getDiagnoses()!=null){
			for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
				graph.removeEdgeWeight(patIllScript.getDiagnoses().get(i).getListItemId(), rel.getListItemId());
			}
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
