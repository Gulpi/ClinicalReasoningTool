package beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;
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
public class PatientIllnessScript /*extends Node*/ implements /*IllnessScriptInterface, */Serializable{

	private Timestamp creationDate; //do we really have that, entering of first item?
	private long sessionId = -1; //necessary or store PISId in C_Session?
	private long id = -1;
	//epi data:
	//private Patient patient; //data from the patient object attached to a case
	private long summaryStatementId = -1; //links the summaryStatement object
	private String test ="hallo";
	/**
	 * We might want to link the illness script directly here, might be faster for comparisons than going through
	 * the session. If final diagnosis is wrong the relation might not be doable thru it...
	 */
	private long illnessScriptId = -1; 
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
	
	//scores: maybe put into separate class?
	private float summStScore = -1; // we might categories here
	private float problemScore = -1; //num and quality of problems
	private float ddxScore = -1; //num, level and correctness of diagnoses
	private float finalDiagnosisScore = -1; //level and correctness of diagnosis
	
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
	public List<RelationProblem> getProblems() {return problems;}
	public void setProblems(List<RelationProblem> problems) {this.problems = problems;}
	
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	
	public void addProblem(String problemIdStr){
		long problemId = Long.valueOf(problemIdStr.trim());
		addProblem(problemId);
	}
	
	public void addProblem(long problemId){
		if(problems==null) problems = new ArrayList<RelationProblem>();
		RelationProblem relProb = new RelationProblem(problemId, this.id);
		//TODO we have to check whether is already in list...
		problems.add(relProb);
		save();
		
	}
	private void save(){
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
	
}
