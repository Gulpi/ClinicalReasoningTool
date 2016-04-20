package actions.scoringActions;

import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import database.DBClinReason;

/**
 * @author ingahege
 *TODO
 */
public class ScoringCnxAction implements ScoringAction{

	public ScoreBean scoreAction(long cnxId, PatientIllnessScript patIllScript){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiEdge edge = g.getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnxId);
		//MultiVertex
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		
		/*ScoreBean scoreBean = scoreContainer.getScoreBeanByScoredItem(cnxId);
		if(scoreBean==null){ //then this item has not yet been scored: 
			scoreBean = new ScoreBean(patIllScript.getId(), mvertex.getVertexId(), mvertex.getType());
			if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
				calculateAddActionScoreBasedOnExpert(mvertex, scoreBean, patIllScript.getParentId(), patIllScript.getCurrentStage());				
						
			if(g.getPeerNums()>MIN_PEERS) //we have enough peers, so we can score based on this as well:
				calculateAddActionScoreBasedOnPeers(mvertex, scoreBean, g.getPeerNums());
			
			scoreContainer.addScore(scoreBean);
			calculateOverallScore(scoreBean); 
			new DBClinReason().saveAndCommit(scoreBean);
			
		}
		
		scoreOverallCnx(scoreContainer); //update this to also consider problems the learner has not (yet) come up with  
		return scoreBean;*/
		return null;
	}
}
