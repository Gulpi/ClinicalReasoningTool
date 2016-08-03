package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.ScoringListAction;
import application.ErrorMessageContainer;
import beans.*;
import beans.scripts.*;
import beans.graph.Graph;
import beans.relation.*;
import beans.scoring.ScoreBean;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import database.DBList;
import model.Synonym;
import properties.IntlConfiguration;
import util.CRTLogger;

public class AddDiagnosisAction implements AddAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public AddDiagnosisAction(PatientIllnessScript patIllScript){
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
	
	/**
	 * @param idStr either an id or syn_id (for a synonym)
	 * @param name
	 * @param xStr (e.g. "199.989894") -> we have to convert it into int
	 * @param yStr
	 */
	public void add(String idStr, String name, String xStr, String yStr){ 
		new RelationController().initAdd(idStr, name, xStr, yStr, this);
	}
	
	public void addRelation(long id, String name, int x, int y, long synId){
		 addRelation(id, name, x, y, synId, false);

	}
	
	public void addRelation(long id, String name, int x, int y, long synId, boolean isJoker){
		if(patIllScript.getDiagnoses()==null) patIllScript.setDiagnoses(new ArrayList<RelationDiagnosis>());
		RelationDiagnosis rel = new RelationDiagnosis(id, patIllScript.getId(), synId);		
		if(patIllScript.getDiagnoses().contains(rel)){
			createErrorMessage(IntlConfiguration.getValue("ddx.duplicate"),"optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getDiagnoses().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(new NavigationController().isExpEdit()){
			rel.setStage(patIllScript.getStage());
		}
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position

		patIllScript.getDiagnoses().add(rel);
		rel.setDiagnosis(new DBList().selectListItemById(id));
		save(rel);
		notifyLog(rel);
		updateGraph(rel);
		triggerScoringAction(rel, isJoker);	
		((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).setAttribute("ddx", rel);

	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		new ErrorMessageContainer().addErrorMessage("ddxform", summary, details, sev);
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,.....
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = AddAction.MIN_Y;
		if(patIllScript.getDiagnoses()!=null || !patIllScript.getDiagnoses().isEmpty()){
			y = patIllScript.getDiagnoses().size() * 26;//CAVE max y!
		}
		if(NavigationController.getInstance().isExpEdit()){
			return new Point(RelationDiagnosis.DEFAULT_X+100,y);
		}
		return new Point(RelationDiagnosis.DEFAULT_X,y);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){
		//TODO save whole problems collection?
		new DBClinReason().saveAndCommit(b);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relProb){
		LogEntry le = new LogEntry(LogEntry.ADDDIAGNOSIS_ACTION, patIllScript.getId(), relProb.getListItemId());
		le.save();
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#initScoreCalc(beans.relation.Relation)
	 */
	public void triggerScoringAction(Beans relDDX, boolean isJoker){
		new ScoringAddAction().scoreAction(((RelationDiagnosis) relDDX).getListItemId(), this.patIllScript, isJoker);
		//new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_DDX_LIST, Relation.TYPE_DDX);

	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);

		// add implicit edges:
		if(patIllScript.getTests()!=null){
			for(int i=0; i < patIllScript.getTests().size(); i++){
				graph.addImplicitEdge(rel.getListItemId(), patIllScript.getTests().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		}
		if(patIllScript.getMngs()!=null){
			for(int i=0; i < patIllScript.getMngs().size(); i++){
				graph.addImplicitEdge(rel.getListItemId(), patIllScript.getMngs().get(i).getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		}
		if(patIllScript.getProblems()!=null){
			for(int i=0; i < patIllScript.getProblems().size(); i++){
				graph.addImplicitEdge(patIllScript.getProblems().get(i).getListItemId(), rel.getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
