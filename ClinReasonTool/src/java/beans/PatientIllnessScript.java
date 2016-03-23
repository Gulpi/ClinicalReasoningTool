package beans;
import java.beans.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;

import beanActions.*;
import beans.relation.*;
import controller.AjaxController;
import controller.ConceptMapController;
import database.DBClinReason;

/**
 * This is the Illness script the learner creates during the VP session.
 * @author ingahege
 *
 */
/*@ManagedBean(name = "patillscript", eager = true)*/
@SessionScoped
/**
 * @author ingahege
 *
 */
public class PatientIllnessScript extends Beans/*extends Node*/ implements /*IllnessScriptInterface, */Serializable, PropertyChangeListener{

	public static final int TYPE_LEARNER_CREATED = 1;
	public static final int TYPE_EXPERT_CREATED = 2;
	
	private static final long serialVersionUID = 1L;
	private Timestamp creationDate;
	private long sessionId = -1; //necessary or store PISId in C_Session? and/or vpId?
	/**
	 * the VP the patIllScript is related to. We need this in addition to the sessionId to be able to connect 
	 * the learners' script to the authors'/experts' script.
	 */
	private long vpId;
	private long userId; //needed so that we can display all the users' scripts to him
	/**
	 * Id of the PatientIllnessScript
	 */
	private long id = -1;
	private Locale locale; // do we need this here to determine which list to load?
	/**
	 * the patientIllnessScript created by the expert based on the VP
	 */
	private PatientIllnessScript expertPatIllScript;
	
	private SummaryStatement summSt;
	private long summStId = -1;
	//epi data:
	//private Patient patient; //data from the patient object attached to a case
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
	 * created by learner or expert
	 */
	private int type = TYPE_LEARNER_CREATED;
	/**
	 * List of related problems to the PatientIllnessScript
	 */
	private List<RelationProblem> problems;
	private List<RelationDiagnosis> diagnoses; //contains all diagnoses, including the final(s)?
	private List<RelationManagement> mngs;
	private List<RelationTest> tests;
	
	/**
	 * key = cnxId (Long), value = Connection object
	 */
	private Map<Long,Connection> conns;
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
	public PatientIllnessScript(long sessionId, long userId, long vpId, Locale loc){
		if(sessionId>0) this.sessionId = sessionId;
		if(userId>0) this.userId = userId;
		if(vpId>0) this.vpId = vpId;
		this.locale = loc;
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
	public PatientIllnessScript getExpertPatIllScript() {return expertPatIllScript;}
	public void setExpertPatIllScript(PatientIllnessScript expertPatIllScript) {this.expertPatIllScript = expertPatIllScript;}	
	public long getVpId() {return vpId;}
	public void setVpId(long vpId) {this.vpId = vpId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	public List<RelationDiagnosis> getDiagnoses() {return diagnoses;}
	public void setDiagnoses(List<RelationDiagnosis> diagnoses) {this.diagnoses = diagnoses;}
	public List<RelationManagement> getMngs() {return mngs;}
	public void setMngs(List<RelationManagement> mngs) {this.mngs = mngs;}	
	public List<RelationTest> getTests() {return tests;}
	public void setTests(List<RelationTest> tests) {this.tests = tests;}
	public Map<Long,Connection> getConns() {return conns;}
	public void setConns(Map<Long,Connection> conns) {this.conns = conns;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}	
	public Locale getLocale() {return locale;}
	public void setLocale(Locale locale) {this.locale = locale;}	
	public SummaryStatement getSummSt() {return summSt;}
	public void setSummSt(SummaryStatement summSt) {this.summSt = summSt;}	
	public long getSummStId() {return summStId;}
	public void setSummStId(long summStId) {this.summStId = summStId;}
	
	public void addProblem(String idStr, String name){ new AddProblemAction(this).add(idStr, name);}
	public void addProblem(String idStr, String name, String x, String y){ new AddProblemAction(this).add(idStr, name, x,y);}
	public void addDiagnosis(String idStr, String name){ new AddDiagnosisAction(this).add(idStr, name);}
	public void addDiagnosis(String idStr, String name, String x, String y){ new AddDiagnosisAction(this).add(idStr, name, x,y);}
	public void addTest(String idStr, String name){ new AddTestAction(this).add(idStr, name);}
	public void addTest(String idStr, String name, String x, String y){ new AddTestAction(this).add(idStr, name, x,y);}
	public void addMng(String idStr, String name){ new AddMngAction(this).add(idStr, name);}
	public void addMng(String idStr, String name, String x, String y){ new AddMngAction(this).add(idStr, name, x,y);}
	
	public void delProblem(String idStr){ new DelProblemAction(this).delete(idStr);}
	public void delDiagnosis(String idStr){ new DelDiagnosisAction(this).delete(idStr);}
	public void delTest(String idStr){ new DelTestAction(this).delete(idStr);}
	public void delMng(String idStr){ new DelMngAction(this).delete(idStr);}

	public void reorderProblems(String idStr, String newOrderStr){ new MoveProblemAction(this).reorder(idStr, newOrderStr);}
	public void reorderDiagnoses(String idStr, String newOrderStr){ new MoveDiagnosisAction(this).reorder(idStr, newOrderStr);}
	public void reorderTests(String idStr, String newOrderStr){ new MoveTestAction(this).reorder(idStr, newOrderStr);}
	public void reorderMngs(String idStr, String newOrderStr){ new MoveMngAction(this).reorder(idStr, newOrderStr);}

	public void changeProblem(String probRelIdStr,String newProbId){new ChangeProblemAction(this).changeProblem(probRelIdStr, newProbId);}
	public void changeDiagnosis(String probRelIdStr,String newProbId){new ChangeDiagnosisAction(this).changeDiagnosis(probRelIdStr, newProbId);}
	public void changeTest(String probRelIdStr,String newProbId){new ChangeTestAction(this).changeTest(probRelIdStr, newProbId);}
	public void changeMng(String probRelIdStr,String newProbId){new ChangeMngAction(this).changeMng(probRelIdStr, newProbId);}

	public void changeMnM(String idStr, String newValue){new ChangeDiagnosisAction(this).toggleMnM(idStr, newValue);}
	public void addConnection(String sourceId, String targetId){new AddConnectionAction(this).add(sourceId,targetId);}
	public void delConnection(String idStr){new DelConnectionAction(this).delete(idStr);}

	public String getProblemsJson(){ return new ConceptMapController().getRelationsToJson(problems);}
	public String getDdxJson(){return new ConceptMapController().getRelationsToJson(diagnoses);}
	public String getTestsJson(){return new ConceptMapController().getRelationsToJson(tests);}
	public String getMngsJson(){return new ConceptMapController().getRelationsToJson(mngs);}
	public String getConnsJson(){return new ConceptMapController().getConnsToJson(conns);}	
	public void saveSummStatement(String idStr, String text){}
	public void save(){
		boolean isNew = false;
		if(getId()<=0) isNew = true;
		new DBClinReason().saveAndCommit(this);
		if(isNew) notifyLog();
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
	
	public RelationProblem getProblemBySourceId(long id){return (RelationProblem) getRelationBySourceId(problems, id);}	
	public RelationProblem getProblemById(long id){return (RelationProblem) getRelationById(problems, id);}	
	public RelationDiagnosis getDiagnosisBySourceId(long id){return (RelationDiagnosis) getRelationBySourceId(diagnoses, id);}
	public RelationDiagnosis getDiagnosisById(long id){return (RelationDiagnosis) getRelationById(diagnoses, id);}	
	public RelationTest getTestBySourceId(long id){return (RelationTest) getRelationBySourceId(tests, id);}	
	public RelationTest getTestById(long id){return (RelationTest) getRelationById(tests, id);}		
	public RelationManagement getMngBySourceId(long id){return (RelationManagement) getRelationBySourceId(mngs, id);}	
	public RelationManagement getMngById(long id){return (RelationManagement) getRelationById(mngs, id);}	

	
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
	
	private void notifyLog(){
		LogEntry le = new LogEntry( LogEntry.CRTPATILLSCRIPT_ACTION, this.getSessionId(), -1, this.getId());
		new DBClinReason().saveAndCommit(le);
	}
}
