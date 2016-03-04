package model;
/**
 * Diagnoses associated with (Patient) IllnessScripts.  
 * @author ingahege
 *
 */
public class Diagnosis extends Node implements IllnessScriptValue{
	//private long id = -1;
	//private String name; //might not be necessary if retrieved from a list
	private int category = -1; //real diagnosis (e.g. ICD-10, patho mechanism etc)

}
