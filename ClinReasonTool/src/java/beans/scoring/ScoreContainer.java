package beans.scoring;

import java.io.Serializable;
import java.util.*;
import database.DBScoring;
import util.CRTLogger;

/**
 * Contains all scores for the PatientIllnessScript
 * @author ingahege
 *
 */
public class ScoreContainer implements Serializable{

	private static final long serialVersionUID = 1L;

	private long patIllScriptId; //maybe not necessary
	
	private List<ScoreBean> scores;
	public ScoreContainer(long patIllScriptId){
		this.patIllScriptId = patIllScriptId;
	}
	public List<ScoreBean> getScores(){return scores;}
	public void setScores(List<ScoreBean> scores) {this.scores = scores;}
	
	public void addScore(ScoreBean score){
		if(scores==null) scores = new ArrayList<ScoreBean>();
		if(!scores.contains(score)) scores.add(score);
	}
	
	/** 
	 * @param type
	 * @return
	 */
	public ScoreBean getScoreBeanByTypeAndItemId(int type, long itemId){
		if(scores==null || scores.isEmpty()) return null;
		Iterator<ScoreBean> it = scores.iterator();
		while(it.hasNext()){
			ScoreBean sb = it.next(); 
			if(sb.getType()==type && sb.getScoredItem()==itemId) return sb;
		}
		return null;
	}
	
	public ScoreBean getListScoreBeanByStage(int type, int stage){
		if(scores==null || scores.isEmpty()) return null;
		Iterator<ScoreBean> it = scores.iterator();
		while(it.hasNext()){
			ScoreBean sb = it.next(); 
			if(sb.getType()==type && sb.getStage()==stage) return sb;
		}
		return null;
	}
	
	public List<ScoreBean> getScoresByType(int type){
		if(scores==null) return null;
		List<ScoreBean> scoresForType = new ArrayList<ScoreBean>();
		for(int i=0; i<scores.size(); i++){
			ScoreBean score = scores.get(i);
			if(score.getType()==type) scoresForType.add(score);
		}
		return scoresForType; //TODO sort for stage
	}
	
	public void initScoreContainer(){
		scores = new DBScoring().selectScoreBeansByPatIllScriptId(this.patIllScriptId);
		CRTLogger.out("ScoreContainer init done", CRTLogger.LEVEL_TEST);
	}

}
