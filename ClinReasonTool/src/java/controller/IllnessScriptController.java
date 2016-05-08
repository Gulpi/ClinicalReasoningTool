package controller;

import java.io.Serializable;
import java.util.*;

import javax.faces.context.FacesContext;

import application.AppBean;
import beans.PatientIllnessScript;
import beans.SummaryStatement;
import beans.relation.Relation;
import beans.scoring.PeerBean;
import beans.scoring.ScoreBean;
import database.DBClinReason;
import database.DBScoring;
import util.CRTLogger;

/**
 * Helper class for creating and loading Illness scripts based on sessionId, userId, or id....
 * @author ingahege
 *
 */
public class IllnessScriptController implements Serializable{

	private static final long serialVersionUID = 1L;


	//TODO: we could also get the current script from the already loaded list -> reduces DB calls!
	public PatientIllnessScript loadPatIllScriptById(long id, long userId){
		if(id>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptById(id);
			/*if(patillscript==null){
				patillscript = createAndSaveNewPatientIllnessScript(userId); 
			}*/
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
		
			/*if(patillscript==null)
				patillscript = createAndSaveNewPatientIllnessScript(userId, parentId); */
			
			return patillscript;
		}
		return null;
	}
	
	/*public PatientIllnessScript loadPatIllScriptBySessionId(long sessionId, long userId){
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
	}*/
	

		
	/**We create a new PatientIllnessScript and save it. 
	 * @param sessionId
	 */
	public PatientIllnessScript createAndSaveNewPatientIllnessScript(long userId, long vpId){
		if(vpId<=0 || userId<=0) return null;
		Locale loc = FacesContext.getCurrentInstance().getApplication().getViewHandler().calculateLocale(FacesContext.getCurrentInstance());
		PatientIllnessScript patillscript = new PatientIllnessScript( userId, vpId, loc);
		patillscript.save();

		CRTLogger.out("New PatIllScript created for vp_id: " + vpId, CRTLogger.LEVEL_PROD);
		addScriptCreationToPeerBean(patillscript);
		return patillscript;
	}
	
	/**
	 * We create a PeerBean for the creation of the script of increment the number in the peerBean.
	 * @param patillscript
	 */
	private void addScriptCreationToPeerBean(PatientIllnessScript patillscript){
		PeerBean peer = AppBean.getPeers().getPeerBeanByIllScriptCreationActionAndParentId(patillscript.getParentId());
		if(peer==null){
			peer = new PeerSyncController().createNewPeerBean(ScoreBean.TYPE_SCRIPT_CREATION, patillscript.getParentId(), -1, 0, 0);
		}
		else{
			peer.incrPeerNum();
			new DBScoring().saveAndCommit(peer);
		}
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
