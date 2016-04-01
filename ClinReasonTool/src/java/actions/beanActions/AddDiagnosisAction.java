package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import actions.scoringActions.Scoreable;
import beans.*;
import beans.graph.Graph;
import beans.relation.*;
import controller.NavigationController;
import database.DBClinReason;

public class AddDiagnosisAction implements AddAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public AddDiagnosisAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String)
	 */
	public void add(String idStr, String name){ 
		long id = Long.valueOf(idStr.trim());
		addDiagnosis(id, name, -1, -1);
	}
	
	public void add(String idStr, String name, String xStr, String yStr){ 
		long id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());		
		addDiagnosis(id, name, (int)x, (int)y);
	}
	
	private void addDiagnosis(long id, String name, int x, int y){
		if(patIllScript.getDiagnoses()==null) patIllScript.setDiagnoses(new ArrayList<RelationDiagnosis>());
		RelationDiagnosis relDDX = new RelationDiagnosis(id, patIllScript.getId());		
		if(patIllScript.getDiagnoses().contains(relDDX)){
			createErrorMessage("Diagnosis already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		relDDX.setOrder(patIllScript.getDiagnoses().size());
		if(x<0 && y<0){//setDefault x,y for problem
			Point p = calculateNewItemPosInCanvas();
			relDDX.setX(p.x);
			relDDX.setY(p.y);
		}
		else{ //problem has been created from the concept map, therefore we have a position
			relDDX.setX(x);
			relDDX.setY(y);
		}
		patIllScript.getDiagnoses().add(relDDX);
		relDDX.setDiagnosis(new DBClinReason().selectListItemById(id));
		save(relDDX);
		notifyLog(relDDX);
		updateGraph(relDDX);
		triggerScoringAction(relDDX);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		 // MyFacesContextFactory factory = (MyFacesContextFactory) FactoryFinder.getFactory("MyFacesContextFactory");
		 // CRTFacesContext facesContext = factory.getFacesContextBySessionId(patIllScript.getSessionId());
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,.....
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getDiagnoses()!=null || !patIllScript.getDiagnoses().isEmpty()){
			y = patIllScript.getDiagnoses().size() * 25;//CAVE max y!
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
		LogEntry le = new LogEntry(LogEntry.ADDDIAGNOSIS_ACTION, patIllScript.getSessionId(), relProb.getListItemId());
		le.save();
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#initScoreCalc(beans.relation.Relation)
	 */
	public void triggerScoringAction(Beans relDDX){
		//try{
			//new ProblemScoring().calcScoreForAddDiagb(patIllScript, relProb);
		//}
		//catch(WrongProblemError wpe){}
	}
	
	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#updateGraph(beans.relation.Relation)
	 */
	public void updateGraph(Relation rel) {
		Graph graph = new NavigationController().getCRTFacesContext().getGraph();
		graph.addMultiVertex(rel, IllnessScriptInterface.TYPE_LEARNER_CREATED);	
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

		System.out.println(graph.toString());
	}
}
