package controller;

import java.util.*;

import beans.graph.Graph;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;

/**
 * Calculates the scores of actions and items based on the Graph
 * we can uses this for helper actions???
 * @author ingahege
 *
 */
public class ScoringController {
	
	public static final float FULL_SCORE = (float) 1;
	public static final float HALF_SCORE = (float) 0.5; //e.g. a synonyma entered....
	public static final float NO_SCORE = (float) 0; //might be 0.25 if we want to give the learner credit for doing something...
	public static final float RED_SCORE_LATESTAGE = (float) 0;
	
	public static final String ICON_PREFIX = "icon-ok";
	//define possible scoring algorithms:
	public static final int SCORING_ALGORITHM_BASIC = 1;
	public ScoringController(){}
	
	public String getIconForScore(int type, long itemId){
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		if (scoreContainer==null) return ""; //no icon
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(type,itemId);
		if(scoreBean==null || scoreBean.getOverallScore()<=0) return "";
		if(scoreBean.getOverallScore()==1) return ICON_PREFIX+1;
		if(scoreBean.getOverallScore()<1) return ICON_PREFIX+2;
		return "";
	}
}
