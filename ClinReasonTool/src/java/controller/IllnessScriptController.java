package controller;

import java.io.Serializable;
import java.util.*;

import javax.faces.context.FacesContext;

import application.AppBean;
import beans.scripts.*;
import beans.relation.Relation;
import beans.scoring.PeerBean;
import beans.scoring.ScoreBean;
import database.DBClinReason;
import database.DBScoring;
import util.CRTLogger;
import util.StringUtilities;

/**
 * Helper class for creating and loading Illness scripts based on sessionId, userId, or id....
 * @author ingahege
 *
 */
public class IllnessScriptController implements Serializable{

	private static final long serialVersionUID = 1L;
	static private IllnessScriptController instance = new IllnessScriptController();
	static public IllnessScriptController getInstance() { return instance; }


	//TODO: we could also get the current script from the already loaded list -> reduces DB calls!
	public PatientIllnessScript loadPatIllScriptById(long id, long userId){
		if(id>0){
			PatientIllnessScript patillscript = new DBClinReason().selectPatIllScriptById(id);
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
	public PatientIllnessScript loadIllnessScriptsByVpId(long userId, String vpId){
		if(vpId!=null && !vpId.equals("") && userId>0){
			PatientIllnessScript patillscript =new DBClinReason().selectPatIllScriptsByUserIdAndVpId(userId, vpId);
			
			return patillscript;
		}
		return null;
	}
	

		
	/**We create a new PatientIllnessScript and save it. 
	 * At this point the userId is already the internal userId of the tool! 
	 * @param sessionId
	 */
	public PatientIllnessScript createAndSaveNewPatientIllnessScript(long userId, String vpId, int systemId){
		if(vpId==null || userId<=0) return null;
		Locale loc = LocaleController.getInstance().getScriptLocale();//FacesContext.getCurrentInstance().getApplication().getViewHandler().calculateLocale(FacesContext.getCurrentInstance());
		PatientIllnessScript patillscript = new PatientIllnessScript( userId, vpId, loc, systemId);
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
		try{
			PeerBean peer = AppBean.getPeers().getPeerBeanByIllScriptCreationActionAndVpId(patillscript.getVpId());
			if(peer==null){
				peer = new PeerSyncController(AppBean.getPeers()).createNewPeerBean(ScoreBean.TYPE_SCRIPT_CREATION, patillscript.getVpId(), -1, 0, 0, 0, 0);
			}
			else{
				peer.incrPeerNum();
				//new DBScoring().saveAndCommit(peer);
			}
			new DBScoring().saveAndCommit(peer);
		}
		catch(Exception e){
			CRTLogger.out("IllnessScriptController.addScriptCreationToPeerBean:" + StringUtilities.stackTraceToString(e) , CRTLogger.LEVEL_ERROR);
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
	
	/**
	 * init the loading of VPScriptRef objects
	 * @return
	 */
	public Map<String, VPScriptRef> initVpScriptRefs(){
		List<VPScriptRef> vprefs = DBClinReason.getVPScriptRefs();
		Map<String, VPScriptRef> refs = new HashMap<String, VPScriptRef>();
		if(vprefs==null) return null;
		for(int i=0; i<vprefs.size(); i++){
			refs.put(vprefs.get(i).getVpId()+"_"+vprefs.get(i).getSystemId(), vprefs.get(i));
		}
		return refs;
	}
	
	
}
