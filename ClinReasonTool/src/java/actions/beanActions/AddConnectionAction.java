package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddCnxAction;
import application.ErrorMessageContainer;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.relation.Connection;
import beans.relation.Relation;
import beans.scripts.IllnessScriptInterface;
import controller.ConceptMapController;
import controller.GraphController;
import controller.NavigationController;
import beans.LogEntry;
import database.DBClinReason;
import properties.IntlConfiguration;
import util.CRTLogger;

public class AddConnectionAction implements Scoreable{

	private PatientIllnessScript patIllScript;
	
	public AddConnectionAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(java.beans.Beans)
	 */
	public void save(Beans b) {
		new DBClinReason().saveAndCommit(b);
	}

	public void notifyLog(Relation rel) {}	
	public void notifyLog(Connection cnx) {
		LogEntry le = new LogEntry(LogEntry.ADDCONNECTION_ACTION, patIllScript.getId(), cnx.getStartId(), cnx.getTargetId());
		le.save();	
	}

	public void add(String sourceIdStr, String targetIdStr) {
		add(sourceIdStr, targetIdStr, null, null);
	}
	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String, java.lang.String)
	 */
	public void add(String sourceIdStr, String targetIdStr, String startEpIdxStr, String targetEpIdxStr) {
		String startType = sourceIdStr.substring(0, sourceIdStr.indexOf("_")+1);
		String targetType = targetIdStr.substring(0, targetIdStr.indexOf("_")+1);
		//ConceptMapController cmc = new ConceptMapController();
		sourceIdStr = sourceIdStr.substring(sourceIdStr.indexOf("_")+1);
		targetIdStr = targetIdStr.substring(targetIdStr.indexOf("_")+1);
		long sourceId = Long.valueOf(sourceIdStr);
		long targetId = Long.valueOf(targetIdStr);
		int startEpIdx = 0; //Integer.parseInt(startEpIdxStr);
		int targetEpIdx = 0; //Integer.parseInt(targetEpIdxStr);
		try{
			startEpIdx = Integer.parseInt(startEpIdxStr);
			targetEpIdx = Integer.parseInt(targetEpIdxStr);
		}
		catch(Exception e){}
		
		addConnection(sourceId, targetId, GraphController.getTypeByPrefix(startType), GraphController.getTypeByPrefix(targetType), MultiEdge.WEIGHT_EXPLICIT, startEpIdx, targetEpIdx);
	}
	
	protected void addConnection(long sourceId, long targetId, int startType, int targetType, int weight, int startEpIdx, int targetEpIdx){
		if(patIllScript.getConns()==null) patIllScript.setConns(new TreeMap<Long, Connection>());
		
		Connection cnx = new Connection(sourceId, targetId, this.patIllScript.getId(), startType, targetType, patIllScript.getStageForAction());
		cnx.setWeight(weight);
		//this should not happen, since we already handle it on the client side:
		if(patIllScript.getConns().containsValue(cnx)){
			createErrorMessage(IntlConfiguration.getValue("cnx.duplicate"), "", FacesMessage.SEVERITY_ERROR, cnx.getTargetType());
			return; //cnx already made... 
		}
		cnx.setStartEpIdx(startEpIdx);
		cnx.setTargetEpIdx(targetEpIdx);
		save(cnx); //first save to get an id...
		patIllScript.getConns().put(new Long(cnx.getId()), cnx);
		updateGraph(cnx);
		notifyLog(cnx);
		((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).setAttribute("id2", GraphController.PREFIX_CNX + cnx.getId());

		//initScoreCalc(relProb);		
	}

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans cnx, boolean isJoker) {
		new ScoringAddCnxAction().scoreAction(((Connection)cnx).getId(), this.patIllScript, isJoker);
		
	}

	public void createErrorMessage(String summary, String details, Severity sev, int targetType) {
		String formId = "probform";
		if(targetType==Relation.TYPE_DDX) formId = "cnxform";
		else if(targetType==Relation.TYPE_TEST) formId = "testform";
		else if(targetType == Relation.TYPE_MNG) formId = "mngform";
		new ErrorMessageContainer().addErrorMessage(formId, summary, details, sev);
		
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Connection cnx) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addExplicitEdge(cnx, patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED, cnx.getWeight());
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
