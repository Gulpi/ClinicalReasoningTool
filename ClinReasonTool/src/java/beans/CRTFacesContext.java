package beans;

import java.io.*;
import java.util.*;

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
	private String test = "hallo";
	
	private PatientIllnessScript patillscript;
	
	public CRTFacesContext(){
		setSessionId();
		loadPatIllScript();
	}
	public CRTFacesContext(long sessionId){
		this.sessionId = sessionId;
		this.test = "hallo";
	}
	
		
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	private void setSessionId(){
		Map<String,String[]> p = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		String[] p1 = p.get("sessionid");
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
		new DBClinReason().saveBean(patillscript);
	}
	
	public PatientIllnessScript getPatillscript() { return patillscript;}
	public void setPis(PatientIllnessScript patillscript) {this.patillscript = patillscript;}

	@Override
	public void addMessage(String arg0, FacesMessage arg1) {
		// TODO Auto-generated method stub
		
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Severity getMaximumSeverity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<FacesMessage> getMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<FacesMessage> getMessages(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

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
		new AjaxController(FacesContext.getCurrentInstance()).receiveAjax(patillscript);
	}

}
