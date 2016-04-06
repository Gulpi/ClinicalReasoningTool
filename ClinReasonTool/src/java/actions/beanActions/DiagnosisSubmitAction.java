package actions.beanActions;

import beans.PatientIllnessScript;
import database.DBClinReason;

/**
 * Learner has to decided to submit ddx...
 * @author ingahege
 *
 */
public class DiagnosisSubmitAction {

	private PatientIllnessScript patIllScript;
	
	public DiagnosisSubmitAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	public void submitDDX(){
		patIllScript.setSubmitted(true);
		new DBClinReason().saveAndCommit(patIllScript);
		//what to do now....
	}
}
