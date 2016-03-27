package beans;

import java.io.*;
import java.util.*;
import java.beans.*;

import javax.faces.application.*;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.*;
import javax.faces.component.UIViewRoot;
import javax.faces.context.*;
import javax.faces.render.RenderKit;

import controller.AjaxController;
import controller.IllnessScriptController;
import database.DBClinReason;

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
	/**
	 * all scripts of the user, needed for the overview/portfolio page to display a list. 
	 * TODO: we only need id and a name, so maybe we do not have to load the full objects? or get 
	 * them from view?
	 */
	private List<PatientIllnessScript> scriptsOfUser;
	
	private FeedbackBean feedbackBean;
	/**
	 * Detailed scores for this patIllScript
	 */
	private ScoreContainer scoreContainer;
	
	public CRTFacesContext(){
		setUserId();
		//loadAndSetScriptsOfUser(); //this loads all scripts, we do not necessarily have to do that here, only if overview page is opened!
		boolean isNewPatIllScript = loadAndSetPatIllScript();
		feedbackBean = new FeedbackBean(false, patillscript.getParentId());
		if(!isNewPatIllScript) loadScoreContainer();
		FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().put(CRT_FC_KEY, this);
	}

	private void setUserId(){
		String setUserIdStr = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_USER);
		if(setUserIdStr!=null) this.userId = (Long.valueOf(setUserIdStr).longValue());
	}

	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}

	private void loadAndSetScriptsOfUser(){	setScriptsOfUser(isc.loadScriptsOfUser());}
	private void loadScoreContainer(){
		//TODO: we have to load previous scores for this patIllScript.:
		
	}
	/**
	 * load PatientIllnessScript based on id or sessionId
	 */
	public boolean loadAndSetPatIllScript(){ 
		long id = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		long sessionId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SESSION);
		boolean isNew = true;
		if(id>0){
			isNew = false;
			setPatillscript(isc.loadPatIllScriptById(id));
		}
		else setPatillscript(isc.loadPatIllScriptBySessionId(sessionId));
		
		//TODO error handling!!!!
		return isNew;
	}
	public void loadAndSetPatIllScript(long id){ setPatillscript(isc.loadPatIllScriptById(id));}
	
	public PatientIllnessScript getPatillscript() { 
		return patillscript;
		//FacesContext fc = FacesContextWrapper.getCurrentInstance();
		//return (PatientIllnessScript) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(PATILLSCRIPT_KEY);
	}
	public void setPatillscript(PatientIllnessScript patillscript) { 
		this.patillscript = patillscript;
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(PATILLSCRIPT_KEY, patillscript);
	}	
	public List<PatientIllnessScript> getScriptsOfUser() {return this.scriptsOfUser;}
	private void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser){this.scriptsOfUser = scriptsOfUser;}	
	public ScoreContainer getScoreContainer() {return scoreContainer;}
	public void setScoreBean(ScoreContainer scoreContainer) {this.scoreContainer = scoreContainer;}

	/* we land here from an ajax request....*/
	public void ajaxResponseHandler() throws IOException {
		new AjaxController().receiveAjax(this.getPatillscript());
	}
	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContextWrapper#getWrapped()
	 */
	/*public FacesContext getWrapped() {
		return this; //or return this ????
	}*/
}
