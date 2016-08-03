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
	 * Algorithm based on long Menu answer rating.... We rate synonyma as part scores... 
	 * @param ddxs
	 * @param scoreBean
	 */
	private float calculateAddActionScoreBasedOnExpert(List<MultiVertex>ddxs, ScoreContainer cont, PatientIllnessScript patIllScript){
		try{
			int correctNum = 0; 
			int partlyCorrectNum = 0; 
			int numFinalDDXLearner = 0; 
			int numFinalDDXExp = 0;
			float score = 0;
			
			PatientIllnessScript expIllScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
			//if learner submits the diagnoses too late, we reduce the score:
			
				
			List<Relation> expFinals= new ArrayList<Relation>();
			List<Relation> leanerFinals= new ArrayList<Relation>();
			for(int i=0; i<ddxs.size(); i++){
				MultiVertex vert = ddxs.get(i);
				RelationDiagnosis expRel = (RelationDiagnosis)vert.getExpertVertex(); 
				RelationDiagnosis learnerRel = (RelationDiagnosis)vert.getLearnerVertex(); 
				long itemId = vert.getVertexId();
				if(learnerRel!=null) itemId = learnerRel.getListItemId();
				ScoreBean scoreBean = cont.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_FINAL_DDX, vert.getVertexId());
				if(scoreBean == null) scoreBean = new ScoreBean(patIllScript, itemId, ScoreBean.TYPE_FINAL_DDX);

				scoreBean.setTiming(patIllScript.getSubmittedStage(), expIllScript.getSubmittedStage());
				if(learnerRel!=null && learnerRel.getTier() == RelationDiagnosis.TIER_FINAL &&  expRel.getTier() == RelationDiagnosis.TIER_FINAL){
					//then both have defined the same vertex as final diagnosis, now look for synonyma: 
					if(learnerRel.getSynId()>0 && expRel.getSynId()<=0) //learner has used synonym, expert not
						partlyCorrectNum++; 
					else  correctNum++;
				}
				if(learnerRel!=null && learnerRel.getTier() == RelationDiagnosis.TIER_FINAL){
					leanerFinals.add(learnerRel);
					numFinalDDXLearner++;
				}
				if(expRel!=null && expRel.getTier() == RelationDiagnosis.TIER_FINAL){
					expFinals.add(expRel);
					numFinalDDXExp++;
				}
				cont.addScore(scoreBean);
							
				float expScore = 0;
				if(correctNum==0 && partlyCorrectNum==0){
					//TODO check here also whether the expert has listed the diagnosis in his list and give at least some credit?
					//or is this already covered with the list score? 
					expScore = ScoringController.NO_SCORE;
				}
				else {
					//TODO we probably also have to include the weight of the synonyma here: 
					float corrScore = (correctNum *ScoringController.FULL_SCORE + partlyCorrectNum * ScoringController.HALF_SCORE)/ (correctNum+partlyCorrectNum);
					expScore = (corrScore - (numFinalDDXLearner - numFinalDDXExp))/numFinalDDXExp;				
				}
				scoreBean.setScoreBasedOnExp(expScore, false);
				score += expScore;
				new DBClinReason().saveAndCommit(scoreBean);
			}
			
			if(score < ScoringController.FULL_SCORE) 
				patIllScript.addErrors(new ErrorController().checkError(leanerFinals,expFinals));

			return score/ddxs.size();
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
			return -1;
		}
		
	}
	

	/*private void getLearnerAndExpFinalDDXs(List<MultiVertex>ddxs, List<MultiVertex> learnerFinalDDX, List<MultiVertex> expFinalDDX){
		if(ddxs==null) return;
		for(int i=0; i<ddxs.size(); i++){
			MultiVertex vert = ddxs.get(i);
			if(((RelationDiagnosis)vert.getExpertVertex()).getTier() ==RelationDiagnosis.TIER_FINAL)
				expFinalDDX.add(vert);
			if(((RelationDiagnosis)vert.getLearnerVertex()).getTier() ==RelationDiagnosis.TIER_FINAL)
				learnerFinalDDX.add(vert);			
		}
	}*/
}
