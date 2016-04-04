package beans.graph;

import java.util.*;

import javax.faces.bean.SessionScoped;

import org.jgrapht.graph.DirectedWeightedMultigraph;

import beans.Connection;
import beans.IllnessScriptInterface;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import controller.ConceptMapController;
import controller.GraphController;
import model.Synonym;

/**
 * A Graph that models the components of (Patient-)IllnessScripts in a MultiGraph. 
 * Vertices (Multivertex) are Problems, Diagnoses, Tests, and Management options. Edges (MultiEdge) are explicit 
 * connections made in the concept map/or stemming from an IllnessScript or implicit connections made by putting 
 * e.g. referencing a Problem and a Diagnosis in a PatientIllnessScript.
 * CAVE: Currently it is not a MultiGraph, because we subsumed the multiple edges into a MultiEdge
 * @author ingahege
 *
 */
@SessionScoped
public class Graph extends DirectedWeightedMultigraph<MultiVertex, MultiEdge> {

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
	 * We look for the Vertex for the given relation and remove the learnerVertex from it (only learnerVertices can be
	 * removed!!!). If there do not 
	 * @param rel
	 */
	public void removeMultiVertex(Relation rel){
		MultiVertex vertex = this.getVertexById(rel.getListItemId());
		if(vertex==null) return; //Should not happen
		vertex.setLearnerVertex(null);
		//Shall we remove the vertex from the graph if there is no relation attached? (we still might have some peer nums)
		//then also the edges would be removed automatically.
	
	}
	
	public void removeExplicitEdgeWeight(Connection cnx, int illScriptType){
		MultiEdge edge = this.getEdge(this.getVertexById(cnx.getStartId()), this.getVertexById(cnx.getTargetId()));
		if(edge==null) return; //should not happen
		edge.removeExplicitWeight(illScriptType);
	}
	
	public void removeEdgeWeight(long sourceId, long targetId, int illScriptType){
		MultiEdge edge = this.getEdge(this.getVertexById(sourceId), this.getVertexById(targetId));
		if(edge==null) return; //should not happen
		edge.removeWeight(illScriptType);
	}

	
	/**
	 * We can call this for any addAction, no matter of main ListItem or Synonym, 
	 * Check whether for this Relation a MultiVertex exists, if not create one. If yes, 
	 * we check whether this Relation has been added or needs to be updated (e.g. because the learner has now 
	 * changed from the synonym to the main ListItem entry. 
	 * @param rel ALWAYS the Relation containing the ListItem (optional with the synonymId)
	 * @param illnessScriptType
	 */
	public void addVertex(Relation rel, int illScriptType){
		MultiVertex multiVertex = getVertexById(rel.getListItemId());
		if(multiVertex==null){ //create a new one:
			multiVertex = new MultiVertex(rel, illScriptType); 
			super.addVertex(multiVertex);
		}
		else{ //we only have to update the relation in the MultiVertex
			Relation relInVertex = multiVertex.getRelationByType(illScriptType); 
			multiVertex.addRelation(relInVertex, illScriptType); //relation not yet added			
		}
	}
	

	/**
	 * @param cnx
	 * @param patIllScript
	 * @param type (see definition in IllnessScriptInterface)
	 */
	public void addExplicitEdge(Connection cnx, PatientIllnessScript patIllScript, int type){
		Relation source = patIllScript.getRelationByIdAndType(cnx.getStartId(), cnx.getStartType());
		Relation target = patIllScript.getRelationByIdAndType(cnx.getTargetId(), cnx.getTargetType());
		addOrUpdateEdge(getVertexById(source.getListItemId()), getVertexById(target.getListItemId()), type, MultiEdge.WEIGHT_EXPLICIT);
	}
	
	public void addImplicitEdge(long sourceId, long targetId, int type){
		addOrUpdateEdge(this.getVertexById(sourceId), this.getVertexById(targetId), type, MultiEdge.WEIGHT_IMPLICIT);
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
	private boolean addOrUpdateEdge(MultiVertex source, MultiVertex target, int type, int weight){
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

	public MultiVertex getVertexById(long vertexId){
		Iterator<MultiVertex> it = this.vertexSet().iterator();
		while(it.hasNext()){
			MultiVertex vi = it.next();
			if(vi.getVertexId()==vertexId) return vi;
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
			Iterator<MultiVertex> it = this.vertexSet().iterator();
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
