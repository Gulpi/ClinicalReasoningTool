package actions.beanActions;

import java.util.*;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import controller.NavigationController;
import database.DBClinReason;

public class DiagnosisSubmissionRevertAction {

	
	private PatientIllnessScript patIllScript;
	
	public DiagnosisSubmissionRevertAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/**
	 * We remove the final diagnosis flag for all WRONG (<0.5) diagnoses, so that the learner can retry...
	 */
	public void revertSubmission(){
		List<RelationDiagnosis> finalDDX = patIllScript.getFinalDiagnoses();
		List<ScoreBean> scores = new NavigationController().getCRTFacesContext().getLearningAnalytics().getScoreContainer().getScoresByType(ScoreBean.TYPE_FINAL_DDX);
		if(finalDDX==null) return;
		for(int i=0; i<finalDDX.size(); i++){
			RelationDiagnosis ddx = finalDDX.get(i);
			ScoreBean score =  new NavigationController().getCRTFacesContext().getLearningAnalytics().getScoreContainer().getScoreBeanByTypeAndItemId(ScoreBean.TYPE_FINAL_DDX, ddx.getListItemId());
			if(score.getScoreBasedOnExp()<DiagnosisSubmitAction.scoreForAllowReSubmit) 
				ddx.setTier(RelationDiagnosis.TIER_NONE);			
			 notifyLog(ddx);
		}
		new DBClinReason().saveAndCommit(finalDDX);
	}
	
	private void notifyLog(Relation rel){
		LogEntry log = new LogEntry(LogEntry.CHGDDXTIER_ACTION, patIllScript.getId(), rel.getListItemId());
		log.save();
	}
}
