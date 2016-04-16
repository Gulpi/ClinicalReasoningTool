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
import controller.NavigationController;
import database.DBClinReason;

/**
 * Learner has to actively submit ddx...
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction /*implements Scoreable*/{

	private PatientIllnessScript patIllScript;
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void submitDDX(){
		if(patIllScript.getSubmitted()){
			createErrorMessage("DDX already submitted", "", FacesMessage.SEVERITY_WARN);
			return; //todo error message
		}
		
		ScoreBean scoreBean = triggerScoringAction(patIllScript);
		//if learner submits the wrong diagnoses we allow him to submit 
		if(scoreBean.getScoreBasedOnExp()==0){
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
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
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
		notifyLogChgTier(rel.getId(), rel.getTier());
	}

	public ScoreBean triggerScoringAction(Beans beanToScore) {
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
