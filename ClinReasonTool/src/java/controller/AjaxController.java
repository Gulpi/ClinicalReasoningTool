package controller;

import java.beans.Statement;
import java.io.IOException;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import beans.*;
import util.*;

public class AjaxController {
	//CRTFacesContext facesContext;
	
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
	    FacesContext facesContext2 = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = facesContext2.getExternalContext();
	    Map<String, String> reqParams = externalContext.getRequestParameterMap();
	    if(reqParams!=null){
	    	String sessionId = reqParams.get("session_id");
	    	if(patillscript==null || sessionId==null|| Long.parseLong(sessionId)!=patillscript.getSessionId()){
	    		System.out.println("Error: SessionId is:"+ sessionId + ", patIllScript.sessionId="+patillscript.getSessionId());
	    		return; //TODO we need some more error handling here, how can this happen? What shall we do? 
	    	}
	    	String methodName = reqParams.get("type");
	    	String idStr = reqParams.get("id");
	    	String nameStr = reqParams.get("name");
	    	Statement stmt; 
	    	if(nameStr!=null && !nameStr.trim().equals("")) stmt = new Statement(patillscript, methodName, new Object[]{idStr, nameStr});
	    	else stmt = new Statement(patillscript, methodName, new Object[]{idStr});
	    	
	    	try {
				stmt.execute();				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(StringUtilities.stackTraceToString(e));
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
		appendMessag(xmlResponse);
		//facesContext.
		xmlResponse.append("</response>");
		return xmlResponse.toString();
	}
	
	/**
	 * If e.g. an error has occurred we get the message from the FacesContext and include it into the xml response.
	 * @param xmlResponse
	 */
	private void appendMessag(StringBuffer xmlResponse){
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

}
