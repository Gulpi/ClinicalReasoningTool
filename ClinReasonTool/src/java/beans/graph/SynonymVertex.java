package beans.graph;

import beans.IllnessScriptInterface;

/**
 * A simpler vertex than MulitVertex that is used to add synonyma to the graph.
 * @author ingahege
 * @deprecated
 */
public class SynonymVertex implements VertexInterface{

	private String label;
	private String vertexId;
	private boolean learnerAdded; 
	private boolean expAdded;
	private boolean illScriptAdded;
	private int peerNums;
	/**
	 * Problem, DDX,...
	 */
	private int type;
	
	public SynonymVertex(){}
	/*public SynonymVertex(String label){
		this.label = label;
	}
	
	public SynonymVertex(String label, String id){
		this.label = label;
		this.vertexId = id;
	}
	
	public SynonymVertex(String label, int illScriptType, long synId){
		this.label = label;
		setTypeByIllScriptType(illScriptType);
		vertexId = String.valueOf(synId); //SYN_VERTEXID_PREFIX+sourceVertexId+"_"+counter;
	}*/
	
	/*public void setTypeByIllScriptType(int illScriptType){
		if(illScriptType==IllnessScriptInterface.TYPE_LEARNER_CREATED)
			learnerAdded = true;
		if(illScriptType==IllnessScriptInterface.TYPE_EXPERT_CREATED)
			expAdded = true;
		if(illScriptType==IllnessScriptInterface.TYPE_ILLNESSSCRIPT)
			illScriptAdded = true;		
	}*/
	
	/* (non-Javadoc)
	 * @see beans.graph.VertexInterface#getLabel()
	 */
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}	
	public String getVertexId() {return vertexId;}
	public void setVertexId(String vertexId) {this.vertexId = vertexId;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof SynonymVertex && ((SynonymVertex)o).getVertexId().equals(getVertexId())) 
				return true;		
		return false;
	}
	
	public String toString(){return label;}	
	public boolean isLearnerVertex(){return this.learnerAdded;}
	public boolean isExpertVertex(){return this.expAdded;}
	public boolean isIllScriptVertex(){return this.illScriptAdded;}
	public int getPeerNums() {return peerNums;}
	
}
