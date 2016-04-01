package beans.graph;

import beans.IllnessScriptInterface;
import beans.relation.Relation;
/**
 * This is a vertex container, that can contains from which soure this vertex has been added.
 * @author ingahege
 *
 */
public class MultiVertex extends SimpleVertex implements VertexInterface{

	private int peerNums; //how many peers have added this item (e.g. Problem) to thier PatientIllnessScript
	private Relation learnerVertex; //e.g. RelationProblem of learner
	private Relation expertVertex;
	private Relation illScriptVertex;
	//private String vertexId; //the ListItemId
	
	public MultiVertex(){}
	public MultiVertex(Relation vertex, int illnessScriptType){
		super.setType(vertex.getRelationType());
		super.setLabel(vertex.getLabel());
		setVertexId(String.valueOf(vertex.getListItemId()));
		this.addRelation(vertex, illnessScriptType);
	}
	
	public void addRelation(Relation rel, int illnessScriptType){
		if(learnerVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED)
			learnerVertex = rel;
		if(expertVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			expertVertex = rel;
		if(illScriptVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT)
			illScriptVertex = rel;
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof MultiVertex && ((MultiVertex)o).getVertexId().equals(getVertexId())) 
				return true;		
		return false;
	}
	
	/**
	 * We check whether the multiVertex already contains the VertexInterface (as learner-, expert-, or IllnessScript
	 * vertex.
	 * @param vertexIF
	 * @return
	 */
	public boolean containsRelation(Relation vertexIF){
		if(learnerVertex!=null && learnerVertex.equals(vertexIF)) return true;
		if(expertVertex!=null && expertVertex.equals(vertexIF)) return true;
		if(illScriptVertex!=null && illScriptVertex.equals(vertexIF)) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getLabel()+" ("+getVertexId()+"), learner: "+ isLearnerVertex() + ", exp: " + isExpertVertex() + ", illscript: " + isIllScriptVertex();
	}
	
	public boolean isLearnerVertex(){
		if(learnerVertex==null) return false;
		return true;
	}
	public boolean isExpertVertex(){
		if(expertVertex==null) return false;
		return true;
	}
	
	public boolean isIllScriptVertex(){
		if(illScriptVertex==null) return false;
		return true;
	}
	public int getPeerNums() {return peerNums;}
	public Relation getLearnerVertex() {return learnerVertex;}
	public Relation getExpertVertex() {return expertVertex;}
	public Relation getIllScriptVertex() {return illScriptVertex;}	
}
