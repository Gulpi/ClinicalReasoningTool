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
import database.DBClinReason;

/**
 * The facesContext for a session....
 * @author ingahege
 *
 */
@ManagedBean(name = "crtContext", eager = true)
@SessionScoped
public class CRTFacesContext extends FacesContext implements Serializable{
	private static final long serialVersionUID = 1L;
	private long sessionId = -1;
	/**
	 * Messages (e.g. errors occured during ajax request handling
	 */
	private List<FacesMessage> messages;	
	private PatientIllnessScript patillscript;
	private List<PatientIllnessScript> scriptsOfUser;
	/**
	 * Detailed scores for this patIllScript
	 */
	private Score scores;
	
	public CRTFacesContext(){
		setSessionId();
		loadScriptsOfUser(); //this loads all scripts
		loadPatIllScript();
	}
	public CRTFacesContext(long sessionId){
		this.sessionId = sessionId;
		loadPatIllScript();
	}
	
		
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	private void setSessionId(){
		String sessionIdStr = new AjaxController().getRequestParamByKey("session_id");
		if(sessionIdStr!=null) this.sessionId = (Long.valueOf(sessionIdStr).longValue());
	}

	private void loadScriptsOfUser(){
		String userIdStr = new AjaxController().getRequestParamByKey("user_id");
		long userId = 0;
		if(userIdStr!=null){
			userId = Long.valueOf(userIdStr).longValue();
		}
		if(userId>0){
			this.scriptsOfUser = new DBClinReason().selectPatIllScriptsByUserId(userId);
		}
	}
	//TODO: we could also get the current script from the already loaded list -> reduces DB calls!
	private void loadPatIllScript(){
		if(sessionId>0){
			patillscript = new DBClinReason().selectPatIllScriptBySessionId(sessionId);
			if(patillscript==null) createAndSaveNewPatientIllnessScript(); 
		}
		else patillscript = null;
	}
	
	private void createAndSaveNewPatientIllnessScript(){
		patillscript = new PatientIllnessScript(this.sessionId, new AjaxController().getLocale());
		patillscript.save();
		System.out.println("New PatIllScript created for session_id: " + this.sessionId);
		//this.addPropertyChangeListener(new PatientIllnessScript());
	}
	
	public PatientIllnessScript getPatillscript() { return patillscript;}
	public void setPatillscript(PatientIllnessScript patillscript) {this.patillscript = patillscript;}
	public List<PatientIllnessScript> getScriptsOfUser() {return scriptsOfUser;}
	public void setScriptsOfUser(List<PatientIllnessScript> scriptsOfUser) {this.scriptsOfUser = scriptsOfUser;}
	/**
	 * @param arg0
	 * @param msg
	 */
	public void addMessage(String clientId, FacesMessage msg) {
		if(messages==null) messages = new ArrayList<FacesMessage>();
		messages.add(msg);		
	}
	public FacesMessage getCurrentMessage(){
		if(messages==null || messages.isEmpty()) return null; 
		return messages.get(0);
	}
	public void addMessage(FacesMessage msg) { addMessage(null, msg);}
	public void removeMessages(){messages=null;}

	@Override
	public Application getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> getClientIdsWithMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalContext getExternalContext() {
		//return super.getE
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Severity getMaximumSeverity() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages()
	 */
	public Iterator<FacesMessage> getMessages() {
		if(messages!=null)return messages.iterator();
		return null;}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
	 */
	public Iterator<FacesMessage> getMessages(String arg0) {		return messages.iterator();}

	@Override
	public RenderKit getRenderKit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRenderResponse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getResponseComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResponseStream getResponseStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseWriter getResponseWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UIViewRoot getViewRoot() {
		//UIViewRoot r = new UIViewRoot();
		//r.
		return null;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void responseComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponseStream(ResponseStream arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponseWriter(ResponseWriter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setViewRoot(UIViewRoot arg0) {
		// TODO Auto-generated method stub
		
	}
	/* we land here from an ajax request....*/
	public void ajaxResponseHandler() throws IOException {
		new AjaxController().receiveAjax(patillscript);
	}

}
