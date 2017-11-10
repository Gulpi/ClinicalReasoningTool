package actions.scoringActions;

import java.util.*;

import application.AppBean;
import beans.scripts.*;
import beans.user.SessionSetting;
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
	private static final int MAX_DISTANCE_TO_SCORE = 3;
	/**
	 * scoring algorithm:
	 * expert: 
	 * @param patIllScriptId
	 */
	public float scoreAction(long listItem, PatientIllnessScript patIllScript){
		if(patIllScript.isExpScript()) return -1;
		NavigationController nav = new NavigationController();
		//if(nav.getCRTFacesContext().getSessSetting().getListMode()==SessionSetting.LIST_MODE_NONE)
		//	return ScoringAction.NO_SCORING_POSSIBLE; //no list is used, so, we cannot compare the diagnoses with the experts'
		
		Graph g = nav.getCRTFacesContext().getGraph();
		
		List<MultiVertex> mvertices = g.getVerticesByType(Relation.TYPE_DDX);
		ScoreContainer scoreContainer = nav.getCRTFacesContext().getScoreContainer();
		float overallScore = 0;
		
		if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
			overallScore = calculateAddActionScoreBasedOnExpert(mvertices, scoreContainer, patIllScript);				
		
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


			//expert has chosen "No diagnosis" for this VP:
			if(expIllScript.getFinalDDXType()==PatientIllnessScript.FINAL_DDX_NO || patIllScript.getFinalDDXType()==PatientIllnessScript.FINAL_DDX_NO){
				return handleNoDiagnosisScoring(patIllScript, expIllScript, cont);		
			}
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
			//no scoring possible, because no list was used:
			if(new NavigationController().getCRTFacesContext().getSessSetting().getListMode()==SessionSetting.LIST_MODE_NONE)
				corrScore = ScoringAction.NO_SCORING_POSSIBLE;
			
			finalListScore.setScoreBasedOnExp(corrScore, isChg);
			new DBClinReason().saveAndCommit(finalListScore);
			
			if(corrScore < ScoringController.HALF_SCORE && corrScore>=0) 
				patIllScript.addErrors(new ErrorController().checkError(learnerFinals,expFinals));

			return corrScore;
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return -1;
		}
	}
	
	/**
	 * Expert has chosen "no diagnosis", so we check here, whether the learner has chosen the same.
	 * If so, score is 1, else 0.
	 * @param patIllScript
	 * @return
	 */
	private float handleNoDiagnosisScoring(PatientIllnessScript patIllScript, PatientIllnessScript expScript, ScoreContainer cont){
		ScoreBean scoreBean = cont.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_FINAL_DDX, 0);
		boolean isChg = true;
		float score = 0;
		if(scoreBean == null){
			scoreBean = new ScoreBean(patIllScript, 0, ScoreBean.TYPE_FINAL_DDX);
			cont.addScore(scoreBean);
			isChg = false;
		}
		
		//learner AND expert have chosen no diagnosis
		if(patIllScript.getFinalDDXType()==PatientIllnessScript.FINAL_DDX_NO && expScript.getFinalDDXType()==PatientIllnessScript.FINAL_DDX_NO){
			score = 1;
		}
		scoreBean.setScoreBasedOnExp(score, isChg);
		new DBClinReason().saveAndCommit(scoreBean);
		ScoreBean finalListScore = cont.getScoreByType(ScoreBean.TYPE_FINAL_DDX_LIST);
		if(finalListScore==null){
			finalListScore = new ScoreBean(patIllScript, -1, ScoreBean.TYPE_FINAL_DDX_LIST);
			cont.addScore(finalListScore);
			isChg = false;
		}
		finalListScore.setScoreBasedOnExp(score, isChg);
		new DBClinReason().saveAndCommit(finalListScore);
		//in addition we have to create a scoreBean for the AddDDXAction, so that the feedback is displayed correctly:
		
		ScoreBean addScore = cont.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_ADD_DDX, 0);
		if(addScore==null){
			addScore = new ScoreBean(patIllScript, 0, ScoreBean.TYPE_ADD_DDX);
			cont.addScore(addScore);
		}
		addScore.setScoreBasedOnExp(ScoringController.SCORE_EXP_SAMEAS_LEARNER, isChg);
		new DBClinReason().saveAndCommit(addScore);
		return  score;
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
			itemScore = ScoringController.NO_SCORE;
			//learner & expert have selected this ddx as final
			if(expRel!=null && expRel.isFinalDDX())  itemScore = ScoringController.FULL_SCORE;
			
			//learner has wrong final diagnosis, but we might have a hierarchy relation, so it might not be 100% wrong!
			else { 
				itemScore = caclulateScoreForHierarchyRelation(vert, scoreBean);	
			}
			//if there was no list used, we cannot score anything, thus, we just set a 
			if(new NavigationController().getCRTFacesContext().getSessSetting().getListMode()==SessionSetting.LIST_MODE_NONE)
				itemScore = ScoringAction.NO_SCORING_POSSIBLE;
						
			scoreBean.setScoreBasedOnExp(itemScore, isChg);
			new DBClinReason().saveAndCommit(scoreBean);
			return itemScore;
		}
		return 0;
	}
	
	/**
	 * Learner has chosen a final diagnosis that does not match the expert one. BUT we look here, wether it is close
	 * (e.g. final diagnosis of expert is "lobar pneumonia" and final diagnosis of learner is "pneumonia"). For such a 
	 * relation the learner should get some credit, depending on how far the items are apart.
	 * @param learnerRel
	 * @return
	 */
	private float caclulateScoreForHierarchyRelation(MultiVertex learnerVertex, ScoreBean sb){
		float score = 0;
		Graph g = NavigationController.getInstance().getCRTFacesContext().getGraph();
		GraphController gctrl = new GraphController(g);
		
		List<MultiVertex> expFinalDiagnoses = gctrl.getExpertFinalVertices();
		if(expFinalDiagnoses==null || expFinalDiagnoses.isEmpty()) return 0;
		int distance = -99;
		MultiVertex closestExpVertex=null;
		//look for the closest item:
		for(int i=0; i<expFinalDiagnoses.size();i++){
			//distance positive: learner more specific, distance negative: learner less specific:
			int tmpdistance = g.getHierarchyDistance(learnerVertex, expFinalDiagnoses.get(i)); 
			int dikstra = g.getDistance(learnerVertex, expFinalDiagnoses.get(i)); 
			if(tmpdistance!=(int) Double.POSITIVE_INFINITY && (distance==-99 || tmpdistance<distance)){
				distance = tmpdistance;
				closestExpVertex = expFinalDiagnoses.get(i);
			}
		}
		if(distance==-99) return 0; //no relation between final ddxs of expert and learner
		sb.setDistance(distance);
		if(closestExpVertex!=null) sb.setExpItemId(closestExpVertex.getVertexId());
		if(Math.abs(distance)<=MAX_DISTANCE_TO_SCORE)
			score = (float) 1-((float)Math.abs(distance)/10);
		return score;
		
	}
}
