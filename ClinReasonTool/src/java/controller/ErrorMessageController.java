package controller;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class ErrorMessageController {

	
	public void addErrorMessage(String summary, String details, Severity sev){
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
	}
	
	public void toXml(StringBuffer xmlResponse){
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
