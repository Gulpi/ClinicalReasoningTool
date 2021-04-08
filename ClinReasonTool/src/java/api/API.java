package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import api.impl.Hello;
import api.impl.LearningAnalytics1;
import api.impl.PeerSyncAPI;
import api.impl.SpacyStructureStatsAPI;
import api.impl.SummaryStatementAPI;
import net.casus.util.String2HashKey;

/**
 * simple API dispatcher
 * 
 * @author gulpi
 *
 */
@ManagedBean(name = "api", eager = true)
@ApplicationScoped
public  class API  extends Observable implements Serializable {
	Map<String,ApiInterface> implementations = new HashMap<String,ApiInterface>();
		
	public API() {
		implementations.put("la", new LearningAnalytics1());
		implementations.put("peerSync", new PeerSyncAPI());
		implementations.put("stmt", new SummaryStatementAPI());
		implementations.put("spacy", new SpacyStructureStatsAPI());
	}
	
	public String getResult() {
		String auth_param = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getHeader("auth");
		// have to be extended... dummy check only !!
		/*if (auth_param == null || !auth_param.equals("crt_auth")) {
			return null;
		}*/
	
		String impl_param = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("impl");
		ApiInterface impl = null;
		if (impl_param != null && (impl = implementations.get(impl_param)) != null) {
			return impl.handle();
		}
		return null;
	}
	
    
}
