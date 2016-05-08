package beans.scoring;

import java.util.*;

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
		List<PeerBean> actionBeeans = new ArrayList();
		for(int i=0; i<beans.size(); i++){
			PeerBean pb = beans.get(i);
			if(pb.getAction()==action) actionBeeans.add(pb);
		}
		if(actionBeeans.isEmpty()) return null;
		return actionBeeans;
	}
}
