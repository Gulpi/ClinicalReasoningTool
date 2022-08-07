package actions.beanActions;

import java.util.*;

import actions.scoringActions.ScoringListAction;
import beans.LogEntry;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.RelationNursingAim;
import beans.relation.Relation;
import beans.scoring.ScoreBean;
import controller.NavigationController;
import database.DBClinReason;
import util.CRTLogger;

public class DelNursingAimAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelNursingAimAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Relation)o);
		new DBClinReason().saveAndCommit(patIllScript.getNursingAims());
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELNAIM_ACTION, patIllScript.getId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getNursingAims()==null || patIllScript.getNursingAims().isEmpty()){
			//todo error msg
			return;		
		}
		RelationNursingAim rel = patIllScript.getNursingAimById(Long.parseLong(id));
		patIllScript.getNursingAims().remove(rel);
		updateGraph(rel);
		new ActionHelper().reOrderItems(patIllScript.getNursingAims());
		new DelConnectionAction(patIllScript).deleteConns(rel.getId());
		notifyLog(rel);
		save(rel);
		new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_NAIM_LIST, Relation.TYPE_NURSAIM);

	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.DelAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel){
		Graph graph = NavigationController.getInstance().getMyFacesContext().getGraph();
		MultiVertex vertex = graph.getVertexByIdAndType(rel.getListItemId(), Relation.TYPE_NURSAIM);
		if(vertex==null) return; //Should not happen
		vertex.setLearnerVertex(null);
		//remove complete edge param for all these edges:
		
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
