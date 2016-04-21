package beans;

import java.io.*;
import java.util.*;
import java.beans.*;

import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.ServletContext;

import application.AppBean;
import beans.graph.Graph;
import beans.relation.Relation;
import beans.scoring.FeedbackContainer;
import beans.scoring.ScoreContainer;
import controller.*;
import database.DBClinReason;
import util.CRTLogger;

/**
 * The facesContext for a session....
 * We put the CRTFacesContext into the ExternalContext of the FacesContext, so that we can access it throughout the
 * users' session.
 * @author ingahege
 *
 */
@ManagedBean(name = "crtContext", eager = true)
@SessionScoped
public class CRTFacesContext /*extends FacesContextWrapper*/ implements Serializable{
	//public static final String PATILLSCRIPT_KEY = "patillscript";
	//public static final String PATILLSCRIPTS_KEY = "patillscripts";
	public static final String CRT_FC_KEY = "crtfc";
	
	private static final long serialVersionUID = 1L;
	//private long sessionId = -1; //not sure we need the session_id
	private long userId = -1;
	private IllnessScriptController isc = new IllnessScriptController();
	private PatientIllnessScript patillscript;
	private Graph graph;
	private UserSetting userSetting;
	//private boolean feedbackOn;

	/**
	 * all scripts of the user, needed for the overview/portfolio page to display a list. 
	 * TODO: we only need id and a name, so maybe we do not have to load the full objects? or get 
	 * them from view?
	 */
	private List<PatientIllnessScript> scriptsOfUser;
	
	private FeedbackContainer feedbackContainer;
	/**
	 * Detailed scores for this patIllScript
	 */
	private ScoreContainer scoreContainer;
	
	public CRTFacesContext(){
		setUserId();
		loadAndSetScriptsOfUser(); //this loads all scripts, we do not necessarily have to do that here, only if overview page is opened!
		loadAndSetPatIllScript();
		userSetting = new UserSetting(); //TODO get from Database...
	    FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().put(CRTFacesContext.CRT_FC_KEY, this);
	    //if(patillscript!=null) 
	}
	
	private void initGraph(){
		if(graph!=null) return; //nothing todo, can this happen?
		graph = new Graph(patillscript.getParentId());
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);	
	}

	private void setUserId(){
		String setUserIdStr = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_USER);
		if(setUserIdStr!=null) this.userId = (Long.valueOf(setUserIdStr).longValue());
	}

	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public Graph getGraph() {return graph;}
	public UserSetting getUserSetting() {return userSetting;}
	public void setUserSetting(UserSetting userSetting) {this.userSetting = userSetting;}
	public void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser) {this.scriptsOfUser = scriptsOfUser;}

	/*public void setCurrentStage(){
		new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_STAGE);
	}*/
	private void loadAndSetScriptsOfUser(){	setScriptsOfUser(isc.loadScriptsOfUser());}
	
	private void loadScoreAndFeedbackContainer(){
		if(this.getPatillscript()!=null) {
			scoreContainer = new ScoreContainer(this.getPatillscript().getId());
			feedbackContainer = new FeedbackContainer(this.getPatillscript().getId());
			scoreContainer.initScoreContainer();	
			feedbackContainer.initFeedbackContainer();
		}
	}
	
	/**
	 * load PatientIllnessScript based on id or sessionId
	 */
	public void loadAndSetPatIllScript(){ 
		long id = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		if(this.patillscript!=null && this.patillscript.getId()==id) return; //current script loaded....
		long sessionId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SESSION);
		long vpId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_VP);
		if(id<=0 && sessionId<=0 && vpId<=0) return; //then user has opened the overview page...y
		//boolean isNew = true;
		if(id>0){
			setPatillscript(isc.loadPatIllScriptById(id, userId));
		}
		else if(sessionId>0) setPatillscript(isc.loadPatIllScriptBySessionId(sessionId, userId));
		else if(vpId>0 && this.userId>0) setPatillscript(isc.loadIllnessScriptsByParentId(this.userId, vpId));
		//TODO error handling!!!!
		loadExpScripts();
		loadScoring();
		initGraph();		
	}
	
	private void loadScoring(){
		
		//todo call loading from DB method
		loadScoreAndFeedbackContainer();
	}
	
	private void loadExpScripts(){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    AppBean app = (AppBean) context.getAttribute(AppBean.APP_KEY);
	    app.addExpertPatIllnessScriptForParentId(patillscript.getParentId());
	    app.addIllnessScriptForDiagnoses(patillscript.getDiagnoses(), patillscript.getParentId());

	}
	
	public PatientIllnessScript getPatillscript() { return patillscript;}
	public void setPatillscript(PatientIllnessScript patillscript) { this.patillscript = patillscript;}	
	public List<PatientIllnessScript> getScriptsOfUser() {return this.scriptsOfUser;}
	//private void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser){this.scriptsOfUser = scriptsOfUser;}	
	public ScoreContainer getScoreContainer() {
		if(scoreContainer==null) scoreContainer = new ScoreContainer(this.getPatillscript().getId());
		return scoreContainer;
	}
	
	public FeedbackContainer getFeedbackContainer() {
		if(feedbackContainer==null) feedbackContainer = new FeedbackContainer(this.getPatillscript().getId());
		return feedbackContainer;
	}
	public void setScoreBean(ScoreContainer scoreContainer) {this.scoreContainer = scoreContainer;}
	public void toogleExpFeedback(String toggleStr, String taskStr){feedbackContainer.toogleExpFeedback(toggleStr, taskStr);}
	//public void setExpFeedbackTask(String toogleStr, String taskStr){feedbackContainer.setExpFeedback(toogleStr, taskStr);}
	/* we land here from an ajax request for any actions concerning the patientIllnessScript....*/
	public void ajaxResponseHandler() throws IOException {
		new AjaxController().receiveAjax(this.getPatillscript());
	}
	
	/* we land here from an ajax request for any actions concerning the facesContext....*/	
	public void ajaxResponseHandler2() throws IOException {
		new AjaxController().receiveAjax(this);
	}
	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContextWrapper#getWrapped()
	 */
	/*public FacesContext getWrapped() {
		return this; //or return this ????
	}*/

}
