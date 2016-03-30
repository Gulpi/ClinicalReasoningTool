package actions.beanActions;

import java.util.*;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;

public class DelDiagnosisAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Relation)o);
		new DBClinReason().saveAndCommit(patIllScript.getDiagnoses());
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELDIAGNOSIS_ACTION, patIllScript.getSessionId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getDiagnoses()==null || patIllScript.getDiagnoses().isEmpty()){
			//todo error msg
			return;		
		}
		RelationDiagnosis rel = patIllScript.getDiagnosisById(Long.parseLong(id));
		patIllScript.getDiagnoses().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getDiagnoses());
		new DelConnectionAction(patIllScript).deleteConnsByTargetId(rel.getId());
		notifyLog(rel);
		save(rel);
	}
}
