package actions.scoringActions;

import java.util.*;

import application.AppBean;
import beans.scripts.*;
import beans.relation.summary.*;
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
		//SummaryStatementController.checkForSemanticQualifiers(patIllScript.getSummSt(), null);

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
			calculateAddActionScoreBasedOnExpert(scoreBean, expScript, patIllScript, isChg);				
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
	private void calculateAddActionScoreBasedOnExpert(ScoreBean scoreBean, PatientIllnessScript expScript, PatientIllnessScript learnerScript, boolean isChg){

		if(learnerScript==null || learnerScript.getSummSt()==null || learnerScript.getSummSt().getText()==null || learnerScript.getSummSt().getText().trim().equals(""))
		{
			scoreBean.setScoreBasedOnExp(ScoringController.NO_SCORE, isChg);
			return;
		}
		//else{
			/*
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
			
		}		*/
		new SummaryStatementController().initSummStRating(expScript, learnerScript, this);
		doScoring(learnerScript.getSummSt(), expScript.getSummSt());
		if(expScript.getSummSt()!=null && learnerScript.getSummSt()!=null)
			scoreBean.setTiming(learnerScript.getSummSt().getStage(), expScript.getSummSt().getStage());

		ScoringController.getInstance().setFeedbackInfo(scoreBean, isChg, false);

	}

	public void doScoring(SummaryStatement learnerSt, SummaryStatement expSt){
		//if(learnerSt==null || expSt==null) return;
		//scoring starts:
		int sqScoreNew = calculateSemanticQualScoreBasic(learnerSt); //score SQ
		int sqScore = calculateSemanticQualScore(expSt, learnerSt);
		learnerSt.setSqScore(sqScore);
		learnerSt.setSqScoreBasic(sqScoreNew);
		calculateNarrowing(learnerSt, expSt);
		calculateTransformation(expSt, learnerSt);
		calculatePersonScore(learnerSt);
		calculateGlobal(learnerSt);
		new DBClinReason().saveAndCommit(learnerSt);
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
		int hits = learnerSt.getSQSpacyHits();
		int expHits = expSt.getSQSpacyHits();
		if(expHits==0) expHits = 2;
		
		float perc = (float) hits / (float)expHits;
		learnerSt.setSqScorePerc(perc);
		if(hits==2) return 1;
		if(perc >=0.7 ) return 2;
		if(perc >=0.3 ) return 1;
		return 0;
		/*if(learnerSt.getSqHits()==null || learnerSt.getSqHits().isEmpty()) return 0;
		if(expSt.getSqHits()==null || expSt.getSqHits().isEmpty()) return 2; //should not happen, would be a bad statement
		if(learnerSt.getSQSpacyHits() / expSt.getSQSpacyHits() >=0.6 ) return 2;
		if(learnerSt.getSQSpacyHits() / expSt.getSQSpacyHits() >=0.3 ) return 1;
		return 0;		*/
	}
	
	/**
	 * Strictly counting numbers of semantic qualifiers
	 * used for pilot study
	 * @param learnerSt
	 * @return
	 */
	private int calculateSemanticQualScoreBasic(SummaryStatement learnerSt){
		int hits = learnerSt.getSQSpacyHits();
		if(hits<2) return 0;
		if(hits>4) return 2;
		return 1;
		/*if(learnerSt.getSqHits()==null || learnerSt.getSqHits().isEmpty() || learnerSt.getSqHits().size()<=2) return 0;
		if(learnerSt.getSqHits().size() >4) return 2;*/
		//return 1;
	}
	
	private void calculatePersonScore(SummaryStatement learnerSt){
		if(learnerSt.getPerson()!=null) learnerSt.setPersonScore(1);
		else learnerSt.setPersonScore(0);
	}
	
	/**
	 * 
	 * @param learnerSt
	 */
	private void calculateNarrowing(SummaryStatement st, SummaryStatement expSt){
		float upperBorder = (float) 0.75;//0.66
		float lowerBoarder = (float) 0.25; //0.34
		try{
			if(st==null || st.getItemHits()==null || st.getItemHits().isEmpty()){
				st.setNarrowingScore(0);
				return;
			}
			//CRTLogger.out("", 1);
			int fdgsHits = st.getFindingHitsNum();
			//we have no findings at all, which speaks for no narrowing
			/*if(fdgsHits==0){
				st.setNarrowingScore(0);
				return;
			}*/
			//int narrowingMatches = st.getFindingHitsNum() + st.getDiagnosesHitsNum() + st.getAnatomyHitsNum();
			
			//number of matches (findings, diagnoses, anatomy) between statement and expert map or statement
			//int expMatchNarr = st.getExpMatchNarrowing(); 
			
			//found findings, ddx and anatomy terms in expert statement: 
			int expStNum = expSt.getFindingHitsNum() + expSt.getDiagnosesHitsNum() + expSt.getAnatomyHitsNum();
			int learnerStNum = st.getFindingHitsNum() + st.getDiagnosesHitsNum() + st.getAnatomyHitsNum();
			//version 0.2:
			float diffStMatches = (float) (expStNum - st.getExpMatchesNum()); 
			float percDiffStMatches = (float) 0.0;
			if(expStNum!=0)
				percDiffStMatches = (float)diffStMatches/(float)expStNum;
			if(diffStMatches<=0) st.setNarrowingScore(2);
			else if (percDiffStMatches>upperBorder) st.setNarrowingScore(0);
			else if (percDiffStMatches<lowerBoarder) st.setNarrowingScore(2);
			else st.setNarrowingScore(1);
			st.setNarr1Score(percDiffStMatches);
			
			//version 0.3:
			//additional findings/diagnoses/anatomy in the learner statement, which are not present in the expert map or statement
			int addItemsNum = fdgsHits + st.getDiagnosesHitsNum() + st.getAnatomyHitsNum() - st.getExpMatchNarrowing(); 
			float percDiffStAdd = (float) 0.0; 
			if(expStNum!=0) percDiffStAdd = (float)addItemsNum/(float)expStNum;
			if (percDiffStMatches <= lowerBoarder && percDiffStAdd < lowerBoarder) st.setNarrowingScoreNew(2);
			else if (percDiffStMatches <= lowerBoarder && percDiffStAdd >= lowerBoarder) st.setNarrowingScoreNew(1);			
			else if (percDiffStMatches < upperBorder && percDiffStMatches >= lowerBoarder && percDiffStAdd < upperBorder) st.setNarrowingScoreNew(1);
			else if (percDiffStMatches < upperBorder && percDiffStMatches >= lowerBoarder && percDiffStAdd >= upperBorder) st.setNarrowingScoreNew(0);
			else if (percDiffStMatches >= upperBorder) st.setNarrowingScoreNew(0);
			st.setNarr2Score(percDiffStAdd);

			// 20200515 MA:
			// narr1Score and narr2Score can be NaN, should be checked!!!!
			if (Float.isNaN(st.getNarr1Score())) {
				st.setNarr1Score(0.0f);
			}
			if (Float.isNaN(st.getNarr2Score())) {
				st.setNarr2Score(0.0f);
			}
		}
		catch (Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
		}
	}
	
	/**
	 * Score for transformation (e.g. Pulse 180 -> Tachycardia); 0=none, 1=some, 2=frequent/appropriate
	 * we look for non-transformed hits (with SIUnits) and transformed matches with list of transforming rules.
	 * @param expSt
	 * @param learnerSt
	 * @return
	 */
	private void calculateTransformation(SummaryStatement expSt, SummaryStatement learnerSt){
		if(learnerSt==null || learnerSt.getText()==null || learnerSt.getText().trim().equals("")){
			learnerSt.setTransformationScore(0);
			return;
		}
		int siUnitsNum = learnerSt.getUnitsNumForTransformation(expSt.getUnits());
		//too many non-transformed terms or no hits at all:
		if(siUnitsNum>5 || learnerSt.getItemHits()==null) {
			learnerSt.setTransformationScore(0); 
			learnerSt.setTransformScorePerc((float)0.0);
			return;
		}
		int transformNum = calculateTransformedItems(learnerSt);
		int transformNumExp = calculateTransformedItems(expSt);
		if(transformNumExp==0) transformNumExp = 2; //avoid division by 0 and have a default value of 2!
		learnerSt.setTransformNum(transformNum);
		//score calculation:
		if(transformNum<=0){  //no transformed items:
			learnerSt.setTransformationScore(0);
			learnerSt.setTransformScorePerc((float)0.0);
			//if(transformNum>3) learnerSt.setTransformationScore(2);
			//else learnerSt.setTransformationScore(1);
			return;
		}
		
		int textlength = learnerSt.getText().length();
		int textLenCat = 0;
		if(textlength>500) textLenCat = 2; 
		else if(textlength>200) textLenCat = 1;
		float tmp1 = ((float) transformNum - (float) siUnitsNum);
		float tmp2 = (float) (transformNumExp + textLenCat);
		learnerSt.tempExpTransfNum = transformNumExp;
		learnerSt.setTransformScorePerc( tmp1 / tmp2 );
		if(learnerSt.getTransformScorePerc()>0.7)  learnerSt.setTransformationScore(2);
		else if(learnerSt.getTransformScorePerc()<0.16) learnerSt.setTransformationScore(0);
		else learnerSt.setTransformationScore(1);
	}
	
	/**
	 * We look how many elements in the statement are transformed by comparing it with the rules (e.g. prefix=tachy)
	 * and comparing it with transformed findings (e.g. fever)
	 * @param st
	 * @return
	 */
	private int calculateTransformedItems(SummaryStatement st){
		int transformNum = 0;
		
		List<TransformRule> myTransformRules = SummaryStatementController.getOrFetchTransformRules();
		//identify matches with the transformation rules:
		if(myTransformRules!=null){
			for(int i=0; i<myTransformRules.size(); i++){
				TransformRule tr = myTransformRules.get(i);
				Iterator<SummaryStElem> it = st.getItemHits().iterator();
				while(it.hasNext()){
				//for(int j=0; j<st.getItemHits().size(); j++){
					SummaryStElem s = it.next(); //st.getItemHits().get(j);
					if(tr.getType()==TransformRule.TYPE_PREFIX && s.getListItem()!=null && s.getListItem().getName().toLowerCase().startsWith(tr.getName())){
						transformNum++;
						s.setTransform(TransformRule.TYPE_PREFIX);
					}
					
					else if(tr.getType()==TransformRule.TYPE_SUFFIX && s.getListItem()!=null && s.getListItem().getName().toLowerCase().endsWith(tr.getName())){
						s.setTransform(TransformRule.TYPE_SUFFIX);
						transformNum++;
					}
				}
			}
		}
		
		//identify matches with other findings (e.g. Fever);
		if(st.getItemHits()!=null){
			Iterator<SummaryStElem> it2 = st.getItemHits().iterator();
			while(it2.hasNext()){
			//for(int i=0; i< st.getItemHits().size(); i++){
				if(it2.next().getTransform()==TransformRule.TYPE_FINDING)
					transformNum++;
			}
		}
		return transformNum;
	}
	
	/**
	 * we sum up all category scores, if sum>=6 global score is 2, if the sum >=2 score is 1 and otherwise 0.
	 * @param st
	 */
	private void calculateGlobal(SummaryStatement st){
		if(st.getText()==null || st.getText().length()<=25) {
			st.setGlobalScore(0);
			return;
		}
		int sum = st.getTransformationScore() + st.getNarrowingScore() + st.getSqScore() + st.getPersonScore() + st.getAccuracyScore();
		if(sum<=2) st.setGlobalScore(0);
		else if(sum>=6) st.setGlobalScore(2); 
		else st.setGlobalScore(1);
	}
}
