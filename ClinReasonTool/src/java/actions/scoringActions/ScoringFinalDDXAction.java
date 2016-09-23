package actions.scoringActions;

import java.util.*;

import application.AppBean;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.*;
import beans.scoring.*;
import controller.*;
import database.DBClinReason;
import util.*;

/**
 * We score the final diagnoses a learner has submitted
 * @author ingahege
 *
 */
public class ScoringFinalDDXAction /*implements ScoringAction*/{

	/**
	 * scoring algorithm:
	 * expert: 
	 * @param patIllScriptId
	 */
	public float scoreAction(long listItem, PatientIllnessScript patIllScript){
		if(patIllScript.isExpScript()) return -1;
		NavigationController nav = new NavigationController();
		Graph g = nav.getCRTFacesContext().getGraph();
		
		List<MultiVertex> mvertices = g.getVerticesByType(Relation.TYPE_DDX);
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		float overallScore = 0;
		
		//List<ScoreBean> scoreBeans = scoreContainer.getScoreBeansByType(ScoreBean.TYPE_FINAL_DDX);
		//if(scoreBeans==null){ //then this action has not yet been scored: 
		//	scoreBean = new ScoreBean(patIllScript.getId(), -1, ScoreBean.TYPE_FINAL_DDX);
			if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
				overallScore = calculateAddActionScoreBasedOnExpert(mvertices, scoreContainer, patIllScript);				
						
			//if(g.getPeeums()>MIN_PEERS) //we have enough peers, so we can score based on this as well:
			//	calculateAddActionScoreBasedOnPeers(mvertex, scoreBean, g.getPeerNums());
			
			//scoreContainer.addScore(scoreBean);
			//TODO calculateOverallScore(scoreBean); 
			//new DBClinReason().saveAndCommit(scoreBean);			
		//}
		return overallScore;
	}
	
	/**
	 * Calculating a score for each final diagnosis of the learner and calculating an overall score for 
	 * all final diagnoses compared to the expert. 
	 * @param ddxs
	 * @param scoreBean
	 */
	private float calculateAddActionScoreBasedOnExpert(List<MultiVertex>ddxs, ScoreContainer cont, PatientIllnessScript patIllScript){
		try{
			
			PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
							
			List<RelationDiagnosis> expFinals = expIllScript.getFinalDiagnoses();//new ArrayList<Relation>();
			List<RelationDiagnosis> learnerFinals = patIllScript.getFinalDiagnoses();//new ArrayList<Relation>();
			float sumScore = 0;
			boolean isChg = true;
			//calculate individual score for each of the learner's final diagnosis:s
			for(int i=0; i<ddxs.size(); i++){
				MultiVertex vert = ddxs.get(i);
				sumScore +=scoreFinalDDXItem(vert, cont, patIllScript, expIllScript);
			}
			//now score the final ddx list score
			ScoreBean finalListScore = cont.getScoreByType(ScoreBean.TYPE_FINAL_DDX_LIST);
			if(finalListScore==null){
				finalListScore = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_FINAL_DDX_LIST);
				cont.addScore(finalListScore);
				isChg = false;
			}
				
			float corrScore;
			//all item scores have been scored and expert has no additional items, the scalb method punishes a gunshot approach ;-) 
			if(learnerFinals.size()>=expFinals.size()){
				corrScore = (sumScore/learnerFinals.size()) / (Math.scalb(1, (expFinals.size()-learnerFinals.size())));				
			}
			else{	//expert has more finals than learner (then these have not yet been considered):
				corrScore = (sumScore/expFinals.size());
			}
			finalListScore.setScoreBasedOnExp(corrScore, isChg);
			new DBClinReason().saveAndCommit(finalListScore);
			
			if(corrScore < ScoringController.FULL_SCORE) 
				patIllScript.addErrors(new ErrorController().checkError(learnerFinals,expFinals));

			return corrScore;
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
			return -1;
		}
		
	}
	
	/**
	 * Calculate score (in comparison with expert) for a learner's final diagnosis.
	 * @param vert
	 * @param cont
	 * @param patIllScript
	 * @param expIllScript
	 * @return
	 */
	private float scoreFinalDDXItem(MultiVertex vert, ScoreContainer cont, PatientIllnessScript patIllScript, PatientIllnessScript expIllScript){
		RelationDiagnosis expRel = (RelationDiagnosis)vert.getExpertVertex(); 
		RelationDiagnosis learnerRel = (RelationDiagnosis)vert.getLearnerVertex(); 
		float itemScore = 0;
		boolean isChg = true;
		//score the single learner final ddx
		if(learnerRel!=null && learnerRel.isFinalDDX()){
			long itemId = learnerRel.getListItemId();
			ScoreBean scoreBean = cont.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_FINAL_DDX, vert.getVertexId());
			if(scoreBean == null){
				scoreBean = new ScoreBean(patIllScript, itemId, ScoreBean.TYPE_FINAL_DDX);
				cont.addScore(scoreBean);
				isChg = false;
			}

			scoreBean.setTiming(patIllScript.getSubmittedStage(), expIllScript.getSubmittedStage());
			//learner & expert have selected this ddx as final
			if(expRel!=null && expRel.isFinalDDX())  itemScore = ScoringController.FULL_SCORE;
			
			//learner has wrong final diagnosis:
			else itemScore = ScoringController.NO_SCORE;
			
			scoreBean.setScoreBasedOnExp(itemScore, isChg);
			new DBClinReason().saveAndCommit(scoreBean);
			return itemScore;
		}
		return 0;
	}
}
