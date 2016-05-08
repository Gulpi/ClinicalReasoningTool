package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.ScoringListAction;
import beans.IllnessScriptInterface;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.relation.Relation;
import beans.relation.RelationManagement;
import beans.relation.RelationTest;
import beans.scoring.ScoreBean;
import controller.ErrorMessageController;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import database.DBList;
import util.CRTLogger;

public class AddTestAction implements AddAction, Scoreable/*, FeedbackCreator*/{
	
	private PatientIllnessScript patIllScript;
	
	public AddTestAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(java.beans.Beans)
	 */
	public void save(Beans b) { new DBClinReason().saveAndCommit(b);}

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		LogEntry le = new LogEntry(LogEntry.ADDTEST_ACTION, patIllScript.getId(), rel.getListItemId());
		le.save();
	}

	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String)
	 */
	public void add(String idStr, String name){ add(idStr, name, "-1", "-1");}
	
	/**
	 * @param idStr either an id or syn_id (for a synonym)
	 * @param name
	 * @param xStr (e.g. "199.989894") -> we have to convert it into int
	 * @param yStr
	 */
	public void add(String idStr, String name, String xStr, String yStr){ 
		new RelationController().initAdd(idStr, name, xStr, yStr, this);
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#addRelation(long, java.lang.String, int, int, long)
	 */
	public void addRelation(long id, String name, int x, int y, long synId){
		if(patIllScript.getTests()==null) patIllScript.setTests(new ArrayList<RelationTest>());
		RelationTest rel = new RelationTest(id, patIllScript.getId(), synId);		
		if(patIllScript.getTests().contains(rel)){
			createErrorMessage("Test already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getTests().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position

		patIllScript.getTests().add(rel);
		rel.setTest(new DBList().selectListItemById(id));
		save(rel);
		updateGraph(rel);
		notifyLog(rel);
		triggerScoringAction(rel);				
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getTests()!=null || !patIllScript.getTests().isEmpty()){
			y = patIllScript.getTests().size() * 25; //CAVE max y! 
		}
		return new Point(RelationTest.DEFAULT_X,y);
	}

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans rel) {
		new ScoringAddAction().scoreAction(((RelationTest) rel).getListItemId(), this.patIllScript);
		//new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_TEST_LIST, Relation.TYPE_TEST);

	}
	public void createErrorMessage(String summary, String details, Severity sev) {
		new ErrorMessageController().addErrorMessage(summary, details, sev);		
	}

	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	
		// add implicit edges:
		for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
			graph.addImplicitEdge(patIllScript.getDiagnoses().get(i).getListItemId(), rel.getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
