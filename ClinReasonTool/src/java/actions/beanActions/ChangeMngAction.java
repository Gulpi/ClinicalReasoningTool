package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationManagement;

import database.DBClinReason;
import database.DBList;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeMngAction implements ChgAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public ChangeMngAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void changeMng(String oldProbIdStr, String newProbIdStr){
		long oldProbId = Long.valueOf(oldProbIdStr.trim());
		long newProbId = Long.valueOf(newProbIdStr.trim());
		changeMng(oldProbId, newProbId);
	}
	
	public void changeMng(long newMngId, long mngRel){
		RelationManagement mngToChg = patIllScript.getMngById(mngRel);
		ListItem oldMng = new DBList().selectListItemById(mngToChg.getListItemId());
		ListItem newMng = new DBList().selectListItemById(newMngId);
		if(mngToChg!=null && newMng!=null && oldMng!=null){
			notifyLog(mngToChg, newMngId);
			mngToChg.setManagement(newMng);
			mngToChg.setListItemId(newMng.getItem_id());
			save(mngToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans mngToChg, long newMngId){
		LogEntry le = new LogEntry(LogEntry.CHGMNG_ACTION, patIllScript.getId(), ((Relation)mngToChg).getListItemId(), newMngId);
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
