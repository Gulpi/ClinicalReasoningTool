package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import actions.scoringActions.ScoringListAction;
import application.ErrorMessageContainer;
import beans.LogEntry;
import beans.scripts.*;
import beans.graph.Graph;
import beans.helper.TypeAheadBean;
import beans.relation.Relation;
import beans.relation.RelationDiagnosis;
import beans.relation.RelationManagement;
import beans.scoring.ScoreBean;
import beans.scripts.IllnessScriptInterface;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import database.DBList;
import properties.IntlConfiguration;
import util.CRTLogger;

public class AddMngAction implements AddAction, Scoreable{
	
	private PatientIllnessScript patIllScript;
	
	public AddMngAction(PatientIllnessScript patIllScript){
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
		new LogEntry(LogEntry.ADDMNG_ACTION, patIllScript.getId(), rel.getListItemId()).save();
		new TypeAheadBean(rel.getListItemId(), Relation.TYPE_MNG).save();

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
	
	public void addRelation(long id, String prefix, int x, int y, long synId){		
		addRelation(id, prefix, x, y, synId, false);
	}
	public void addRelation(long id, String name, int x, int y, long synId, boolean isJoker){
		if(patIllScript.getMngs()==null) patIllScript.setMngs(new ArrayList<RelationManagement>());
		RelationManagement rel = new RelationManagement(id, patIllScript.getId(), synId);		
		if(patIllScript.getMngs().contains(rel)){
			createErrorMessage(IntlConfiguration.getValue("mng.duplicate"),"optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getMngs().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(new NavigationController().isExpEdit()){
			rel.setStage(patIllScript.getStage());
		}
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position

		patIllScript.getMngs().add(rel);
		rel.setManagement(new DBList().selectListItemById(id));
		save(rel);
		updateGraph(rel);
		notifyLog(rel);
		triggerScoringAction(rel, isJoker);	
		((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).setAttribute("mng", rel);

	}
	

	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = AddAction.MIN_Y;
		if(patIllScript.getMngs()!=null || !patIllScript.getMngs().isEmpty()){
			y = patIllScript.getMngs().size() * 26; //CAVE max y! 
		}
		if(NavigationController.getInstance().isExpEdit()){
			return new Point(RelationManagement.DEFAULT_X+100,y);
		}
		return new Point(RelationManagement.DEFAULT_X,y);
	}

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans rel, boolean isJoker) {
		new ScoringAddAction().scoreAction(((RelationManagement) rel).getListItemId(), this.patIllScript, isJoker, Relation.TYPE_MNG);
		//new ScoringListAction(this.patIllScript).scoreList(ScoreBean.TYPE_MNG_LIST, Relation.TYPE_MNG);
	}
	
	public void createErrorMessage(String summary, String details, Severity sev) {
		new ErrorMessageContainer().addErrorMessage("mngform", summary, details, sev);		
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	
		// add implicit edges:
		if( patIllScript.getDiagnoses()!=null && patIllScript.getDiagnoses().size()>0){
			for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
				graph.addImplicitEdge(patIllScript.getDiagnoses().get(i).getListItemId(), rel.getListItemId(), IllnessScriptInterface.TYPE_LEARNER_CREATED);
			}
		}
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
