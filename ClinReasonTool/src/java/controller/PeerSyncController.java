package controller;

import java.util.*;

import actions.scoringActions.ScoringOverallAction;
import application.AppBean;
import beans.scripts.*;
import beans.relation.Relation;
import beans.scoring.LearningAnalyticsBean;
import beans.scoring.PeerBean;
import beans.scoring.PeerContainer;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
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
		PeerContainer peerCont = AppBean.getPeers();
		//TODO only select scripts that are at the same stage as the expertscript?
		scripts = new DBClinReason().selectLearnerPatIllScriptsByPeerSync();
		if(scripts==null || scripts.isEmpty()){
			CRTLogger.out("PeerSyncController.sync - nothing to sync", CRTLogger.LEVEL_PROD);
		}
		for(int i=0; i<scripts.size(); i++){
			PatientIllnessScript script = scripts.get(i);
			LearningAnalyticsBean lab = new LearningAnalyticsBean(script.getId(), script.getUserId(), script.getVpId());		
			syncItems(script.getProblems()/*, peers*/, script.getVpId());
			syncItems(script.getDiagnoses()/*, peers*/, script.getVpId());
			syncItems(script.getMngs()/*, peers*/, script.getVpId());
			syncItems(script.getTests()/*, peers*/, script.getVpId());
			if(lab!=null && lab.getScoreContainer()!=null){
				List<ScoreBean> scores = lab.getScoreContainer().getScores();
				syncSummSt(scores,script.getVpId());
				syncOverallScore(lab, script.getVpId());
				syncListActionScores(scores/*, peers*/, script.getVpId());
			}
			
			script.setPeerSync(true);
			CRTLogger.out("Peer sync: " + script.getId() + "done", CRTLogger.LEVEL_PROD);
		}
		new DBClinReason().saveAndCommit(scripts); //save the changed sync status....
		CRTLogger.out("Peer sync done", CRTLogger.LEVEL_PROD);
	}
	
	/**
	 * We add up the scores of the list actions scores and nums in the PeerBeans...
	 * TODO: mark the bean on the last stage -> quicker access!
	 * @param scores
	 * @param peers
	 * @param parentId
	 */
	private void syncListActionScores(List<ScoreBean> scores, /*List<PeerBean> peers,*/ String vpId){
		if(scores==null) return;
		PeerContainer peerCont = AppBean.getPeers();
		if(peerCont==null) return;
		for(int i=0; i<scores.size(); i++){
			ScoreBean score = scores.get(i);
			if(score.isListScoreBean()){ //we only consider list scoring here!
				PeerBean peerBean = peerCont.getPeerBeanByVpIdActionAndStage(score.getType(), score.getStage(), vpId);
				if(peerBean==null) peerBean = createNewPeerBean(score.getType(), vpId, -1, score.getScoreBasedOnIllScript(), score.getStage());
				else{
					peerBean.incrPeerNum();
					peerBean.incrScoreSum(score.getOrgScoreBasedOnExp());
					new DBScoring().saveAndCommit(peerBean);
				}
			}
		}
	}
	

	/**
	 * add the summary Statement score to the peerBeans.
	 * @param sumScore
	 * @param parentId
	 */
	private void syncSummSt(List<ScoreBean> scores, String vpId){
		PeerContainer peerCont = AppBean.getPeers();
		if(peerCont==null || scores==null) return;
		ScoreBean sumScore = null;
		//get the score for the summary Statement:
		for(int i=0; i<scores.size(); i++){
			sumScore = scores.get(i);
			if(sumScore.getType()==ScoreBean.TYPE_SUMMST) break;
		}
		if(sumScore==null) return; //can happen?
		List<PeerBean> pbs = peerCont.getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_SUMMST);
		PeerBean peerBean = null;
		if(pbs==null || pbs.isEmpty()){
			peerBean = new PeerBean(ScoreBean.TYPE_SUMMST, vpId, 0, -1);			
		}
		else peerBean = pbs.get(0);
		peerBean.incrPeerNum();
		peerBean.incrScoreSum(sumScore.getScoreBasedOnExp());
	}
	
	private void syncOverallScore(LearningAnalyticsBean lab, String vpId){
		PeerContainer peerCont = AppBean.getPeers();
		if(peerCont==null || lab==null) return;
		ScoreBean overallScore = lab.getOverallScore();

		if(overallScore==null){ //then calculate it here:
			overallScore = new ScoringOverallAction().scoreAction(lab);
		}
		if(overallScore==null) return;
		List<PeerBean> pbs = peerCont.getPeerBeansByActionAndVpId(vpId, ScoreBean.TYPE_OVERALL_SCORE);
		PeerBean peerBean = null;
		if(pbs==null || pbs.isEmpty()){
			peerBean = new PeerBean(ScoreBean.TYPE_OVERALL_SCORE, vpId, 0, -1);			
		}
		else peerBean = pbs.get(0);
		peerBean.incrPeerNum();
		peerBean.incrScoreSum(overallScore.getScoreBasedOnExp());	
	}
		
	/**
	 * We go thru the list of items and increase the peerNum for each item
	 * @param rels
	 * @param peers
	 * @param listAction
	 */
	private void syncItems(List rels, /*List<PeerBean> peers,*/ String vpId){
		if(rels==null || rels.isEmpty()) return;
		PeerContainer peerCont = AppBean.getPeers();
		if(peerCont==null) return;
		for(int i=0; i<rels.size(); i++){
			Relation rel = (Relation) rels.get(i);
			PeerBean peerBean = peerCont.getPeerBeanByActionVpIdAndItemId(rel.getRelationType(), vpId, rel.getListItemId());
			if(peerBean==null){
				peerBean = createNewPeerBean(rel.getRelationType(), vpId,  rel.getListItemId(), 0);				
			}
			else{
				peerBean.incrPeerNum();
				new DBScoring().saveAndCommit(peerBean);
			}
		}
	}
	
	/**
	 * This action for this script has not yet have been performed by any peer, so we create 
	 * a new PeerBean and store it in the peerContainer and in the database (peerNum = 1).
	 * @param action
	 * @param patIllScriptId
	 * @return
	 */
	protected PeerBean createNewPeerBean(int action, String vpId, long itemId, float score, int stage){
		PeerBean pb = new PeerBean(action, vpId, 1, itemId);
		pb.setScoreSum(score);
		pb.setStage(stage);
		new DBScoring().saveAndCommit(pb);
		cont.addPeerBean(pb, vpId);
		return pb;
	}
	
	
	private PeerBean createNewPeerBean(int action, String vpId, long itemId, float score){
		return createNewPeerBean(action, vpId, itemId, score, -1);
	}
}