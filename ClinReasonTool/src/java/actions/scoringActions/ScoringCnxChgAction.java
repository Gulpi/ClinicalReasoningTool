package actions.scoringActions;

import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import controller.NavigationController;
import util.CRTLogger;

/**
 * learner has changed the weight of the connection, so we adapt the score....
 * @author ingahege
 *
 */
public class ScoringCnxChgAction {
	public void scoreAction(long cnxId, PatientIllnessScript patIllScript){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiEdge edge = g.getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnxId);
		//MultiVertex
		ScoreContainer scoreContainer = new NavigationController().getCRTFacesContext().getScoreContainer();
		
		ScoreBean scoreBean = scoreContainer.getScoreBeanByTypeAndItemId(ScoreBean.TYPE_ADD_CNX, cnxId);
		if(scoreBean==null){
			CRTLogger.out("No scoreBean for cnxid: " + cnxId, CRTLogger.LEVEL_ERROR);
			return;
		}
		if(g.getExpertPatIllScriptId()>0) //otherwise we do not have an experts' patIllScript to compare with				
			calculateAddActionScoreBasedOnExpert(edge, scoreBean, patIllScript);						
	}
	
	/**
	 * 
	 * @param edge
	 * @param scoreBean
	 * @param patIllScript
	 */
	private void calculateAddActionScoreBasedOnExpert(MultiEdge edge, ScoreBean scoreBean, PatientIllnessScript patIllScript){
		if(edge.getExpCnxId()<=0 ) return; //then score is anyway 0, and cannot be improved by changing the weight
		//if(edge.get)
	}

}
