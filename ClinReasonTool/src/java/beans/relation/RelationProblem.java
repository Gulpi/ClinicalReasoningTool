package beans.relation;

import java.io.Serializable;

/**
 * Relation between an (Patient-)IllnessScript and a problem. We need this to specify a problem, e.g. whether it is 
 * almost proving a diagnosis or rarely occirs with a diagnosis.
 * We might need more qualifiers,...
 * @author ingahege
 *
 */
public class RelationProblem implements Relation, Serializable{

	public static final int QUALIFIER_RARE = 0; 
	public static final int QUALIFIER_MEDIUM = 1;
	public static final int QUALIFIER_OFTEN = 2;
	
	private long sourceId = -1; //can be problem, test, management, diagnosis
	private long destId = -1; //(Patient)Illnesscript
	/**
	 * problems: key-finding, other,... (?)
	 */
	private int value = -1; //key finding,...
	
	/**
	 * how often is a problem prevalent in a diagnosis (rare, medium, often)
	 */
	private int qualifier = -1;
	
	public RelationProblem(){}
	public RelationProblem(long sourceId, long destId){
		this.setSourceId(sourceId);
		this.setDestId(destId);
	}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public long getDestId() {return destId;}
	public void setDestId(long destId) {this.destId = destId;}	
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationProblem && ((RelationProblem)o).getSourceId()==this.sourceId && ((RelationProblem)o).getDestId()==this.destId)
				return true;
		}
		return false;
	}
}
