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
	 * Messages (e.g. errors occured during ajax request handling)s
	 */
	private List<FacesMessage> messages;
	//private String test = "hallo";
	
	private PatientIllnessScript patillscript;
	/**
	 * Detailed scores for this patIllScript
	 */
	private Score scores;
	
	public CRTFacesContext(){
		setSessionId();
		loadPatIllScript();
	}
	public CRTFacesContext(long sessionId){
		this.sessionId = sessionId;
		loadPatIllScript();
	}
	
		
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	private void setSessionId(){
		Map<String,String[]> p = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		String[] p1 = p.get("session_id");
		if (p1 != null && p1.length>0){
			System.out.println(p1[0]);
			this.sessionId = (Long.valueOf(p1[0]).longValue());
		}
	}
	//move to a DB class
	private void loadPatIllScript(){
		patillscript = new DBClinReason().selectPatIllScriptBySessionId(sessionId);
		if(patillscript==null) createAndSaveNewPatientIllnessScript(); 
	}
	
	private void createAndSaveNewPatientIllnessScript(){
		patillscript = new PatientIllnessScript(this.sessionId);
		patillscript.save();
		System.out.println("New PatIllScript created for session_id: " + this.sessionId);
		//this.addPropertyChangeListener(new PatientIllnessScript());
	}
	
	public PatientIllnessScript getPatillscript() { return patillscript;}
	public void setPatillscript(PatientIllnessScript patillscript) {this.patillscript = patillscript;}

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
		// TODO Auto-generated method stub
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
