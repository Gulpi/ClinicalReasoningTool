package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringAddAction;
import beans.*;
import beans.graph.Graph;
import beans.relation.*;
import controller.NavigationController;
import controller.RelationController;
import database.DBClinReason;
import model.Synonym;
import util.Logger;

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
		/*long id;
		int type = AddAction.ADD_TYPE_MAINITEM;
		if(idStr.startsWith(Synonym.SYN_VERTEXID_PREFIX)){
			type = AddAction.ADD_TYPE_SYNITEM;
			id = Long.valueOf(idStr.substring(Synonym.SYN_VERTEXID_PREFIX.length()));
		}
		else id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());
		
		if(type==AddAction.ADD_TYPE_MAINITEM) addDiagnosis(id, name, (int)x, (int)y);
		else{
			//we have to find the parent id of the synonym.
			Synonym syn = new DBClinReason().selectSynonymById(id);
			addDiagnosis(syn.getListItemId(), name, (int)x, (int)y, id); //then we add a synonym
		}
		/*long id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());		
		addDiagnosis(id, name, (int)x, (int)y);*/
	}
	/*private void addDiagnosis(long id, String name, int x, int y){
		addDiagnosis(id, name, x, y, -1);
	}*/
	
	public void addRelation(long id, String name, int x, int y, long synId){
		if(patIllScript.getDiagnoses()==null) patIllScript.setDiagnoses(new ArrayList<RelationDiagnosis>());
		RelationDiagnosis rel = new RelationDiagnosis(id, patIllScript.getId(), synId);		
		if(patIllScript.getDiagnoses().contains(rel)){
			createErrorMessage("Diagnosis already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		rel.setOrder(patIllScript.getDiagnoses().size());
		rel.setStage(patIllScript.getCurrentStage());
		if(x<0 && y<0) rel.setXAndY(calculateNewItemPosInCanvas());		
		else rel.setXAndY(new Point(x,y)); //problem has been created from the concept map, therefore we have a position

		patIllScript.getDiagnoses().add(rel);
		rel.setDiagnosis(new DBClinReason().selectListItemById(id));
		save(rel);
		notifyLog(rel);
		updateGraph(rel);
		triggerScoringAction(rel);		
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
		LogEntry le = new LogEntry(LogEntry.ADDDIAGNOSIS_ACTION, patIllScript.getId(), relProb.getListItemId());
		le.save();
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#initScoreCalc(beans.relation.Relation)
	 */
	public void triggerScoringAction(Beans relDDX){
		new ScoringAddAction().scoreAction(((RelationDiagnosis) relDDX).getListItemId(), ((RelationDiagnosis) relDDX).getDestId());

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
		Logger.out(graph.toString(), Logger.LEVEL_TEST);
	}
}
