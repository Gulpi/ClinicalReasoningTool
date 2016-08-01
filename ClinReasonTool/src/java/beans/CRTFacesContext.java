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
	Locale defaultLoc = new Locale("en");
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
		setUser();
		
		if(user!=null){
		    FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().put(CRTFacesContext.CRT_FC_KEY, this);
			initSession();
			userSetting = new UserSetting(); //TODO get from Database...
		} 
	    CRTLogger.out(FacesContextWrapper.getCurrentInstance().getApplication().getStateManager().getViewState(FacesContext.getCurrentInstance()), CRTLogger.LEVEL_TEST);
	    Monitor.addHttpSession((HttpSession)FacesContextWrapper.getCurrentInstance().getExternalContext().getSession(true));
	}
	
	private void initGraph(){
		if(graph!=null && patillscript!=null && graph.getVpId().equals(patillscript.getVpId())) return; //nothing todo, graph already loaded
		graph = new Graph(patillscript.getVpId());
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);	
	}

	/**
	 * Setting the userId, either directly from query param or from query param as an external userid (then we 
	 * have to get the User object and the userId from the database). If no user is found we create a new one. 
	 */
	private void setUser(){
		String setUserIdStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_USER);
		String extUserId = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_USER_EXT);
		int systemId = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_SYSTEM, -1);
		if(setUserIdStr==null && extUserId==null) return;
		if(user!=null && user.getUserId()==Long.valueOf(setUserIdStr).longValue()) return; 
		if(user!=null && user.getExtUserId()!=null && user.getExtUserId().equals(extUserId)) return; 
		if(setUserIdStr!=null && !setUserIdStr.trim().equals(""))
			user = new DBUser().selectUserById(Long.valueOf(setUserIdStr).longValue());
		//if(setUserIdStr!=null) this.userId = (Long.valueOf(setUserIdStr).longValue());
		if(user==null && extUserId!=null && !extUserId.trim().equals("")){
			user =  new UserController().getUser(systemId, extUserId);
			//if(u!=null) this.userId = u.getUserId();
		}
		if(user==null){
			CRTLogger.out("Userid is null", CRTLogger.LEVEL_ERROR);
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
		return analyticsContainer;
	}

	public void initScriptContainer(){
		if(scriptContainer==null){
			scriptContainer = new PatIllScriptContainer(user.getUserId());
			scriptContainer.loadScriptsOfUser();
		}		
	}
	
	public PatIllScriptContainer getScriptContainer(){ 
		return scriptContainer;
	}

	/**
	 * Init of the scoreContainer (in LearningAnalytics), learningAnalytics object, and FeedbackBean container 
	 * @param parentId
	 */
	private void initFeedbackContainer(){		
		if(patillscript!=null) {
			analyticsContainer.addLearningAnalyticsBean(patillscript.getId(), patillscript.getVpId());
			if(feedbackContainer==null){
				feedbackContainer = new FeedbackContainer(patillscript.getId());				
				feedbackContainer.initFeedbackContainer();
			}
			AppBean.getPeers().loadPeersForPatIllScript(patillscript.getVpId());
		}
	}
	
	private void initLearningAnalyticsContainer(){
		if(analyticsContainer == null){ //load learningAnalyticsContainer also if not script is edited -> needed for charts etc...
			analyticsContainer = new LearningAnalyticsContainer(user.getUserId());
		}
		
	}
	
	/**
	 * load PatientIllnessScript based on id or sessionId
	 */
	public void initSession(){ 
		if(user==null) setUser();
		this.getAppBean().getViewHandler().calculateLocale(this);

		initScriptContainer(); //this loads all scripts, needed for overview page and availability bias determination
		initLearningAnalyticsContainer();
		long id = AjaxController.getInstance().getLongRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		String vpId = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_VP);
		//long extUserId = new AjaxController().getLongRequestParamByKey(AjaxController.REQPARAM_USER_EXT);
		int systemId = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_SYSTEM, -1);

		//setUserIdFromExt(extUserId);
		if(this.patillscript!=null && (id<0 || this.patillscript.getId()==id)) return; //current script already loaded....

		if(id<=0 && vpId==null) return; //then user has opened the overview page...y

		if(id>0){ //open an created script
			this.patillscript = isc.loadPatIllScriptById(id, user.getUserId());
			if(this.patillscript!=null && user==null) 
				setUser(this.patillscript.getUserId()); //can happen if we have to re-init after timeout, there we do not get the userId, just the illscriptId

		}
		else if(vpId!=null && !vpId.equals("") && systemId>0 && user!=null){ //look whether script created, if not create it...
			this.patillscript = isc.loadIllnessScriptsByVpId(user.getUserId(), vpId);
			if(this.patillscript==null){
				this.patillscript = isc.createAndSaveNewPatientIllnessScript(user.getUserId(), vpId, systemId);
			}
		}
		//TODO error handling!!!!
		
		loadExpScripts();
		initFeedbackContainer();
		if(this.patillscript!=null) initGraph();		
	}
	
	/**
	 * load exp PatientIllnessScript based on id
	 */
	public void initExpEditSession(){ 
		if(user==null) setUser();
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
	    app.addExpertPatIllnessScriptForVpId(patillscript.getVpId());
	    app.addIllnessScriptForDiagnoses(patillscript.getDiagnoses(), patillscript.getVpId());
	}
	
	public AppBean getAppBean(){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	   return (AppBean) context.getAttribute(AppBean.APP_KEY);
		
	}
	
	public PatientIllnessScript getPatillscript() { 
		return patillscript;
	}
		
	public void setPatillscript(PatientIllnessScript patillscript) { this.patillscript = patillscript;}	
	
	public ScoreContainer getScoreContainer() {
		LearningAnalyticsBean lab = getLearningAnalytics();
		if(lab==null) return null;
		return lab.getScoreContainer();
	}
	
	public FeedbackContainer getFeedbackContainer() {
		if(feedbackContainer==null) feedbackContainer = new FeedbackContainer(this.getPatillscript().getId());
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

	@Override
	public FacesContext getWrapped() {
		return FacesContext.getCurrentInstance();
	}
	
	public ExpViewPatientIllnessScript getExpPatIllScript(){
		return new ExpViewPatientIllnessScript(graph, patillscript.getCurrentStage());
	}
	
	public boolean isExpEdit(){
		if(this.patillscript!=null && this.patillscript.getType()==IllnessScriptInterface.TYPE_EXPERT_CREATED) return true;
		return false;
	}
}
