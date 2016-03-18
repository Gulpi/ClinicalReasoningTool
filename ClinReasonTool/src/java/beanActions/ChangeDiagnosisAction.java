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
public class ChangeDiagnosisAction implements ChgAction{

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
		ListItem oldDDX = new DBClinReason().selectListItemById(ddxToChg.getSourceId());
		ListItem newDDX = new DBClinReason().selectListItemById(newDDXId);
		if(ddxToChg!=null && newDDX!=null && oldDDX!=null){
			notifyLog(ddxToChg, newDDXId);
			ddxToChg.setDiagnosis(newDDX);
			ddxToChg.setSourceId(newDDX.getItem_id());
			save(ddxToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Relation ddxToChg, long newDDXId){
		LogEntry le = new LogEntry(LogEntry.CHGDDX_ACTION, patIllScript.getSessionId(), ddxToChg.getSourceId(), newDDXId);
		le.save();
	}

	public void notifyMnMLog(Relation ddxToChg, int newMnM){
		LogEntry le = new LogEntry(LogEntry.CHGDDXMNM_ACTION, patIllScript.getSessionId(), ddxToChg.getSourceId(), newMnM);
		le.save();
	}
	
	public void save(Relation rel){
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
}
