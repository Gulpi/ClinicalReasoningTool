package beanActions;

import java.beans.Beans;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationManagement;

import database.DBClinReason;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeMngAction implements ChgAction{

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
		ListItem oldMng = new DBClinReason().selectListItemById(mngToChg.getSourceId());
		ListItem newMng = new DBClinReason().selectListItemById(newMngId);
		if(mngToChg!=null && newMng!=null && oldMng!=null){
			notifyLog(mngToChg, newMngId);
			mngToChg.setManagement(newMng);
			mngToChg.setSourceId(newMng.getItem_id());
			save(mngToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans mngToChg, long newMngId){
		LogEntry le = new LogEntry(LogEntry.CHGMNG_ACTION, patIllScript.getSessionId(), ((Relation)mngToChg).getSourceId(), newMngId);
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}
}
