package beans.graph;

import beans.IllnessScriptInterface;
import beans.relation.Relation;
/**
 * This is a vertex container, that can contains from which soure this vertex has been added.
 * @author ingahege
 *
 */
public class MultiVertex /*extends SynonymVertex*/ implements VertexInterface{

	/**
	 * how many peers have added this item (e.g. Problem) to their PatientIllnessScript, this includes all synonyma
	 */
	private int peerNums; 
	private Relation learnerVertex; //e.g. RelationProblem of learner (can include a synonym)s
	private Relation expertVertex;
	private Relation illScriptVertex;
	private String label;
	private long vertexId;
	/**
	 * Problem, DDX,... see definitions in Relation
	 */
	private int type; 
	
	public MultiVertex(){}
	
	public MultiVertex(Relation rel, int illnessScriptType){
		setType(rel.getRelationType());
		setLabel(rel.getLabel());
		this.vertexId = rel.getListItemId();
		this.addRelation(rel, illnessScriptType);
	}
	
	/**
	 * We add the Relation object depending on the illScriptType. 
	 * If an expert or illscript Relation has already by added, we do NOT update. the learner Relation is always updated. 
	 * @param rel
	 * @param illnessScriptType
	 */
	public void addRelation(Relation rel, int illnessScriptType){
		if(/*learnerVertex==null && */ illnessScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED)
			learnerVertex = rel;
		if(expertVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			expertVertex = rel;
		if(illScriptVertex==null && illnessScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT)
			illScriptVertex = rel;
		/*if(illnessScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED)
			learnerAdded = true;
		if(illnessScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			expAdded = true;
		if(illnessScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT)
			illScriptAdded = true;*/
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof MultiVertex && ((MultiVertex)o).getVertexId()==getVertexId()) 
				return true;		
		return false;
	}
	
	/**
	 * We check whether the multiVertex already contains the VertexInterface (as learner-, expert-, or IllnessScript
	 * vertex.
	 * @param vertexIF
	 * @return
	 */
	public Relation getRelationByType(int illScriptType){
		if(illScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED) return learnerVertex;
		if(illScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED) return expertVertex;
		if(illScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT) return illScriptVertex;
		/*if(expertVertex!=null && expertVertex.equals(rel)) return true;
		if(illScriptVertex!=null && illScriptVertex.equals(rel)) return true;
		return false;*/ 
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getLabel()+" ("+getVertexId()+"), learner: "+ isLearnerVertex() + ", exp: " + isExpertVertex() + ", illscript: " + isIllScriptVertex();

		//return getLabel()+" ("+getVertexId()+"), learner: "+ isLearnerAdded() + ", exp: " + isExpAdded() + ", illscript: " + isIllScriptAdded();
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
	/*public Relation getLearnerVertex() {return learnerVertex;}
	public Relation getExpertVertex() {return expertVertex;}
	public Relation getIllScriptVertex() {return illScriptVertex;}	*/

	public long getVertexId() {return vertexId;}
	public void setVertexId(long vertexId) {this.vertexId = vertexId;}
	public Relation getLearnerVertex() {return learnerVertex;}
	public void setLearnerVertex(Relation learnerVertex) {this.learnerVertex = learnerVertex;}
	public Relation getExpertVertex() {return expertVertex;}
	public void setExpertVertex(Relation expertVertex) {this.expertVertex = expertVertex;}
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	
	/*public boolean isLearnerAdded() {return learnerAdded;}
	public void setLearnerAdded(boolean learnerAdded) {this.learnerAdded = learnerAdded;}
	public boolean isExpAdded() {return expAdded;}
	public void setExpAdded(boolean expAdded) {this.expAdded = expAdded;}
	public boolean isIllScriptAdded() {return illScriptAdded;}
	public void setIllScriptAdded(boolean illScriptAdded) {this.illScriptAdded = illScriptAdded;}
	public void setPeerNums(int peerNums) {this.peerNums = peerNums;}*/
	
}
