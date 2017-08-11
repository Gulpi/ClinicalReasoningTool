package controller;

import java.util.*;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import beans.CRTFacesContext;

/**
 * We init here some application stuff, like hibernate,....
 * @author ingahege
 *@deprecated
 */
public class MyFacesContextFactory extends FacesContextFactory{

	/**
	 * Container in which we store the FacesContext for this application.
	 * Long=sessionId
	 */
	Map<Long, CRTFacesContext> facesContexts;
	private FacesContextFactory delegate;
	
	 public MyFacesContextFactory(FacesContextFactory facesContextFactory) {
		      delegate = facesContextFactory;
		      //FactoryFinder.setFactory("MyFacesContextFactory", "MyFacesContextFactory");
		      //getELContext().putContext(FacesContext.class, this);		      
	 }

	@Override
	public FacesContext getFacesContext(Object arg0, Object arg1, Object arg2, Lifecycle arg3) throws FacesException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * When a new CRTFacesContext is created we add it to the Map
	 * @param fc
	 */
	public void registerFacesContext(CRTFacesContext fc){
		/*if(facesContexts==null) facesContexts = new HashMap<Long,CRTFacesContext>();
		if(facesContexts.get(new Long(fc.getSessionId()))!=null){
			System.out.println("Session already registered");
		}
		else facesContexts.put(new Long(fc.getSessionId()), fc);*/
	}
	
	public CRTFacesContext getFacesContextBySessionId(long sessionId){
		if(facesContexts==null || facesContexts.isEmpty()) return null; 
		return facesContexts.get(new Long(sessionId));
	}
}
