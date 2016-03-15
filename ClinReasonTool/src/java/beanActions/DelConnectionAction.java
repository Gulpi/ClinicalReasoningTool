package beanActions;

import java.beans.Beans;

import beans.Connection;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationProblem;
import database.DBClinReason;

public class DelConnectionAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelConnectionAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	@Override
	public void save(Object o) {
		new DBClinReason().deleteAndCommit(o);		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(java.lang.Object)
	 */
	public void notifyLog(Object o) {
		Connection c = (Connection) o;
		LogEntry le = new LogEntry(LogEntry.DELCONNECTION_ACTION, patIllScript.getSessionId(), c.getStartId(), c.getTargetId());
		le.save();			
	}

	@Override
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getConns()==null || patIllScript.getConns().isEmpty()){
			//todo error msg
			return;		
		}
		Connection conn = (Connection) patIllScript.getConns().get(Long.getLong(id));
		patIllScript.getConns().remove(conn);
		new ActionHelper().reOrderItems(patIllScript.getProblems());
		notifyLog(conn);
		save(conn);
		
	}

}
