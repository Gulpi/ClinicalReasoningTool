package beans.relation;
import java.sql.Timestamp;
import java.util.*;

import beans.graph.VertexInterface;
import model.ListItem;

//import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The relation of an object
 * to an (Patient) IllnessScript. 
 * @author ingahege
 */
public interface Relation{

	public static final int TYPE_PROBLEM = 1;
	public static final int TYPE_DDX = 2;
	public static final int TYPE_TEST = 3;
	public static final int TYPE_MNG = 4;

	/**
	 * ListItemId
	 * @return
	 */
	public long getListItemId(); 
	public void setListItemId(long lisItemId); 
	public ListItem getListItem();
	public String getLabel();
	/**
	 * =PatientIllnessScriptId or IllnessScriptId
	 * @return
	 */
	public long getDestId(); 
	public void setDestId(long destId);
	public int getOrder(); 
	public void setOrder(int o); 	
	public long getId();
	public int getRelationType();


}
