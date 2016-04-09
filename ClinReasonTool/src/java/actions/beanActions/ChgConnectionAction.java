package actions.beanActions;

import beans.Connection;
import beans.LogEntry;
import beans.PatientIllnessScript;
import database.DBClinReason;

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
		notifyLog(conn);
	}
	
	private void save(Connection conn){
		new DBClinReason().saveAndCommit(conn);
	}
	
	private void notifyLog(Connection cnx){
		LogEntry le = new LogEntry(LogEntry.CHGCNXWEIGHT_ACTION, patIllScript.getId(), cnx.getStartId(), cnx.getTargetId());
		le.save();
	}
}
