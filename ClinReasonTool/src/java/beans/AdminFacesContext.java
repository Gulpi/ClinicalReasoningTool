package beans;

import java.io.*;
import java.util.*;
import java.beans.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import application.AppBean;
import application.Monitor;
import beans.graph.Graph;
import beans.scoring.*;
import beans.scripts.*;
import beans.user.User;
import controller.*;
import database.DBEditing;
import database.DBUser;
import util.CRTLogger;

/**
 * The facesContext for a session....
 * We put the CRTFacesContext into the ExternalContext of the FacesContext, so that we can access it throughout the
 * users' session.
 * @author ingahege
 *
 */
@ManagedBean(name = "adminContext", eager = true)
@SessionScoped
public class AdminFacesContext extends FacesContextWrapper implements MyFacesContext/*implements Serializable*/{
	public static final String CRT_FC_KEY = "adminContext";
	
	private User user;
	/**
	 * This is the locale of the navigation etc.
	 */
	private Locale locale;
	private ExpPortfolio adminPortfolio;
	private PatientIllnessScript patillscript;
	private Graph graph;
	
	public String getTest(){return "hallo";}
	
	public AdminFacesContext(){
		ExternalContext ec =  FacesContextWrapper.getCurrentInstance().getExternalContext();
		locale = LocaleController.setLocale();
		try{
			CRTLogger.out(FacesContextWrapper.getCurrentInstance().getApplication().getStateManager().getViewState(FacesContext.getCurrentInstance()), CRTLogger.LEVEL_TEST);
			Monitor.addHttpSession((HttpSession)FacesContextWrapper.getCurrentInstance().getExternalContext().getSession(true));
		}
		catch(Exception e){}

	}
	
	public void setUser(User u){this.user = u;}
	public Graph getGraph(){return graph;}
	public long getUserId() {
		if(user!=null) return user.getUserId();
		NavigationController.getInstance().redirect("/crt/src/html/admin/login.xhtml");
		return -1;
	}
	
	public AppBean getAppBean(){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	   return (AppBean) context.getAttribute(AppBean.APP_KEY);
		
	}
	

	/** 
	 * we land here from an ajax request for any actions concerning the patientIllnessScript....
	 **/
	public void ajaxResponseHandler() throws IOException {
		AjaxController.getInstance().receiveAjax(this.getPatillscript());
	}

	public FacesContext getWrapped() {return FacesContext.getCurrentInstance();}
	
	public Locale getLocale(){return locale;}//LocaleController.getLocale(this).getLanguage();}	
	public String getLanguage(){return locale.getLanguage();}
	
	public ExpPortfolio getAdminPortfolio(){
		if(adminPortfolio==null && user!=null) initAdminPortfolio();
		if(adminPortfolio!=null) return adminPortfolio;
		return null;
	}
	
	private void initAdminPortfolio(){
		if(user==null) return;
		adminPortfolio = new ExpPortfolio(user);
	}
	
	public void initSession(){ 
		if(user==null) return;
		long id = AjaxController.getInstance().getLongRequestParamByKey(AjaxController.REQPARAM_SCRIPT);
		if(this.patillscript!=null && (id<0 || this.patillscript.getId()==id)) return; //current script already loaded....
		if(id<=0) return;

		if(id>0) this.patillscript = adminPortfolio.getExpScriptById(id);
		
		if(this.patillscript!=null) initGraph();		
	}
	
	public void reset(){
		this.patillscript = null;
		this.graph = null;
	}
	
	private void initGraph(){	    
		if(graph!=null) return; //nothing todo, graph already loaded
		graph = new Graph(patillscript.getVpId());
		if(graph!=null) graph.setExpEdit(true);

	}
	public PatientIllnessScript getPatillscript() {return this.patillscript;}
	public ScoreContainer getScoreContainer(){return null;}

}
