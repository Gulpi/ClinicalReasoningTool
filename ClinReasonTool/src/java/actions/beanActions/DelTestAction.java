package actions.beanActions;

import java.util.*;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;

public class DelTestAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelTestAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object o) {
		new DBClinReason().deleteAndCommit((Relation)o);
		new DBClinReason().saveAndCommit(patIllScript.getTests());
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELTEST_ACTION, patIllScript.getSessionId(), ((Relation)o).getSourceId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getTests()==null || patIllScript.getTests().isEmpty()){
			//todo error msg
			return;		
		}
		RelationTest rel = patIllScript.getTestById(Long.parseLong(id));
		patIllScript.getTests().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getTests());
		//TODO:
		//new DelConnectionAction(patIllScript).deleteConnsByTargetId(rel.getId());
		notifyLog(rel);
		save(rel);
	}
}
