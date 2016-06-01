package application;

import java.io.InputStream;
import java.util.*;

import javax.faces.FactoryFinder;
import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.management.relation.Relation;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import beans.IllnessScript;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.relation.RelationDiagnosis;
import beans.scoring.PeerContainer;
import controller.JsonCreator;
import controller.PeerSyncController;
import database.DBClinReason;
import database.HibernateUtil;
import util.CRTLogger;
import properties.IntlConfiguration;

/**
 * We init here some application stuff, like hibernate,....
 * TODO: we could remove the scripts if no parent VP is open.
 * @author ingahege
 *
 */
@ManagedBean(name = "crtInit", eager = true)
@ApplicationScoped
public class AppBean extends ApplicationWrapper implements HttpSessionListener{

	public static final String DEFAULT_LOCALE="en"; //TODO get from properties!
	public static final String[] ACCEPTED_LOCALES = new String[]{"en", "de"}; //TODO get from properties!
	public static List<Graph> graphs;
	public static final String APP_KEY = "AppBean";
	public static IntlConfiguration intlConf;
	/**
	 * Any application-wide properties, file is in WEB-INF/classes/globalsettings.properties
	 */
	public static final Properties properties = new Properties();
	
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

	private static PeerContainer peers = new PeerContainer();
	/**
	 * called when the application is started... We init Hibernate and a ViewHandler (for Locale handling)
	 * We also put this AppBean into the ServletContext for later access to the applicationScoped scripts
	 * Loading any 
	 */
	public AppBean(){
		HibernateUtil.initHibernate();
		intlConf = new IntlConfiguration();
		setViewHandler(new CRTViewHandler(FacesContext.getCurrentInstance().getApplication().getViewHandler()));
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    context.setAttribute(APP_KEY, this);
	    try{
	    	//load properties for the application(file is in WEB-INF/classes:
	    	InputStream input =  Thread.currentThread().getContextClassLoader().getResourceAsStream("globalsettings.properties");
	    	properties.load(input);
	    }
	    catch(Exception e){}
	    //does not have to be done on every restart:
	    new JsonCreator().initJsonExport(); 
	   
		//MeshImporter.main("en");
	    CRTLogger.out("Init done", CRTLogger.LEVEL_PROD);
	    new PeerSyncController(peers).sync();
	}
	

	
	//public static Monitor getMonitor() {return monitor;}

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
	
	
	public synchronized void addIllnessScriptForDiagnoses(List diagnoses, long parentId){
	/*	if(ilnessScripts==null) ilnessScripts = new HashMap<Long, List<IllnessScript>>();
		if(parentId>0 && !ilnessScripts.containsKey(new Long(parentId))){
			List<IllnessScript> scripts = new DBClinReason().selectIllScriptByDiagnoses(diagnoses);
			if(scripts!=null) ilnessScripts.put(new Long(parentId), scripts);
			//todo init graphs?
		}*/
	}
	
	public static PatientIllnessScript getExpertPatIllScript(long parentId) {
		if(expertPatIllScripts!=null)
			return expertPatIllScripts.get(new Long(parentId));
		return null;
	}
	public static List<IllnessScript> getIlnessScripts(long parentId) {
		if(ilnessScripts==null) return null;
		return ilnessScripts.get(new Long(parentId));
	}
	
	
	public static PeerContainer getPeers() {return peers;}

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
