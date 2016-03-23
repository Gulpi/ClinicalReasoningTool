package controller;

import java.util.*;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import beans.CRTFacesContext;
import database.HibernateUtil;

/**
 * We init here some application stuff, like hibernate,....
 * @author ingahege
 *
 */
@ManagedBean(name = "crtInit", eager = true)
@ApplicationScoped
public class CRTInit extends ApplicationWrapper{

	public static final String DEFAULT_LOCALE="en"; 
	public static final String[] ACCEPTED_LOCALES = new String[]{"en", "de"};
	/**
	 * Container in which we store the FacesContext for this application.
	 * Long=sessionId
	 */
	//Map<Long, CRTFacesContext> facesContexts;
	//private FacesContextFactory delegate;
	
	public CRTInit(){
		HibernateUtil.initHibernate();
		setViewHandler(new CRTViewHandler(FacesContext.getCurrentInstance().getApplication().getViewHandler()));
		//MeshImporter.main(null);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ApplicationWrapper#getWrapped()
	 */
	public Application getWrapped() {
		return FacesContext.getCurrentInstance().getApplication();
	}
}
