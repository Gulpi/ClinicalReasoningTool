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
	}
	
	/**
	 * @param vertices (List of Relation items)
	 * @param illnessScriptType
	 */
	private void addVertices(List vertices, int illnessScriptType){
		if(vertices==null) return;
		for(int i=0; i<vertices.size(); i++){
			//add 
			graph.addMultiVertex((Relation) vertices.get(i), illnessScriptType);
		}
	}

	public void addSynonymaVerticesAndEdges(MultiVertex vertex){
		//Relation rel = (Relation) vertex;
		Relation rel = vertex.getExpertVertex();
		Set<Synonym> synonyma = rel.getListItem().getSynonyma();
		if(synonyma==null || synonyma.isEmpty()) return; //no synonyma for this ListItem
		Iterator<Synonym> it = synonyma.iterator();
		int counter = 1;
		while(it.hasNext()){
			Synonym syn = it.next();
			SimpleVertex synVertex = new SimpleVertex(syn.getName(), IllnessScriptInterface.TYPE_SYNONYMA, vertex.getVertexId(), counter);
			graph.addVertex(synVertex);
			graph.createAndAddEdge(synVertex, vertex, IllnessScriptInterface.TYPE_SYNONYMA, MultiEdge.WEIGHT_NONE);
			counter++;
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
	
	/*public void addExplicitEdge(Connection cnx, PatientIllnessScript patIllScript, int type){
		Relation source = patIllScript.getRelationByIdAndType(cnx.getStartId(), cnx.getStartType());
		Relation target = patIllScript.getRelationByIdAndType(cnx.getTargetId(), cnx.getTargetType());
		graph.createAndAddEdge(graph.getVertexById(source.getListItemId()), graph.getVertexById(target.getListItemId()), type, MultiEdge.WEIGHT_EXPLICIT);
	}*/
	
	private void addImplicitEdgesOfPatientIllnessScript(PatientIllnessScript patIllScript, int type){
		if(patIllScript==null || patIllScript.getDiagnoses()==null) return;
		for(int i=0; i < patIllScript.getDiagnoses().size(); i++){
			//add problems -> ddx
			for(int j=0; j < patIllScript.getProblems().size(); j++){
				graph.createAndAddEdge(graph.getVertexById(patIllScript.getProblems().get(j).getListItemId()), graph.getVertexById(patIllScript.getDiagnoses().get(i).getListItemId()), type, MultiEdge.WEIGHT_IMPLICIT);
			}
			//add ddx -> tests
			for(int j=0; j < patIllScript.getTests().size(); j++){
				graph.createAndAddEdge(graph.getVertexById(patIllScript.getDiagnoses().get(i).getListItemId()), graph.getVertexById(patIllScript.getTests().get(j).getListItemId()), type, MultiEdge.WEIGHT_IMPLICIT);
			}
			//add ddx -> mng
			for(int j=0; j < patIllScript.getMngs().size(); j++){
				graph.createAndAddEdge(graph.getVertexById(patIllScript.getDiagnoses().get(i).getListItemId()), graph.getVertexById(patIllScript.getMngs().get(j).getListItemId()), type, MultiEdge.WEIGHT_IMPLICIT);
			}

		}
	}
	

}
