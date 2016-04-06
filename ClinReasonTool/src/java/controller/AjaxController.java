package controller;

import java.beans.Statement;
import java.io.IOException;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

import org.apache.commons.lang.StringUtils;

import beans.*;
import util.*;

public class AjaxController {
	public static final String REQPARAM_USER = "user_id";
	public static final String REQPARAM_SESSION = "session_id";
	public static final String REQPARAM_SCRIPT = "script_id";
	public static final String REQPARAM_VP = "vp_id";
	public static final String REQPARAM_LOC = "locale";
	public static final String REQPARAM_STAGE = "stage";
	
	public AjaxController(/*CRTFacesContext fc*/){
		//this.facesContext = fc;
	}

	
	/**
	 * Receive an ajax call and process it. 
	 * params: type = method to call, id=id to add/remove...
	 * @param patillscript
	 * @throws IOException
	 */
	public void receiveAjax(PatientIllnessScript patillscript) throws IOException {
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
	    //ExternalContext externalContext = facesContext2.getExternalContext();
	    //ExternalContext ec = fcw.getExternalContext();
	   // Map<String, Object> sessionMap = externalContext.getSessionMap().get("patIll);
	    Map<String, String> reqParams = externalContext.getRequestParameterMap();
	    if(reqParams!=null){
	    	String patillscriptId = reqParams.get(REQPARAM_SCRIPT);
	    	if(patillscript==null || patillscriptId==null|| Long.parseLong(patillscriptId)!=patillscript.getId()){
	    		System.out.println("Error: patillscriptId is:"+ patillscriptId + ", patIllScript.sessionId="+patillscript.getSessionId());
	    		return; //TODO we need some more error handling here, how can this happen? What shall we do? 
	    	}
	    	String methodName = reqParams.get("type");
	    	String idStr = reqParams.get("id");
	    	String nameStr = reqParams.get("name");
	    	String x = reqParams.get("x");
	    	String y = reqParams.get("y");
	    	patillscript.updateStage(reqParams.get(REQPARAM_STAGE));

	    	String patIllScriptId = reqParams.get(REQPARAM_SCRIPT); //TODO check whether belongs to currently loaded script!
	    	Statement stmt; 
	    	if(x!=null && !x.trim().equals("")){
	    		stmt = new Statement(patillscript, methodName, new Object[]{idStr, nameStr,x,y});
	    	}
	    	else if(nameStr!=null && !nameStr.trim().equals("")) 
	    		stmt = new Statement(patillscript, methodName, new Object[]{idStr, nameStr});
	    	else stmt = new Statement(patillscript, methodName, new Object[]{idStr});
	    	
	    	try {
				stmt.execute();				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.out(StringUtilities.stackTraceToString(e), Logger.LEVEL_PROD);
			}
	    	patillscript.toString();
	    	responseAjax(externalContext, reqParams.get("id"));
	    }
	}
	

	
	/**
	 * handles the ajax response (xml)
	 * @param externalContext
	 * @throws IOException
	 */
	private void responseAjax(ExternalContext externalContext, String responseId) throws IOException{
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    externalContext.setResponseContentType("text/xml");
	    externalContext.setResponseCharacterEncoding("UTF-8");    
	    externalContext.getResponseOutputWriter().write(createResponseXML(responseId));
	    facesContext.responseComplete();
	}
	
	/**
	 * assemble the xml response, include the id and if applicable a message.
	 * @param responseParam
	 * @return
	 */
	private String createResponseXML(String responseParam){
		
		//TODO: we might want to add more info to the response....
		StringBuffer xmlResponse = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><id>");
		xmlResponse.append(responseParam+"</id>");
		appendMessage(xmlResponse);
		//facesContext.
		xmlResponse.append("</response>");
		return xmlResponse.toString();
	}
	
	/**
	 * If e.g. an error has occurred we get the message from the FacesContext and include it into the xml response.
	 * @param xmlResponse
	 */
	private void appendMessage(StringBuffer xmlResponse){
		List<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessageList();
		if(msgs!=null && !msgs.isEmpty()){ //per default we only display last message
			FacesMessage fmsg = msgs.get(0);	
		//if(((CRTFacesContext) facesContext).getCurrentMessage()!=null){ //then we have to include a message into the response:
			//String msg = "";facesContext.getCurrentMessage().getSummary();
			/*if(msg!=null)*/ 
		if(fmsg.getSummary()!=null && !fmsg.getSummary().trim().equals(""))
				xmlResponse.append("<msg>"+fmsg.getSummary()+"</msg>");
				xmlResponse.append("<ok>0</ok>"); //indicates that an error has occured
		}
		else xmlResponse.append("<ok>1</ok>"); //no error has occured
	}
	
	public String getRequestParamByKey(String key){
		Map<String,String[]> p = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		String[] p1 = p.get(key);
		if (p1 != null && p1.length>0){
			System.out.println(p1[0]);
			return p1[0];
			//this.sessionId = (Long.valueOf(p1[0]).longValue());
		}
		return null;
	}
	
	public long getIdRequestParamByKey(String key){
		String id = getRequestParamByKey(key);
		if(id!=null && !id.trim().equals("")){
			return Long.parseLong(id);
		}
		return -1;
	}
	
	/**
	 * get Locale based on request param or facesContext. We have to do this, because the patientIllnessScript 
	 * might be in a different Locale than the current browser Locale. 
	 * 
	 * @return
	 */
/*	public Locale getLocale(){
		Locale loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(loc==null) loc = new Locale(CRTInit.DEFAULT_LOCALE);
		String locStr = getRequestParamByKey(REQPARAM_LOC);
		if(locStr!=null && !locStr.trim().equals("")){ 
			for(int i=0; i<CRTInit.ACCEPTED_LOCALES.length; i++){
			if(locStr.equals(CRTInit.ACCEPTED_LOCALES[i]))
					loc = new Locale(locStr);
			}
		}
		
		return loc;
	}*/
}
