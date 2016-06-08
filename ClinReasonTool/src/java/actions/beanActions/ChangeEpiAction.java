package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.scripts.*;
import beans.relation.*;
import database.DBClinReason;
import database.DBList;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 * @deprecated
 */
public class ChangeEpiAction /*extends ChgAction*/{

	private PatientIllnessScript patIllScript;
	
	public ChangeEpiAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void changeEpi(String oldProbIdStr, String newProbIdStr){
		long oldProbId = Long.valueOf(oldProbIdStr.trim());
		long newProbId = Long.valueOf(newProbIdStr.trim());
		changeEpi(oldProbId, newProbId);
	}
	
	public void changeEpi(long newEpiId, long epiRel){
		RelationEpi epiToChg = patIllScript.getEpiById(epiRel);
		ListItem oldEpi = new DBList().selectListItemById(epiToChg.getListItemId());
		ListItem newEpi = new DBList().selectListItemById(newEpiId);
		if(epiToChg!=null && newEpi!=null && oldEpi!=null){
			notifyLog(epiToChg, newEpiId);
			epiToChg.setEpi(newEpi);
			epiToChg.setListItemId(newEpi.getItem_id());
			save(epiToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans epiToChg, long newEpiId){
		LogEntry le = new LogEntry(LogEntry.CHGPROBLEM_ACTION, patIllScript.getId(), ((Relation)epiToChg).getListItemId(), newEpiId);
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}

	public void triggerScoringAction(Beans beanToScore, boolean isJoker) {
		// TODO Auto-generated method stub
		
	}

}
