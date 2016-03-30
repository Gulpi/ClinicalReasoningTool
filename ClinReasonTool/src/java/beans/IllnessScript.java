package beans;

import javax.faces.bean.SessionScoped;

import beans.relation.*;
import beans.helper.*;
import java.beans.Beans;
import java.io.Serializable;


import java.util.*;
/**
 * Each VP/case can have an associated IllnessScript which includes the basic data of the disease(s) covered in the 
 * case. An IllnessScript represents a typical representation of a disease (which may vary from actual PatientIllnessScripts) 
 * IllnessScripts can be fetched in different ways depending on the type of feedback/use case: 
 * - based on the problems related to it (we need a matching algorithm for that) 
 * - based on the parentId or the final diagnosis of the expert's PatientIllnessScript 
 * - based on the ddx and final diagnoses of the learners' PatientIllnessScript 
 * - based on a search term (from the portfolio/overview page) 
 * @author ingahege
 *
 */
@SessionScoped
public class IllnessScript extends Beans implements Serializable, IllnessScriptInterface{

	private static final long serialVersionUID = 1L;

	/**
	 * -1= gender not relevant, 1=male is precondition, 2=female is precondition (females more often affected)
	 */
	//private int gender = -1;
	
	
	private long id; 
	private long userId; //user who created the IllnessScript -> later use
	
	/**
	 * The id of the parent object, e.g. a VP 
	 */	
	private long parentId;
	/**
	 * 1=acute, 2=subacute, 3=chronic, -1 = NA
	 */
	private int courseOfTime = -1; //do we need a range here?
	
	//private List<Rel_IS_Problem> problems; //we might need a connector object for attributes?
	/**
	 * related diagnoses to the covered diagnosis - we could also link to related IllnessScripts?
	 */
	//private List<Rel_IS_Diagnosis> ddx;
	/**
	 * Diagnosis this IllnessScript is covering
	 */
	private RelationDiagnosis diagnosis; 
	private long diagnosisId; 
	/**
	 * for final diagnosis
	 */
	private List<RelationManagement> managements;
	
	/**
	 * Here we store all values that are important for this illness script. See definitions above. 
	 */
	private Map<IllnessScriptKey, IllnessScriptValue> values;
	
	/**
	 * external resources (such as uTube videos or websites) that have additional information for this IS
	 */
	private List<String> externalResources;
	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public long getParentId() {return parentId;}
	public void setParentId(long parentId) {this.parentId = parentId;}
	public int getCourseOfTime() {return courseOfTime;}
	public void setCourseOfTime(int courseOfTime) {this.courseOfTime = courseOfTime;}
	public long getDiagnosisId() {return diagnosisId;}
	public void setDiagnosisId(long diagnosisId) {this.diagnosisId = diagnosisId;}	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	
	/* (non-Javadoc)
	 * @see beans.IllnessScriptInterface#getType()
	 */
	public int getType() {
		return IllnessScriptInterface.TYPE_ILLNESSSCRIPT;
	} 
	
	
	
	/*
	 * age: range and set (e.g. child, elderly,...)
	 * gender: set (predefined values)
	 * ethnicity: set
	 * smoking: yes/no
	 * BMI: set
	 * travel to (regions): set
	 * environmental influences: set (?) 
	 * other condition/therapy: Node object 
	 * occupation: set
	 * pets: yes/no or set
	 * hobbies: set
	 * sexual orientation: set
	 * close contact: yes/no
	 * 
	 */
}
