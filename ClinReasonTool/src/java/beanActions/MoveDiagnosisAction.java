package beanActions;

import java.beans.Beans;
import java.util.*;

import beans.*;
import beans.relation.*;
import database.DBClinReason;

public class MoveDiagnosisAction implements MoveAction{

	private PatientIllnessScript patIllScript;
	
	public MoveDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.MoveAction#save()
	 */
	public void save(List l){
		new DBClinReason().saveAndCommit(l);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.MoveAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		LogEntry le = new LogEntry(LogEntry.MOVEDIAGNOSIS_ACTION, patIllScript.getSessionId(), rel.getSourceId(), rel.getOrder());
		le.save();			
	}

	/* (non-Javadoc)
	 * @see beanActions.MoveAction#reorder(java.lang.String)
	 */
	public void reorder(String idStrMovedItem,  String newOrderStr) {		
		String[] newOrderArr = newOrderStr.split("&");
		List<RelationDiagnosis> newList  = new ArrayList<RelationDiagnosis>();
		if(newOrderArr==null || newOrderArr.length==0) return;
		for(int i=0; i<newOrderArr.length; i++){
			String idStr = newOrderArr[i];
			idStr = idStr.substring(8);	
			idStrMovedItem = idStrMovedItem.substring(8);
			RelationDiagnosis rel = this.patIllScript.getDiagnosisById(Long.parseLong(idStr));
			if(idStrMovedItem.equals(idStr)) notifyLog(rel);
			rel.setOrder(i);
			newList.add(rel);
		}
		patIllScript.setDiagnoses(newList);
		save(patIllScript.getDiagnoses());
	}
}
