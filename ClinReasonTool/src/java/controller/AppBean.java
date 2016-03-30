package controller;

import java.util.*;

import javax.faces.FactoryFinder;
import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.management.relation.Relation;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import beans.IllnessScript;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.relation.RelationDiagnosis;
import database.DBClinReason;
import database.HibernateUtil;

/**
 * We init here some application stuff, like hibernate,....
 * TODO: we could remove the scripts if no parent VP is open.
 * @author ingahege
 *
 */
@ManagedBean(name = "crtInit", eager = true)
@ApplicationScoped
public class AppBean extends ApplicationWrapper implements HttpSessionListener{

	public static final String DEFAULT_LOCALE="en"; 
	public static final String[] ACCEPTED_LOCALES = new String[]{"en", "de"};
	public static List<Graph> graphs;
	public static final String APP_KEY = "AppBean";
	
	/**
	 * we dynamically load the experts' scripts into the map if a learner opens a VP that has not been
	 * opened before by a learner.  
	 * 
	 */
	public static Map<Long, PatientIllnessScript> expertPatIllScripts;

	/**
	 * we dynamically load the illness scripts into the map if a learner opens a VP that has not been
	 * opened before by a learner.  
	 * 
	 */
	public static Map<Long, List<IllnessScript>> ilnessScripts;

	public static Map<Long, Graph> graph;
	
	/**
	 * called when the application is started... We init Hibernate and a ViewHandler (for Locale handling)
	 * We also put this AppBean into the ServletContext for later access to the applicationScoped scripts
	 */
	public AppBean(){
		HibernateUtil.initHibernate();
		setViewHandler(new CRTViewHandler(FacesContext.getCurrentInstance().getApplication().getViewHandler()));
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    context.setAttribute(APP_KEY, this);
	    
		//ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		//factory.setApplication(this);
	    System.out.println("Init done");
		//MeshImporter.main(null);
	}
	
	/**
	 * We have one experts' patientIllnessScript per parentId (=VPId). If it has not yet been loaded (by another 
	 * learner working on the same VP, we load it and put it into the expertPatIllScripts Map.
	 * @param parentId
	 */
	public synchronized void addExpertPatIllnessScriptForParentId(long parentId){
		if(expertPatIllScripts==null) expertPatIllScripts = new HashMap<Long, PatientIllnessScript>();
		if(parentId>0 && !expertPatIllScripts.containsKey(new Long(parentId))){
			PatientIllnessScript expScript = new DBClinReason().selectExpertPatIllScript(parentId);
			if(expScript!=null) expertPatIllScripts.put(new Long(parentId), expScript);
			//if(graphs!=null && graphs.get(new Long(parentId)!=null)) return
			//todo init graphs?
		}
	}
	
	
	/**
	 * We can have a list of IllnessScripts per VP (patient can suffer from more than one diagnosis). 
	 * If the list has not yet been loaded, we load it now and put it into the ilnessScripts Map.
	 * @param parentId
	 */
	/*public synchronized void addIllnessScriptForParentId(long parentId){
		if(ilnessScripts==null) ilnessScripts = new HashMap<Long, List<IllnessScript>>();
		if(parentId>0 && !ilnessScripts.containsKey(new Long(parentId))){
			List<IllnessScript> scripts = new DBClinReason().selectIllScriptByParentId(parentId);
			if(scripts!=null) ilnessScripts.put(new Long(parentId), scripts);
			//todo init graphs?
		}
	}*/
	
	public synchronized void addIllnessScriptForDiagnoses(List diagnoses, long parentId){
		if(ilnessScripts==null) ilnessScripts = new HashMap<Long, List<IllnessScript>>();
		if(parentId>0 && !ilnessScripts.containsKey(new Long(parentId))){
			List<IllnessScript> scripts = new DBClinReason().selectIllScriptByDiagnoses(diagnoses);
			if(scripts!=null) ilnessScripts.put(new Long(parentId), scripts);
			//todo init graphs?
		}
	}
	
	
	
	
	public static PatientIllnessScript getExpertPatIllScript(long parentId) {
		if(expertPatIllScripts!=null)
			return expertPatIllScripts.get(new Long(parentId));
		return null;
	}
	public static List<IllnessScript> getIlnessScripts(long parentId) {
		return ilnessScripts.get(new Long(parentId));
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ApplicationWrapper#getWrapped()
	 */
	public Application getWrapped() {
		return FacesContext.getCurrentInstance().getApplication();
	}

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
