package beans;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;

import beanActions.AddProblemAction;
import beans.relation.*;
import database.DBClinReason;

/**
 * This is the Illness script the learner creates during the VP session.
 * @author ingahege
 *
 */
/*@ManagedBean(name = "patillscript", eager = true)
@SessionScoped*/
/**
 * @author ingahege
 *
 */
public class PatientIllnessScript extends Beans/*extends Node*/ implements /*IllnessScriptInterface, */Serializable, PropertyChangeListener{

	private static final long serialVersionUID = 1L;
	private Timestamp creationDate;
	private long sessionId = -1; //necessary or store PISId in C_Session?
	private long id = -1;
	/**
	 * the patientIllnessScript created by the expert based on the VP
	 */
	private PatientIllnessScript expertPatIllScript;
	//epi data:
	//private Patient patient; //data from the patient object attached to a case
	//private long summaryStatementId = -1; //links the summaryStatement object
	/**
	 * We might want to link the illness script directly here, might be faster for comparisons than going through
	 * the session. If final diagnosis is wrong the relation might not be doable thru it...
	 */
	//private long illnessScriptId = -1; 
	/**
	 * 1=acute, 2=subacute, 3=chronic
	 */
	
	private int courseOfTime = -1;
	/**
	 * List of related problems to the PatientIllnessScript
	 */
	private List<RelationProblem> problems;
	//private List<Rel_IS_Diagnosis> ddx; //contains all diagnoses, including the final(s)?
	//private List<Rel_IS_Management> managements;
	//private List<Rel_IS_Test> tests;
	
	/**
	 * we might want to have this separately for quicker access, since this enables accessing IS
	 * more than one? Or do we have to have then multiple PIS/VP???
	 */
	//private Rel_IS_Diagnosis finalDiagnosis; 
	/**
	 * have the components of the IS been submitted by the learner? If yes for certain components no more changes 
	 * can be made (?)
	 */
	private boolean submitted;
	
	public PatientIllnessScript(){
		System.out.println("hallo");
	}
	public PatientIllnessScript(long sessionId){
		this.sessionId = sessionId;
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	public int getCourseOfTime() {return courseOfTime;}
	public void setCourseOfTime(int courseOfTime) {this.courseOfTime = courseOfTime;}
	public void chgCourseOfTime(String courseOfTimeStr) { 
		setCourseOfTime(Integer.parseInt(courseOfTimeStr));
		save();
	}
	public List<RelationProblem> getProblems() {return problems;}
	public void setProblems(List<RelationProblem> problems) {this.problems = problems;}
	public Timestamp getCreationDate(){ return this.creationDate;} //setting is done in DB
	public void addProblem(String problemIdStr, String name){ new AddProblemAction(this).add(problemIdStr, name);}	
	public PatientIllnessScript getExpertPatIllScript() {return expertPatIllScript;}
	public void setExpertPatIllScript(PatientIllnessScript expertPatIllScript) {this.expertPatIllScript = expertPatIllScript;}
	
	public void save(){		
		new DBClinReason().saveBean(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof PatientIllnessScript && ((PatientIllnessScript)o).id ==this.id)
				return true;
		}
		return false;
	}
	
	/*public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.addPropertyChangeListener(listener);
    }*/

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt!=null){
			System.out.println(evt.getPropertyName());
		}		
	}
}
