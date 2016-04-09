package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringFinalDDXAction;
import actions.scoringActions.ScoringListAction;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import controller.NavigationController;
import database.DBClinReason;

/**
 * Learner has to actively submit ddx...
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction implements Scoreable{

	private PatientIllnessScript patIllScript;
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void submitDDX(){
		patIllScript.setSubmittedStage(patIllScript.getCurrentStage());
		new DBClinReason().saveAndCommit(patIllScript);
		notifyLog();
		
		//all the final ddx should have been tagged before, so no need to save the final ddx here....
		//we could now display some feedback, experts final diagnoses.
	}
	
	/**
	 * @param idStr
	 * @param tierStr (see definitions above)
	 */
	public void changeTier(String idStr, String tierStr){
		long id = Long.valueOf(idStr.trim());
		RelationDiagnosis rel = patIllScript.getDiagnosisById(id);
		rel.setTier(Integer.valueOf(tierStr.trim()));
		new DBClinReason().saveAndCommit(rel);
		new ScoringListAction(patIllScript).scoreList(Relation.TYPE_DDX);
	}

	public void triggerScoringAction(Beans beanToScore) {
		new ScoringFinalDDXAction().scoreAction(-1, patIllScript.getId());
		
	}
	
	private void notifyLog(){
		LogEntry log = new LogEntry(LogEntry.SUBMITDDX_ACTION, patIllScript.getId(), -1);
		log.save();
	}

}
