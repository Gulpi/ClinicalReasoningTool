package actions.beanActions;

import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.relation.Relation;
import controller.ConceptMapController;
import controller.NavigationController;
import beans.Connection;
import beans.IllnessScriptInterface;
import beans.LogEntry;
import database.DBClinReason;

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
		LogEntry le = new LogEntry(LogEntry.ADDCONNECTION_ACTION, patIllScript.getSessionId(), cnx.getStartId());
		le.save();	
	}

	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String, java.lang.String)
	 */
	public void add(String sourceIdStr, String targetIdStr) {
		String startType = sourceIdStr.substring(0, sourceIdStr.indexOf("_")+1);
		String targetType = targetIdStr.substring(0, targetIdStr.indexOf("_")+1);
		ConceptMapController cmc = new ConceptMapController();
		sourceIdStr = sourceIdStr.substring(sourceIdStr.indexOf("_")+1);
		targetIdStr = targetIdStr.substring(targetIdStr.indexOf("_")+1);
		long sourceId = Long.valueOf(sourceIdStr);
		long targetId = Long.valueOf(targetIdStr);
		
		addConnection(sourceId, targetId, cmc.getTypeByPrefix(startType), cmc.getTypeByPrefix(targetType));
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

	@Override
	public void triggerScoringAction(Beans rel) {
		// TODO Auto-generated method stub
		
	}

	public void createErrorMessage(String summary, String details, Severity sev) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Connection cnx) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addExplicitEdge(cnx, patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
		System.out.println(graph.toString());
	}
}
