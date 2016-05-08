package actions.beanActions;

import java.beans.Beans;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringFinalDDXAction;
import actions.scoringActions.ScoringListAction;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import controller.ErrorMessageController;
import controller.NavigationController;
import database.DBClinReason;

/**
 * Learner has to actively submit ddx...
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction /*implements Scoreable*/{

	private PatientIllnessScript patIllScript;
	private static final float scoreForAllowReSubmit = (float) 0.5; //of learner gets less than 50% correct on final diagnoses, we allow re-submit
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void submitDDX(){
		if(patIllScript.getSubmitted()){
			createErrorMessage("DDX already submitted", "", FacesMessage.SEVERITY_WARN);
			return; //todo error message
		}
		
		float score = triggerScoringAction(patIllScript);
		//if learner submits the wrong diagnoses we allow him to re-submit 
		if(score<scoreForAllowReSubmit){
			patIllScript.setSubmittedStage(patIllScript.getCurrentStage());
			new DBClinReason().saveAndCommit(patIllScript);
		}
		notifyLog();
		
		//all the final ddx should have been tagged before, so no need to save the final ddx here....
		//we could now display some feedback, experts final diagnoses.
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	private void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageController().addErrorMessage(summary, details, sev);
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
		//re-score the ddx list.... or re-score the item?
		//new ScoringListAction(patIllScript).scoreList(ScoreBean.TYPE_DDX_LIST, Relation.TYPE_DDX);
		notifyLogChgTier(rel.getId(), rel.getTier());
	}

	public float triggerScoringAction(Beans beanToScore) {
		return new ScoringFinalDDXAction().scoreAction(-1, patIllScript);
		
	}
	
	private void notifyLog(){
		LogEntry log = new LogEntry(LogEntry.SUBMITDDX_ACTION, patIllScript.getId(), -1);
		log.save();
	}
	
	private void notifyLogChgTier(long relId, int tier){
		LogEntry log = new LogEntry(LogEntry.CHGDDXTIER_ACTION, patIllScript.getId(), relId, tier);
		log.save();
	}

}
