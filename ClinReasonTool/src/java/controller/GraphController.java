package controller;

import java.util.*;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import beans.IllnessScript;
import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.graph.Graph;
import beans.graph.VertexInterface;
import beans.relation.RelationProblem;

public class GraphController {

	private Graph graph;
	
	public GraphController(Graph g){
		this.graph = g;
	}
	public void addExpPatIllScript(long parentId){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    AppBean app = (AppBean) context.getAttribute(AppBean.APP_KEY);
	    PatientIllnessScript expIllScript = app.getExpertPatIllScript(parentId);
	    if(expIllScript!=null){
	    	addVerticesOfPatientIllnessScript(expIllScript, IllnessScriptInterface.TYPE_EXPERT_CREATED);
	    	addEdgesOfPatientIllnessScript(expIllScript.getConns());
	    }
	}
	public void addLearnerPatIllScript(long parentId){
		PatientIllnessScript patIllScript = new NavigationController().getCRTFacesContext().getPatillscript();
	    addVerticesOfPatientIllnessScript(patIllScript, IllnessScriptInterface.TYPE_LEARNER_CREATED);
	    addEdgesOfPatientIllnessScript(patIllScript.getConns());
	}
	
	public void addIllnessScript(long parentId){
	    ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    AppBean app = (AppBean) context.getAttribute(AppBean.APP_KEY);
	    List<IllnessScript> illScripts = app.getIlnessScripts(parentId);
	    if(illScripts!=null){
	    	Iterator<IllnessScript> it = illScripts.iterator();
	    	while(it.hasNext()){
	    		IllnessScript illScript = it.next();
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
	
	private void addEdgesOfPatientIllnessScript(Map conxs){
		if(conxs==null) return;
	}
	
	private void addVertices(List vertices, int illnessScriptType){
		if(vertices==null) return;
		for(int i=0; i<vertices.size(); i++){
			boolean added = graph.addVertex((VertexInterface) vertices.get(i), illnessScriptType);
		}
		
	}
}
