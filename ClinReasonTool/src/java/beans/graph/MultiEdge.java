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
	public static final int WEIGHT_SLIGHTLY_RELATED = 3;
	public static final int WEIGHT_SOMEWHAT_RELATED = 4;
	public static final int WEIGHT_HIGHLY_RELATED = 5;
	public static final int WEIGHT_SPEAKS_AGAINST = 6;
	public static final int WEIGHT_PARENT = 7;  //an item higher up in the hierarchy

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
	//private long sourceId;
	//private long targetId; 
	private long learnerCnxId;
	private long expCnxId;
	public MultiEdge(){}
	public MultiEdge(int type, int weight){
		addParam(type, weight);
	}
	public MultiEdge(Map types){
		this.types = types;
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
	
	public long getLearnerCnxId() {return learnerCnxId;}
	public void setLearnerCnxId(long learnerCnxId) {this.learnerCnxId = learnerCnxId;}
	public long getExpCnxId() {return expCnxId;}
	public void setExpCnxId(long expCnxId) {this.expCnxId = expCnxId;}
	/**
	 * Changes can only be made for learners' scripts
	 */
	public void removeExplicitWeight(){
		if(types==null || types.get(new Integer(IllnessScriptInterface.TYPE_LEARNER_CREATED))==null) return;
		types.put(new Integer(IllnessScriptInterface.TYPE_LEARNER_CREATED),new Integer(WEIGHT_IMPLICIT));		
	}
	
	public void changeExplicitWeight(int newWeight){
		if(types==null || types.get(new Integer(IllnessScriptInterface.TYPE_LEARNER_CREATED))==null) return;
		types.put(new Integer(IllnessScriptInterface.TYPE_LEARNER_CREATED),new Integer(newWeight));		
	}
	
	public void removeWeight(int illScriptType){
		if(types==null || types.get(new Integer(illScriptType))==null) return;
		types.remove(new Integer(illScriptType));		
	}
	
	public boolean isExplicitLearnerEdge(){
		return isExplicitEdge(IllnessScriptInterface.TYPE_LEARNER_CREATED);
	}
	public boolean isExplicitExpertEdge(){
		return isExplicitEdge(IllnessScriptInterface.TYPE_EXPERT_CREATED);
	}
	
	private boolean isExplicitEdge(int type){
		if(types==null || types.get(new Integer(type))==null) return false;
		int weight = types.get(new Integer(type)).intValue();
		if(weight>=2 && weight<=6) return true;
		return false;		
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
	public int getLearnerWeight(){
		return getParamByType(IllnessScriptInterface.TYPE_LEARNER_CREATED);
	}

	public int getExpertWeight(){
		return getParamByType(IllnessScriptInterface.TYPE_EXPERT_CREATED);
	}
	
	private int getParamByType(int illScriptType){
		Integer param = types.get(new Integer(illScriptType)); 
		if(param==null) return 0;
		return param.intValue();
	}
	public Map getParams(){ return types;}
	/*public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public long getTargetId() {return targetId;}
	public void setTargetId(long targetId) {this.targetId = targetId;}*/
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null && o instanceof MultiEdge){
			MultiEdge e = (MultiEdge) o;
			if(e.getSource().equals(getSource()) && e.getTarget().equals(getTarget())) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#getTarget()
	 */
	public MultiVertex getTarget(){return (MultiVertex) super.getTarget();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#getSource()
	 */
	public MultiVertex getSource(){return (MultiVertex) super.getSource();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultWeightedEdge#getWeight()
	 */
	public double getWeight(){return super.getWeight();}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.DefaultEdge#toString()
	 */
	public String toString(){
		return "edge: " + this.getSource().getLabel()+"-"+this.getTarget().getLabel()+" types: " + types.toString()+"\n";
	}
	
	public void mergeParams(Map<Integer, Integer> addtypes){
		if(addtypes==null) return;
		Iterator<Integer> it = addtypes.keySet().iterator();
		while(it.hasNext()){
			Integer key = it.next();
			Integer value = addtypes.get(key);
			if(types.get(key)==null) types.put(key, value); //no param for this key, so add
			else{
				//param added, but weaker than the new one:
				if(types.get(key).equals(new Integer(WEIGHT_IMPLICIT)) || types.get(key).equals(new Integer(WEIGHT_IMPLICIT )))
					types.put(key, value);				
			}
		}
	}
}
