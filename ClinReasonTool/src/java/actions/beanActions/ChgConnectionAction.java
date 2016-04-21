package actions.beanActions;

import beans.Connection;
import beans.IllnessScriptInterface;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiEdge;
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
		long id = Long.parseLong(idStr.substring(6));
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
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		MultiEdge edge = graph.getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnx.getId());
		edge.changeExplicitWeight(cnx.getWeight());
		//graph.addExplicitEdge(cnx, patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);	}
}
