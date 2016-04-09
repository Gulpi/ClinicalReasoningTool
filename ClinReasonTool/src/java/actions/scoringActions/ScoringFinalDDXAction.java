package actions.scoringActions;

import java.util.*;

import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.scoring.*;
import controller.ErrorController;
import controller.NavigationController;
import controller.ScoringController;
import database.DBClinReason;
import util.Logger;
import util.StringUtilities;

/**
 * We score the final diagnoses a learner has submitted
 * @author ingahege
 *
 */
public class ScoringFinalDDXAction implements ScoringAction{

	/**
	 * scoring algorithm:
	 * expert: 
	 * @param patIllScriptId
	 */
	public void scoreAction(long listItem, long patIllScriptId){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> mvertices = g.getVerticesByType(Relation.TYPE_DDX);
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByType(ScoreBean.TYPE_FINAL_DDX);
		if(scoreBean==null){ //then this action has not yet been scored: 
			scoreBean = new ScoreBean(patIllScriptId, -1, ScoreBean.TYPE_FINAL_DDX);
			if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
				calculateAddActionScoreBasedOnExpert(mvertices, scoreBean);				
						
			//if(g.getPeerNums()>MIN_PEERS) //we have enough peers, so we can score based on this as well:
			//	calculateAddActionScoreBasedOnPeers(mvertex, scoreBean, g.getPeerNums());
			
			scoreContainer.addScore(scoreBean);
			//TODO calculateOverallScore(scoreBean); 
			new DBClinReason().saveAndCommit(scoreBean);
		}
		
	}
	
	/**
	 * Algorithm based on long Menu answer rating.... We rate synonyma as part scores... 
	 * @param ddxs
	 * @param scoreBean
	 */
	private void calculateAddActionScoreBasedOnExpert(List<MultiVertex>ddxs, ScoreBean scoreBean){
		try{
			int correctNum = 0; 
			int partlyCorrectNum = 0; 
			int numFinalDDXLearner = 0; 
			int numFinalDDXExp = 0;
			List<Relation> expFinals= new ArrayList<Relation>();
			List<Relation> leanerFinals= new ArrayList<Relation>();
			for(int i=0; i<ddxs.size(); i++){
				MultiVertex vert = ddxs.get(i);
				RelationDiagnosis expRel = (RelationDiagnosis)vert.getExpertVertex(); 
				RelationDiagnosis learnerRel = (RelationDiagnosis)vert.getExpertVertex(); 
				
				if(learnerRel.getTier() == RelationDiagnosis.TIER_FINAL &&  expRel.getTier() == RelationDiagnosis.TIER_FINAL){
					//then both have defined the same vertex as final diagnosis, now look for synonyma: 
					if(learnerRel.getSynId()>0 && expRel.getSynId()<=0) //learner has used synonym, expert not
						partlyCorrectNum++; 
					else  correctNum++;
				}
				if(learnerRel.getTier() == RelationDiagnosis.TIER_FINAL){
					leanerFinals.add(learnerRel);
					numFinalDDXLearner++;
				}
				if(expRel.getTier() == RelationDiagnosis.TIER_FINAL){
					expFinals.add(expRel);
					numFinalDDXExp++;
				}
				
			}
			if(correctNum==0 && partlyCorrectNum==0){
				//TODO check here also whether the expert has listed the diagnosis in his list and give at least some credit?
				//or is this already covered with the list score? 
				scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE);
				return;
			}
			//TODO we probably also have to include the weight of the synonyma here: 
			float corrScore = (correctNum *ScoringController.FULL_SCORE + partlyCorrectNum * ScoringController.HALF_SCORE)/ (correctNum+partlyCorrectNum);
			float expScore = (corrScore - (numFinalDDXLearner - numFinalDDXExp))/numFinalDDXExp;
			scoreBean.setScoreBasedOnExp(expScore);
			if(expScore == ScoringController.NO_SCORE) new ErrorController().checkError(scoreBean, leanerFinals,expFinals);
		}
		catch (Exception e){
			Logger.out(StringUtilities.stackTraceToString(e), Logger.LEVEL_PROD);
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
