package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.casus.util.String2HashKey;

/**
 * View to display open sessions...
 * @author gulpi
 *
 */
@ManagedBean(name = "api", eager = true)
@ApplicationScoped
public  class API  extends Observable implements Serializable {
		
	public API() {
	}
	
	public String getResult() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		/*ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
	    externalContext.setResponseContentType("text/xml");
	    externalContext.setResponseCharacterEncoding("UTF-8"); */
	    //for additional id info (e.g. when adding a new connection)
		Object o = FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletRequest req = ((HttpServletRequest) o);
		
		String id = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id");
		
		String result = "";
		String2HashKey bean = new String2HashKey("id",id);
		try {
			result = new ObjectMapper().writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
    
}
