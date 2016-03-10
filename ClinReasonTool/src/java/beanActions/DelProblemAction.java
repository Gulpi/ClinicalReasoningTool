package beanActions;

import java.util.List;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;

public class DelProblemAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelProblemAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Relation rel) {
		new DBClinReason().deleteAndCommit(rel);
		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		LogEntry le = new LogEntry(LogEntry.DELPROBLEM_ACTION, patIllScript.getSessionId(), rel.getSourceId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getProblems()==null || patIllScript.getProblems().isEmpty()){
			//todo error msg
			return;		
		}
		RelationProblem rel = patIllScript.getProblemById(Long.parseLong(id));
		patIllScript.getProblems().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getProblems());
		notifyLog(rel);
		save(rel);
	}
	


}
