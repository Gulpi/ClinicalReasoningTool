package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import beans.*;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.relation.*;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import model.Synonym;
import util.Logger;
import actions.feedbackActions.FeedbackCreator;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.Scoreable;

/**
 * A problem is added to a PatientIllnessScript by picking an item from the list, either from the list view 
 * or from the concept map view.We add the new problem to the problems list in the PatientIllnessScript, save it, 
 * and trigger a scoring and feedback action.
 * @author ingahege
 *
 */
public class AddProblemAction implements AddAction, Scoreable, FeedbackCreator{

	private PatientIllnessScript patIllScript;
	
	public AddProblemAction(PatientIllnessScript patIllScript){
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
		new RelationController().initAdd(idStr, name, xStr, yStr, this);
		/*if(!xStr.equals("-1")){ //then we come from the concept map
			
		}*/
		/*long id;
		int type = AddAction.ADD_TYPE_MAINITEM;
		if(idStr.startsWith(Synonym.SYN_VERTEXID_PREFIX)){
			type = AddAction.ADD_TYPE_SYNITEM;
			id = Long.valueOf(idStr.substring(Synonym.SYN_VERTEXID_PREFIX.length()));
		}
		else id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());
		
		if(type==AddAction.ADD_TYPE_MAINITEM) addProblem(id, name, (int)x, (int)y);
		else{
			//we have to find the parent id of the synonym.
			Synonym syn = new DBClinReason().selectSynonymById(id);
			addProblem(syn.getListItemId(), name, (int)x, (int)y, id); //then we add a synonym
		}*/
	}
	/*private void addProblem(long id, String name, int x, int y){
		addProblem(id, name, x, y, -1);
	}*/
	
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#addRelation(long, java.lang.String, int, int, long)
	 */
	public void addRelation(long id, String name, int x, int y, long synId){
		if(patIllScript.getProblems()==null) patIllScript.setProblems(new ArrayList<RelationProblem>());
		RelationProblem rel = new RelationProblem(id, patIllScript.getId(), synId);		
		if(patIllScript.getProblems().contains(rel)){
			createErrorMessage("Problem already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getProblems().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position
		patIllScript.getProblems().add(rel);
		rel.setProblem(new DBClinReason().selectListItemById(id));
		save(rel);
		notifyLog(rel);
		updateGraph(rel);
		triggerScoringAction(rel);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getProblems()!=null || !patIllScript.getProblems().isEmpty()){
			y = patIllScript.getProblems().size() * 25; //CAVE max y! 
		}
		return new Point(RelationProblem.DEFAULT_X,y);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){ new DBClinReason().saveAndCommit(b); }
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relProb){
		LogEntry le = new LogEntry(LogEntry.ADDPROBLEM_ACTION, patIllScript.getId(), relProb.getListItemId());
		le.save();
	}
	

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans relProb){		
		new ScoringAddAction().scoreAction(((RelationProblem) relProb).getListItemId(), this.patIllScript);
	}

	@Override
	public void triggerFeedbackAction() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		for(int i=0; i<patIllScript.getDiagnoses().size(); i++){
			graph.addImplicitEdge(rel.getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
		}
		Logger.out(graph.toString(), Logger.LEVEL_TEST);
	}
}
