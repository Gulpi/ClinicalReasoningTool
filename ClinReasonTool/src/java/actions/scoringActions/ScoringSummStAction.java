package actions.scoringActions;

import java.util.*;

import application.AppBean;
import beans.scripts.*;
import beans.relation.SummaryStatement;
import beans.scoring.*;
import controller.*;
import database.DBClinReason;
import database.DBScoring;
import util.CRTLogger;
import util.StringUtilities;

/**
 * We have multiple summary statement scores (one for each stage)...
 * @author ingahege
 *
 */
public class ScoringSummStAction {
	/**
	 * if the summary statement is shorter, we do not even consider to score it
	 */
	private static final int MIN_LENGTH_SUMST = 10;
	public static final int RUBRIC_SQ = 1;
	public static final int RUBRIC_ACCURACY = 2;
	public static final int RUBRIC_NARROWDDX = 3;
	public static final int RUBRIC_INFOTRAMSFORM = 4;
	public static final int RUBRIC_GLOBALRATE = 5;
	public static final int RUBRIC_PATNAME = 6;
	
	/**
	 * scoring triggered from chart page, if no summary statement has been created so far, it will 
	 * be created now (obviously with score=0, because otherwise it would have been scored before.)
	 * @param labean
	 * @return
	 */
	public ScoreBean scoreAction(LearningAnalyticsBean labean){
		PatientIllnessScript patillscript = NavigationController.getInstance().getCRTFacesContext().getPatillscript();
		if(patillscript!=null && patillscript.getId()==labean.getPatIllScriptId())
			return scoreAction(patillscript, patillscript.getCurrentStage());
		return null;
	}
	/**
	 * call when learner saves the summary statement? 
	 * call when learner enters the next stage.
	 * @param patIllScript
	 */
	public ScoreBean scoreAction(PatientIllnessScript patIllScript, int stage){
		SummaryStatementController.checkForSemanticQualifiers(patIllScript.getSummSt());

		if(patIllScript.isExpScript()) return null;
		PatientIllnessScript expScript = AppBean.getExpertPatIllScript(patIllScript.getVpId());
		ScoreContainer scoreContainer = NavigationController.getInstance().getCRTFacesContext().getScoreContainer();		
		ScoreBean scoreBean = scoreContainer.getListScoreBeanByStage(ScoreBean.TYPE_SUMMST, stage);
		boolean isChg = true;
		//if(scoreBean!=null) return;
		if(scoreBean==null){
			scoreBean = new ScoreBean(patIllScript, patIllScript.getSummStId(), ScoreBean.TYPE_SUMMST, stage);
			if(patIllScript.getSummSt()!=null) scoreBean.setDetails(patIllScript.getSummSt().getText());
			isChg = false;
		}
		//we analyze the text concerning semantic qualifiers:
		if(expScript!=null && expScript.getSummSt()!=null){ //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript.getSummSt(), patIllScript.getSummSt(), isChg);				
		}
		//if(g.getPeerNums()>ScoringController.MIN_PEERS) //we have enough peers, so we can score based on this as well:
		//calculateAddActionScoreBasedOnPeers(edge, scoreBean, g.getPeerNums());
		
		scoreContainer.addScore(scoreBean);
		new DBScoring().saveAndCommit(scoreBean);	
		return scoreBean;
	}
	
	/**
	 * Either correct (1) or wrong (0)
	 * @param scoreBean
	 * @param courseOfTimeExp
	 * @param courseOfTimeLearner
	 */
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, SummaryStatement expSt, SummaryStatement learnerSt, boolean isChg){

		if(learnerSt==null || learnerSt.getText()==null || learnerSt.getText().trim().equals(""))
			scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
		else{
			
			//not enough useful text is there:
			if(learnerSt.getText().length()<=MIN_LENGTH_SUMST){
				scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
			}
			
			//we have some text to analyze, but currently we only have the check for semantic qualifiers...
			else{
				int sqScore = calculateSemanticQualScore(expSt, learnerSt); //0, 1, or 2
				//int transfScore = calculateTransformation(expSt, learnerSt, null); //0, 1, or 2
				if(sqScore==2) scoreBean.setScoreBasedOnExp(ScoringController.FULL_SCORE, isChg);
				//for the moment we give half score if we find a few or no sematic qualifiers:
				else scoreBean.setScoreBasedOnExp(ScoringController.HALF_SCORE, isChg);
			}
			
		}		
		if(expSt!=null && learnerSt!=null)
			scoreBean.setTiming(learnerSt.getStage(), expSt.getStage());

		ScoringController.getInstance().setFeedbackInfo(scoreBean, isChg, false);

	}
	
	/**
	 * score = 0, if no semantic qualifiers used or less than 30% of the expert
	 * score = 1, if more than 30% and less than 60% of the expert
	 * score = 2, if more than 60% of the expert
	 * @param expSt
	 * @param learnerSt
	 * @return
	 */
	private int calculateSemanticQualScore(SummaryStatement expSt, SummaryStatement learnerSt){
		if(learnerSt.getSqHits()==null || learnerSt.getSqHits().isEmpty()) return 0;
		if(expSt.getSqHits()==null || expSt.getSqHits().isEmpty()) return 2; //should not happen, would be a bad statement
		if(learnerSt.getSqHits().size() / expSt.getSqHits().size() >=0.6 ) return 2;
		if(learnerSt.getSqHits().size() / expSt.getSqHits().size() >=0.3 ) return 1;
		return 0;		
	}
	
	/**
	 * Strictly counting numbers of semantic qualifiers
	 * @param learnerSt
	 * @return
	 */
	public int calculateSemanticQualScoreNew(SummaryStatement learnerSt){
		if(learnerSt.getSqHits()==null || learnerSt.getSqHits().isEmpty() || learnerSt.getSqHits().size()<=2) return 0;
		if(learnerSt.getSqHits().size() >4) return 2;
		return 1;
	}
	
	/**
	 * 
	 * @param learnerSt
	 */
	public void calculateNarrowing(SummaryStatement st, SummaryStatement expSt){
		try{
			if(st==null || st.getItemHits()==null || st.getItemHits().isEmpty()){
				st.setNarrowingScore(0);
				return;
			}
			//we have no findings at all, which speaks for no narrowing
			if(st.getFindingHits()==null || st.getFindingHits().isEmpty()){
				st.setNarrowingScore(0);
				return;
			}
			int narrowingMatches = st.getFindingHitsNum() + st.getDiagnosesHitsNum() + st.getAnatomyHitsNum();
			
			//number of matches (findings, diagnoses, anatomy) between statement and expert map or statement
			int expMatchNarr = st.getExpMatchNarrowing(); 
			
			//found findings, ddx and anatomy terms in expert statement: 
			int expStNum = expSt.getFindingHitsNum() + expSt.getDiagnosesHitsNum() + expSt.getAnatomyHitsNum();
			
			//version 0.2:
			float diffStMatches = (float) (expStNum - st.getExpMatchesNum()); 
			
			float percDiffStMatches = (float)diffStMatches/(float)expStNum;
			if(diffStMatches<=0) st.setNarrowingScore(2);
			else if (percDiffStMatches>=0.66) st.setNarrowingScore(0);
			else if (percDiffStMatches<=0.33) st.setNarrowingScore(2);
			else st.setNarrowingScore(1);
			
			//version 0.3:
			//additional findings/diagnoses/anatomy in the learner statement, which are not present in the expert map or statement
			int addItemsNum = st.getFindingHitsNum() + st.getDiagnosesHitsNum() + st.getAnatomyHitsNum() - st.getExpMatchNarrowing(); 
			float percDiffStAdd = (float)addItemsNum/(float)expStNum;
			if (percDiffStMatches >= 0.66 && percDiffStAdd < 0.33) st.setNarrowingScoreNew(2);
			else if (percDiffStMatches >= 0.66 && percDiffStAdd >= 0.33) st.setNarrowingScoreNew(1);			
			else if (percDiffStMatches < 0.66 && percDiffStMatches >= 0.33 && percDiffStAdd < 0.66) st.setNarrowingScoreNew(1);
			else if (percDiffStMatches < 0.66 && percDiffStMatches >= 0.33 && percDiffStAdd >= 0.66) st.setNarrowingScoreNew(0);
			else if (percDiffStMatches < 0.33) st.setNarrowingScoreNew(0);
			// version 0.1: float int issue resolved 20191216
			/*float tmpScore = (float) expMatchNarr / (float) narrowingMatches;
			
			if(tmpScore < 0.3) st.setNarrowingScore(0);
			else if (tmpScore > 0.6) st.setNarrowingScore(2);
			else st.setNarrowingScore(1);*/
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
		}
	}
	
	/**
	 * Score for transformation (e.g. Pulse 180 -> Tachycardia); 0=none, 1=some, 2=frequent/appropriate
	 * @param expSt
	 * @param learnerSt
	 * @return
	 */
	public int calculateTransformation(SummaryStatement expSt, SummaryStatement learnerSt){
		if(learnerSt==null || learnerSt.getText()==null || learnerSt.getText().trim().equals("")) return 0;
		
		return 0;
		
	}
	

}
