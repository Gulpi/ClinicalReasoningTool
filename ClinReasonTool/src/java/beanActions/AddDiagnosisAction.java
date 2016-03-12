package beanActions;

import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import beans.*;
import beans.relation.*;
import database.DBClinReason;
import errors.WrongProblemError;
import scoringActions.ProblemScoring;

public class AddDiagnosisAction implements AddAction{

	private PatientIllnessScript patIllScript;
	
	public AddDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String)
	 */
	public void add(String idStr, String name){ addDiagnosis(idStr, name);}
	
	private void addDiagnosis(String idStr, String name){
		long id = Long.valueOf(idStr.trim());
		addDiagnosis(id, name);
	}
	
	private void addDiagnosis(long id, String name){
		if(patIllScript.getDiagnoses()==null) patIllScript.setDiagnoses(new ArrayList<RelationDiagnosis>());
		RelationDiagnosis relDDX = new RelationDiagnosis(id, patIllScript.getId());		
		if(patIllScript.getDiagnoses().contains(relDDX)){
			createErrorMessage("Diagnosis already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		relDDX.setOrder(patIllScript.getDiagnoses().size());
		patIllScript.getDiagnoses().add(relDDX);
		relDDX.setDiagnosis(new DBClinReason().selectListItemById(id));
		save(relDDX);
		notifyLog(relDDX);
		initScoreCalc(relDDX);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		 // MyFacesContextFactory factory = (MyFacesContextFactory) FactoryFinder.getFactory("MyFacesContextFactory");
		 // CRTFacesContext facesContext = factory.getFacesContextBySessionId(patIllScript.getSessionId());
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){
		//TODO save whole problems collection?
		new DBClinReason().saveAndCommit(b);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relProb){
		LogEntry le = new LogEntry(LogEntry.ADDDIAGNOSIS_ACTION, patIllScript.getSessionId(), relProb.getSourceId());
		le.save();
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#initScoreCalc(beans.relation.Relation)
	 */
	public void initScoreCalc(Relation relProb){
		//try{
			//new ProblemScoring().calcScoreForAddDiagb(patIllScript, relProb);
		//}
		//catch(WrongProblemError wpe){}
	}
}
