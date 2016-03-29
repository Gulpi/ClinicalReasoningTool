package beans;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.SessionScoped;
import database.DBClinReason;

/**
 * Contains all elements we need for the different types of Feedback, such as the illnessScripts,
 * patientIllnessScript of the expert for this VP, or any peer stuff we want to display.
 * 
 * @author ingahege
 *
 */
@SessionScoped
public class FeedbackBean extends Beans implements Serializable{

	/**
	 * the patientIllnessScript created by the expert based on the VP
	 */
	//private PatientIllnessScript expertPatIllScript;
	/**
	 * A patient can suffer from more than one diagnosis, therefore, a patientIllnessScript can be attached 
	 * to more than one IllnessScript. We load the illnessScripts based on diagnoses attached to the expertPatIllScript
	 * OR if there is no expertPatIllScript we have to load them based on the diagnoses in the learners patientIllnessScript.
	 * 
	 */
	//private List<IllnessScript> relatedIllnessScripts;

	
	public FeedbackBean(){};
	/**
	 * @param loadExpScript load the PatientIllnessScript of the expert for this VP
	 * @param loadAllIllScripts load related IllnessScripts for this VP 
	 * @param parentId id of the parent object, e.g. VP 
	 */
	public FeedbackBean(boolean loadAllIllScripts, long parentId){
		//if(loadAllIllScripts) -> this is already done when opening the tool for a VP
		//	this.relatedIllnessScripts = new DBClinReason().selectIllScriptByParentId(parentId);
		//we always have to load the expert' script because expert feedback is based on it. 
		//this.expertPatIllScript = new DBClinReason().selectExpertPatIllScript(parentId);
	}
	
	/*public PatientIllnessScript getExpertPatIllScript() {return expertPatIllScript;}
	public void setExpertPatIllScript(PatientIllnessScript expertPatIllScript) {this.expertPatIllScript = expertPatIllScript;}	
	public List<IllnessScript> getRelatedIllnessScripts() {return relatedIllnessScripts;}
	public void setRelatedIllnessScripts(List<IllnessScript> relatedIllnessScripts) {this.relatedIllnessScripts = relatedIllnessScripts;}
*/
}
