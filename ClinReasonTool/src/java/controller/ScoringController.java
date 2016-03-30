package controller;

import java.util.*;

import beans.graph.Graph;

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
	//define possible scoring algorithms:
	public static final int SCORING_ALGORITHM_BASIC = 1;
	private Graph graph;
	public ScoringController(Graph g){
		graph = g;
	}
	
	
}
