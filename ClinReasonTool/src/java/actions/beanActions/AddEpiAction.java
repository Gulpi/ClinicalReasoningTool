package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import beans.*;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.relation.*;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import database.DBList;
import model.Synonym;
import util.CRTLogger;
import actions.scoringActions.ScoringAddAction;
import application.ErrorMessageContainer;
import actions.scoringActions.Scoreable;

/**
 * A problem is added to a PatientIllnessScript by picking an item from the list, either from the list view 
 * or from the concept map view.We add the new problem to the problems list in the PatientIllnessScript, save it, 
 * and trigger a scoring and feedback action.
 * @author ingahege
 *
 */
public class AddEpiAction /*implements AddAction, Scoreable*/{

	private PatientIllnessScript patIllScript;
	
	public AddEpiAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String)
	 */
	public void add(String idStr, String name){ 
		//addProblem(idStr, name);
		//long id = Long.valueOf(idStr.trim());
		add(idStr, name, "-1", "-1");
	}
	

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#add(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void add(String idStr, String name, String xStr, String yStr){ 
		//new RelationController().initAdd(idStr, name, xStr, yStr, this);
	}
	
	public void addRelation(long id, String name, int x, int y, long synId){
		addRelation(id, name, x, y, synId, false);
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#addRelation(long, java.lang.String, int, int, long)
	 */
	public void addRelation(long id, String name, int x, int y, long synId, boolean isJoker){
		if(patIllScript.getEpis()==null) patIllScript.setEpis(new ArrayList<RelationEpi>());
		RelationEpi rel = new RelationEpi(id, patIllScript.getId(), synId);		
		if(patIllScript.getEpis().contains(rel)){
			createErrorMessage("Epi already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getEpis().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position
		patIllScript.getEpis().add(rel);
		rel.setEpi(new DBList().selectListItemById(id));
		save(rel);
		notifyLog(rel);
		updateGraph(rel);
		triggerScoringAction(rel,isJoker);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageContainer().addErrorMessage(summary, details, sev);
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getEpis()!=null || !patIllScript.getEpis().isEmpty()){
			y = patIllScript.getEpis().size() * 25; //CAVE max y! 
		}
		return new Point(RelationEpi.DEFAULT_X,y);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){ new DBClinReason().saveAndCommit(b); }
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relEpi){
		LogEntry le = new LogEntry(LogEntry.ADDEPI_ACTION, patIllScript.getId(), relEpi.getListItemId());
		le.save();
	}
	

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans relEpi, boolean isJoker){		
		new ScoringAddAction().scoreAction(((RelationEpi) relEpi).getListItemId(), this.patIllScript, isJoker, Relation.TYPE_EPI);
	}


	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = NavigationController.getInstance().getMyFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		for(int i=0; i<patIllScript.getDiagnoses().size(); i++){
			graph.addImplicitEdge(rel.getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
