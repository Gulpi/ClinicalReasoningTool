package beanActions;

import java.beans.Beans;
import java.util.*;

import beans.*;
import beans.relation.RelationTest;
import beans.relation.Relation;
import database.DBClinReason;

public class MoveTestAction implements MoveAction{

	private PatientIllnessScript patIllScript;
	
	public MoveTestAction(PatientIllnessScript patIllScript){
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
		LogEntry le = new LogEntry(LogEntry.MOVETEST_ACTION, patIllScript.getSessionId(), relBeforeReSort.getSourceId(), relBeforeReSort.getOrder());
		le.save();			
	}

	/* (non-Javadoc)
	 * @see beanActions.MoveAction#reorder(java.lang.String)
	 */
	public void reorder(String idStrMovedItem, String newOrderStr) {		
		String[] newOrderArr = newOrderStr.split("&");
		List<RelationTest> newList  = new ArrayList<RelationTest>();
		if(newOrderArr==null || newOrderArr.length==0) return;
		for(int i=0; i<newOrderArr.length; i++){
			String idStr = newOrderArr[i];
			idStr = idStr.substring(8);	
			idStrMovedItem = idStrMovedItem.substring(8);
			RelationTest relProb = this.patIllScript.getTestBySourceId(Long.parseLong(idStr));
			if(idStrMovedItem.equals(idStr)) notifyLog(relProb);
			relProb.setOrder(i);
			newList.add(relProb);
		}
		patIllScript.setTests(newList);
		save(patIllScript.getTests());
		//now we change the order attribute of each Relationproblem object:
		//new ActionHelper().reOrderItems(this.patIllScript.getProblems());
	}
}
