package beanActions;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeProblemAction implements ChgAction{

	private PatientIllnessScript patIllScript;
	
	public ChangeProblemAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void changeProblem(String oldProbIdStr, String newProbIdStr){
		long oldProbId = Long.valueOf(oldProbIdStr.trim());
		long newProbId = Long.valueOf(newProbIdStr.trim());
		changeProblem(oldProbId, newProbId);
	}
	
	public void changeProblem(long newProbId, long probRel){
		RelationProblem probToChg = patIllScript.getProblemById(probRel);
		ListItem oldProblem = new DBClinReason().selectListItemById(probToChg.getSourceId());
		ListItem newProblem = new DBClinReason().selectListItemById(newProbId);
		if(probToChg!=null && newProblem!=null && oldProblem!=null){
			notifyLog(probToChg, newProbId);
			probToChg.setProblem(newProblem);
			probToChg.setSourceId(newProblem.getItem_id());
			save(probToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Relation probToChg, long newProbId){
		LogEntry le = new LogEntry(LogEntry.CHGPROBLEM_ACTION, patIllScript.getSessionId(), probToChg.getSourceId(), newProbId);
		le.save();
	}
	
	public void save(Relation rel){
		new DBClinReason().saveAndCommit(rel);
	}
}
