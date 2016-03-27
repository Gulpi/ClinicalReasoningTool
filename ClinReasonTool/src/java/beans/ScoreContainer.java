package beans;

import java.beans.Beans;

import java.util.*;

/**
 * Contains all scores for the PatientIllnessScript
 * @author ingahege
 *
 */
public class ScoreContainer {
	//private long sessionId;
	private long patIllScriptId; //maybe not necessary
	
	private List<ScoreBean> scores; //maybe have a map with actionType as key and then a list of ScoreBean objects?

	public List<ScoreBean> getScores() {return scores;}
	public void setScores(List<ScoreBean> scores) {this.scores = scores;}
	public void addScore(ScoreBean score){
		if(scores==null) scores = new ArrayList<ScoreBean>();
		//TODO: check whether item has already been scored, what then? update score? not really....
		scores.add(score);
	}

}
