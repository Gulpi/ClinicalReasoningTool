package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.graph.MultiVertex;
import beans.relation.*;
import beans.scoring.ScoreBean;
import controller.GraphController;
import controller.NavigationController;
import controller.ScoringController;
import database.DBClinReason;
import database.DBList;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeProblemAction implements ChgAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public ChangeProblemAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/**
	 * Called from map, where we could allow to change the item to any other item, currently not enabled.
	 * @param oldProbIdStr
	 * @param newProbIdStr
	 */
	public void changeProblem(String oldProbIdStr, String changeModeStr){
		
		long oldProbId = Long.valueOf(oldProbIdStr.trim());
		int changeMode = Integer.valueOf(changeModeStr.trim());
		if(changeMode==1) toggleProblem(oldProbId); //change prefix
		if(changeMode==2 || changeMode==3) changeProblem(oldProbId); //synonyma or hierarchy item
		//changeProblem(oldProbId, newProbId);
	}
	
	/*public void toggleProblem(String relIdStr){
		if(!StringUtils.isAlphanumeric(relIdStr)) return;
		toggleProblem(Long.valueOf(relIdStr).longValue());
	}*/
		
	/**
	 * We toggle the prefix (No ... vs ...)
	 * @param relIdStr
	 */
	private void toggleProblem(long relId){		
		RelationProblem probToChg = patIllScript.getProblemById(relId);
		probToChg.togglePrefix();
		save(probToChg);
		notifyLogTogglePrefix(probToChg);
		//TODO re-score:
		//ScoreBean score = new ScoringController().getScoreBeanForItem(Relation.TYPE_PROBLEM, probToChg.getListItemId());
		new ScoringAddAction(true).scoreAction(probToChg.getListItemId(), patIllScript, false);
	}
	
	/**
	 * called from lists, where we only have the the current id, we change it to the new id, which is stored in the
	 * scoreBean.
	 * @param oldProbIdStr
	 */
	private void changeProblem(long oldProbId){
		RelationProblem probToChg = patIllScript.getProblemById(oldProbId);
		ScoreBean score = new ScoringController().getScoreBeanForItem(Relation.TYPE_PROBLEM, probToChg.getListItemId());
		//change in RelationProblem & Vertex:
		
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex expVertex = g.getVertexById(score.getExpItemId());
		MultiVertex learnerVertexOld = g.getVertexById(probToChg.getListItemId());
		if(!expVertex.equals(learnerVertexOld)){ //then it is NOT a synonyma, but a hierarchy node
				new GraphController(g).transferEdges(learnerVertexOld, expVertex);		
				g.removeVertex(learnerVertexOld);
				if(expVertex.getLearnerVertex()==null) expVertex.setLearnerVertex(probToChg);
		}
		changeRelation(expVertex, probToChg);	
		//we re-score the item:
		new ScoringAddAction(true).scoreAction(expVertex.getVertexId(), patIllScript, false);
	}
	
	private void changeRelation(MultiVertex expVertex, RelationProblem probToChg){
		notifyLog(probToChg, expVertex.getExpertVertex().getListItem().getItem_id());
		probToChg.setProblem(expVertex.getExpertVertex().getListItem());
		probToChg.setListItemId(expVertex.getExpertVertex().getListItem().getItem_id());
		probToChg.setSynId(-1);	
		save(probToChg);		
	}
	
	
	/*private void changeProblem(long newProbId, long probRel){
		RelationProblem probToChg = patIllScript.getProblemById(probRel);
		ListItem oldProblem = new DBList().selectListItemById(probToChg.getListItemId());
		ListItem newProblem = new DBList().selectListItemById(newProbId);
		if(probToChg!=null && newProblem!=null && oldProblem!=null){
			notifyLog(probToChg, newProbId);
			probToChg.setProblem(newProblem);
			probToChg.setListItemId(newProblem.getItem_id());
			save(probToChg);		
		}
		//else -> error...
	}*/
	
	public void notifyLog(Beans probToChg, long newProbId){
		LogEntry le = new LogEntry(LogEntry.CHGPROBLEM_ACTION, patIllScript.getId(), ((Relation)probToChg).getListItemId(), newProbId);
		le.save();
	}
	
	private void notifyLogTogglePrefix(Relation probToChg){
		LogEntry le = new LogEntry(LogEntry.TOGGLE_PREFIX_ACTION, patIllScript.getId(), probToChg.getListItemId(), probToChg.getPrefix());
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}

	@Override
	public void triggerScoringAction(Beans beanToScore, boolean isJoker) {
		// TODO Auto-generated method stub
		
	}
}
