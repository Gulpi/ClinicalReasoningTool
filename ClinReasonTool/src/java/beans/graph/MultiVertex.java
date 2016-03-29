package beans.graph;

import beans.IllnessScriptInterface;
import beans.relation.Relation;
import beans.relation.RelationProblem;

/**
 * This is a vertex container, that can contains from which soure this vertex has been added.
 * @author ingahege
 *
 */
public class MultiVertex {

	private int peerNums; //how many peers have added this item (e.g. Problem) to thier PatientIllnessScript
	private VertexInterface learnerVertex; //e.g. RelationProblem of learner
	private VertexInterface expertVertex;
	private VertexInterface illScriptVertex;
	private long vertexId; //the ListItemId
	private int type;
	public MultiVertex(){}
	public MultiVertex(VertexInterface vertex, int illnessScriptType){
		type = vertex.getVertextype();
		addVertexInterface(vertex, illnessScriptType);
	}
	public long getVertexId() {return vertexId;}
	public void setVertexId(long vertexId) {this.vertexId = vertexId;}
	
	public void addVertexInterface(VertexInterface vertex, int illnessScriptType){
		if(learnerVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED)
			learnerVertex = vertex;
		if(expertVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			expertVertex = vertex;
		if(illScriptVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT)
			illScriptVertex = vertex;
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof MultiVertex && ((MultiVertex)o).getVertexId()==this.vertexId) 
				return true;		
		return false;
	}
	
	/**
	 * We check whether the multiVertex already contains the VertexInterface (as learner-, expert-, or IllnessScript
	 * vertex.
	 * @param vertexIF
	 * @return
	 */
	public boolean containsVertexInterface(VertexInterface vertexIF){
		if(learnerVertex!=null && learnerVertex.equals(vertexIF)) return true;
		if(expertVertex!=null && expertVertex.equals(vertexIF)) return true;
		if(illScriptVertex!=null && illScriptVertex.equals(vertexIF)) return true;
		return false;
	}
		
}
