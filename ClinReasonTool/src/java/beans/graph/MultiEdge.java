package beans.graph;

import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;

import beans.IllnessScriptInterface;


/**
 * @author ingahege
 *
 */
public class MultiEdge extends DefaultWeightedEdge{

	public static final int WEIGHT_NONE = 0;	
	public static final int WEIGHT_IMPLICIT = 1; //a connection has made in the concept map, tests have been associated to DDX  
	public static final int WEIGHT_EXPLICIT = 2; //implicit connection - being in the same illnessScript
	/**
	 * key = type (see definition in IllnessScriptInterface)
	 * value = weight (for peers the number of conx)
	 */
	Map<Integer, Integer> types;
	/*private int edgeExplicitInPeers = WEIGHT_NONE; //how many peers have included this cnx.
	private int edgeImplicitInPeers = WEIGHT_NONE;
	private int edgeInexpPatIllScript = WEIGHT_NONE; //has expert make a implicit/explicit conx
	private int edgeInLearnerPatIllScript = WEIGHT_NONE;
	private int edgeInIllScript = WEIGHT_NONE; //can be none or explicit (no implicit!)*/
	private long sourceId;
	private long targetId; 
	public MultiEdge(){}
	public MultiEdge(int type, int weight){
		addParam(type, weight);
	}
	
	/**
	 * We add a type to the types Map, if it has not yet been in the Map OR if the new weight is greater than the one 
	 * before, we update the Map for this type. (if you want to update in any case, use the updateParam method) 
	 * @param type (see static definitions in IllnessScriptInterface
	 * @param weight (see static definitions in this class)
	 */
	public void addParam(int type, int weight){
		if(types==null) types = new HashMap<Integer, Integer>();
		if(!types.containsKey(new Integer(type)))
				types.put(new Integer(type), new Integer(weight));
		//if the weight is stronger than the one before, we update the parameter, this can happen e.g. in the learners' 
		//patIllScript, when we add implicit cnx first or learner adds a connection.
		else{
			if(types.get(new Integer(type)) < weight){
				types.put(new Integer(type), new Integer(weight));
			}
			
		}
	}
	
	/**
	 * When learner has added/removed a connection, we have to update this in the graph as well. 
	 * @param type
	 * @param weight
	 */
	public void updateParam(int type, int weight){
		if(types==null) types = new HashMap<Integer, Integer>();
		types.put(new Integer(type), new Integer(weight));
		
	}
	
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public long getTargetId() {return targetId;}
	public void setTargetId(long targetId) {this.targetId = targetId;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null && o instanceof MultiEdge){
			MultiEdge e = (MultiEdge) o;
			if(e.getSourceId()==this.getSourceId() && e.getTargetId()==this.getTargetId()) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#getTarget()
	 */
	public VertexInterface getTarget(){return (VertexInterface) super.getTarget();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#getSource()
	 */
	public VertexInterface getSource(){return (VertexInterface) super.getSource();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultWeightedEdge#getWeight()
	 */
	public double getWeight(){return super.getWeight();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#toString()
	 */
	public String toString(){
		return "edge: " + this.getSource().getVertexId()+"-"+this.getTarget().getVertexId();
	}
	
	public void setSource(Object o){}
	
}
