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
}
