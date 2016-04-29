package controller;

import java.util.*;

import application.AppBean;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.scoring.PeerBean;
import beans.scoring.PeerContainer;
import beans.scoring.ScoreBean;
import database.DBClinReason;
import database.DBScoring;
import util.CRTLogger;

/**
 * We get all patIllScripts that have not yet been added to the peer table. We add these to the 
 * peers and set the sync flag to true.
 * @author ingahege
 *
 */
public class PeerSyncController {
	
	List<PatientIllnessScript> scripts;
	PeerContainer cont;
	
	public PeerSyncController(){}
	public PeerSyncController(PeerContainer cont){
		this.cont = cont;
	}
	/**
	 * look for scripts and if there are new ones, add them to the peer table...
	 */
	public synchronized void sync(){
		//TODO only select scripts that are at the same stage as the expertscript?
		scripts = new DBClinReason().selectLearnerPatIllScriptsByPeerSync();
		if(scripts==null || scripts.isEmpty()){
			CRTLogger.out("PeerSyncController.sync - nothing to sync", CRTLogger.LEVEL_PROD);
		}
		for(int i=0; i<scripts.size(); i++){
			PatientIllnessScript script = scripts.get(i);
			List<ScoreBean> scores = new DBScoring().selectScoreBeansByPatIllScriptId(script.getId());
			List<PeerBean> peers = getPeerResponsesForParentId(script.getParentId());
			
			syncList(script.getProblems(), peers, script.getParentId());
			syncList(script.getDiagnoses(), peers, script.getParentId());
			syncList(script.getMngs(), peers, script.getParentId());
			syncList(script.getTests(), peers, script.getParentId());
			
			syncListActionScores(scores, peers, script.getParentId());
			script.setPeerSync(true);
			CRTLogger.out("Peer sync: " + script.getId() + "done", CRTLogger.LEVEL_PROD);
		}
		new DBClinReason().saveAndCommit(scripts); //save the changed sync status....
		CRTLogger.out("Peer sync done", CRTLogger.LEVEL_PROD);
	}
	
	/**
	 * We add up the scores of the list actions scores and nums in the PeerBeans...
	 * @param scores
	 * @param peers
	 * @param parentId
	 */
	private void syncListActionScores(List<ScoreBean> scores, List<PeerBean> peers, long parentId){
		if(scores==null) return;
		for(int i=0; i<scores.size(); i++){
			ScoreBean score = scores.get(i);
			if(score.isListScoreBean()){ //we only consider list scoring here!
				PeerBean peerBean = getPeerBeanByActionAndStage(peers, score.getType(), score.getStage());
				if(peerBean==null) peerBean = createNewPeerBean(score.getType(), parentId, -1, score.getScoreBasedOnIllScript(), score.getStage());
				else{
					peerBean.incrPeerNum();
					peerBean.incrScoreSum(score.getScoreBasedOnExp());
					new DBScoring().saveAndCommit(peerBean);
				}
			}
		}
	}
	

		
	/**
	 * We go thru the list of items and increase the peerNum for each item
	 * @param rels
	 * @param peers
	 * @param listAction
	 */
	private void syncList(List rels, List<PeerBean> peers, long parentId){
		if(rels==null || rels.isEmpty()) return;
		for(int i=0; i<rels.size(); i++){
			Relation rel = (Relation) rels.get(i);
			PeerBean peerBean = getPeerBeanByActionAndItemId(peers, rel.getRelationType(), rel.getListItemId());
			if(peerBean==null){
				peerBean = createNewPeerBean(rel.getRelationType(), parentId,  rel.getListItemId(), 0);				
			}
			else{
				peerBean.incrPeerNum();
				new DBScoring().saveAndCommit(peerBean);
			}
		}
	}
	
	/**
	 * We go thru the list of items and 
	 * @param rels
	 * @param peers
	 * @param listAction
	 */
	/*private void syncListScore(List rels, List<PeerBean> peers,  int listAction){
		if(rels==null || rels.isEmpty()) return;
		for(int i=0; i<rels.size(); i++){
			Relation rel = (Relation) rels.get(i);
			PeerBean peerBean = getPeerBeanByListAction(peers, listAction);
			if(peerBean==null){
				peerBean = createNewPeerBean(listAction, rel.getDestId());				
			}
			else{
				peerBean.incrPeerNum();
				new DBScoring().saveAndCommit(peerBean);
			}
		}
	}*/
	
	/**
	 * This action for this script has not yet have been performed by any peer, so we create 
	 * a new PeerBean and store it in the peerContainer and in the database (peerNum = 1).
	 * @param action
	 * @param patIllScriptId
	 * @return
	 */
	private PeerBean createNewPeerBean(int action, long parentId, long itemId, float score, int stage){
		PeerBean pb = new PeerBean(action, parentId, 1, itemId);
		pb.setScoreSum(score);
		pb.setStage(stage);
		new DBScoring().saveAndCommit(pb);
		cont.addPeerBean(pb, parentId);
		return pb;
	}
	
	private PeerBean createNewPeerBean(int action, long parentId, long itemId, float score){
		return createNewPeerBean(action, parentId, itemId, score, -1);
	}
	
	private List<PeerBean> getPeerResponsesForParentId(long parentId){
		List<PeerBean> peers = null;
		if(cont!=null) cont.getPeerBeans(parentId);
		if(peers==null || peers.isEmpty()){
			peers = new DBScoring().selectPeerBeansByParentId(parentId);
			cont.addPeerBeans(peers, parentId); //caching...
		}
		return peers;
	}
	
	/*private PeerBean getPeerBeanByListAction(List<PeerBean> peers, int action, int stage){		
		for(int i=0; i<peers.size(); i++){
			if(peers.get(i).getAction()==action && peers.get(i).getStage() == stage) return peers.get(i);
		}
		return null;	}*/
	
	private PeerBean getPeerBeanByActionAndItemId(List<PeerBean> peers, int action, long itemId){
		for(int i=0; i<peers.size(); i++){
			if(peers.get(i).getAction()==action && peers.get(i).getItemId() == itemId) return peers.get(i);
		}
		return null;
	}

	
	private PeerBean getPeerBeanByActionAndStage(List<PeerBean> peers, int action, int stage){
		for(int i=0; i<peers.size(); i++){
			if(peers.get(i).getAction()==action && peers.get(i).getStage() == stage) return peers.get(i);
		}
		return null;
	}
	

	public void loadPeersForPatIllScript(long parentId){
		if(AppBean.getPeers()!=null && AppBean.getPeers().getPeerBeans(parentId)!=null) return;
		List<PeerBean> beans = new DBScoring().selectPeerBeansByParentId(parentId);
		AppBean.getPeers().addPeerBeans(beans, parentId);
	}
	
	/**
	 * We select all learner scripts and recalculate the list scores....
	 */
	/*public synchronized void recalcListScores(){
		
		recalcListScore(ScoreBean.TYPE_PROBLEM_LIST);
		//int overallNum = scripts.size();
	}*/
	
	/*private void recalcListScore(int listType, long parentId){
		List<ScoreBean> scores = new DBScoring().selectScoreBeansByActionTypeAndPatIllScriptId(listType, parentId);
		if(scores==null || scores.isEmpty()) return;
		int overallNum = scores.size(); //we need the information how many learner scripts we have....
		Map<Integer, Float> overallScores = new HashMap<Integer, Float>(); //key = stage, value overScoreNums
		for(int i=0; i<scores.size(); i++){
			ScoreBean sb = scores.get(i);
			if(overallScores.get(new Integer(sb.getStage()))==null){
				overallScores.put(new Integer(sb.getStage()), new Float(sb.getScoreBasedOnExp()));				
			}
			else{
				Float os = overallScores.get(new Integer(sb.getStage()));
				os = os + sb.getScoreBasedOnExp();
			}
		}
		Iterator<Integer> it = overallScores.keySet().iterator();
		while(it.hasNext()){
			int stage = it.next().intValue();
			PeerBean pb = new PeerBean(listType, parentId, overallNum, stage);
			
		}
		
	}*/
}
