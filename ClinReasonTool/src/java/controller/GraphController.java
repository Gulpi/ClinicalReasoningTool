package controller;

import java.util.*;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import application.AppBean;
import beans.Connection;
import beans.IllnessScript;
import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.graph.*;
import beans.relation.Relation;
import model.Synonym;

/**
 * Controls the graph creation and manipulation based on PatientIllnessScript and IllnessScript objects 
 * @author ingahege
 *
 */
public class GraphController {

	public static final String PREFIX_PROB = "cmprb_";
	public static final String PREFIX_EPI = "cmepi_";
	public static final String PREFIX_DDX = "cmddx_";
	public static final String PREFIX_MNG = "cmmng_";
	public static final String PREFIX_CNX = "cmcnx_";
	public static final String PREFIX_TEST = "cmtes_";
	private Graph graph;
	
	public GraphController(Graph g){
		this.graph = g;
	}
	
	/**
	 * Add all vertices (e.g. Problem, DDX) to the graph and all explicit (Connection objects) and implicit edges
	 * of the experts' PatientIllnessScript for this parentId
	 * @param parentId
	 */
	public void addExpPatIllScript(long parentId){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    AppBean app = (AppBean) context.getAttribute(AppBean.APP_KEY);
	    PatientIllnessScript expIllScript = app.getExpertPatIllScript(parentId);
	    if(expIllScript!=null){
	    	graph.setExpertPatIllScriptId(expIllScript.getId());
	    	addVerticesOfPatientIllnessScript(expIllScript, IllnessScriptInterface.TYPE_EXPERT_CREATED);
	    	addExplicitEdgesOfPatientIllnessScript(expIllScript, IllnessScriptInterface.TYPE_EXPERT_CREATED); //and connect all implicitly
		    addImplicitEdgesOfPatientIllnessScript(expIllScript,  IllnessScriptInterface.TYPE_EXPERT_CREATED);
		    
	    	//TODO add synonyma in ListItem...
	    }
	}
	/**
	 * Add all vertices (e.g. Problem, DDX) to the graph and all explicit (Connection objects) and implicit edges
	 * of the learners' PatientIllnessScript for this parentId
	 * @param parentId
	 */
	public void addLearnerPatIllScript(long parentId){
		PatientIllnessScript patIllScript = new NavigationController().getCRTFacesContext().getPatillscript();
	    addVerticesOfPatientIllnessScript(patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	    addExplicitEdgesOfPatientIllnessScript(patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	    addImplicitEdgesOfPatientIllnessScript(patIllScript,  IllnessScriptInterface.TYPE_LEARNER_CREATED);
	}
	
	/**
	 * Add all vertices (e.g. Problem, DDX) to the graph and all explicit edges of the IllnessScript for this parentId
	 * (IllnessScripts only have explicit edges, since they only include ONE diagnosis, every other item explicitly
	 * relates to this diagnosis.
	 * @param parentId	 
	 * */
	public void addIllnessScripts(long parentId){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    AppBean app = (AppBean) context.getAttribute(AppBean.APP_KEY);
	    List<IllnessScript> illScripts = app.getIlnessScripts(parentId);
	    
	    if(illScripts!=null){
	    	Iterator<IllnessScript> it = illScripts.iterator();
	    	while(it.hasNext()){
	    		IllnessScript illScript = it.next();
	    		graph.addIllScriptId(illScript.getId());
	    		//addVertices(illScript.getProblems());
	    		//...
	    	}
	    }
	}	
	
	private void addVerticesOfPatientIllnessScript(PatientIllnessScript patIllScript, int illnessScriptType){
		 addVertices( patIllScript.getProblems(),illnessScriptType);
		 addVertices( patIllScript.getDiagnoses(), illnessScriptType);
		 addVertices( patIllScript.getMngs(), illnessScriptType);
		 addVertices( patIllScript.getTests(), illnessScriptType);
		 addVertices( patIllScript.getEpis(), illnessScriptType);

	}
	
	/**
	 * if a relation has a synId we add the SimpleVertex for the syn:
	 * Learnerskript: we ONLY add the SimpleVertex
	 * ExpertSkript: we add the main relation and ALL synonyma as SimpleVertex
	 * @param vertices (List of Relation items)
	 * @param illnessScriptType
	 */
	private void addVertices(List vertices, int illnessScriptType){
		if(vertices==null) return;
		for(int i=0; i<vertices.size(); i++){
			Relation rel = (Relation) vertices.get(i);
			graph.addVertex(rel, illnessScriptType);
		}
	}
	
	private void addExplicitEdgesOfPatientIllnessScript(PatientIllnessScript patIllScript, int type){
		if(patIllScript==null || patIllScript.getConns()==null) return;
		Iterator<Connection> it =  patIllScript.getConns().values().iterator();
		while (it.hasNext()){
			Connection cnx = (Connection) it.next();
			graph.addExplicitEdge(cnx, patIllScript, type);
		}
	}
	
	private void addImplicitEdgesOfPatientIllnessScript(PatientIllnessScript patIllScript, int illScriptType){
		if(patIllScript==null || patIllScript.getDiagnoses()==null) return;
		for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
			//add problems -> ddx
			for(int j=0; j < patIllScript.getProblems().size(); j++){
				//graph.addImplicitEdge(sourceId, targetId, type);
				graph.addImplicitEdge(patIllScript.getProblems().get(j).getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), illScriptType);
			}
			for(int j=0; j < patIllScript.getEpis().size(); j++){
				//graph.addImplicitEdge(sourceId, targetId, type);
				graph.addImplicitEdge(patIllScript.getEpis().get(j).getListItemId(), patIllScript.getDiagnoses().get(i).getListItemId(), illScriptType);
			}

			//add ddx -> tests
			for(int j=0; j < patIllScript.getTests().size(); j++){
				graph.addImplicitEdge(patIllScript.getDiagnoses().get(i).getListItemId(), patIllScript.getTests().get(j).getListItemId(), illScriptType);
			}
			//add ddx -> mng
			for(int j=0; j < patIllScript.getMngs().size(); j++){
				graph.addImplicitEdge(patIllScript.getDiagnoses().get(i).getListItemId(), patIllScript.getMngs().get(j).getListItemId(), illScriptType);
			}

		}
	}
	
	/**
	 * 1 = no feedback
	 * 2 = exp feedback
	 * 3 = illscript fb
	 * 4 = peer feedback
	 * @param vertex
	 * @return
	 */
	private boolean[] getDisplayModusOfVertex(VertexInterface vertex, boolean[] feedbackModus){
		feedbackModus = new boolean[]{true, true, false, false}; //show learner and expert, TODO get from user settings and/or store in FacesContext
		boolean[] displayModus = new boolean[]{false, false, false, false}; //learner, expert, illscript, peer 
		int currentStage = new NavigationController().getCRTFacesContext().getPatillscript().getCurrentStage();	
		/*boolean showExp = false; 
		boolean showLearner = false; 
		boolean showIllScript = false; 
		boolean showPeer = false; */
		
		//if(vertex instanceof MultiVertex){
			MultiVertex mvertex = (MultiVertex) vertex;
			/*Relation learnerRel = ;*/
			Relation expRel = mvertex.getExpertVertex();
		
			if(mvertex.isLearnerVertex() && feedbackModus[0]){
				displayModus[0] = true; //we always show what the learner has entered
				if(feedbackModus[1] && mvertex.isExpertVertex()) //if learner has added this and expert as well display expert node:
					displayModus[1] = true;
				if(feedbackModus[3]) displayModus[3] = true; //show peers
			}
			else if(!mvertex.isLearnerVertex()){ //now learner has not added node:
				if(feedbackModus[1] && mvertex.isExpertVertex() && expRel.getStage()<=currentStage) //but exp has added node
					displayModus[1] = true;
			}	
			return displayModus;
		//}
		/*if(vertex instanceof SynonymVertex){ //then it is a synonym
			
		}*/
		
	}
	
	public static int getTypeByPrefix(String prefix){
		if(prefix==null) return 0;
		if(prefix.equals(PREFIX_PROB)) return Relation.TYPE_PROBLEM;
		if(prefix.equals(PREFIX_DDX)) return Relation.TYPE_DDX;
		if(prefix.equals(PREFIX_TEST)) return Relation.TYPE_TEST;
		if(prefix.equals(PREFIX_MNG)) return Relation.TYPE_MNG;
		if(prefix.equals(PREFIX_EPI)) return Relation.TYPE_EPI;

		return 0;
	}
	
	public static String getPrefixByType(int type){
		if(type==Relation.TYPE_PROBLEM) return PREFIX_PROB;
		if(type==Relation.TYPE_DDX) return PREFIX_DDX;
		if(type==Relation.TYPE_TEST) return PREFIX_TEST;
		if(type==Relation.TYPE_MNG) return PREFIX_MNG;
		if(type==Relation.TYPE_EPI) return PREFIX_EPI;

		return "";
	}
}
