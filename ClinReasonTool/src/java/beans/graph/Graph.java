package beans.graph;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jgrapht.graph.DirectedWeightedMultigraph;

import beans.PatientIllnessScript;
import controller.AppBean;
import controller.GraphController;

public class Graph extends DirectedWeightedMultigraph<MultiVertex, Edge> {

	private static final long serialVersionUID = 1L;
	private long parentId; //e.g. VPId,...
	private long userId;
	private long expertPatIllScriptId;
	private long illScriptId;
	private GraphController gctrl;
	
	public Graph(long parentId){
		super(Edge.class);
		gctrl = new GraphController(this);
		gctrl.addExpPatIllScript(parentId);
		gctrl.addLearnerPatIllScript(parentId);
		gctrl.addIllnessScript(parentId);
	}
	
	
	/*public void initGraphForPatIllScript(PatientIllnessScript patIllScript){
		gctrl.addExpPatIllScript(patIllScript.getParentId());
		gctrl.addLearnerPatIllScript(parentId);
	}*/
	
	/**
	 * If a multiVertex for this vertexInterface has not yet been created we create a new one and add it. 
	 * Otherwise we add the vertexInterface to the MultiVertex.
	 * @param vertex
	 * @param illScriptType
	 * @return
	 */
	public boolean addVertex(VertexInterface vertex, int illScriptType){
		MultiVertex multiVertex = getVertexById(vertex.getVertexId());
		if(multiVertex==null){
			multiVertex = new MultiVertex(vertex, illScriptType);
			return super.addVertex(multiVertex);
		}
		if(multiVertex.containsVertexInterface(vertex)) return false;
		multiVertex.addVertexInterface(vertex, illScriptType);
		return true;
	}

	public MultiVertex getVertexById(long vertexId){
		Iterator it = this.vertexSet().iterator();
		while(it.hasNext()){
			MultiVertex vi = (MultiVertex) it.next();
			if(vi.getVertexId() == vertexId) return vi;
		}
		return null;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Graph: parent_id = " + this.parentId + ", vertices: ");
		if(this.vertexSet()!=null){
			Iterator it = this.vertexSet().iterator();
			while(it.hasNext()){
				sb.append(it.next().toString() +", ");
			}
		}
		return sb.toString();
	}
	
}
