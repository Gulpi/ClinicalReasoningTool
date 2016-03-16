package beans.relation;
import java.sql.Timestamp;

//import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The relation of an object
 * to an (Patient) IllnessScript. 
 * @author ingahege
 */
public interface Relation /*extends DefaultWeightedEdge*/{


	public long getSourceId(); 
	public void setSourceId(long sourceId); 
	public long getDestId(); 
	public void setDestId(long destId);
	public int getOrder(); 
	public void setOrder(int o); 	
	public long getId();
	//better to extend Rectangle?

}
