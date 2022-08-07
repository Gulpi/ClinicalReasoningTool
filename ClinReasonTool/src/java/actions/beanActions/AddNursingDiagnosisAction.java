package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.ScoringListAction;
import application.ErrorMessageContainer;
import beans.LogEntry;
import beans.scripts.*;
import beans.graph.Graph;
import beans.helper.TypeAheadBean;
import beans.relation.Relation;
import beans.relation.RelationNursingDiagnosis;
import beans.scoring.ScoreBean;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import controller.RelationController;
import controller.XAPIController;
import database.DBClinReason;
import beans.list.ListItem;
import properties.IntlConfiguration;
import util.CRTLogger;

public class AddNursingDiagnosisAction implements AddAction, Scoreable{
	
	private PatientIllnessScript patIllScript;
	
	public AddNursingDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(java.beans.Beans)
	 */
	public void save(Beans b) { new DBClinReason().saveAndCommit(b);}

	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		new LogEntry(LogEntry.ADDNDDX_ACTION, patIllScript.getId(), rel.getListItemId()).save();
		if(!patIllScript.isExpScript()) new TypeAheadBean(rel.getListItemId(), Relation.TYPE_NDDX).save();

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
		new RelationController().initAdd(idStr, name, xStr, yStr, this, patIllScript.getLocale() );
	}
	
	public void addRelation(/*long id, String prefix*/ListItem li, int x, int y, long synId){		
		addRelation(/*id, prefix*/li, x, y, synId, false);
	}
	public void addRelation(/*long id, String name*/ ListItem li, int x, int y, long synId, boolean isJoker){
		if(patIllScript.getNursingDiagnoses()==null) patIllScript.setNursingDiagnoses(new ArrayList<RelationNursingDiagnosis>());
		RelationNursingDiagnosis rel = new RelationNursingDiagnosis(li.getItem_id(), patIllScript.getId(), synId);		
		if(patIllScript.getNursingDiagnoses().contains(rel)){
			createErrorMessage(IntlConfiguration.getValue("nddx.duplicate"),"optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getNursingDiagnoses().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(patIllScript.isExpScript()){
			rel.setStage(patIllScript.getStage());
		}
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position

		patIllScript.getNursingDiagnoses().add(rel);
		//rel.setManagement(new DBList().selectListItemById(id));
		rel.setDiagnosis(li);
		save(rel);
		updateGraph(rel);
		notifyLog(rel);
		triggerScoringAction(rel, isJoker);	
		if(!patIllScript.isExpScript()) updateXAPIStatement(rel);

	}
	

	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = AddAction.MIN_Y;
		if(patIllScript.getNursingDiagnoses()!=null || !patIllScript.getNursingDiagnoses().isEmpty()){
			y = patIllScript.getNursingDiagnoses().size() * 26; //CAVE max y! 
		}
		if(patIllScript.isExpScript()){
			return new Point(RelationNursingDiagnosis.DEFAULT_X+100,y);
		}
		return new Point(RelationNursingDiagnosis.DEFAULT_X,y);
	}

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans rel, boolean isJoker) {
		new ScoringAddAction().scoreAction(((RelationNursingDiagnosis) rel).getListItemId(), this.patIllScript, isJoker, Relation.TYPE_NDDX);
		new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_NDDX_LIST, Relation.TYPE_NDDX);
	}
	
	public void createErrorMessage(String summary, String details, Severity sev) {
		new ErrorMessageContainer().addErrorMessage("nddxform", summary, details, sev);		
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = NavigationController.getInstance().getMyFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	
		// add implicit edges:
		/*if( patIllScript.getDiagnoses()!=null && patIllScript.getDiagnoses().size()>0){
			for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
				graph.addImplicitEdge(patIllScript.getDiagnoses().get(i).getListItemId(), rel.getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		*/
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
	
	public void updateXAPIStatement(Relation rel){
		XAPIController.getInstance().addOrUpdateAddStatement(rel);
	}
}
