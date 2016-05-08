package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddCnxAction;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.relation.Relation;
import controller.ConceptMapController;
import controller.ErrorMessageController;
import controller.GraphController;
import controller.NavigationController;
import beans.Connection;
import beans.IllnessScriptInterface;
import beans.LogEntry;
import database.DBClinReason;
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

	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String, java.lang.String)
	 */
	public void add(String sourceIdStr, String targetIdStr) {
		String startType = sourceIdStr.substring(0, sourceIdStr.indexOf("_")+1);
		String targetType = targetIdStr.substring(0, targetIdStr.indexOf("_")+1);
		//ConceptMapController cmc = new ConceptMapController();
		sourceIdStr = sourceIdStr.substring(sourceIdStr.indexOf("_")+1);
		targetIdStr = targetIdStr.substring(targetIdStr.indexOf("_")+1);
		long sourceId = Long.valueOf(sourceIdStr);
		long targetId = Long.valueOf(targetIdStr);
		
		addConnection(sourceId, targetId, GraphController.getTypeByPrefix(startType), GraphController.getTypeByPrefix(targetType));
	}
	
	private void addConnection(long sourceId, long targetId, int startType, int targetType){
		if(patIllScript.getConns()==null) patIllScript.setConns(new TreeMap());
		Connection cnx = new Connection(sourceId, targetId, this.patIllScript.getId(), startType, targetType);		
		/*if(patIllScript.getConns().contains(relProb)){
			createErrorMessage("Problem already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}*/
		//relProb.setOrder(patIllScript.getProblems().size());
		save(cnx); //first save to get an id...
		patIllScript.getConns().put(new Long(cnx.getId()), cnx);
		updateGraph(cnx);
		notifyLog(cnx);
		//initScoreCalc(relProb);		
	}

	/* (non-Javadoc)
	 * @see actions.scoringActions.Scoreable#triggerScoringAction(java.beans.Beans)
	 */
	public void triggerScoringAction(Beans cnx) {
		new ScoringAddCnxAction().scoreAction(((Connection)cnx).getId(), this.patIllScript);
		
	}

	public void createErrorMessage(String summary, String details, Severity sev) {
		new ErrorMessageController().addErrorMessage(summary, details, sev);
		
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Connection cnx) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addExplicitEdge(cnx, patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		CRTLogger.out(graph.toString(), CRTLogger.LEVEL_TEST);
	}
}
