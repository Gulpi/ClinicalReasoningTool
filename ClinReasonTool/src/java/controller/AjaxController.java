package controller;

import java.beans.Statement;
import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import beans.*;
import util.*;

public class AjaxController {
	FacesContext facesContext;
	
	public AjaxController(FacesContext fc){
		this.facesContext = fc;
	}

	
	/**
	 * Receive an ajax call and process it. 
	 * params: type = method to call, id=id to add/remove...
	 * @param patillscript
	 * @throws IOException
	 */
	public void receiveAjax(PatientIllnessScript patillscript) throws IOException {
	    //FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = facesContext.getExternalContext();
	    Map<String, String> reqParams = externalContext.getRequestParameterMap();
	    if(reqParams!=null){
	    	String methodName = reqParams.get("type");
	    	Statement stmt = new Statement(patillscript, methodName, new Object[]{new String(reqParams.get("id"))});
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
	    externalContext.setResponseContentType("text/xml");
	    externalContext.setResponseCharacterEncoding("UTF-8");    
	    externalContext.getResponseOutputWriter().write(createResponseXML(responseId));
	    facesContext.responseComplete();
	}
	
	/**
	 * assemble the xml response
	 * @param responseParam
	 * @return
	 */
	private String createResponseXML(String responseParam){
		//TODO: we might want to add more info to the response....
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><id>"+responseParam+"</id></response>";
		return s;
	}

}
