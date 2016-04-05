package beans.relation;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import beans.graph.VertexInterface;
import model.ListItem;
import model.Synonym;

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
	public static final int TYPE_CNX = 5;

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
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimun 2 stages (before & after diagnosis submission)
	 */
	public int getStage();
	/**
	 * If the user has chosen a synonym of the main ListItem we return it here, otherwise null.
	 * @return
	 */
	public Synonym getSynonym(); 
	public Set<Synonym> getSynonyma();
	public long getSynId();
	public String getIdWithPrefix();
	
	public String getLabelOrSynLabel();
	public String getShortLabelOrSynShortLabel();
}
