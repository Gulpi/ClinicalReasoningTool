package beans.scripts;

import java.beans.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.*;

import org.apache.commons.lang.StringUtils;

import actions.beanActions.*;
import application.AppBean;
import beans.*;
import beans.error.MyError;
import beans.relation.*;
import beans.scoring.ScoreBean;
import controller.AjaxController;
import controller.NavigationController;
import controller.ScoringController;
import database.DBClinReason;
import properties.IntlConfiguration;

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
	/**
	 * We need this for example for determining availability biases...
	 */
	private Timestamp lastAccessDate;
	/**
	 * the VP the patIllScript is related to. We need this in addition to the sessionId to be able to connect 
	 * the learners' script to the authors'/experts' script.
	 * @deprecated
	 */
	//private long parentId;
	/**
	 * unique across systems and vps. format: "vpId_systemId"
	 */
	private String vpId;
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
	 * This is the real current stage and in contrast to currentStage this is not written into database and is updated on 
	 * each stage change. We need this for editing expert scripts...
	 */
	private int stage = 1;
	/**
	 * How confident is the learner with his ddxs. (1-100)
	 */
	private int confidence;
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
	
	/**
	 * Stage at which the learner really has to submit a diagnosis, ONLY expert scripts!
	 */
	private int maxSubmittedStage = -1;
	
	/**
	 * key=error id, value=error
	 * It is complicated to have lists in Maps in hibernate, therefore, we currently have a map with all errors and the unique 
	 * id as key...
	 */
	private List<MyError> errors;
	//private Map<Integer, List<MyError>> errors;
	//private FinalDiagnosisSubmission finalddxs;
	
	/**
	 * Has this script added to the peer table? ONLY learner scripts!
	 */
	private boolean peerSync = false;
	
	public PatientIllnessScript(){}
	public PatientIllnessScript(long userId, String vpId, Locale loc, int systemId){
		//if(sessionId>0) this.sessionId = sessionId;
		if(userId>0) this.userId = userId;
		//if(parentId>0) this.parentId = parentId;
		this.locale = loc;
		this.vpId = vpId +"_"+systemId;
		//this.summSt = new SummaryStatement();
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}

	public int getStage() {
		updateStage(AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_STAGE));
		return stage;
	}
	public void setStage(int stage) {this.stage = stage;}
	public int getCourseOfTime() {return courseOfTime;}
	public void setCourseOfTime(int courseOfTime) {this.courseOfTime = courseOfTime;}
	public List<RelationProblem> getProblems() {return problems;}
	public List<RelationProblem> getProblemsStage() { return getRelationsByStage(problems);}
	public void setProblems(List<RelationProblem> problems) {this.problems = problems;}
	public Timestamp getCreationDate(){ return this.creationDate;} //setting is done in DB	
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}	
	public Timestamp getLastAccessDate() {return lastAccessDate;}
	public void setLastAccessDate(Timestamp lastAccessDate) {this.lastAccessDate = lastAccessDate;}

	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	public List<RelationDiagnosis> getDiagnoses() {return diagnoses;}
	public List<RelationDiagnosis> getDiagnosesStage() { return getRelationsByStage(diagnoses);}
	public void setDiagnoses(List<RelationDiagnosis> diagnoses) {this.diagnoses = diagnoses;}
	public List<RelationManagement> getMngs() {return mngs;}
	public List<RelationManagement> getMngsStage() { return getRelationsByStage(mngs);}
	public void setMngs(List<RelationManagement> mngs) {this.mngs = mngs;}	
	public List<RelationTest> getTests() {return tests;}
	public List<RelationTest> getTestsStage() { return getRelationsByStage(tests);}
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
	public SummaryStatement getSummStStage() {
		if(summSt==null) return null;
		if(summSt.getStage()<=stage) return summSt;
		return null;
	}
	public void setSummSt(SummaryStatement summSt) {this.summSt = summSt;	}		
	public boolean isPeerSync() {return peerSync;}
	public boolean getPeerSync() {return peerSync;}
	public void setPeerSync(boolean peerSync) {this.peerSync = peerSync;}
	public int getConfidence() {return confidence;}
	public void setConfidence(int confidence) {this.confidence = confidence;}
	public long getSummStId() {return summStId;}
	public void setSummStId(long summStId) {this.summStId = summStId;}	
	public Note getNote() {return note;}
	public void setNote(Note note) {this.note = note;}
	public long getNoteId() {return noteId;}
	public void setNoteId(long noteId) {this.noteId = noteId;}
	
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	
	public int getCurrentStage() {return currentStage;}	
	public int getMaxSubmittedStage() {
		if(maxSubmittedStage>0) return maxSubmittedStage;
		return submittedStage; //default
	}
	public void setMaxSubmittedStage(int maxSubmittedStage) {this.maxSubmittedStage = maxSubmittedStage;}
	
	public int getCurrentStageWithUpdate() {
		updateStage(AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_STAGE));
		return currentStage;
	}	
	public void setCurrentStage(int currentStage) { this.currentStage = currentStage;}		
	public List<MyError> getErrors() {return errors;}
	
	/**
	 * @return all Errors that have been made at the current stage
	 */
	public List<MyError> getErrorsCurrStage() {
		if(errors==null) return null;
		List<MyError> stageErr = new ArrayList<MyError>();
		for(int i=0; i<errors.size(); i++){
			if(errors.get(i).getStage()==this.currentStage) stageErr.add(errors.get(i));
		}
		if(stageErr==null || stageErr.isEmpty()) return null;
		return stageErr;
	}
	
	public void setErrors(List<MyError> errors) {this.errors = errors;}
	public void addErrors(List<MyError> list){
		if(list==null || list.isEmpty()) return;
		for(int i=0; i<list.size(); i++){
			addError(list.get(i));
		}
	}
	
	/**
	 * Add an error, if for the stage and type no error has been occured.
	 * @param err
	 */
	public boolean addError(MyError err){
		if(err==null) return false;
		if(errors==null) errors = new ArrayList<MyError>();
		if(!errors.contains(err)){
			errors.add(err);
			return true;
		}
		return false;
	}	
	public boolean getSubmitted() {
		if(submittedStage>0) return true;
		return false;
	}	
	public int getSubmittedStage() {return submittedStage;}
	

	public void setSubmittedStage(int submittedStage) {this.submittedStage = submittedStage;}
	//public void setSubmitted(boolean submitted) {this.submitted = submitted;}
	public void addProblem(String idStr, String prefix){new AddProblemAction(this).add(idStr, prefix);}
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
	public void moveItem(String idStr, String x, String y){ new DragDropAction(this).move(idStr, x, y);}

	public void changeProblem(String idStr,String changeMode){new ChangeProblemAction(this).changeProblem(idStr, changeMode);}
	//public void changeProblem(String relIdStr){new ChangeProblemAction(this).changeProblem(relIdStr);}
	//public void toggleProblem(String probRelIdStr){new ChangeProblemAction(this).toggleProblem(probRelIdStr);}
	//public void changeDiagnosis(String relIdStr){new ChangeDiagnosisAction(this).changeDiagnosis(relIdStr);}
	//public void changeMng(String relIdStr){new ChangeMngAction(this).changeMng(relIdStr);}
	//public void changeTest(String relIdStr){new ChangeTestAction(this).changeTest(relIdStr);}

	public void changeDiagnosis(String idStr,String changeMode){new ChangeDiagnosisAction(this).changeDiagnosis(idStr, changeMode);}
	public void changeTest(String idStr,String changeMode){new ChangeTestAction(this).changeTest(idStr, changeMode);}
	public void changeMng(String idStr,String changeMode){new ChangeMngAction(this).changeMng(idStr, changeMode);}
	public void changeEpi(String idStr,String changeMode){new ChangeEpiAction(this).changeEpi(idStr, changeMode);}
	public void changeMnM(String idStr/*, String newValue*/){new ChangeDiagnosisAction(this).toggleMnM(idStr/*, newValue*/);}
	
	public void addConnection(String sourceId, String targetId){new AddConnectionAction(this).add(sourceId,targetId);}
	public void delConnection(String idStr){new DelConnectionAction(this).delete(idStr);}
	public void chgConnection(String idStr, String weightStr){new ChgConnectionAction(this).chgConnection(idStr, weightStr);}

	public void saveSummStatement(String idStr, String text){new SummaryStatementChgAction(this).updateOrCreateSummaryStatement( idStr, text);}
	public void saveNote(String idStr, String text){new NoteChgAction(this).updateOrCreateNote( idStr, text);}
	public void submitDDX(String idStr){new DiagnosisSubmitAction(this).submitDDX(idStr);}
	public void submitDDXAndConf(String idStr, String confStr){new DiagnosisSubmitAction(this).submitDDXAndConf(idStr, confStr);}
	public void resetFinalDDX(String idStr){new DiagnosisSubmissionRevertAction(this).revertSubmission();}
	
	//public void submitDDX(){new DiagnosisSubmitAction(this).submitDDX();}
	public void changeTier(String idStr, String tierStr){new DiagnosisSubmitAction(this).changeTier(idStr, tierStr);}
	public void changeConfidence(String idStr, String confVal){new ChgPatIllScriptAction(this).changeConfidence(idStr, confVal);}
	public void chgCourseOfTime(String courseOfTimeStr) { new ChgPatIllScriptAction(this).chgCourseOfTime(courseOfTimeStr);}

	public void addJoker(String idStr, String type){ new JokerAction(this).addJoker(type);}
	
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
	/*public void propertyChange(PropertyChangeEvent evt) {
		if(evt!=null){
			CRTLogger.out(evt.getPropertyName(), CRTLogger.LEVEL_TEST);	

		}		
	}*/
	
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
	
	private List getRelationsByStage(List items){
		if(items==null || items.isEmpty()) return null;
		List<Relation> stageList = new ArrayList<Relation>();
		for(int i=0; i< items.size(); i++){
			Relation rel = (Relation) items.get(i);
			if(rel.getStage()<=stage) stageList.add(rel);
		}
		return stageList; //nothing found
	}
	
	public List<RelationDiagnosis> getFinalDiagnoses(){
		if(diagnoses==null || diagnoses.isEmpty()) return null; 
		List<RelationDiagnosis> finals = new ArrayList<RelationDiagnosis>();
		for(int i=0; i<diagnoses.size(); i++){
			if(diagnoses.get(i).isFinalDiagnosis()) finals.add(diagnoses.get(i));
		}
		return finals;
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
			this.stage = stageNum; //we always update the stage
			if(stageNum > this.currentStage){
				new ScoringController().scoringListsForStage(this, currentStage);
				this.setCurrentStage(stageNum);	
				save();
			}
		}
	}
	
	public int getErrorNum(){
		if(errors==null) return 0;
		return errors.size();
	}
	
	public String getCourseOfTimeScore(){ 
		return new ScoringController().getIconForScore(ScoreBean.TYPE_COURSETIME, -1);
	}
	/**
	 * return the expert's summary statement at the current stage...
	 * @return
	 */
	public String getSummStExp(){
		if(this.type==IllnessScriptInterface.TYPE_LEARNER_CREATED){
			PatientIllnessScript expScript = AppBean.getExpertPatIllScript(this.vpId);
			if(expScript==null || expScript.getSummSt()==null || expScript.getSummSt().getStage()>currentStage) return IntlConfiguration.getValue("summst.exp.no");
			return expScript.getSummSt().getText();
		}
		return "";
	}
	
	/**
	 * We offer the user to retry the diagnoses submission if he has less than 100%
	 * TODO: we might make this more specific
	 * @return
	 */
	public boolean getOfferTryAgain(){
		List<ScoreBean>scores = new NavigationController().getCRTFacesContext().getLearningAnalytics().getScoreContainer().getScoresByType(ScoreBean.TYPE_FINAL_DDX);
		if(scores==null || scores.isEmpty()) return true;
		for(int i=0; i<scores.size(); i++){
			if(scores.get(i).getScoreBasedOnExpPerc()<=DiagnosisSubmitAction.scoreForAllowReSubmit) return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean getOfferContinueCase(){
		//if(!getOfferTryAgain()) return false; //solution was correct anyway
		if(AppBean.getExpertPatIllScript(this.getVpId())==null) return true; //we have no expert's script....
		if(getOfferTryAgain() && this.currentStage < AppBean.getExpertPatIllScript(this.getVpId()).getMaxSubmittedStage()) return true;
		return false; //end of case and/or 100% score
	}
	
	public boolean getOfferSolution(){
		if(!getOfferTryAgain()) return false; //solution was correct anyway
		if(AppBean.getExpertPatIllScript(this.getVpId())==null) return true; //we have no expert's script....
		if(this.currentStage == AppBean.getExpertPatIllScript(this.getVpId()).getMaxSubmittedStage() /*&& this.currentStage >= AppBean.getExpertPatIllScript(this.getParentId()).getSubmittedStage()*/) return true;
		return false;
	}

}
