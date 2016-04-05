package beans.relation;

import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import controller.ConceptMapController;
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
public class RelationTest extends Beans implements Relation, Rectangle, Serializable{

	private static final long serialVersionUID = 1L;
	public static final int QUALIFIER_RARE = 0; 
	public static final int QUALIFIER_MEDIUM = 1;
	public static final int QUALIFIER_OFTEN = 2;
	public static final int DEFAULT_X = 5; //default x position of problems in canvas
	
	
	private long id;
	/**
	 * can be problem, test, management, diagnosis
	 */
	private long listItemId; 
	/**
	 * (Patient)Illnesscript
	 */
	private long destId; 
	
	private int order;
	
	/**
	 * x position of the problem in the concept map canvas
	 */
	private int x;
	/**
	 * y position of the problem in the concept map canvas
	 */
	private int y;
	
	//also include height/width
	/**
	 * problems: key-finding, other,... (?)
	 */
	private int value; //key finding,...
	
	/**
	 * how often is a problem prevalent in a diagnosis (rare, medium, often)
	 */
	private int qualifier;
	
	private int stage;
	
	//private Timestamp creationDate;
	
	private ListItem test;
	/**
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	private long synId;
	
	public RelationTest(){}
	public RelationTest(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) this.synId = synId;
	}
	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	public long getDestId() {return destId;}
	public void setDestId(long destId) {this.destId = destId;}	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}	
	public ListItem getTest() {return test;}
	public void setTest(ListItem test) {this.test = test;}		
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ return ConceptMapController.PREFIX_TEST+this.getId();}
	
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationTest && ((RelationTest)o).getListItemId()==this.listItemId && ((RelationTest)o).getDestId()==this.destId)
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Rectangle#toJson()
	 */
	public String toJson(){
		return new RelationController().getRelationToJson(this);
		/*StringBuffer sb = new StringBuffer();		
		sb.append("{\"label\":\""+this.getTest().getName()+"\",\"shortlabel\":\""+this.getTest().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+"}");		
		return sb.toString();*/
	}

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
	 * @see beans.relation.Relation#getSynonym()
	 */
	public Synonym getSynonym(){ return new RelationController().getSynonym(this.synId,this);}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return test.getSynonyma();}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynId()
	 */
	public long getSynId() {return synId;}
	public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(synId<=0) return test.getName();
		else return getSynonym().getName();
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getShortLabelOrSynShortLabel()
	 */
	public String getShortLabelOrSynShortLabel(){		
		return StringUtils.abbreviate(getLabelOrSynLabel(), ListItem.MAXLENGTH_NAME);
	}
	
	public String getScore(){
		return new ScoringController().getIconForScore(this.getListItemId());
		//sreturn "icon-ok2";
	}
}