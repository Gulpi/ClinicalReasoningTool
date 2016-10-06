package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import actions.scoringActions.ScoringCnxsAction;
import beans.LogEntry;
import beans.scripts.*;
import beans.graph.Graph;
import beans.relation.Connection;
import beans.scripts.IllnessScriptInterface;
import controller.GraphController;
import controller.NavigationController;
import database.DBClinReason;
import util.CRTLogger;

public class DelConnectionAction /*implements DelAction*/{
	private PatientIllnessScript patIllScript;
	
	public DelConnectionAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(java.lang.Object)
	 */
	public void delete(Object o) {
		new DBClinReason().deleteAndCommit((Connection)o);		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(java.lang.Object)
	 */
	public void notifyLog(Object o) {
		Connection c = (Connection) o;
		LogEntry le = new LogEntry(LogEntry.DELCONNECTION_ACTION, patIllScript.getId(), c.getStartId(), c.getTargetId());
		le.save();			
	}

	/**
	 * Delete a connection by its id (format cnx_1234). If the id does not start with "cnx_" do nothing. 
	 * This can happen if the connection is not correctly initialized (then e.g. it is con_123, but the id is not 
	 * the cnx id stored in the database).
	 * @param idStr
	 */
	public void delete(String idStr) {
		if(idStr==null || idStr.trim().equals("") || !idStr.startsWith(GraphController.PREFIX_CNX)|| patIllScript==null || patIllScript.getConns()==null || patIllScript.getConns().isEmpty()){
			//todo error msg
			return;		
		}
		
		idStr = idStr.substring(GraphController.PREFIX_CNX.length());
		long id = Long.parseLong(idStr);
		Connection conn = (Connection) patIllScript.getConns().get(new Long(id));
		patIllScript.getConns().remove(new Long(id));
		//new ActionHelper().reOrderItems(patIllScript.getProblems());
		if(conn!=null){
			notifyLog(conn);
			updateGraph(conn);
			delete(conn);	
		}
	}
	
	protected void deleteConns(long id){
		deleteConnsByStartId(id);
		deleteConnsByTargetId(id);
		new ScoringCnxsAction(patIllScript).scoreConnections(patIllScript.getCurrentStage());
	}
	/**
	 * Called when we delete a problem -> we check for existing connections and delete them as well.
	 * @param sourceId
	 */
	private void deleteConnsByStartId(long startId){
		List<Connection> connsToDelete =  getConnsToDelByStartId(startId);
		List<LogEntry> logs = new ArrayList<LogEntry>();
		if(connsToDelete==null) return;
		for(int i=0; i<connsToDelete.size(); i++){
			Connection conn = connsToDelete.get(i);
			updateGraph(conn);
			patIllScript.getConns().remove(conn);		
			logs.add(new LogEntry(LogEntry.DELCNXAFTERSTARTNODE_ACTION, patIllScript.getId(), conn.getStartId(), conn.getTargetId()));
		}
		new DBClinReason().deleteAndCommit(connsToDelete);
		new DBClinReason().saveAndCommit(logs);
	}
	
	/**
	 * Called when we delete a problem -> we check for existing connections and delete them as well.
	 * @param sourceId
	 */
	private void deleteConnsByTargetId(long targetId){
		List<Connection> connsToDelete =  getConnsToDelByTargetId(targetId);
		List<LogEntry> logs = new ArrayList<LogEntry>();
		if(connsToDelete==null) return;
		for(int i=0; i<connsToDelete.size(); i++){
			Connection conn = connsToDelete.get(i);
			patIllScript.getConns().remove(conn);		
			logs.add(new LogEntry(LogEntry.DELCNXAFTERTARGETNODE_ACTION, patIllScript.getId(), conn.getStartId(), conn.getTargetId()));
		}
		new DBClinReason().deleteAndCommit(connsToDelete);
		new DBClinReason().saveAndCommit(logs);
	}
	
	/**
	 * We get all conncetions with the given startId 
	 * @param startId
	 * @return List<Connection> or null
	 */
	private List<Connection> getConnsToDelByStartId(long startId){
		List<Connection> connsToDelete = new ArrayList<Connection>();
		if(patIllScript.getConns()==null || patIllScript.getConns().isEmpty()) return null;
		Iterator<Connection> it = patIllScript.getConns().values().iterator();
		while(it.hasNext()){
			Connection conn = it.next();
			if(conn.getStartId()==startId) connsToDelete.add(conn);
		}
		if(connsToDelete.isEmpty()) return null;
		return connsToDelete;
	}
	
	/**
	 * We get all connections with the given targetId 
	 * @param startId
	 * @return List<Connection> or null
	 */
	private List<Connection> getConnsToDelByTargetId(long targetId){
		List<Connection> connsToDelete = new ArrayList<Connection>();
		if(patIllScript.getConns()==null || patIllScript.getConns().isEmpty()) return null;
		Iterator<Connection> it = patIllScript.getConns().values().iterator();
		while(it.hasNext()){
			Connection conn = it.next();
			if(conn.getTargetId()==targetId) connsToDelete.add(conn);
		}
		if(connsToDelete.isEmpty()) return null;
		return connsToDelete;
	}
	
	private void updateGraph(Connection connToDel){
		Graph g = NavigationController.getInstance().getMyFacesContext().getGraph();
		g.removeExplicitEdgeWeight(connToDel.getId());
		CRTLogger.out(g.toString(), CRTLogger.LEVEL_TEST);
	}
}
