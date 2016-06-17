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
	private Map<Long, List<PeerBean>> peerBeans;
	
	
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
	}
		
	public Map<Long, List<PeerBean>> getPeerBeans() {return peerBeans;}
	public void addPeerBeans( List<PeerBean> peers, long parentId){
		if(peerBeans==null) peerBeans = new HashMap<Long, List<PeerBean>>();
		peerBeans.put(new Long(parentId), peers);
	}
	
	public List<PeerBean> getPeerBeans(long parentId){
		if(peerBeans==null) return null;
		return peerBeans.get(new Long(parentId));
	}
	
	public void addPeerBean(PeerBean bean, long parentId){
		List<PeerBean> peers = getPeerBeans(parentId);
		if(peers!=null) peers.add(bean); //List already there, so we add the peerBean
		else{
			List<PeerBean> l = new ArrayList<PeerBean>();
			l.add(bean);
			peerBeans.put(new Long(parentId), l);
		}
	}
	
	/**
	 * Gets the peer score for a certain action of a certain item (e.g. percentage of peers who have added "cough" in a VP.
	 * @param actionType
	 * @param parentId
	 * @param itemId
	 * @return
	 */
	public PeerBean getPeerBeanByActionParentIdAndItemId(int actionType, long parentId, long itemId){
		if(peerBeans==null || peerBeans.get(new Long(parentId))==null) return null;
		List<PeerBean> beans = peerBeans.get(new Long(parentId));
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
	public PeerBean getPeerBeanByParentIdActionAndStage(int action, int stage, long parentId){
		if(peerBeans==null || peerBeans.get(new Long(parentId))==null) return null;
		List<PeerBean> beans = peerBeans.get(new Long(parentId));
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
	 */
	public PeerBean getPeerBeanByIllScriptCreationActionAndParentId(long parentId){
		if(peerBeans==null || peerBeans.get(new Long(parentId))==null) return null;
		List<PeerBean> beans = peerBeans.get(new Long(parentId));
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
	public List<PeerBean> getPeerBeansByActionAndParentId(long parentId, int action){
		if(peerBeans==null || peerBeans.get(new Long(parentId))==null) return null;
		List<PeerBean> beans = peerBeans.get(new Long(parentId));
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
	public List<PeerBean> getPeerBeansByAction(int action, PatIllScriptContainer sc){
		if(peerBeans==null || sc==null || sc.getScriptsOfUser()==null) return null;
		List<PeerBean> peerBeansForAction = new ArrayList<PeerBean>();
		Iterator<PatientIllnessScript> it = sc.getScriptsOfUser().iterator();
		while (it.hasNext()){			
			List<PeerBean> beans = peerBeans.get(new Long(it.next().getParentId()));
			PeerBean bean = getListPeerBeanOfLastStage(action, beans);
			if(bean!=null) peerBeansForAction.add(bean);
			/*for(int i=0; i<beans.size(); i++){
				PeerBean pb = beans.get(i);
				if(pb.getAction()==action){
					peerBeansForAction.add(pb);
					break;
				}				
			}*/
		}
		return peerBeansForAction;
	}
	
	/**
	 * returns all peerBeans for all scripts - maybe for later use, this is also something that can be cached!
	 * @param action
	 * @return
	 */
	public List<PeerBean> getPeerBeansByAction(int action){
		if(peerBeans==null) return null;
		List<PeerBean> peerBeansForAction = new ArrayList<PeerBean>();		
		Iterator<List<PeerBean>> it = peerBeans.values().iterator();
		while (it.hasNext()){
			List<PeerBean> beans = it.next();
			PeerBean bean = getListPeerBeanOfLastStage(action, beans);
			if(bean!=null) peerBeansForAction.add(bean);
			/*for(int i=0; i<beans.size(); i++){
				PeerBean pb = beans.get(i);
				if(pb.getAction()==action){
					peerBeansForAction.add(pb);
					break;
				}				
			}*/
		}
		return peerBeansForAction;
	}
	
	/**
	 * Returns the list score of the given type at the final stage of a case. (either end of case or the stage the learner was last on)
	 * @param type
	 * @return
	 */
	private PeerBean getListPeerBeanOfLastStage(int type, List<PeerBean> beans){
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
	public void loadPeersForPatIllScript(long parentId){
		if(AppBean.getPeers()!=null && AppBean.getPeers().getPeerBeans(parentId)!=null) return;
		List<PeerBean> beans = new DBScoring().selectPeerBeansByParentId(parentId);
		AppBean.getPeers().addPeerBeans(beans, parentId);
	}
}
