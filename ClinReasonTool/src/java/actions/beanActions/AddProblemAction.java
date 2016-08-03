package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.tool.hbm2x.StringUtils;

import beans.*;
import beans.graph.Graph;
import beans.relation.*;
import beans.scoring.ScoreBean;
import beans.scripts.*;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import database.DBList;
import properties.IntlConfiguration;
import util.CRTLogger;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.ScoringListAction;
import application.ErrorMessageContainer;
import actions.scoringActions.Scoreable;

/**
 * A problem is added to a PatientIllnessScript by picking an item from the list, either from the list view 
 * or from the concept map view.We add the new problem to the problems list in the PatientIllnessScript, save it, 
 * and trigger a scoring and feedback action.
 * @author ingahege
 *
 */
public class AddProblemAction implements AddAction, Scoreable{

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
	public void add(String idStr, String prefix, String xStr, String yStr){ 
		new RelationController().initAdd(idStr, prefix, xStr, yStr, this);
	}
	
	public void addRelation(long id, String prefix, int x, int y, long synId){		
		addRelation(id, prefix, x, y, synId, false);
	}
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#addRelation(long, java.lang.String, int, int, long)
	 */
	public void addRelation(long id, String prefix, int x, int y, long synId, boolean isJoker){
		if(patIllScript.getProblems()==null) patIllScript.setProblems(new ArrayList<RelationProblem>());
		RelationProblem rel = new RelationProblem(id, patIllScript.getId(), synId);		
		if(patIllScript.getProblems().contains(rel)){
			createErrorMessage(IntlConfiguration.getValue("findings.duplicate"),"optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		if(prefix!=null && !prefix.trim().equals("") && StringUtils.isAlphanumeric(prefix)){ //check whether a prefix has been chosen
			rel.setPrefix(Integer.valueOf(prefix).intValue());
		}
		rel.setOrder(patIllScript.getProblems().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(NavigationController.getInstance().isExpEdit()){
			rel.setStage(patIllScript.getStage());
		}
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position
		patIllScript.getProblems().add(rel);
		rel.setProblem(new DBList().selectListItemById(id));
		save(rel);
		notifyLog(rel);
		updateGraph(rel);
		triggerScoringAction(rel, isJoker);	
		//((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).setAttribute("prob", rel);

	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageContainer().addErrorMessage("probform", summary, details, sev);
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = AddAction.MIN_Y;
		if(patIllScript.getProblems()!=null || !patIllScript.getProblems().isEmpty()){
			y = patIllScript.getProblems().size() * 26; //CAVE max y! 
		}
		//if an expert script we have to position the item on the x axis to the left:
		if(NavigationController.getInstance().isExpEdit()){
			return new Point(RelationProblem.DEFAULT_X+100,y);
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
	public void triggerScoringAction(Beans relProb, boolean isJoker){		
		new ScoringAddAction().scoreAction(((RelationProblem) relProb).getListItemId(), this.patIllScript, isJoker, Relation.TYPE_PROBLEM);
		//new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_PROBLEM_LIST, Relation.TYPE_PROBLEM);

	}

	@Override
	/*public void triggerFeedbackAction() {
		// TODO Auto-generated method stub
		
	}*/

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		for(int i=0; i<patIllScript.getDiagnoses().size(); i++){
			graph.addImplicitEdge(rel.getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
