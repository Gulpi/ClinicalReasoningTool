package model;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;


import java.util.*;
/**
 * Each VP/case can have an associated IllnessScript which includes the basic data of the disease(s) covered in the 
 * case.
 * @author ingahege
 *
 */
@ManagedBean
@SessionScoped
public class IllnessScript extends Node implements IllnessScriptInterface{

	//private int age_min = -1;
	//private int age_max = -1;
	/**
	 * -1= gender not relevant, 1=male is precondition, 2=female is precondition (females more often affected)
	 */
	//private int gender = -1;
	/**
	 * 1=acute, 2=subacute, 3=chronic
	 */
	private int courseOfTime = -1; //do we need a range here?
	
	//private List<Rel_IS_Problem> problems; //we might need a connector object for attributes?
	/**
	 * related diagnoses to the covered diagnosis - we could also link to related IllnessScripts?
	 */
	private List<Rel_IS_Diagnosis> ddx;
	/**
	 * Diagnosis this IllnessScript is covering
	 */
	private Diagnosis diagnosis; 
	
	/**
	 * for final diagnosis
	 */
	private List<Rel_IS_Management> managements;
	
	/**
	 * Here we store all values that are important for this illness script. See definitions above. 
	 */
	private Map<IllnessScriptKey, IllnessScriptValue> values;
	
	/**
	 * external resources (such as uTube videos or websites) that have additional information for this IS
	 */
	private List<String> externalResources; 
	
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
