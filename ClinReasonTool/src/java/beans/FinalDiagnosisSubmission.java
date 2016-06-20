package beans;

import java.util.*;

import beans.error.MyError;
import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import controller.NavigationController;
import beans.scripts.*;
/**
 * View for the final diagnoses submission process....
 * @author ingahege
 * @deprecated (?)
 */
public class FinalDiagnosisSubmission {

		private PatientIllnessScript patillscript;
		//private List<MyError> errors;
		List<RelationDiagnosis> finalDDXs;
		List<ScoreBean> scores;
		
		public FinalDiagnosisSubmission(PatientIllnessScript patillscript){
			this.patillscript = patillscript;
			//errors = patillscript.getErrors();
			finalDDXs = patillscript.getFinalDiagnoses();
			scores = new NavigationController().getCRTFacesContext().getLearningAnalytics().getScoreContainer().getScoresByType(ScoreBean.TYPE_FINAL_DDX);
		}
		public List<RelationDiagnosis> getFinalDDXs(){ return finalDDXs;}
		public List<ScoreBean> getScores(){
			 return scores;
		}
		
		/**
		 * We offer the user to retry the diagnoses submission if he has less than 100%
		 * TODO: we might make this more specific
		 * @return
		 */
		public boolean getOfferTryAgain(){
			if(scores==null || scores.isEmpty()) return true;
			for(int i=0; i<scores.size(); i++){
				if(scores.get(i).getScoreBasedOnExpPerc()<1) return true;
			}
			return false;
		}
}
