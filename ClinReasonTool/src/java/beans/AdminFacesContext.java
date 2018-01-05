package beans;

import java.io.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import application.AppBean;
import application.Monitor;
import beans.graph.Graph;
import beans.list.ListInterface;
import beans.scoring.*;
import beans.scripts.*;
import beans.user.Auth;
import beans.user.User;
import controller.*;
import util.CRTLogger;
import util.StringUtilities;

/**
 * The facesContext for a session....
 * We put the CRTFacesContext into the ExternalContext of the FacesContext, so that we can access it throughout the
 * users' session.
 * @author ingahege
 *
 */
@ManagedBean(name = "adminContext", eager = true)
@SessionScoped
public class AdminFacesContext extends FacesContextWrapper implements MyFacesContext, Serializable{
	private static final long serialVersionUID = 1L;

	public static final String CRT_FC_KEY = "adminContext";
	
	private User user;
	/**
	 * This is the locale of the navigation etc.
	 */
	private Locale locale;
	private ExpPortfolio adminPortfolio;
	private PatientIllnessScript patillscript;
	private Graph graph;
	private ReportBean reports;
	private FeedbackContainer feedbackContainer;
	private LearningAnalyticsBean labean;
	
	public String getTest(){return "hallo";}
	
	public AdminFacesContext(){
		//ExternalContext ec =  FacesContextWrapper.getCurrentInstance().getExternalContext();
		locale = LocaleController.setLocale();
		try{
			CRTLogger.out(FacesContextWrapper.getCurrentInstance().getApplication().getStateManager().getViewState(FacesContext.getCurrentInstance()), CRTLogger.LEVEL_TEST);
			Monitor.addHttpSession((HttpSession)FacesContextWrapper.getCurrentInstance().getExternalContext().getSession(true));
		}
		catch(Exception e){}

	}
	
	public void setUser(User u){this.user = u;}
	public User getUser(){return user;}
	public Graph getGraph(){
		return graph;}
	public long getUserId() {
		if(user!=null) return user.getUserId();
		NavigationController.getInstance().redirect("/crt/src/html/admin/login.xhtml");
		return -1;
	}
	
	public boolean isView(){
		String path = FacesContextWrapper.getCurrentInstance().getExternalContext().getRequestServletPath();
		if(path!=null && path.contains("view/exp_boxes_view")) return true;
		return false;
	}
	
	
	public ReportBean getReports() {
		if(reports==null)
			reports = new ReportBean();
		return reports;
	}
	
	public List<FeedbackBean> getFeedbackBeans(){
		if(feedbackContainer==null && this.patillscript==null) return null;
		if(feedbackContainer == null || feedbackContainer.getUserId()!=patillscript.getUserId()){
			feedbackContainer = new FeedbackContainer(patillscript.getId(), patillscript.getUserId());				
			feedbackContainer.initFeedbackContainer();
		}
		if(feedbackContainer==null) return null;
		return feedbackContainer.getFeedbackBeansList();
	}

	public void setReports(ReportBean reports) {
		this.reports = reports;
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
	
	public void ajaxResponseHandlerReports() throws IOException{
		AjaxController.getInstance().receiveResportsAjax(reports);
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
	
	/* (non-Javadoc)
	 * @see beans.MyFacesContext#initSession()
	 */
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
	
	public void initGraph(){	  
		if(graph!=null && patillscript!=null && graph.isSameGraph(patillscript.getVpId(), patillscript.getId())) return; //nothing todo, graph already loaded

		//if(graph!=null) return; //nothing todo, graph already loaded
		graph = new Graph(patillscript.getVpId(), true, patillscript.getId());
		//if(graph!=null) graph.setExpEdit(true);

	}
	public PatientIllnessScript getPatillscript() {
		/*if(this.patillscript==null){ //can happen if an expert map is edited from a VP system, then we have to load/create it here...
			try{
				new Auth().loginAdminsViaAPI();
			}
			catch (Exception e){}
			this.patillscript = new ExpPortfolio().getOrCreateExpScriptFromVPSystem();
			if(this.patillscript!=null) initGraph();

		}*/
		return this.patillscript;
	}
	public void setPatillscript(PatientIllnessScript pi) {this.patillscript = pi;}
	public ScoreContainer getScoreContainer(){
		if((labean==null && patillscript!=null) || labean.getPatIllScriptId() != patillscript.getId())
			labean = new LearningAnalyticsBean(patillscript.getId(), patillscript.getUserId(), patillscript.getVpId());
		if(labean!=null) return labean.getScoreContainer();
		return null;
	}
	
	/**
	 * If the edit page is opened via an API from a VP authoring system, we get a paramater ("api=true")
	 * We then have to log the user in and load the script... 
	 * @return
	 */
	public boolean isOpenedViaAPI(){
		boolean isViaAPI =  AjaxController.getInstance().getBooleanRequestParamByKey(AjaxController.REQPARAM_API, false);
		String vpId = AjaxController.getInstance().getRequestParamByKey("vp_id");

		if(isViaAPI){
			try{
				if(this.user==null){ //load user:
					new Auth().loginAdminsViaAPI();
				}
				if(this.user!=null && (this.patillscript==null || !this.patillscript.getVpIdCrop().trim().equals(vpId))){ //load script and init graph:
					this.patillscript = new ExpPortfolio(this.user).getOrCreateExpScriptFromVPSystem();
					if(this.patillscript!=null) initGraph();
				}
			}
			catch (Exception e){
				CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			}
		}
		return isViaAPI;
	}
	
	public List<ListInterface> getSearchedListItems(){
		ListController lc = new ListController();
		String mode =  AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_SEARCH_MODE);
		String lang =  AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_LOC);
		String searchterm =  AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_SEARCHTERM);
		
		List<ListInterface> items = lc.getListItems(lang, searchterm, mode);
		return items;		 
	}

}
