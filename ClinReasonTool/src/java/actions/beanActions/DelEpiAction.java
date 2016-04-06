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

public class DelEpiAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelEpiAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object rel) {
		new DBClinReason().deleteAndCommit(rel);
		new DBClinReason().saveAndCommit(patIllScript.getEpis()); //orderNrs have changed, so we have to save all
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELEPI_ACTION, patIllScript.getSessionId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getEpis()==null || patIllScript.getEpis().isEmpty()){
			//todo error msg
			return;		
		}
		RelationEpi rel = patIllScript.getEpiById(Long.parseLong(id));
		patIllScript.getEpis().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getEpis());		
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
				graph.removeEdgeWeight(rel.getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId());
			}
		}
		Logger.out(graph.toString(), Logger.LEVEL_TEST);
	}
}
