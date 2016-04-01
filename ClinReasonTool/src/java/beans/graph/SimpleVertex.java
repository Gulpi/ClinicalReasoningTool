package beans.graph;

/**
 * A simpler vertex than MulitVertex that is used to add synonyma to the graph.
 * @author ingahege
 *
 */
public class SimpleVertex implements VertexInterface{

	public static final String SYN_VERTEXID_PREFIX = "syn_";
	private String label;
	private String vertexId;
	/**
	 * Problem, DDX,...
	 */
	private int type;
	
	public SimpleVertex(){}
	public SimpleVertex(String label){
		this.label = label;
	}
	
	public SimpleVertex(String label, int type, String sourceVertexId, int counter){
		this.label = label;
		this.type = type;
		vertexId = SYN_VERTEXID_PREFIX+sourceVertexId+"_"+counter;
	}
	
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
		if(o !=null && o instanceof SimpleVertex && ((SimpleVertex)o).getVertexId().equals(getVertexId())) 
				return true;		
		return false;
	}
	
	public String toString(){
		return label;
	}
	
}
