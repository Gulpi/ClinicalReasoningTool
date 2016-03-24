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
 * TODO shall we put the CRTFacesContext into the ExternalContext as well???
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
	private List<PatientIllnessScript> scriptsOfUser;

	/**
	 * Detailed scores for this patIllScript
	 */
	//private Score scores; -> put in FacesContext
	
	public CRTFacesContext(){
		//setSessionId();
		setUserId();
		//Locale loc = new AjaxController().getLocale();
		//loadAndSetScriptsOfUser(); //this loads all scripts, we do not necessarily have to do that here, only if overview page is opened!
		loadAndSetPatIllScript();
		
		//FacesContextWrapper.getCurrentInstance().getExternalContext().getSessionMap().put(CRT_FC_KEY, this);
	}
	/*public CRTFacesContext(long sessionId){
		this.sessionId = sessionId;
		loadAndSetPatIllScript();
	}*/
			
	//public long getSessionId() {return sessionId;}
	//public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	/*private void setSessionId(){
		String sessionIdStr = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_SESSION);
		if(sessionIdStr!=null) this.sessionId = (Long.valueOf(sessionIdStr).longValue());
	}*/
	private void setUserId(){
		String setUserIdStr = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_USER);
		if(setUserIdStr!=null) this.userId = (Long.valueOf(setUserIdStr).longValue());
	}

	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}

	private void loadAndSetScriptsOfUser(){	setScriptsOfUser(isc.loadScriptsOfUser());}
	
	/**
	 * load PatientIllnessScript based on id or sessionId
	 */
	public void loadAndSetPatIllScript(){ 
		long id = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		long sessionId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SESSION);
		if(id>0)
			setPatillscript(isc.loadPatIllScriptById(id));
		else setPatillscript(isc.loadPatIllScriptBySessionId(sessionId));
		
		//TODO error handling!!!!
	}
	public void loadAndSetPatIllScript(long id)
	{ 
		setPatillscript(isc.loadPatIllScriptById(id));
	}
	
	public PatientIllnessScript getPatillscript() { 
		return patillscript;
		//FacesContext fc = FacesContextWrapper.getCurrentInstance();
		//return (PatientIllnessScript) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(PATILLSCRIPT_KEY);
	}
	public void setPatillscript(PatientIllnessScript patillscript) { 
		this.patillscript = patillscript;
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(PATILLSCRIPT_KEY, patillscript);
	}
	
	public List<PatientIllnessScript> getScriptsOfUser() {
		return this.scriptsOfUser;
	}
	private void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser){
		this.scriptsOfUser = scriptsOfUser;
	}

	public String getTest(){
		return FacesContextWrapper.getCurrentInstance().toString();
	}
	
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
