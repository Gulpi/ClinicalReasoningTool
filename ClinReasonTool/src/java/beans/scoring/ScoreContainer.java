package beans.scoring;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;

import database.DBClinReason;
import database.DBScoring;
import util.Logger;

/**
 * Contains all scores for the PatientIllnessScript
 * @author ingahege
 *
 */
public class ScoreContainer implements Serializable{

	private static final long serialVersionUID = 1L;

	private long patIllScriptId; //maybe not necessary
	
	private Map<Long, ScoreBean> scores; //maybe have a map with actionType as key and then a list of ScoreBean objects?

	public ScoreContainer(long patIllScriptId){
		this.patIllScriptId = patIllScriptId;
	}
	public  Map<Long, ScoreBean> getScores() {return scores;}
	public void setScores(Map<Long, ScoreBean> scores) {this.scores = scores;}
	public void addScore(ScoreBean score){
		if(scores==null) scores = new HashMap<Long, ScoreBean>();
		//TODO: check whether item has already been scored, what then? update score? not really....
		scores.put(new Long(score.getScoredItem()), score);
	}
	
	/**
	 * Be aware: Only call if you are sure that there is only ONE results, which is valif for all list scores and the ddxFinal score. 
	 * @param type
	 * @return
	 */
	public ScoreBean getScoreBeanByType(int type){
		if(scores==null || scores.isEmpty()) return null;
		Iterator<ScoreBean> it = scores.values().iterator();
		while(it.hasNext()){
			ScoreBean sb = it.next(); 
			if(sb.getType()==type) return sb;
		}
		return null;
	}
	public ScoreBean getScoreBeanByScoredItem(long itemId){
		if(scores==null || scores.isEmpty()) return null;
		return scores.get(new Long(itemId));
	}
	
	public void initScoreContainer(){
		scores = new DBScoring().selectScoreBeansByPatIllScriptId(this.patIllScriptId);
		Logger.out("ScoreContainer init done");
	}

}
