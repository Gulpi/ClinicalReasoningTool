package actions.beanActions;

import java.beans.Beans;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringFinalDDXAction;
import actions.scoringActions.ScoringListAction;
import application.ErrorMessageContainer;
import beans.LogEntry;
import beans.scripts.*;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import controller.NavigationController;
import database.DBClinReason;
import util.StringUtilities;

/**
 * Learner has to actively submit ddx...
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction /*implements Scoreable*/{

	private PatientIllnessScript patIllScript;
	public static final float scoreForAllowReSubmit = (float) 0.5; //of learner gets less than 50% correct on final diagnoses, we allow re-submit
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}

	public void submitDDXAndConf(String idsStr, String confStr){		
		new ChgPatIllScriptAction(patIllScript).changeConfidence(confStr); //first we handle the confidence stuff....
		submitDDX(idsStr);
	}
	
	
	
	/**
	 * Called when diagnosis are submitted one at a time (link for submission provided for each diagnosis). 
	 * We then have to change the tier for the diagnosis to "final" before scoring.
	 * @param idStr
	 */
	public void submitDDX(String idStr){
		boolean hasDDX = changeTierForDDXs(idStr);
		if(!hasDDX) return;
		float score = triggerScoringAction(patIllScript);
		//if learner submits the wrong diagnoses we allow him to re-submit 
		if(score<scoreForAllowReSubmit){
			patIllScript.setSubmittedStage(patIllScript.getCurrentStage());
			new DBClinReason().saveAndCommit(patIllScript);
		}
		notifyLog();
	}
	
	private boolean changeTierForDDXs(String idStr){
		long[] ids = StringUtilities.getLongArrFromString(idStr, "#");
		if(ids==null || ids.length==0){
			createErrorMessage("Diagnosis submission without DDX", "", FacesMessage.SEVERITY_ERROR);
			return false;
		}
		for(int i=0; i<ids.length; i++){
			setFinal( ids[i]);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	private void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageContainer().addErrorMessage(summary, details, sev);
	}


	private void setFinal(long id){
		RelationDiagnosis rel = patIllScript.getDiagnosisById(id);
		if(rel==null || rel.isFinalDiagnosis()) return;	
		rel.setFinalDiagnosis();
	}
	
	private void changeTier(long id, int tier){
		RelationDiagnosis rel = patIllScript.getDiagnosisById(id);
		if(rel==null) return;
		//if(tier == RelationDiagnosis.TIER_FINAL) rel.toggleFinal();
		else if(tier==RelationDiagnosis.TIER_RULEDOUT) rel.toggleRuledOut();
		
		new DBClinReason().saveAndCommit(rel);
		//re-score the ddx list.... or re-score the item?
		//new ScoringListAction(patIllScript).scoreList(ScoreBean.TYPE_DDX_LIST, Relation.TYPE_DDX);
		notifyLogChgTier(rel.getId(), rel.getTier());		
	}
	
	/**
	 * tier can now only be 4 (final) or 5 (ruled out) and we toggle the tier... 
	 * @param idStr
	 * @param tierStr (see definitions above)
	 */
	public void changeTier(String idStr, String tierStr){
		long id = Long.valueOf(idStr.trim());		
		int tier = Integer.valueOf(tierStr.trim());
		changeTier(id, tier);
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
