package controller;

import java.io.Serializable;
import java.util.*;

import javax.faces.context.FacesContext;
import javax.management.relation.Relation;

import beans.IllnessScript;
import beans.PatientIllnessScript;
import beans.graph.VertexInterface;
import database.DBClinReason;

/**
 * Helper class for creating and loading Illness scripts based on sessionId oder userId.
 * @author ingahege
 *
 */
public class IllnessScriptController implements Serializable{
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
	public PatientIllnessScript loadPatIllScriptById(long id){
		if(id>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptById(id);
			if(patillscript==null){
				patillscript = createAndSaveNewPatientIllnessScript(); 
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
	/*public List<IllnessScript> loadIllnessScriptsByParentId(long parentId){
		return new DBClinReason().selectIllScriptByParentId(parentId);
	}*/
	
	public PatientIllnessScript loadPatIllScriptBySessionId(long sessionId){
		if(sessionId>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptBySessionId(sessionId);
			if(patillscript==null){
				patillscript = createAndSaveNewPatientIllnessScript(); 
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
	private PatientIllnessScript createAndSaveNewPatientIllnessScript(){
		long userId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_USER);
		long sessionId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_SESSION);
		long vpId = new AjaxController().getIdRequestParamByKey(AjaxController.REQPARAM_VP);
		Locale loc = FacesContext.getCurrentInstance().getApplication().getViewHandler().calculateLocale(FacesContext.getCurrentInstance());
		PatientIllnessScript patillscript = new PatientIllnessScript(sessionId, userId, vpId, loc);
		patillscript.save();

		System.out.println("New PatIllScript created for session_id: " + sessionId);
		return patillscript;
	}
	
	public Long[] getListItemsFromRelationList(List rels){
		if(rels==null) return null;
		Long[] ids = new Long[rels.size()];
		for(int i=0; i<rels.size(); i++){
			ids[i] = new Long(((VertexInterface) rels.get(i)).getVertexId());
		}
		return ids;
	}
	
	
}
