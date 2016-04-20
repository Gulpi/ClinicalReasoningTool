package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;
import database.DBList;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeProblemAction implements ChgAction, Scoreable{

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
		ListItem oldProblem = new DBList().selectListItemById(probToChg.getListItemId());
		ListItem newProblem = new DBList().selectListItemById(newProbId);
		if(probToChg!=null && newProblem!=null && oldProblem!=null){
			notifyLog(probToChg, newProbId);
			probToChg.setProblem(newProblem);
			probToChg.setListItemId(newProblem.getItem_id());
			save(probToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans probToChg, long newProbId){
		LogEntry le = new LogEntry(LogEntry.CHGPROBLEM_ACTION, patIllScript.getId(), ((Relation)probToChg).getListItemId(), newProbId);
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}

	@Override
	public void triggerScoringAction(Beans beanToScore) {
		// TODO Auto-generated method stub
		
	}
}
