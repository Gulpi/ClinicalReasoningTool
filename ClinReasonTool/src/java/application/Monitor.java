package application;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.*;

import controller.AjaxController;

/**
 * View to display open sessions...
 * @author ingahege
 *
 */
@ManagedBean(name = "monitor", eager = true)
@ApplicationScoped
public  class Monitor extends Observable implements HttpSessionListener, HttpSessionAttributeListener, HttpSessionActivationListener, Serializable {

	private static final long serialVersionUID = 1L;
	private static List<HttpSession>  httpSessions = new ArrayList<HttpSession>();
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionActivationListener#sessionDidActivate(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDidActivate(HttpSessionEvent arg0) {
		addHttpSession(arg0.getSession());		
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
       addHttpSession(arg0.getSession());		
	}
	

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		delHttpSession(arg0.getSession());		
	}
	
    public static void addHttpSession(HttpSession httpSession) {
    	try {
	        if (!httpSessions.contains(httpSession))
	            httpSessions.add(httpSession);
    	}
    	catch(Exception e){}
    }
    
    public void delHttpSession(HttpSession httpSession) {
    	if(httpSessions!=null && httpSessions.contains(httpSession))
    		httpSessions.remove(httpSession);
    }

	public List<HttpSession> getHttpSessions() {
		return httpSessions;
	}   
	
	public void ajaxResponseHandler() throws IOException{
		AjaxController.getInstance().receiveMonitorAjax(this);
	}
	
	public void removeExpScriptFromCache(String vpId){
		AppBean.updateExpertPatIllnessScriptForVpId(vpId);
	}
    
}
