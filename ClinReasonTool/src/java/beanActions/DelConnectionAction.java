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

	@Override
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

}
