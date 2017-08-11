package actions.beanActions;

import java.beans.Beans;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import actions.scoringActions.ScoringFinalDDXAction;
import application.ErrorMessageContainer;
import beans.LogEntry;
import beans.scripts.*;
import controller.ScoringController;
import controller.XAPIController;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import database.DBClinReason;
import model.ListItem;
import util.StringUtilities;

/**
 * Learner has to actively submit ddx as final diagnoses or choose "no diagnosis" (if enabled)
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction /*implements Scoreable*/{

	private PatientIllnessScript patIllScript;
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}

	public void submitDDXAndConf(String idsStr, String confStr){		
		new ChgPatIllScriptAction(patIllScript).changeConfidence(confStr); //first we handle the confidence stuff....
		submitDDX(idsStr);
	}
	
	/**
	 * Expert submits a final diagnosis
	 * @param idStr
	 */
	public void submitExpFinalDiagnosis(String idStr){
		long id = Long.valueOf(idStr).longValue();
		setAndSaveFinal(id);
		//only change submittedStage if it has not yet been set or if the new submittedStage is lower than the current one.
		//@deprecated because we have the submitted stage in the relation now.
		if(patIllScript.getSubmittedStage()<=0 || patIllScript.getSubmittedStage()>patIllScript.getStage()){
			patIllScript.setSubmittedStage(patIllScript.getStage());
			new DBClinReason().saveAndCommit(patIllScript);
		}
	}
		
	/**
	 * Called when diagnosis are submitted one at a time (link for submission provided for each diagnosis). 
	 * We then have to change the tier for the diagnosis to "final" before scoring.
	 * @param idStr (all ids of final diagnoses separated with #)
	 */
	public void submitDDX(String idStr){
		//if idStr = 0, then learner has chosen "no diagnosis" option
		
		boolean hasDDX = changeTierForDDXs(idStr);
		if(!hasDDX) return;
		
		float score = triggerScoringAction(patIllScript);
		//if learner has correctly chosen "no diagnosis" we create a dummy RelationDiagnosis for display purposes:
		if(score==1 && this.patIllScript.getFinalDDXType()==PatientIllnessScript.FINAL_DDX_NO){
			new AddNoDiagnosisAction(this.patIllScript).add();
		}
		//if learner submits the wrong diagnoses we allow him to re-submit 
		if(score>=ScoringController.scoreForAllowReSubmit){
			patIllScript.setSubmittedStage(patIllScript.getCurrentStage());
			patIllScript.save();
		}
		if(!patIllScript.isExpScript()) XAPIController.getInstance().addFinalDiagnosisActionStatement(patIllScript);

		notifyLog();
	}
	
	private boolean changeTierForDDXs(String idStr){
		long[] ids = StringUtilities.getLongArrFromString(idStr, "#");
		if(ids==null || ids.length==0){
			createErrorMessage("Diagnosis submission without DDX", "", FacesMessage.SEVERITY_ERROR);
			return false;
		}
		if(ids[0]==0){ //"no diagnosis" option has been chosen!
			this.patIllScript.setFinalDDXType(PatientIllnessScript.FINAL_DDX_NO);
		}
		else{
			for(int i=0; i<ids.length; i++){
				setFinal( ids[i]);
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	private void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageContainer().addErrorMessage(summary, details, sev);
	}


	private RelationDiagnosis setFinal(long id){
		RelationDiagnosis rel = patIllScript.getDiagnosisById(id);
		if(rel==null || rel.isFinalDDX()) return null;
		int stage = patIllScript.getCurrentStage();
		if(patIllScript.isExpScript()) stage = patIllScript.getStage();
		rel.setFinalDiagnosis(stage);
		new DBClinReason().saveAndCommit(rel);
		return rel;
	}
	
	private void setAndSaveFinal(long id){
		RelationDiagnosis rel = setFinal(id);
		if(rel!=null) new DBClinReason().saveAndCommit(rel);
	}
	
	private void changeTier(long id, int tier){
		RelationDiagnosis rel = patIllScript.getDiagnosisById(id);
		if(rel==null) return;
		//if(tier == RelationDiagnosis.TIER_FINAL) rel.toggleFinal();
		else if(tier==RelationDiagnosis.TIER_RULEDOUT){
			rel.toggleRuledOut();
		}
		else if (tier==RelationDiagnosis.TIER_WORKINGDDX){
			rel.toggleWorkingDDX();
		}
		
		new DBClinReason().saveAndCommit(rel);
		//re-score the ddx list.... or re-score the item?
		//new ScoringListAction(patIllScript).scoreList(ScoreBean.TYPE_DDX_LIST, Relation.TYPE_DDX);
		notifyLogChgTier(rel.getListItemId(), rel.getTier());		
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
	
	private void notifyLogShowSolution(){
		 new LogEntry(LogEntry.SHOWSOL_ACTION, patIllScript.getId(), -1).save();
	}
	
	/**
	 * We save now the submitted stage even if the learner's answer was not correct. This will allow him to 
	 * see the author's solution.
	 */
	public void showSolution(){
		patIllScript.setSubmittedStage(patIllScript.getCurrentStage());
		patIllScript.setShowSolution(patIllScript.getCurrentStage());
		patIllScript.save();
		notifyLogShowSolution();
	}
	
	public void updateXAPIStatement(Relation rel){
		XAPIController.getInstance().addOrUpdateAddStatement(rel);
	}

}
