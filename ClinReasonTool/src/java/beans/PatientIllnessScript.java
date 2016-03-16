package beans;
import java.beans.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;

import beanActions.*;
import beans.relation.*;
import controller.ConceptMapController;
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

	public static final int TYPE_LEARNER_CREATED = 1;
	public static final int TYPE_EXPERT_CREATED = 2;
	
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
	 * the VP the patIllScript is related to. We need this in addition to the sessionId to be able to connect 
	 * the learners' script to the authors'/experts' script.
	 */
	private long vpId;
	/**
	 * created by learner or expert
	 */
	private int type = TYPE_LEARNER_CREATED;
	/**
	 * List of related problems to the PatientIllnessScript
	 */
	private List<RelationProblem> problems;
	private List<RelationDiagnosis> diagnoses; //contains all diagnoses, including the final(s)?
	//private List<Rel_IS_Management> managements;
	//private List<Rel_IS_Test> tests;
	
	private Map conns;
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
	
	public PatientIllnessScript(){}
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
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public void addProblem(String idStr, String name){ new AddProblemAction(this).add(idStr, name);}
	public void addProblem(String idStr, String name, String x, String y){ new AddProblemAction(this).add(idStr, name, x,y);}
	public void delProblem(String idStr, String name){ new DelProblemAction(this).delete(idStr);}
	public void reorderProblems(String idStr, String newOrderStr){ new MoveProblemAction(this).reorder(idStr, newOrderStr);}
	public void changeProblem(String probRelIdStr,String newProbId){new ChangeProblemAction(this).changeProblem(probRelIdStr, newProbId);}
	public void addDiagnosis(String idStr, String name){ new AddDiagnosisAction(this).add(idStr, name);}
	public void delDiagnosis(String idStr, String name){ new DelDiagnosisAction(this).delete(idStr);}
	public void reorderDiagnoses(String idStr, String newOrderStr){ new MoveDiagnosisAction(this).reorder(idStr, newOrderStr);}
	public void addConnection(String sourceId, String targetId){new AddConnectionAction(this).add(sourceId,targetId);}
	public void delConnection(String idStr){new DelConnectionAction(this).delete(idStr);}
	public PatientIllnessScript getExpertPatIllScript() {return expertPatIllScript;}
	public void setExpertPatIllScript(PatientIllnessScript expertPatIllScript) {this.expertPatIllScript = expertPatIllScript;}	
	public long getVpId() {return vpId;}
	public void setVpId(long vpId) {this.vpId = vpId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	public List<RelationDiagnosis> getDiagnoses() {return diagnoses;}
	public void setDiagnoses(List<RelationDiagnosis> diagnoses) {this.diagnoses = diagnoses;}
	public Map getConns() {return conns;}
	public void setConns(Map conns) {this.conns = conns;}
	public String getProblemsJson(){ return new ConceptMapController().getRelationsToJson(problems);}
	public String getDdxJson(){return new ConceptMapController().getRelationsToJson(diagnoses);}
	public String getConnsJson(){return new ConceptMapController().getConnsToJson(conns);}
	
	public void save(){new DBClinReason().saveAndCommit(this);}
	
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
	
	public RelationProblem getProblemBySourceId(long id){return (RelationProblem) getRelationBySourceId(problems, id);}	
	public RelationDiagnosis getDiagnosisBySourceId(long id){return (RelationDiagnosis) getRelationBySourceId(diagnoses, id);}
	public RelationProblem getProblemById(long id){return (RelationProblem) getRelationById(problems, id);}	
	public RelationDiagnosis getDiagnosisById(long id){return (RelationDiagnosis) getRelationById(diagnoses, id);}
	
	private Relation getRelationBySourceId(List items, long sourceId){
		if(items==null || items.isEmpty()) return null;
		for(int i=0; i< items.size(); i++){
			Relation rel = (Relation) items.get(i);
			if(rel.getSourceId()==sourceId) return rel;
		}
		return null; //nothing found
	}
	
	private Relation getRelationById(List items, long itemId){
		if(items==null || items.isEmpty()) return null;
		for(int i=0; i< items.size(); i++){
			Relation rel = (Relation) items.get(i);
			if(rel.getId()==itemId) return rel;
		}
		return null; //nothing found
	}
}
