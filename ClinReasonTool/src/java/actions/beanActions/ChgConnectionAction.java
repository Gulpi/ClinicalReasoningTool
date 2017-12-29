package actions.beanActions;

import actions.scoringActions.ScoringCnxChgAction;
import beans.LogEntry;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.relation.Connection;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import database.DBClinReason;
import util.CRTLogger;

/**
 * Learner changes the weight of a connection
 * @author ingahege
 *
 */
public class ChgConnectionAction {
	private PatientIllnessScript patIllScript;
	
	public ChgConnectionAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void chgConnection(String idStr, String weightStr){
		if(idStr==null || idStr.trim().equals("") || patIllScript==null || patIllScript.getConns()==null || patIllScript.getConns().isEmpty()){
			//todo error msg
			return;		
		}
		long id = Long.parseLong(idStr.substring(4));
		Connection conn = patIllScript.getConns().get(new Long(id));
		if(conn==null) return;
		int newWeight = Integer.parseInt(weightStr);
		conn.setWeight(newWeight);
		save(conn);
		updateGraph(conn);
		notifyLog(conn);
	}
	
	private void save(Connection conn){
		new DBClinReason().saveAndCommit(conn);
	}
	
	private void notifyLog(Connection cnx){
		LogEntry le = new LogEntry(LogEntry.CHGCNXWEIGHT_ACTION, patIllScript.getId(), cnx.getStartId(), cnx.getTargetId());
		le.save();
	}
	
	private void updateGraph(Connection cnx) {
		Graph g = NavigationController.getInstance().getMyFacesContext().getGraph();
		MultiEdge edge = g.getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnx.getId());
		edge.changeExplicitWeight(cnx.getWeight());
		CRTLogger.out(g.toString(), CRTLogger.LEVEL_TEST);	
	}
}
