package beans.relation;
import java.sql.Timestamp;

//import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The relation of an object
 * to an (Patient) IllnessScript. 
 * @author ingahege
 */
public interface Relation /*extends DefaultWeightedEdge*/{

	
	/*private long id = -1;
	 * private long sourceId = -1; //can be problem, test, management, diagnosis
	private long destId = -1; //(Patient)Illnesscript
	private int orderNum = -1;*/
	
	/**
	 * where was this item added to the PIS? Can be either cardId or a stage-type (e.g. AFTER_INITIALDATA)
	 */
	//private long stage = -1;	//only for PIS
	
	//private Timestamp creationDate; //probably only for PIS
	/**
	 * If we allow deleting of e.g. problems for a PIS we could save that it was deleted?
	 */
	//private int deleteFlag = -1; 

	/*public Relation(){}
	public Relation(long sourceId, long destId){
		this.sourceId = sourceId;
		this.destId = destId;
	}*/
	public long getSourceId(); // {return sourceId;}
	public void setSourceId(long sourceId); // {this.sourceId = sourceId;}
	public long getDestId(); // {return destId;}
	public void setDestId(long destId); // {this.destId = destId;}
		
}
