package beans;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import database.DBClinReason;
import database.DBEditing;
import util.CRTLogger;
import beans.scripts.*;
import beans.user.User;
import controller.AjaxController;
/**
 * all scripts of an expert, needed for the overview/portfolio page to display a list. 
 *
 */
@ManagedBean(name = "expport", eager = true)
@SessionScoped
public class ExpPortfolio implements Serializable{

	private static final long serialVersionUID = 1L;
	private long userId;
	private List<PatientIllnessScript> expscripts;

	
	public ExpPortfolio(User u ){
		this.userId = u.getUserId();
		loadScripts();
	}
		
	public List<PatientIllnessScript> getExpscripts() {return expscripts;}
	
	public void addExpertScript(PatientIllnessScript script){
		if(script==null) return;
		if(expscripts==null) expscripts = new ArrayList<PatientIllnessScript>();
		expscripts.add(script);
	}

	/**
	 * if no scripts are available for this user, we display a message
	 * @return
	 */
	public String getNoscripts(){
		if(expscripts==null || expscripts.isEmpty()) return "No scripts available.";
		return "";
	}

	/**
	 * TODO: later on we have to consider the userId to load only scripts that are editable by the current user.
	 */
	private void loadScripts(){
		if(expscripts==null) expscripts = new DBEditing().selectAllExpertPatIllScriptsByUserId(userId);
	}
	
	public PatientIllnessScript getExpScriptById(long id){
		if(expscripts==null) return null;
		Iterator<PatientIllnessScript> it = expscripts.iterator();
		while(it.hasNext()){
			PatientIllnessScript scr = it.next();
			if(scr.getId()==id) return scr;		
		}
		return null;
	}
	
	/**
	 * A new expert script is created and stored into the database
	 */
	public void createNewExpScript(){
		CRTLogger.out("Create new script", CRTLogger.LEVEL_PROD);
		String vpId = AjaxController.getInstance().getRequestParamByKey("vp_id");
		String lang = AjaxController.getInstance().getRequestParamByKey("vp_lang");
		if(lang==null || lang.isEmpty()) lang = "en";
		PatientIllnessScript patillscript = new PatientIllnessScript(userId, vpId, new Locale(lang), 2);
		int maxStage = AjaxController.getInstance().getIntRequestParamByKey("maxstage", -1);
		int maxddxstage = AjaxController.getInstance().getIntRequestParamByKey("maxddxstage", -1);
		patillscript.iniExpertScript(maxStage, maxddxstage);
		new DBClinReason().saveAndCommit(patillscript);
		String vpName = AjaxController.getInstance().getRequestParamByKeyNoDecrypt("vp_name");

		VPScriptRef ref= new VPScriptRef(patillscript.getVpId(), vpName, 2, vpId);
		new DBClinReason().saveAndCommit(ref);
		addExpertScript(patillscript);
		CRTLogger.out("Create new script: id= " + patillscript.getId(), CRTLogger.LEVEL_PROD);

	}
}
