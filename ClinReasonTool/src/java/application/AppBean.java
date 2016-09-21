package application;

import java.io.InputStream;
import java.util.*;

import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import beans.scripts.*;
import beans.graph.Graph;
import beans.scoring.PeerContainer;
import controller.IllnessScriptController;
import controller.JsonCreator;
import controller.MeshImporter;
import controller.PeerSyncController;
import database.DBClinReason;
import database.HibernateUtil;
import util.CRTLogger;
import util.StringUtilities;
import properties.IntlConfiguration;
//import test.TextSimilarityComparing;

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
	private static final Properties properties = new Properties();
	
	/**
	 * we dynamically load the experts' scripts into the map if a learner opens a VP that has not been
	 * opened before by a learner.  
	 * 
	 */
	public static Map<String, PatientIllnessScript> expertPatIllScripts;

	/**
	 * we dynamically load the illness scripts into the map if a learner opens a VP that has not been
	 * opened before by a learner.  
	 * 
	 */
	public static Map<Long, List<IllnessScript>> ilnessScripts;

	public static Map<Long, Graph> graph;

	private static PeerContainer peers = new PeerContainer();
	
	private static Map<String, VPScriptRef> vpScriptRefs;
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
	    	InputStream input =  Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/globalsettings.properties");
	    	properties.load(input);
	    }
	    catch(Exception e){}
	    //does not have to be done on every restart:
	    //new JsonCreator().initJsonExport(); 
	   
		//MeshImporter.main("en");
	   // new TextSimilarityComparing().compareTestData();
	    peers.initPeerContainer();
	    vpScriptRefs =  IllnessScriptController.getInstance().initVpScriptRefs();
	    CRTLogger.out("Init done", CRTLogger.LEVEL_PROD);
	    try{
	    	new PeerSyncController(peers).sync();
	    }
	    catch(Exception e){
	    	CRTLogger.out("AppBean(): " + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
	    }
	}
	

	
	//public static Monitor getMonitor() {return monitor;}

	/**
	 * We have one experts' patientIllnessScript per parentId (=VPId). If it has not yet been loaded (by another 
	 * learner working on the same VP, we load it and put it into the expertPatIllScripts Map.
	 * @param parentId
	 */
	public synchronized PatientIllnessScript addExpertPatIllnessScriptForVpId(String vpId){
		if(expertPatIllScripts==null) expertPatIllScripts = new HashMap<String, PatientIllnessScript>();
		if(vpId!=null && !expertPatIllScripts.containsKey(vpId)){
			PatientIllnessScript expScript = new DBClinReason().selectExpertPatIllScriptByVPId(vpId);
			if(expScript!=null) expertPatIllScripts.put(vpId, expScript);
			return expScript;
			//if(graphs!=null && graphs.get(new Long(parentId)!=null)) return
			//todo init graphs?
		}
		if(vpId!=null && expertPatIllScripts.containsKey(vpId)) return expertPatIllScripts.get(vpId);
		return null;
	}
	
	
	public synchronized void addIllnessScriptForDiagnoses(List diagnoses, String vpId){
	}
	
	public static PatientIllnessScript getExpertPatIllScript(String vpId) {
		if(expertPatIllScripts!=null)
			return expertPatIllScripts.get(vpId);
		return null;
	}
	public static List<IllnessScript> getIlnessScripts(String vpId) {
		if(ilnessScripts==null) return null;
		return ilnessScripts.get(vpId);
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
	
	/**
	 * @return the sharedSecret of the application as defined in the globalsettings.properties file.
	 */
	public static String getSharedSecret(){
		if(properties==null) return null; 
		return properties.getProperty("SharedSecret");
	}
	
	public static String getVPNameByParentId(String id){
		if(vpScriptRefs==null || vpScriptRefs.get(id)==null) return "";
		return vpScriptRefs.get(id).getVpName();
	}
}
