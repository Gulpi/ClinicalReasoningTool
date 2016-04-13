package beans;
import java.beans.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;

import org.apache.commons.lang.StringUtils;

import actions.beanActions.*;
import beans.error.MyError;
import beans.relation.*;
import controller.AjaxController;
import controller.ConceptMapController;
import controller.GraphController;
import controller.NavigationController;
import database.DBClinReason;

/**
 * This is the Illness script the learner creates during the VP session.
 * @author ingahege
 *
 */
/*@ManagedBean(name = "patillscript", eager = true)*/
@SessionScoped
public class PatientIllnessScript extends Beans/*extends Node*/ implements IllnessScriptInterface, Serializable /*, PropertyChangeListener*/ {

	
	private static final long serialVersionUID = 1L;
	private Timestamp creationDate;
	private long sessionId = -1; //necessary or store PISId in C_Session? and/or vpId?
	/**
	 * the VP the patIllScript is related to. We need this in addition to the sessionId to be able to connect 
	 * the learners' script to the authors'/experts' script.
	 */
	private long parentId;
	private long userId; //needed so that we can display all the users' scripts to him
	/**
	 * Id of the PatientIllnessScript
	 */
	private long id = -1;
	private Locale locale; // do we need this here to determine which list to load?	
	private SummaryStatement summSt;
	private long summStId = -1;
	private Note note;
	private long noteId;
	/**
	 * If we get this information from the VP (or other) system, we store it here.
	 */
	private int currentStage;
	/**
	 * 1=acute, 2=subacute, 3=chronic
	 */	
	private int courseOfTime = -1;
	
	/**
	 * created by learner or expert
	 */
	private int type = IllnessScriptInterface.TYPE_LEARNER_CREATED;
	/**
	 * List of related problems to the PatientIllnessScript
	 */
	private List<RelationProblem> problems;
	private List<RelationDiagnosis> diagnoses; //contains all diagnoses, including the final(s)?
	private List<RelationManagement> mngs;
	private List<RelationTest> tests;
	private List<RelationEpi> epis;
		
	/**
	 * key = cnxId (Long), value = Connection object
	 */
	private Map<Long,Connection> conns;
	
	/**
	 * at which stage have the ddx of this script been submitted by the learner? this is important to detect certain errors
	 */
	private int submittedStage;
	
	private List<MyError> errors;
	
	public PatientIllnessScript(){}
	public PatientIllnessScript(long sessionId, long userId, long vpId, Locale loc){
		if(sessionId>0) this.sessionId = sessionId;
		if(userId>0) this.userId = userId;
		if(vpId>0) this.parentId = vpId;
		this.locale = loc;
		//this.summSt = new SummaryStatement();
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
	public long getParentId() {return parentId;}
	public void setParentId(long parentId) {this.parentId = parentId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	public List<RelationDiagnosis> getDiagnoses() {return diagnoses;}
	public void setDiagnoses(List<RelationDiagnosis> diagnoses) {this.diagnoses = diagnoses;}
	public List<RelationManagement> getMngs() {return mngs;}
	public void setMngs(List<RelationManagement> mngs) {this.mngs = mngs;}	
	public List<RelationTest> getTests() {return tests;}
	public void setTests(List<RelationTest> tests) {this.tests = tests;}	
	public List<RelationEpi> getEpis() {return epis;}
	public void setEpis(List<RelationEpi> epis) {this.epis = epis;}
	public Map<Long,Connection> getConns() {return conns;}
	public void setConns(Map<Long,Connection> conns) {this.conns = conns;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}	
	public Locale getLocale() {return locale;}
	public void setLocale(Locale locale) {this.locale = locale;}	
	public SummaryStatement getSummSt() {return summSt;}
	public void setSummSt(SummaryStatement summSt) {
		this.summSt = summSt;
		//if(summSt!=null) summStId= summSt.getId();
	
	}	
	public long getSummStId() {return summStId;}
	public void setSummStId(long summStId) {this.summStId = summStId;}	
	public Note getNote() {return note;}
	public void setNote(Note note) {this.note = note;}
	public long getNoteId() {return noteId;}
	public void setNoteId(long noteId) {this.noteId = noteId;}
	public int getCurrentStage() {
		updateStage(new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_STAGE));
		return currentStage;
	}
	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
		}		
	public List<MyError> getErrors() {return errors;}
	public void setErrors(List<MyError> errors) {this.errors = errors;}
	public void addErrors(List<MyError> list){
		if(list==null || list.isEmpty()) return;
		if(errors==null) errors = new ArrayList<MyError>();
		errors.addAll(list);
	}
	public void addError(MyError err){
		if(err==null) return;
		if(errors==null) errors = new ArrayList<MyError>();
		errors.add(err);
	}	
	public boolean getSubmitted() {
		if(submittedStage>0) return true;
		return false;
	}	
	public int getSubmittedStage() {return submittedStage;}

	public void setSubmittedStage(int submittedStage) {this.submittedStage = submittedStage;}
	//public void setSubmitted(boolean submitted) {this.submitted = submitted;}
	public void addProblem(String idStr, String name){new AddProblemAction(this).add(idStr, name);}
	public void addProblem(String idStr, String name, String x, String y){ new AddProblemAction(this).add(idStr, name, x,y);}
	public void addDiagnosis(String idStr, String name){ new AddDiagnosisAction(this).add(idStr, name);}
	public void addDiagnosis(String idStr, String name, String x, String y){ new AddDiagnosisAction(this).add(idStr, name, x,y);}
	public void addTest(String idStr, String name){ new AddTestAction(this).add(idStr, name);}
	public void addTest(String idStr, String name, String x, String y){ new AddTestAction(this).add(idStr, name, x,y);}
	public void addMng(String idStr, String name){ new AddMngAction(this).add(idStr, name);}
	public void addMng(String idStr, String name, String x, String y){ new AddMngAction(this).add(idStr, name, x,y);}
	public void addEpi(String idStr, String name){ new AddEpiAction(this).add(idStr, name);}
	public void addEpi(String idStr, String name, String x, String y){ new AddEpiAction(this).add(idStr, name, x,y);}
	
	public void delProblem(String idStr){ new DelProblemAction(this).delete(idStr);}
	public void delDiagnosis(String idStr){ new DelDiagnosisAction(this).delete(idStr);}
	public void delTest(String idStr){ new DelTestAction(this).delete(idStr);}
	public void delMng(String idStr){ new DelMngAction(this).delete(idStr);}
	public void delEpi(String idStr){ new DelEpiAction(this).delete(idStr);}

	public void reorderProblems(String idStr, String newOrderStr){ new MoveProblemAction(this).reorder(idStr, newOrderStr);}
	public void reorderDiagnoses(String idStr, String newOrderStr){ new MoveDiagnosisAction(this).reorder(idStr, newOrderStr);}
	public void reorderTests(String idStr, String newOrderStr){ new MoveTestAction(this).reorder(idStr, newOrderStr);}
	public void reorderMngs(String idStr, String newOrderStr){ new MoveMngAction(this).reorder(idStr, newOrderStr);}
	public void moveItem(String idStr, String newOrderStr, String x, String y){ new DragDropAction(this).move(idStr, x, y);}

	public void changeProblem(String probRelIdStr,String newProbId){new ChangeProblemAction(this).changeProblem(probRelIdStr, newProbId);}
	public void changeDiagnosis(String probRelIdStr,String newProbId){new ChangeDiagnosisAction(this).changeDiagnosis(probRelIdStr, newProbId);}
	public void changeTest(String probRelIdStr,String newProbId){new ChangeTestAction(this).changeTest(probRelIdStr, newProbId);}
	public void changeMng(String probRelIdStr,String newProbId){new ChangeMngAction(this).changeMng(probRelIdStr, newProbId);}
	public void changeEpi(String probRelIdStr,String newProbId){new ChangeEpiAction(this).changeEpi(probRelIdStr, newProbId);}
	public void changeMnM(String idStr/*, String newValue*/){new ChangeDiagnosisAction(this).toggleMnM(idStr/*, newValue*/);}
	
	public void addConnection(String sourceId, String targetId){new AddConnectionAction(this).add(sourceId,targetId);}
	public void delConnection(String idStr){new DelConnectionAction(this).delete(idStr);}
	public void chgConnection(String idStr, String weightStr){new ChgConnectionAction(this).chgConnection(idStr, weightStr);}

	public void saveSummStatement(String idStr, String text){new SummaryStatementChgAction(this).updateOrCreateSummaryStatement( idStr, text);}
	public void saveNote(String idStr, String text){new NoteChgAction(this).updateOrCreateNote( idStr, text);}
	public void submitDDX(String idStr){new DiagnosisSubmitAction(this).submitDDX();}
	public void submitDDX(){new DiagnosisSubmitAction(this).submitDDX();}
	public void changeTier(String idStr, String tierStr){new DiagnosisSubmitAction(this).changeTier(idStr, tierStr);}

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

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt!=null){
			System.out.println(evt.getPropertyName());
		}		
	}
	
	public RelationEpi getEpiById(long id){return (RelationEpi) getRelationById(epis, id);}	
	public RelationProblem getProblemById(long id){return (RelationProblem) getRelationById(problems, id);}	
	public RelationDiagnosis getDiagnosisById(long id){return (RelationDiagnosis) getRelationById(diagnoses, id);}	
	public RelationTest getTestById(long id){return (RelationTest) getRelationById(tests, id);}		
	public RelationManagement getMngById(long id){return (RelationManagement) getRelationById(mngs, id);}	

	public Relation getRelationByListItemIdAndType(long id, int type){
		if(type==Relation.TYPE_PROBLEM) return getRelationByListItemId(this.problems, id);
		if(type==Relation.TYPE_DDX) return getRelationByListItemId(this.diagnoses, id);
		if(type==Relation.TYPE_MNG) return getRelationByListItemId(this.mngs, id);
		if(type==Relation.TYPE_TEST) return getRelationByListItemId(this.tests, id);
		if(type==Relation.TYPE_EPI) return getRelationByListItemId(this.epis, id);
		
		return null;
	}
	
	private Relation getRelationByListItemId(List items, long listItemId){
		if(items==null || items.isEmpty()) return null;
		for(int i=0; i< items.size(); i++){
			Relation rel = (Relation) items.get(i);
			if(rel.getListItemId()==listItemId) return rel;
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
	
	public Relation getRelationByIdAndType(long id, int type){
		if(type==Relation.TYPE_PROBLEM) return getRelationById(this.problems, id);
		if(type==Relation.TYPE_DDX) return getRelationById(this.diagnoses, id);
		if(type==Relation.TYPE_MNG) return getRelationById(this.mngs, id);
		if(type==Relation.TYPE_TEST) return getRelationById(this.tests, id);
		if(type==Relation.TYPE_EPI) return getRelationById(this.epis, id);
		
		return null;
	}
	
	private void notifyLog(){
		LogEntry le = new LogEntry( LogEntry.CRTPATILLSCRIPT_ACTION, this.getId(), -1, this.getId());
		new DBClinReason().saveAndCommit(le);
	}
	
	/**
	 * We save the current stage the user is in in the Script. We do not change the current stage if the stage 
	 * is before the current stage (can happen e.g. if the user goes back in a VP)
	 * @param stage
	 */
	public void updateStage(String stage){
		if(StringUtils.isNumeric(stage)){
			int stageNum = Integer.valueOf(stage);
			if(stageNum > this.currentStage){
				this.setCurrentStage(stageNum);	
				save();
			}
		}
	}
	
	public int getErrorNum(){
		if(errors==null) return 0;
		return errors.size();
	}
}
