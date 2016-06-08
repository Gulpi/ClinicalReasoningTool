package controller;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

import beans.*;
import beans.scripts.*;
/**
 * handles the navigation between pages (e.g. overview page and single illnessscript page)
 * when a new page is opened we have to make sure that the CRTFacesContext is up-to-date
 * @author ingahege
 *
 */
@ManagedBean(name = "navController", eager = true)
@RequestScoped
public class NavigationController implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * user has clicked to open the overview/portfolio page. We have to close the current patientIllnessScript
	 * @return
	 */
	public String openOverview(){
		removePatIllScript();	
		return "portfolio";
	}
	
	/**
	 * User has clicked on a link to open a patientIllnessScript, a sessionId has to be included as a request param
	 * called from AjaxController
	 * @return
	 */
	public String openPatIllScript(){
		//long id = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		CRTFacesContext context = getCRTFacesContext(); 
		if(context!=null) context.initSession();
		//TODO error handling
		return "prototype_fs";
	}
	
	/**
	 * User has clicked on a link to open and edit an expert's patientIllnessScript
	 * called from AjaxController
	 * @return
	 */
	public String openExpPatIllScript(){
		//long id = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		CRTFacesContext context = getCRTFacesContext(); 
		if(context!=null) context.initExpEditSession();
		//TODO error handling
		return "exp_boxes";
	}
	
	public String openPatIllScript(String s){
		CRTFacesContext context = getCRTFacesContext(); 
		if(context!=null) context.initSession();
		//TODO error handling
		return "prototype_fs";
	}
	
	public String logout(){
		//log the user out and remove all sessionScoped stuff.....
		return "todo"; 
	}
	
	
	/**
	 * TODO move to CRTFacesContext???? 
	 * we have to remove the current patIllScript (if there is one) and sessionId from the externalContext
	 */
	public void removePatIllScript(){
		CRTFacesContext crtFacesContext = getCRTFacesContext();
		if(crtFacesContext!=null && crtFacesContext.getPatillscript()!=null){
			//crtFacesContext.setSessionId(-1);
			notifyLog(crtFacesContext.getPatillscript());
			crtFacesContext.reset();
		}
	}
	
	public CRTFacesContext getCRTFacesContext(){
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		/*if(cnxt!=null)*/ return cnxt;
	}
	
	/**
	 * Calls isExpEdit in CRTFacesContext to determine whether currently an expert script is edited
	 * @return
	 */
	public boolean isExpEdit(){
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		if(cnxt ==null) return false;
		return cnxt.isExpEdit();
 	}
	
	public static Locale getLocale(){
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		if(cnxt==null) return new Locale("en");
		return cnxt.getViewRoot().getLocale();
	}
	
	private void notifyLog(PatientIllnessScript patillscript){
		LogEntry le = new LogEntry(LogEntry.CLOSEPATILLSCRIPT_ACTION, patillscript.getId(), patillscript.getId());
		le.save();
	}
}
