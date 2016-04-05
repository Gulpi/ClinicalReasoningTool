package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import beans.Connection;
import beans.IllnessScriptInterface;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import controller.NavigationController;
import database.DBClinReason;

public class DelConnectionAction /*implements DelAction*/{
	private PatientIllnessScript patIllScript;
	
	public DelConnectionAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(java.lang.Object)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Connection)o);		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(java.lang.Object)
	 */
	public void notifyLog(Object o) {
		Connection c = (Connection) o;
		LogEntry le = new LogEntry(LogEntry.DELCONNECTION_ACTION, patIllScript.getSessionId(), c.getStartId(), c.getTargetId());
		le.save();			
	}

	public void delete(String idStr) {
		if(idStr==null || idStr.trim().equals("") || patIllScript==null || patIllScript.getConns()==null || patIllScript.getConns().isEmpty()){
			//todo error msg
			return;		
		}
		idStr = idStr.substring(6);
		long id = Long.parseLong(idStr);
		Connection conn = (Connection) patIllScript.getConns().get(new Long(id));
		patIllScript.getConns().remove(new Long(id));
		//new ActionHelper().reOrderItems(patIllScript.getProblems());
		notifyLog(conn);
		save(conn);		
	}
	
	/**
	 * Called when we delete a problem -> we check for existing connections and delete them as well.
	 * @param sourceId
	 */
	protected void deleteConnsByStartId(long startId){
		List<Connection> connsToDelete =  getConnsToDelByStartId(startId);
		List<LogEntry> logs = new ArrayList<LogEntry>();
		if(connsToDelete==null) return;
		for(int i=0; i<connsToDelete.size(); i++){
			Connection conn = connsToDelete.get(i);
			updateGraph(conn);
			patIllScript.getConns().remove(conn);		
			logs.add(new LogEntry(LogEntry.DELCNXAFTERSTARTNODE_ACTION, patIllScript.getSessionId(), conn.getStartId(), conn.getTargetId()));
		}
		new DBClinReason().deleteAndCommit(connsToDelete);
		new DBClinReason().saveAndCommit(logs);
	}
	
	/**
	 * Called when we delete a problem -> we check for existing connections and delete them as well.
	 * @param sourceId
	 */
	protected void deleteConnsByTargetId(long targetId){
		List<Connection> connsToDelete =  getConnsToDelByTargetId(targetId);
		List<LogEntry> logs = new ArrayList<LogEntry>();
		if(connsToDelete==null) return;
		for(int i=0; i<connsToDelete.size(); i++){
			Connection conn = connsToDelete.get(i);
			patIllScript.getConns().remove(conn);		
			logs.add(new LogEntry(LogEntry.DELCNXAFTERTARGETNODE_ACTION, patIllScript.getSessionId(), conn.getStartId(), conn.getTargetId()));
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
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.removeExplicitEdgeWeight(connToDel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		System.out.println(graph.toString());
	}

}