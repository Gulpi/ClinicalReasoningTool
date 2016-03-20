package beanActions;

import java.beans.Beans;
import java.util.*;

import beans.*;
import beans.relation.RelationManagement;
import beans.relation.Relation;
import database.DBClinReason;

public class MoveMngAction implements MoveAction{

	private PatientIllnessScript patIllScript;
	
	public MoveMngAction(PatientIllnessScript patIllScript){
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
	public void notifyLog(Relation relBeforeReSort) {
		LogEntry le = new LogEntry(LogEntry.MOVEMNG_ACTION, patIllScript.getSessionId(), relBeforeReSort.getSourceId(), relBeforeReSort.getOrder());
		le.save();			
	}

	/* (non-Javadoc)
	 * @see beanActions.MoveAction#reorder(java.lang.String)
	 */
	public void reorder(String idStrMovedItem, String newOrderStr) {		
		String[] newOrderArr = newOrderStr.split("&");
		List<RelationManagement> newList  = new ArrayList<RelationManagement>();
		if(newOrderArr==null || newOrderArr.length==0) return;
		for(int i=0; i<newOrderArr.length; i++){
			String idStr = newOrderArr[i];
			idStr = idStr.substring(8);	
			idStrMovedItem = idStrMovedItem.substring(8);
			RelationManagement relProb = this.patIllScript.getMngBySourceId(Long.parseLong(idStr));
			if(idStrMovedItem.equals(idStr)) notifyLog(relProb);
			relProb.setOrder(i);
			newList.add(relProb);
		}
		patIllScript.setMngs(newList);
		save(patIllScript.getMngs());
		//now we change the order attribute of each Relationproblem object:
		//new ActionHelper().reOrderItems(this.patIllScript.getProblems());
	}
}
