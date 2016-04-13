package controller;

import java.io.Serializable;
import java.util.*;

import javax.faces.context.FacesContext;
import beans.PatientIllnessScript;
import beans.SummaryStatement;
import beans.relation.Relation;
import database.DBClinReason;

/**
 * Helper class for creating and loading Illness scripts based on sessionId, userId, or id....
 * @author ingahege
 *
 */
public class IllnessScriptController implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * We load the patientIllnessScripts of a user
	 * @return  List<PatientIllnessScript> or null
	 */
	public List<PatientIllnessScript> loadScriptsOfUser(){
		String userIdStr = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_USER);
		long userId = 0;
		if(userIdStr!=null){
			userId = Long.valueOf(userIdStr).longValue();
		}
		if(userId>0){
			List<PatientIllnessScript> scriptsOfUser = new DBClinReason().selectPatIllScriptsByUserId(userId);
			return scriptsOfUser;
			//setScriptsOfUser(scriptsOfUser);
		}
		return null;
	}
	//TODO: we could also get the current script from the already loaded list -> reduces DB calls!
	public PatientIllnessScript loadPatIllScriptById(long id, long userId){
		if(id>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptById(id);
			if(patillscript==null){
				patillscript = createAndSaveNewPatientIllnessScript(userId); 
			}
			return patillscript;
		}
		else{
			return null;
			//TODO error message?
		}
	}
	
	/**
	 * get all IllnessScripts for a parent id from the database. 
	 * @param parentId
	 * @return
	 */
	public PatientIllnessScript loadIllnessScriptsByParentId(long userId, long parentId){
		if(parentId>0 && userId>0){
			PatientIllnessScript patillscript =new DBClinReason().selectPatIllScriptsByUserIdAndParentId(userId, parentId);
		
			if(patillscript==null)
				patillscript = createAndSaveNewPatientIllnessScript(userId); 
			
			return patillscript;
		}
		return null;
	}
	
	public PatientIllnessScript loadPatIllScriptBySessionId(long sessionId, long userId){
		if(sessionId>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptBySessionId(sessionId);
			if(patillscript==null){
				patillscript = createAndSaveNewPatientIllnessScript(userId); 
			}
			return patillscript;
		}
		else{
			return null;
			//TODO error message?
		}
	}
	

		
	/**We create a new PatientIllnessScript and save it. 
	 * @param sessionId
	 */
	private PatientIllnessScript createAndSaveNewPatientIllnessScript(long userId){
		
		//long userId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_USER);
		long sessionId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SESSION);
		long vpId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_VP);
		if((sessionId<=0 && vpId<=0) || userId<=0) return null;
		Locale loc = FacesContext.getCurrentInstance().getApplication().getViewHandler().calculateLocale(FacesContext.getCurrentInstance());
		PatientIllnessScript patillscript = new PatientIllnessScript(sessionId, userId, vpId, loc);
		patillscript.save();

		System.out.println("New PatIllScript created for session_id: " + sessionId);
		return patillscript;
	}
	
	public Long[] getListItemsFromRelationList(List<Relation> rels){
		if(rels==null) return null;
		Long[] ids = new Long[rels.size()];
		for(int i=0; i<rels.size(); i++){
			ids[i] = new Long(((Relation) rels.get(i)).getListItemId());
		}
		return ids;
	}
	
	
}
