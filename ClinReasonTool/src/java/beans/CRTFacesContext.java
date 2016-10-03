package beans;

import java.io.*;
import java.util.*;
import java.beans.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import application.AppBean;
import application.Monitor;
import beans.graph.Graph;
import beans.scoring.*;
import beans.scripts.*;
import beans.user.User;
import controller.*;
import database.DBEditing;
import database.DBUser;
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
public class CRTFacesContext extends FacesContextWrapper /*implements Serializable*/{
	public static final String CRT_FC_KEY = "crtContext";
	
	//private long userId = -1;
	private User user;
	private IllnessScriptController isc = new IllnessScriptController();
	private PatientIllnessScript patillscript;
	private Graph graph;
	private UserSetting userSetting;	
	/**
	 * This is the locale of the navigation etc. script locale (for lists) is in PatientIllnessScript
	 */
	private Locale locale;
	/**
	 * TODO: get from VP system or calculate from expert script
	 */
	//private int maxStage = 4;
	//private boolean feedbackOn;

	/**
	 * all scripts of the user, needed for the overview/portfolio page to display a list. 
	 * TODO: we only need id and a name, so maybe we do not have to load the full objects? or get 
	 * them from view?
	 * We load it from the portfolio view and also here (in case user does not come from portfolio)
	 */
	private PatIllScriptContainer scriptContainer;
	
	/**
	 * This is only related to the current patIllScript and contains a FeedbackBean per stage
	 * We store the feedback Information in the ScoreBean, so, we do not need the feedbackContainer in the 
	 * LearningAnalyticsContainer.
	 */
	private FeedbackContainer feedbackContainer;

	/**
	 * a container for all LearningAnalyticsBeans of a user which contain all ScoreBeans in ScoreContainer objects.
	 */
	private LearningAnalyticsContainer analyticsContainer;
	
	public CRTFacesContext(){
		long startms = System.currentTimeMillis();

	    CRTLogger.out("Start CRTFacesContext init:"  + startms + "ms", CRTLogger.LEVEL_PROD);

		locale = LocaleController.setLocale();
		setUser();
		if(user!=null){
		    FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().put(CRTFacesContext.CRT_FC_KEY, this);
			initSession();
			userSetting = new UserSetting(); //TODO get from Database...
		}
		try{
			CRTLogger.out(FacesContextWrapper.getCurrentInstance().getApplication().getStateManager().getViewState(FacesContext.getCurrentInstance()), CRTLogger.LEVEL_TEST);
			Monitor.addHttpSession((HttpSession)FacesContextWrapper.getCurrentInstance().getExternalContext().getSession(true));
		}
		catch(Exception e){}
	    CRTLogger.out("End CRTFacesContext init: "  + (System.currentTimeMillis()- startms) +"ms", CRTLogger.LEVEL_PROD);

	}
	
	private void initGraph(){
	    
		if(graph!=null && patillscript!=null && graph.getVpId().equals(patillscript.getVpId())) return; //nothing todo, graph already loaded
		long startms = System.currentTimeMillis();
		CRTLogger.out("Start Graph init:"  + startms + "ms", CRTLogger.LEVEL_PROD);
		graph = new Graph(patillscript.getVpId());
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);	
	    CRTLogger.out("End Graph init: "  + (System.currentTimeMillis() - startms) + "ms", CRTLogger.LEVEL_PROD);

	}

	/**
	 * Setting the userId, either directly from query param or from query param as an external userid (then we 
	 * have to get the User object and the userId from the database). If no user is found we create a new one. 
	 */
	private void setUser(){
		String setUserIdStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_USER);
		String extUserId = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_USER_EXT);
		int systemId = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_SYSTEM, -1);
		if(setUserIdStr==null && extUserId==null){
			//userHasChanged(); //just to be sure...
			return;
		}
		//userIdStr is same as userId of loaded user -> return
		if(user!=null && setUserIdStr!=null && user.getUserId()==Long.valueOf(setUserIdStr).longValue()) return; 
		//extUserId of loaded user is same as extUserId -> return
		if(user!=null && extUserId!=null && user.getExtUserId()!=null && user.getExtUserId().equals(extUserId)) return; 
		if(setUserIdStr!=null && !setUserIdStr.trim().equals("")){
			user = new DBUser().selectUserById(Long.valueOf(setUserIdStr).longValue());
			userHasChanged(); //user is different
		}

		else if(extUserId!=null && !extUserId.trim().equals("")){
			user =  new UserController().getUser(systemId, extUserId);
			userHasChanged(); //user is different
			//if(u!=null) this.userId = u.getUserId();
		}
		if(user==null){
			CRTLogger.out("Userid is null", CRTLogger.LEVEL_ERROR);
			userHasChanged(); //just to be sure...
			FacesContextWrapper.getCurrentInstance().addMessage("",new FacesMessage(FacesMessage.SEVERITY_ERROR, "userid is null",""));
		}
	}
	
	private void setUser(long userId){
		if(user!=null && user.getUserId()==userId) return;
		user =  new DBUser().selectUserById(userId);
	}

	public long getUserId() {
		if(user!=null) return user.getUserId();
		initSession();
		if(user!=null) return user.getUserId();
		return -1;
	}
	//public void setUserId(long userId) {this.userId = userId;}
	public Graph getGraph() {return graph;}
	//public int getMaxStage() {return maxStage;}

	public UserSetting getUserSetting() {return userSetting;}
	public void setUserSetting(UserSetting userSetting) {this.userSetting = userSetting;}
	//public void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser) {this.scriptsOfUser = scriptsOfUser;}
	public LearningAnalyticsBean getLearningAnalytics() {
		if(analyticsContainer==null) return null;//analyticsContainer = new LearningAnalyticsContainer(userId);
		return analyticsContainer.getLearningAnalyticsBeanByPatIllScriptId(patillscript.getId(), patillscript.getVpId());
	}
	
	public LearningAnalyticsContainer getLearningAnalyticsContainer() {
		setUser();
		if(analyticsContainer==null) initLearningAnalyticsContainer();

		return analyticsContainer;
	}

	public void initScriptContainer(){

		if(user==null) setUser();
		if(user==null) return;//not sure why this happens sometimes....
		//if not yet loaded or from a different user we set scriptContainer:
		if(scriptContainer==null || scriptContainer.getUserId()!=user.getUserId()){
			long startms =  System.currentTimeMillis();
		    CRTLogger.out("Start ScripConatainer init:"  + startms +"ms", CRTLogger.LEVEL_PROD);

			scriptContainer = new PatIllScriptContainer(user.getUserId());
			scriptContainer.loadScriptsOfUser();
		    CRTLogger.out("End ScripConatainer init:"  + (System.currentTimeMillis()-startms) + "ms", CRTLogger.LEVEL_PROD);

		}	

	}
	
	public PatIllScriptContainer getScriptContainer(){ 
		if(scriptContainer==null) initScriptContainer();
		return scriptContainer;
	}

	/**
	 * Init of the feedbackContainer (in LearningAnalytics), learningAnalytics object, and FeedbackBean container 
	 * @param parentId
	 */
	private void initFeedbackContainer(){		

		if(patillscript!=null) {
			long startms = System.currentTimeMillis();
			if(feedbackContainer==null || feedbackContainer.getUserId()!=user.getUserId()){
			    CRTLogger.out("Start FeedbackContainer init: "  + startms + "ms", CRTLogger.LEVEL_PROD);

				feedbackContainer = new FeedbackContainer(patillscript.getId(), user.getUserId());				
				feedbackContainer.initFeedbackContainer();
			}
			AppBean.getPeers().loadPeersForPatIllScript(patillscript.getVpId());
		    CRTLogger.out("End FeedbackContainer init:"  + (System.currentTimeMillis()-startms) + "ms", CRTLogger.LEVEL_PROD);

		}

	}
	
	private void initLearningAnalyticsContainer(){
		if(user==null) setUser();
		if(user==null) return;
		long startms = System.currentTimeMillis();
		if(analyticsContainer == null || analyticsContainer.getUserId() != user.getUserId()){ //load learningAnalyticsContainer also if not script is edited -> needed for charts etc...
		    CRTLogger.out("Start LearningAnalyticsContainer init:"  + startms + "ms", CRTLogger.LEVEL_PROD);
			analyticsContainer = new LearningAnalyticsContainer(user.getUserId());
		    CRTLogger.out("End LearningAnalyticsContainer init:"  + (System.currentTimeMillis()-startms) + "ms", CRTLogger.LEVEL_PROD);

		}	
		if(patillscript!=null)
			analyticsContainer.addLearningAnalyticsBean(patillscript.getId(), patillscript.getVpId());

	}
	
	/**
	 * load PatientIllnessScript based on id or sessionId
	 */
	public void initSession(){ 
		long startms = System.currentTimeMillis();
	    CRTLogger.out("Start Session init:" + startms, CRTLogger.LEVEL_PROD);

		/*if(user==null)*/ setUser();
		//this.getAppBean().getViewHandler().calculateLocale(this);

		initScriptContainer(); //this loads all scripts, needed for overview page and availability bias determination
		
		long id = AjaxController.getInstance().getLongRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		String vpId = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_VP);
		//long extUserId = new AjaxController().getLongRequestParamByKey(AjaxController.REQPARAM_USER_EXT);
		int systemId = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_SYSTEM, -1);

		//setUserIdFromExt(extUserId);
		if(this.patillscript!=null && (id<0 || this.patillscript.getId()==id) && this.patillscript.getUserId()==this.user.getUserId()) return; //current script already loaded....

		if(id<=0 && vpId==null) return; //then user has opened the overview page...y

		if(id>0){ //open an created script
			this.patillscript = isc.loadPatIllScriptById(id, user.getUserId());
			if(this.patillscript!=null && user==null) 
				setUser(this.patillscript.getUserId()); //can happen if we have to re-init after timeout, there we do not get the userId, just the illscriptId

		}
		else if(vpId!=null && !vpId.equals("") && systemId>0 && user!=null){ //look whether script created, if not create it...
			this.patillscript = isc.loadIllnessScriptsByVpId(user.getUserId(), vpId+"_"+systemId);
			if(this.patillscript==null){
				this.patillscript = isc.createAndSaveNewPatientIllnessScript(user.getUserId(), vpId, systemId);
			}
		}
		//TODO error handling!!!!
		initLearningAnalyticsContainer();
		loadExpScripts();
		initFeedbackContainer();
		
		if(this.patillscript!=null){
			initGraph();
			//LocaleController.getInstance().setScriptLocale(patillscript.getLocale());
		}
		
		long endms = System.currentTimeMillis();
	    CRTLogger.out("End Session init:"  + (endms-startms) + " ms", CRTLogger.LEVEL_PROD);

	}
	
	/**
	 * load exp PatientIllnessScript based on id
	 */
	public void initExpEditSession(){ 
		/*if(user==null)*/ setUser();
		long id = AjaxController.getInstance().getLongRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		if(this.patillscript!=null && (id<0 || this.patillscript.getId()==id)) return; //current script already loaded....
		if(id<=0) return;

		if(id>0){ //open an created script
			this.patillscript = new DBEditing().selectExpertPatIllScriptById(id);
			if(this.patillscript!=null && user==null) setUser(this.patillscript.getUserId()); //can happen if we have to re-init after timeout, there we do not get the userId, just the illscriptId
		}
		//TODO error handling!!!!
		if(this.patillscript!=null)  initGraph();		
	}
	
	
	public boolean getInitSession(){
		initSession();
		return true;
	}
	
	private void loadExpScripts(){
		if(patillscript==null) return;
		AppBean app = getAppBean();
		PatientIllnessScript expScript = app.addExpertPatIllnessScriptForVpId(patillscript.getVpId());
		//we have to overtake the max stage in which the final ddx has to be submitted from the expert's script: 
		if(patillscript.getMaxSubmittedStage()<=0 && expScript!=null){
			patillscript.setMaxSubmittedStage(expScript.getMaxSubmittedStage());
			patillscript.save();
		}
	    
		app.addIllnessScriptForDiagnoses(patillscript.getDiagnoses(), patillscript.getVpId());
	}
	
	public AppBean getAppBean(){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	   return (AppBean) context.getAttribute(AppBean.APP_KEY);
		
	}
	
	public PatientIllnessScript getPatillscript() { 
		LocaleController.setLocale();
		return patillscript;
	}
		
	public void setPatillscript(PatientIllnessScript patillscript) { this.patillscript = patillscript;}	
	
	public ScoreContainer getScoreContainer() {
		LearningAnalyticsBean lab = getLearningAnalytics();
		if(lab==null) return null;
		return lab.getScoreContainer();
	}
	
	public FeedbackContainer getFeedbackContainer() {
		if(feedbackContainer==null) feedbackContainer = new FeedbackContainer(this.getPatillscript().getId(),user.getUserId());
		return feedbackContainer;
	}
	public void toogleExpBoxFeedback(String toggleStr, String taskStr){
		feedbackContainer.toogleExpBoxFeedback(toggleStr, taskStr);
	}
	
	public void toogleExpFeedback(String toggleStr){
		feedbackContainer.toogleExpFeedback(toggleStr, this.patillscript.getCurrentStage());
	}
	
	public void tooglePeerBoxFeedback(String toggleStr, String taskStr){
		feedbackContainer.tooglePeerBoxFeedback(toggleStr, taskStr, this.patillscript.getCurrentStage());
	}
	
	/** 
	 * we land here from an ajax request for any actions concerning the patientIllnessScript....
	 **/
	public void ajaxResponseHandler() throws IOException {
		AjaxController.getInstance().receiveAjax(this.getPatillscript());
	}
	
	public void ajaxChartResponseHandler() throws IOException {
		AjaxController.getInstance().receiveChartAjax(this);
	}
	
	/* we land here from an ajax request for any actions concerning the facesContext....*/	
	public void ajaxResponseHandler2() throws IOException {
		AjaxController.getInstance().receiveAjax(this);
	}
	
	/**
	 * reset all objects related to the current patientIllnessScript. Call this when the user opens another 
	 * VP
	 */
	public void reset(){
		this.graph = null;
		setPatillscript(null);
		this.graph = null;
		this.feedbackContainer = null;
	}
	
	private void userHasChanged(){
		reset();
		this.analyticsContainer = null;
		this.scriptContainer = null;
	}

	public FacesContext getWrapped() {return FacesContext.getCurrentInstance();}
	
	public ExpViewPatientIllnessScript getExpPatIllScript(){
		int stage = 1;
		if(patillscript!=null) stage = patillscript.getCurrentStage();
		return new ExpViewPatientIllnessScript(graph, stage);
	}
	
	public boolean isExpEdit(){
		if(this.patillscript!=null && this.patillscript.getType()==IllnessScriptInterface.TYPE_EXPERT_CREATED) return true;
		return false;
	}
	
	public Locale getLocale(){return locale;}//LocaleController.getLocale(this).getLanguage();}	
	public String getLanguage(){return locale.getLanguage();}
	public float getScoreForAllowReSubmit(){
		return ScoringController.scoreForAllowReSubmit;
	}
}
