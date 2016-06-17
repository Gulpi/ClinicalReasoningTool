package beans.graph;

import java.util.*;

import javax.faces.bean.SessionScoped;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import beans.Connection;
import beans.scripts.*;
import beans.relation.Relation;
import beans.scripts.IllnessScriptInterface;
import controller.GraphController;
import controller.NavigationController;
import database.DBList;
import model.ListItem;
import util.CRTLogger;

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
	private boolean expEdit = new NavigationController().isExpEdit();
	//private boolean peersConsidered = false; //we have to get this from a property file
	/**
	 * How many peers have completed this patientIllnessScript? If we have enough we can include the peers into 
	 * the scoring algorithm.
	 */
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
	public int getPeerNums() {return peerNums;}
	public void setPeerNums(int peerNums) {this.peerNums = peerNums;}
	public long getParentId() {return parentId;}


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
	
	/**
	 * We change the edge weight for this illnessScriptType to implicit if it was explicit. This needs to be done
	 *  when a user deletes a connection from the concept map.
	 *  Can only be called for learner scripts!
	 * @param cnx
	 */
	public void removeExplicitEdgeWeight(long cnxId){
		MultiEdge edge = getEdgeByCnxId(IllnessScriptInterface.TYPE_LEARNER_CREATED, cnxId);//this.getEdge(this.getVertexById(sourceId), this.getVertexById(targetId));
		if(edge==null) return; //should not happen
		edge.removeExplicitWeight();
	}
	
	public MultiEdge getEdgeByCnxId(int illScriptType, long cnxId){
		if(this.edgeSet()==null) return null;
		Iterator<MultiEdge> it = this.edgeSet().iterator();
		while(it.hasNext()){
			MultiEdge edge = it.next();
			if(illScriptType == IllnessScriptInterface.TYPE_LEARNER_CREATED && edge.getLearnerCnxId()==cnxId) return edge;
			if(illScriptType == IllnessScriptInterface.TYPE_EXPERT_CREATED && edge.getExpCnxId()==cnxId) return edge;			
		}
		return null;
	}
	
	/**
	 * We remove the edge weight (explicit or implicit) from the edge from the source to the target MultiVertex. 
	 * This needs to be done when we remove a MultiVertex vertex for the given illnessScriptType component 
	 * @param sourceId
	 * @param targetId
	 * @param illScriptType
	 */
	public void removeEdgeWeight(long sourceId, long targetId){
		MultiEdge edge = this.getEdge(this.getVertexById(sourceId), this.getVertexById(targetId));
		if(edge==null) return; //should not happen
		edge.removeWeight(IllnessScriptInterface.TYPE_LEARNER_CREATED);
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
		if(rel==null)
			return;
		MultiVertex multiVertex = getVertexById(rel.getListItemId());
		if(multiVertex==null){ //create a new one:
			multiVertex = new MultiVertex(rel, illScriptType); 
			super.addVertex(multiVertex);
		}
		else{ //we only have to update the relation in the MultiVertex
			//Relation relInVertex = multiVertex.getRelationByType(illScriptType); 
			multiVertex.addRelation(rel, illScriptType); //relation not yet added			
		}
		if(illScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			addParentAndChildVertices(multiVertex);
	}
	
	/**
	 * For the expert's script we add all parent and child nodes of the current one
	 * parent: higher in hierarchy (more general)
	 * child: lower in hierarchy (more specific)
	 */
	private void addParentAndChildVertices(MultiVertex vertex){
		if(vertex==null || vertex.getExpertVertex()==null) return;
		List<ListItem> items = new DBList().selectParentAndChildListItems(vertex.getExpertVertex().getListItem());
		if(items==null || items.isEmpty()) return;
		for(int i=0; i< items.size(); i++){
			MultiVertex relatedVertex = addVertex(items.get(i), vertex.getExpertVertex().getRelationType());
			if(vertex.getExpertVertex().getListItem().getFirstCode().length()<items.get(i).getFirstCode().length())
				addHierarchyEdge(vertex, relatedVertex); //vertex is a parent
			else //vertex is the child
				addHierarchyEdge(relatedVertex, vertex);
		}
	}
	
	/**
	 * direction  child -> parent with a special parent weight for the edge.
	 * @param sourcevertex
	 * @param parentvertex
	 */
	private void addHierarchyEdge(MultiVertex childvertex, MultiVertex parentvertex){
		if(childvertex==null || parentvertex==null) return;		
		MultiEdge e = getEdge(childvertex, parentvertex); 
		if(e==null){
			e = new MultiEdge(IllnessScriptInterface.TYPE_EXPERT_CREATED, MultiEdge.WEIGHT_PARENT); 
			addEdge(childvertex, parentvertex, e);
		}
		else e.addParam(IllnessScriptInterface.TYPE_EXPERT_CREATED, MultiEdge.WEIGHT_PARENT);
	}
	
	private MultiVertex addVertex(ListItem li, int type){
		MultiVertex multiVertex = getVertexById(li.getItem_id());
		if(multiVertex==null){ //create a new one:
			multiVertex = new MultiVertex(li, IllnessScriptInterface.TYPE_EXPERT_CREATED, type); 
			super.addVertex(multiVertex);
		}
		return multiVertex;
		//else{} -> nothing to do!?! 
	}
	

	/**
	 * @param cnx
	 * @param patIllScript
	 * @param type (see definition in IllnessScriptInterface)
	 */
	public void addExplicitEdge(Connection cnx, PatientIllnessScript patIllScript, int type){
		Relation source = patIllScript.getRelationByIdAndType(cnx.getStartId(), cnx.getStartType());
		Relation target = patIllScript.getRelationByIdAndType(cnx.getTargetId(), cnx.getTargetType());
		//the weight has to be minimum of the explicit weight or a specified higher weight:s
		int weight = MultiEdge.WEIGHT_EXPLICIT;
		if(cnx.getWeight()>MultiEdge.WEIGHT_EXPLICIT) weight = cnx.getWeight();
		if(source!=null && target!=null){
			addOrUpdateEdge(getVertexById(source.getListItemId()), getVertexById(target.getListItemId()), type, weight, cnx.getId(), patIllScript.getType());
		
		}
	}
	
	public void addImplicitEdge(long sourceId, long targetId, int type){
		addOrUpdateEdge(this.getVertexById(sourceId), this.getVertexById(targetId), type, MultiEdge.WEIGHT_IMPLICIT, -1, -1);
	}
	
	/**
	 * creates and adds a MultiEgde to a grah or of the edge has already been created it does just add the 
	 * new type/weight parameter to the MultiEdge
	 * @param source (e.g. a RelationProblem vertex)
	 * @param target (e.g a RelationDiagnosis vertex)
	 * @param type (see definitions in IllnessScriptInterface)
	 * @param weight (implicit or explicit - see defintions in MultiEdge)
	 * @param expEdit (if true, then currently an expert script is edited, so, the expert connections are the learner conns!!!
	 * @return
	 */
	private boolean addOrUpdateEdge(MultiVertex source, MultiVertex target, int type, int weight, long cnxId, int patIllScriptType){
		

		if(source==null || target==null)
			return false;
		
		MultiEdge e = getEdge(source, target); 
		if(e==null){
			e = new MultiEdge(type, weight); 
			addEdge(source, target, e);
		}
		else e.addParam(type, weight);
		if(cnxId>0 && patIllScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED) e.setLearnerCnxId(cnxId);
		
		if(cnxId>0 && patIllScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED && !expEdit) e.setExpCnxId(cnxId);
		if(cnxId>0 && patIllScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED && expEdit) e.setLearnerCnxId(cnxId);
		return true; //edge created or we have changed the params of it.
		
	}

	public MultiVertex getVertexById(long vertexId){
		Iterator<MultiVertex> it = this.vertexSet().iterator();
		while(it.hasNext()){
			MultiVertex vi = it.next();
			if(vi.getVertexId()==vertexId) return vi;
		}
		return null;
	}
	
	/**
	 * Get all MultiVertex objects of the given type (e.g. Diagnosis, Problem,...)
	 * @param type (see definitions in Relation)
	 * @return List<MultiVertex> or null
	 */
	public List<MultiVertex> getVerticesByType(int type){
		Set<MultiVertex> verts = this.vertexSet();
		if(verts==null) return null;
		List<MultiVertex> list = new ArrayList<MultiVertex>();
		Iterator<MultiVertex> it = verts.iterator();
		while(it.hasNext()){
			MultiVertex mv = it.next();
			if(mv.getType()==type) list.add(mv);				
		}
		return list;
	}
	
	/**
	 * Get all MultiVertex objects of the given type (e.g. Diagnosis, Problem,...), which have 
	 * only been selected by the expert (for joker handling)
	 * @param type (see definitions in Relation)
	 * @return List<MultiVertex> or null
	 */
	public List<MultiVertex> getVerticesByTypeAndStageExpOnly(int type, int stage){
		Set<MultiVertex> verts = this.vertexSet();
		if(verts==null) return null;
		List<MultiVertex> list = new ArrayList<MultiVertex>();
		Iterator<MultiVertex> it = verts.iterator();
		while(it.hasNext()){
			MultiVertex mv = it.next();
			if(mv.getType()==type && mv.getExpertVertex()!=null && mv.getExpertVertex().getStage()<= stage && !mv.isLearnerVertex()) 
				list.add(mv);				
		}
		return list;
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
				MultiEdge e = it.next();
				if(e.isExplicitExpertEdge() || e.isExplicitLearnerEdge()) sb.append(e.toString() +"; ");
			}
			sb.append("]");
		}
		return sb.toString();
	}	
		
	/**
	 * 
	 * Format: {"label":"Cough","shortlabel":"Cough","id":"12345","x": "10","y":"200", "type":"1", "l":"1", "e":"0", "p":"23"}");	
	 * l = learner (1 = added, 0 = not added)
	 * e = expert ( ")
	 * p = peer nums
	 * 
	 * 
	 * @return learners' patIllScript
	 */
	public String getToJson(){
		Set<MultiVertex> vertices = this.vertexSet();
		if(vertices==null || vertices.isEmpty()) return "[]"; 
		Iterator<MultiVertex> it = vertices.iterator();
		StringBuffer sb = new StringBuffer("[");
		
		while(it.hasNext()){
			MultiVertex mv = it.next();
			//Rectangle learnerRel = (Rectangle) mv.getLearnerVertex();
			//if(learnerRel!=null)
				sb.append(mv.toJson());
		}
		if(sb.length()>1) sb.replace(sb.length()-1, sb.length(), ""); //remove the last ","
		sb.append("]");
		//CRTLogger.out(sb.toString(), CRTLogger.LEVEL_TEST);
		return sb.toString();
	}
	
	/**
	 * format: [{"id":"cmcnx_1", "sourceid":"cmprb_1234","targetid":"cmddx_47673", "l":"0", "e":"1", weight_l":"3","weight_e":"4"},....]
	 * @return
	 */
	public String getJsonConns(){
		Set<MultiEdge> edges = this.edgeSet();
		if(edges==null || edges.isEmpty()) return "[]";
		Iterator<MultiEdge> it = edges.iterator();
		StringBuffer sb = new StringBuffer("[");
		while(it.hasNext()){
			MultiEdge edge = it.next();
			if(edge.getLearnerWeight()>=MultiEdge.WEIGHT_EXPLICIT || edge.getExpertWeight()>=MultiEdge.WEIGHT_EXPLICIT){ //then we add the edge to the concept map
				long cnxId = 0;
				String l = "0";
				String e = "0";
				if(edge.getLearnerCnxId()>0){
					cnxId = edge.getLearnerCnxId();
					l = "1";
				}
				else cnxId = edge.getExpCnxId();
				if(edge.getExpCnxId()>0) e = "1";
				MultiVertex sourceVertex = edge.getSource();
				MultiVertex targetVertex = edge.getTarget();
				if((e.equals("1") && l.equals("0"))){ //only expert cnx, do not include connections for which the vertices have not yet been added
					//TODO! currently we do that client side...
				}
				String startIdWithPrefix=null;
				String targetIdWithPrefix=null;
				if(sourceVertex.getLearnerVertex()!=null && targetVertex.getLearnerVertex()!=null){
					startIdWithPrefix = GraphController.getPrefixByType(sourceVertex.getType())+sourceVertex.getLearnerVertex().getId(); 	
					targetIdWithPrefix = GraphController.getPrefixByType(targetVertex.getType())+targetVertex.getLearnerVertex().getId();
				}
				else if(sourceVertex.getExpertVertex()!=null && targetVertex.getExpertVertex()!=null){
					startIdWithPrefix = GraphController.getPrefixByType(sourceVertex.getType())+sourceVertex.getExpertVertex().getId(); 	
					targetIdWithPrefix = GraphController.getPrefixByType(targetVertex.getType())+targetVertex.getExpertVertex().getId();
				}
				if(startIdWithPrefix!=null && targetIdWithPrefix!=null)
					sb.append("{\"id\":\""+GraphController.PREFIX_CNX + cnxId+"\",\"l\":\""+l+"\", \"e\":\""+e+"\", \"sourceid\": \""+startIdWithPrefix+"\",\"targetid\": \""+targetIdWithPrefix+"\",\"weight_l\": \""+edge.getLearnerWeight()+"\", \"weight_e\": \""+edge.getExpertWeight()+"\"},");		
			}	
		}
		if(sb.length()>1) sb.replace(sb.length()-1, sb.length(), ""); //remove the last ","
		sb.append("]");
		//CRTLogger.out(sb.toString(), CRTLogger.LEVEL_TEST);
		return sb.toString();
	}
	
	public List<MultiVertex> getParentVertices(MultiVertex vertex){
		List<MultiVertex> list = new ArrayList();
		getParentVertices(list, vertex);
		if(list.isEmpty()) return null;
		return list;
	}
	
	/**
	 * look whether the expert has chosen a more general term than the learner, if so return the vertex.
	 * @param lvertex
	 * @return
	 */
	public MultiVertex getExpParentVertex(MultiVertex lvertex){
		List<MultiVertex> list = new ArrayList();
		getParentVertices(list, lvertex);
		if(list.isEmpty()) return null;
		for(int i=0; i<list.size(); i++){
			MultiVertex v = list.get(i);
			if(v.isExpertVertex()) return v;
		}
		return null;
	}
	
	/**
	 * look whether the expert has chosen a more general term than the learner, if so return the distance between the expert and the 
	 * learner vertex
	 * @param lvertex
	 * @return
	 */
	/*public int getExpParentDistance(MultiVertex lvertex){
		List<MultiVertex> list = new ArrayList();
		getParentVertices(list, lvertex);
		if(list.isEmpty()) return -1;
		for(int i=0; i<list.size(); i++){
			MultiVertex v = list.get(i);
			if(v.isExpertVertex()) return i+1;
		}
		return -1;
	}*/
	
	/**
	 * returns the distance between two vertices
	 * @param source
	 * @param target
	 * @return
	 */
	public int getDistance(MultiVertex source, MultiVertex target){
		DijkstraShortestPath shortestPath = new DijkstraShortestPath(this, source, target);
		return (int) shortestPath.getPathLength();
	}
	/**
	 * look whether the expert has chosen a more specific term than the learner, if so return the vertex.
	 * Can be more than one!!!! Therefore we return a list! 
	 * @param lvertex
	 * @return
	 */
	public List<MultiVertex> getExpChildVertices(MultiVertex lvertex){
		List<MultiVertex> list = new ArrayList<MultiVertex>();
		getChildVertices(list, lvertex);
		List<MultiVertex> childs = new ArrayList<MultiVertex>();
		if(list.isEmpty()) return null;
		for(int i=0; i<list.size(); i++){
			MultiVertex v = list.get(i);
			if(v.isExpertVertex()) childs.add(v);
		}
		return childs;
	}
	
	/**
	 * look whether the expert has chosen a more specific term than the learner, if so return the distance between the 
	 * learner and the expert.
	 * @param lvertex
	 * @return
	 */
	/*public int getExpChildDistance(MultiVertex lvertex){
		List<MultiVertex> list = new ArrayList();
		getChildVertices(list, lvertex);
		if(list.isEmpty()) return -1;
		for(int i=0; i<list.size(); i++){
			MultiVertex v = list.get(i);
			if(v.isExpertVertex()) return i+1;
		}
		return -1;
	}*/
	
	public List<MultiVertex> getChildVertices(MultiVertex vertex){
		List<MultiVertex> list = new ArrayList();
		getChildVertices(list, vertex);
		if(list.isEmpty()) return null;
		return list;
	}
	
	/**
	 * recursive method to find all parent vertices and add them to the list.
	 * @param list
	 * @param vertex
	 */
	private void getParentVertices(List<MultiVertex> list, MultiVertex vertex){
		Set<MultiEdge> edges = this.incomingEdgesOf(vertex);
		if(edges==null) return; 
		Iterator<MultiEdge> it = edges.iterator(); 
		while(it.hasNext()){
			MultiEdge edge = it.next();
			if(edge.getExpertWeight()==MultiEdge.WEIGHT_PARENT){
				list.add(edge.getSource());
				getParentVertices(list, edge.getSource());
			}
		}	
	}
	
	/**
	 * recursive method to find all parent vertices and add them to the list.
	 * @param list
	 * @param vertex
	 */
	private void getChildVertices(List<MultiVertex> list, MultiVertex vertex){
		Set<MultiEdge> edges = this.outgoingEdgesOf(vertex);
		if(edges==null) return; 
		Iterator<MultiEdge> it = edges.iterator(); 
		while(it.hasNext()){
			MultiEdge edge = it.next();
			if(edge.getExpertWeight()==MultiEdge.WEIGHT_PARENT){
				list.add(edge.getTarget());
				getChildVertices(list, edge.getTarget());
			}
		}	
	}
	
	public Set<MultiEdge> getExplicitLearnerEdges(MultiVertex v){
		if(this.edgesOf(v)==null) return null;
		Iterator<MultiEdge> it = this.edgesOf(v).iterator();
		Set<MultiEdge> edges = new TreeSet<MultiEdge>();
		while(it.hasNext()){
			MultiEdge e = it.next();
			if(e.isExplicitLearnerEdge()) edges.add(e);
		}
		return edges;
	}
}
