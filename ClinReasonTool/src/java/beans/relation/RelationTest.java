package beans.relation;

import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import controller.ConceptMapController;
import controller.GraphController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;

/**
 * Relation between an (Patient-)IllnessScript and a problem. We need this to specify a problem, e.g. whether it is 
 * almost proving a diagnosis or rarely occurs with a diagnosis.
 * We might need more qualifiers,...
 * @author ingahege
 *
 */
public class RelationTest extends Relation implements Rectangle, Serializable{

	private static final long serialVersionUID = 1L;
	public static final int QUALIFIER_RARE = 0; 
	public static final int QUALIFIER_MEDIUM = 1;
	public static final int QUALIFIER_OFTEN = 2;
	public static final int DEFAULT_X = 165; //245; //default x position of problems in canvas
	
	
	//private long id;
	/**
	 * can be problem, test, management, diagnosis
	 */
	//private long listItemId; 
	/**
	 * (Patient)Illnesscript
	 */
	//private long destId; 
	
	//private int order;
	
	/**
	 * x position of the problem in the concept map canvas
	 */
	//private int x;
	/**
	 * y position of the problem in the concept map canvas
	 */
	//private int y;
	
	//also include height/width
	/**
	 * problems: key-finding, other,... (?)
	 */
	private int value; //key finding,...
	
	/**
	 * how often is a problem prevalent in a diagnosis (rare, medium, often)
	 */
	private int qualifier;
	
	//private int stage;
	
	//private Timestamp creationDate;
	
	private ListItem test;
	/**
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	//private long synId;
	
	public RelationTest(){}
	public RelationTest(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}	
	public ListItem getTest() {return test;}
	public void setTest(ListItem test) {this.test = test;}		
	public String getIdWithPrefix(){ return GraphController.PREFIX_TEST+this.getId();}
	

	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_TEST;}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return test.getName();}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getListItem()
	 */
	public ListItem getListItem(){return test;}

	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return test.getSynonyma();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(getSynId()<=0) return test.getName();
		else return getSynonym().getName();
	}
}
