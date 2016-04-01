package beans.graph;

import java.util.*;

import org.jgrapht.graph.DirectedWeightedMultigraph;

import beans.Connection;
import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import controller.GraphController;

/**
 * A Graph that models the components of (Patient-)IllnessScripts in a MultiGraph. 
 * Vertices (Multivertex) are Problems, Diagnoses, Tests, and Management options. Edges (MultiEdge) are explicit 
 * connections made in the concept map/or stemming from an IllnessScript or implicit connections made by putting 
 * e.g. referencing a Problem and a Diagnosis in a PatientIllnessScript.
 * CAVE: Currently it is not a MultiGraph, because we subsumed the multiple edges into a MultiEdge
 * @author ingahege
 *
 */
public class Graph extends DirectedWeightedMultigraph<VertexInterface, MultiEdge> {

	private static final long serialVersionUID = 1L;
	private long parentId; //e.g. VPId,...
	private long userId;
	private long expertPatIllScriptId;
	private boolean peersConsidered = false; //we have to get this from a property file
	private int peerNums; //we might need this for the scoring process?
	private List<Long> illScriptIds;//TODO: more than one! 
	private GraphController gctrl;
	
	public Graph(long parentId){
		super(MultiEdge.class);
		this.parentId = parentId;
		gctrl = new GraphController(this);
		gctrl.addExpPatIllScript(parentId);
		gctrl.addLearnerPatIllScript(parentId);
		gctrl.addIllnessScripts(parentId);
	}
		
	public long getExpertPatIllScriptId() {return expertPatIllScriptId;}
	public void setExpertPatIllScriptId(long expertPatIllScriptId) {this.expertPatIllScriptId = expertPatIllScriptId;}
	public List<Long> getIllScriptIds() {return illScriptIds;}
	public void setIllScriptId(List<Long> illScriptIds) {this.illScriptIds = illScriptIds;}
	public void addIllScriptId(long id){
		if(illScriptIds==null) illScriptIds = new ArrayList<Long>();
		if(!illScriptIds.contains(new Long(id))) illScriptIds.add(new Long(id));
	}


	/**
	 * If a multiVertex for this vertexInterface has not yet been created we create a new one and add it. 
	 * Otherwise we add the vertexInterface to the MultiVertex.
	 * @param vertex
	 * @param illScriptType
	 * @return
	 */
	public boolean addMultiVertex(Relation vertex, int illScriptType){
		VertexInterface multiVertex = getVertexById(vertex.getListItemId());		
		if(multiVertex==null){
			multiVertex = new MultiVertex(vertex, illScriptType);
			super.addVertex(multiVertex);
		}
		else if(((MultiVertex)multiVertex).containsRelation(vertex)) return false;
		else ((MultiVertex)multiVertex).addRelation(vertex, illScriptType);
		if(illScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED){
			gctrl.addSynonymaVerticesAndEdges((MultiVertex)multiVertex);
		}
		return true;
	}
	
	public boolean addVertex(SimpleVertex vertex){
		return super.addVertex(vertex);
	}
	
	/**
	 * @param cnx
	 * @param patIllScript
	 * @param type (see definition in IllnessScriptInterface)
	 */
	public void addExplicitEdge(Connection cnx, PatientIllnessScript patIllScript, int type){
		Relation source = patIllScript.getRelationByIdAndType(cnx.getStartId(), cnx.getStartType());
		Relation target = patIllScript.getRelationByIdAndType(cnx.getTargetId(), cnx.getTargetType());
		createAndAddEdge(getVertexById(source.getListItemId()), getVertexById(target.getListItemId()), type, MultiEdge.WEIGHT_EXPLICIT);
	}
	
	public void addImplicitEdge(long sourceId, long targetId, int type){
		createAndAddEdge(this.getVertexById(sourceId), this.getVertexById(targetId), type, MultiEdge.WEIGHT_IMPLICIT);
	}
	
	/**
	 * creates and adds a MultiEgde to a grah or of the edge has already been created it does just add the 
	 * new type/weight parameter to the MultiEdge
	 * @param source (e.g. a RelationProblem vertex)
	 * @param target (e.g a RelationDiagnosis vertex)
	 * @param type (see definitions in IllnessScriptInterface)
	 * @param weight (implicit or explicit - see defintions in MultiEdge)
	 * @return
	 */
	public boolean createAndAddEdge(VertexInterface source, VertexInterface target, int type, int weight){
		if(source==null || target==null)
			return false;
		MultiEdge e = getEdge(source, target); 
		if(e==null){
			e = new MultiEdge(type, weight); 
			return addEdge(source, target, e);
		}
		else e.addParam(type, weight);
		return false; //edge already there, but we have changed the params of it.
		
	}

	public VertexInterface getVertexById(long vertexId){
		Iterator<VertexInterface> it = this.vertexSet().iterator();
		while(it.hasNext()){
			VertexInterface vi = (VertexInterface) it.next();
			if(vi.getVertexId().equals(vertexId)) return vi;
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractGraph#toString()
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Graph: parent_id = " + this.parentId + ", vertices[ ");
		if(this.vertexSet()!=null){
			Iterator<VertexInterface> it = this.vertexSet().iterator();
			while(it.hasNext()){
				sb.append(it.next().toString() +";\n ");
			}
			sb.append("]\n");
		}
		if(this.edgeSet()!=null){
			sb.append("Edges[ ");
			Iterator<MultiEdge> it = this.edgeSet().iterator();
			while(it.hasNext()){
				sb.append(it.next().toString() +"; ");
			}
			sb.append("]");
		}
		return sb.toString();
	}	
}
