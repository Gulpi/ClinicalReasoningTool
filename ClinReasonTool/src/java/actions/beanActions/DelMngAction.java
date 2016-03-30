package actions.beanActions;

import java.util.*;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;

public class DelMngAction implements DelAction{
	private PatientIllnessScript patIllScript;
	
	public DelMngAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.DelAction#save(beans.relation.Relation)
	 */
	public void save(Object rel) {
		new DBClinReason().deleteAndCommit(rel);
		new DBClinReason().saveAndCommit(patIllScript.getMngs()); //orderNrs have changed, so we have to save all
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Object o) {
		LogEntry le = new LogEntry(LogEntry.DELMNG_ACTION, patIllScript.getSessionId(), ((Relation)o).getListItemId());
		le.save();		
	}

	/* (non-Javadoc)
	 * @see beanActions.DelAction#delete(java.lang.String)
	 */
	public void delete(String id) {
		if(id==null || id.trim().equals("") || patIllScript==null || patIllScript.getMngs()==null || patIllScript.getMngs().isEmpty()){
			//todo error msg
			return;		
		}
		RelationManagement rel = patIllScript.getMngById(Long.parseLong(id));
		patIllScript.getMngs().remove(rel);
		new ActionHelper().reOrderItems(patIllScript.getMngs());		
		notifyLog(rel);
		new DelConnectionAction(patIllScript).deleteConnsByTargetId(rel.getId());
		save(rel);
	}
}
