package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
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
public class ChangeDiagnosisAction implements ChgAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public ChangeDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void changeDiagnosis(String oldDDXIdStr, String newDDXIdStr){
		long oldDDXId = Long.valueOf(oldDDXIdStr.trim());
		long newDDXId = Long.valueOf(newDDXIdStr.trim());
		changeDiagnosis(oldDDXId, newDDXId);
	}
	
	public void changeDiagnosis(long newDDXId, long ddxRel){
		RelationDiagnosis ddxToChg = patIllScript.getDiagnosisById(ddxRel);
		ListItem oldDDX = new DBClinReason().selectListItemById(ddxToChg.getListItemId());
		ListItem newDDX = new DBClinReason().selectListItemById(newDDXId);
		if(ddxToChg!=null && newDDX!=null && oldDDX!=null){
			notifyLog(ddxToChg, newDDXId);
			ddxToChg.setDiagnosis(newDDX);
			ddxToChg.setListItemId(newDDX.getItem_id());
			save(ddxToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans ddxToChg, long newDDXId){
		LogEntry le = new LogEntry(LogEntry.CHGDDX_ACTION, patIllScript.getSessionId(), ((Relation) ddxToChg).getListItemId(), newDDXId);
		le.save();
	}

	private void notifyMnMLog(Beans ddxToChg, int newMnM){
		LogEntry le = new LogEntry(LogEntry.CHGDDXMNM_ACTION, patIllScript.getSessionId(), ((Relation) ddxToChg).getListItemId(), newMnM);
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}
	
	/**
	 * We toogle the Must-Not_miss flag and change the color of the rectangle in the concept map
	 * @param idStr
	 * @param newVal "0"|"1"
	 */
	public void toggleMnM(String idStr, String newVal){
		long id = Long.valueOf(idStr.trim());
		int mnm = Integer.valueOf(newVal.trim());
		RelationDiagnosis ddxToChg = patIllScript.getDiagnosisById(id);
		ddxToChg.setMnm(mnm);
		if(ddxToChg.isMnM()) ddxToChg.setColor(RelationDiagnosis.COLOR_RED);
		else  ddxToChg.setColor(RelationDiagnosis.COLOR_DEFAULT);
		save(ddxToChg);
		notifyMnMLog(ddxToChg, mnm);
	}

	@Override
	public void triggerScoringAction(Beans beanToScore) {
		// TODO Auto-generated method stub
		
	}
}
