package beans.scoring;

import java.util.*;

import application.AppBean;
import beans.scripts.PatIllScriptContainer;
import beans.scripts.PatientIllnessScript;
import database.DBScoring;

/**
 * Container for PeerBean objects with access methods 
 * @author ingahege
 *
 */
public class PeerContainer {

	/**
	 * key= parentId (e.g. VP id), value = list of PeerBeans for this VP
	 */
	private Map<String, List<PeerBean>> peerBeans;
	
	
	public PeerContainer(){
		//initPeerContainer();
	}
	
	/**
	 * Load all PeerBeans into the PeerContainer. Currently we do that for ALL scripts, but this might be too 
	 * big later on if we have > 1000 scripts... 
	 */
	public void initPeerContainer(){
		if(peerBeans==null)
			peerBeans = new DBScoring().selectAllPeerBeans();
		if(peerBeans==null) peerBeans = new HashMap<String, List<PeerBean>>();
			
	}
		
	public Map<String, List<PeerBean>> getPeerBeans() {return peerBeans;}
	public void addPeerBeans( List<PeerBean> peers, String vpId){
		if(peerBeans==null) peerBeans = new HashMap<String, List<PeerBean>>();
		peerBeans.put(vpId, peers);
	}
	
	public List<PeerBean> getPeerBeans(String vpId){
		if(peerBeans==null) return null;
		return peerBeans.get(vpId);
	}
	
	public void addPeerBean(PeerBean bean, String vpId){
		List<PeerBean> peers = getPeerBeans(vpId);
		if(peers!=null) peers.add(bean); //List already there, so we add the peerBean
		else{
			List<PeerBean> l = new ArrayList<PeerBean>();
			l.add(bean);
			peerBeans.put(vpId, l);
		}
	}
	
	/**
	 * Gets the peer score for a certain action of a certain item (e.g. percentage of peers who have added "cough" in a VP.
	 * @param actionType
	 * @param parentId
	 * @param itemId
	 * @return
	 */
	public PeerBean getPeerBeanByActionVpIdAndItemId(int actionType, String vpId, long itemId){
		if(peerBeans==null || peerBeans.get(vpId)==null) return null;
		List<PeerBean> beans = peerBeans.get(vpId);
		for(int i=0; i<beans.size(); i++){
			PeerBean pb = beans.get(i);
			if(pb.getAction()==actionType && pb.getItemId()==itemId) return pb;
		}
		return null;
	}
	
	/**
	 * Get the peerBean object for a given parentId, action and stage (can only be ONE!)
	 * @param action
	 * @param stage
	 * @param parentId
	 * @return PeerBean or null
	 */
	public PeerBean getPeerBeanByVpIdActionAndStage(int action, int stage, String vpId){
		if(peerBeans==null || peerBeans.get(vpId)==null) return null;
		List<PeerBean> beans = peerBeans.get(vpId);
		for(int i=0; i<beans.size(); i++){
			if(beans.get(i).getAction()==action && beans.get(i).getStage() == stage) return beans.get(i);
		}
		return null;
	}
	
	/**
	 * returns the PeerBean for the action of creation of an patientIllnessScript for the given 
	 * parentId or null if no script has been created so far. 
	 * We use this to store the overall number of peers who have created a script.
	 *  
	 * @param parentId
	 * @return
	 * @deprecated
	 */
	/*public PeerBean getPeerBeanByIllScriptCreationActionAndParentId(String parentId){
		if(peerBeans==null || peerBeans.get(new Long(parentId))==null) return null;
		List<PeerBean> beans = peerBeans.get(new Long(parentId));
		for(int i=0; i<beans.size(); i++){
			PeerBean pb = beans.get(i);
			if(pb.getAction()==ScoreBean.TYPE_SCRIPT_CREATION) return pb;
		}
		return null;
	}*/
	
	/**
	 * returns the PeerBean for the action of creation of an patientIllnessScript for the given 
	 * parentId or null if no script has been created so far. 
	 * We use this to store the overall number of peers who have created a script.
	 *  
	 * @param parentId
	 * @return
	 */
	public PeerBean getPeerBeanByIllScriptCreationActionAndVpId(String parentId){
		if(peerBeans==null || peerBeans.get(parentId)==null) return null;
		List<PeerBean> beans = peerBeans.get(parentId);
		for(int i=0; i<beans.size(); i++){
			PeerBean pb = beans.get(i);
			if(pb.getAction()==ScoreBean.TYPE_SCRIPT_CREATION) return pb;
		}
		return null;
	}
	
	/**
	 * returns the PeerBean for the action of creation of an patientIllnessScript for the given 
	 * parentId or null if no script has been created so far. 
	 * We use this to store the overall number of peers who have created a script.
	 *  
	 * @param parentId
	 * @return
	 */
	public List<PeerBean> getPeerBeansByActionAndVpId(String vpId, int action){
		if(peerBeans==null || peerBeans.get(vpId)==null) return null;
		List<PeerBean> beans = peerBeans.get(vpId);
		List<PeerBean> actionBeans = new ArrayList<PeerBean>();
		for(int i=0; i<beans.size(); i++){
			PeerBean pb = beans.get(i);
			if(pb.getAction()==action) actionBeans.add(pb);
		}
		if(actionBeans.isEmpty()) return null;
		return actionBeans;
	}
	
	/**
	 * returns the peerBeans for all scripts in the learner's scriptContainer
	 * @param action
	 * @param sc
	 * @return
	 */
	public List<PeerBean> getPeerBeansByActionLastStage(int action, PatIllScriptContainer sc){
		if(peerBeans==null || sc==null || sc.getScriptsOfUser()==null) return null;
		List<PeerBean> peerBeansForAction = new ArrayList<PeerBean>();
		Iterator<PatientIllnessScript> it = sc.getScriptsOfUser().iterator();
		while (it.hasNext()){			
			List<PeerBean> beans = peerBeans.get(it.next().getVpId());
			PeerBean bean = getPeerBeanOfLastStage(action, beans);
			if(bean!=null) peerBeansForAction.add(bean);
		}
		return peerBeansForAction;
	}
	
	/**
	 * returns peerBeans fro actions that are independent from stage (e.g. overall performance)
	 * @param action
	 * @return
	 */
	public List<PeerBean> getPeerBeansByAction(int action, PatIllScriptContainer sc){
		if(peerBeans==null || sc==null || sc.getScriptsOfUser()==null) return null;
		List<PeerBean> peerBeansForAction = new ArrayList<PeerBean>();		
		Iterator<PatientIllnessScript> it = sc.getScriptsOfUser().iterator();
		while (it.hasNext()){			
			List<PeerBean> beans = peerBeans.get(it.next().getVpId());
			PeerBean bean = getPeerBean(action, beans);
			if(bean!=null) peerBeansForAction.add(bean);
		}
		return peerBeansForAction;
	}
	
	/**
	 * Returns the list score of the given type at the final stage of a case. (either end of case or the stage the learner was last on)
	 * @param type
	 * @return
	 */
	private PeerBean getPeerBeanOfLastStage(int type, List<PeerBean> beans){
		if(beans==null || beans.isEmpty()) return null;
		Iterator<PeerBean> it = beans.iterator();
		PeerBean lastBean = null;
		while(it.hasNext()){
			PeerBean pb = it.next(); 
			if(pb.getAction()==type && (lastBean==null || pb.getStage()>lastBean.getStage()))
				lastBean= pb;
		}
		return lastBean;
	}
	
	/**
	 * Returns the list score of the given type at the final stage of a case. (either end of case or the stage the learner was last on)
	 * @param type
	 * @return
	 */
	private PeerBean getPeerBean(int type, List<PeerBean> beans){
		if(beans==null || beans.isEmpty()) return null;
		Iterator<PeerBean> it = beans.iterator();
		PeerBean lastBean = null;
		while(it.hasNext()){
			PeerBean pb = it.next(); 
			if(pb.getAction()==type /*&& (lastBean==null || pb.getStage()>lastBean.getStage())*/)
				lastBean= pb;
		}
		return lastBean;
	}

	
	/*private List<PeerBean> getPeerResponsesForParentId(long parentId){
		List<PeerBean> peers = null;
		if(cont!=null) cont.getPeerBeans(parentId);
		if(peers==null || peers.isEmpty()){
			peers = new DBScoring().selectPeerBeansByParentId(parentId);
			cont.addPeerBeans(peers, parentId); //caching...
		}
		return peers;
	}*/
	
	/*private PeerBean getPeerBeanByListAction(List<PeerBean> peers, int action, int stage){		
		for(int i=0; i<peers.size(); i++){
			if(peers.get(i).getAction()==action && peers.get(i).getStage() == stage) return peers.get(i);
		}
		return null;	}*/

	/**
	 * should be obsolete, because we load all PeerBeans at the start of the application....
	 * @param parentId
	 */
	public void loadPeersForPatIllScript(String vpId){
		if(AppBean.getPeers()!=null && AppBean.getPeers().getPeerBeans(vpId)!=null) return;
		List<PeerBean> beans = new DBScoring().selectPeerBeansByVPId(vpId);
		AppBean.getPeers().addPeerBeans(beans, vpId);
	}
}
