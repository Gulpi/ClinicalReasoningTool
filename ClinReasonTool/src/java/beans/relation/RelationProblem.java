package beans.relation;

import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import controller.ConceptMapController;
import controller.RelationController;
import model.ListItem;
import model.Synonym;

/**
 * Relation between an (Patient-)IllnessScript and a problem. We need this to specify a problem, e.g. whether it is 
 * almost proving a diagnosis or rarely occurs with a diagnosis.
 * We might need more qualifiers,...
 * @author ingahege
 *
 */
public class RelationProblem extends Beans implements Relation, Rectangle, Serializable{

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
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	private long synId;
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
	
	private Timestamp creationDate;
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimun 2 stages (before & after diagnosis submission)
	 */
	private int stage; //when was item created, e.g. cardId of case
	
	private ListItem problem;
	/**
	 * If a user has selected a synonym instead of the main item, we store this here in addition to the main item.
	 */
	
	public RelationProblem(){}
	public RelationProblem(long listItemId, long destId, long synId){
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
	public ListItem getProblem() {return problem;}
	public ListItem getListItem() {return getProblem();}
	public void setProblem(ListItem problem) {this.problem = problem;}		
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}		
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}	
	public long getSynId() {return synId;}
	public void setSynId(long synId) {this.synId = synId;}
	
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ 
		/*if(synId<=0)*/ return ConceptMapController.PREFIX_PROB+this.getId();
		//return ConceptMapController.PREFIX_PROB+synId;
	}
	
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationProblem && ((RelationProblem)o).getListItemId()==this.listItemId && ((RelationProblem)o).getDestId()==this.destId)
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
		if(synId<=0)
			sb.append("{\"label\":\""+this.getProblem().getName()+"\",\"shortlabel\":\""+this.getProblem().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+"}");		
		else{
			sb.append("{\"label\":\""+this.getSynonym().getName()+"\",\"shortlabel\":\""+this.getSynonym().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+"}");		

		}
		return sb.toString();*/
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_PROBLEM;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return problem.getName();}
	
	public Synonym getSynonym(){
		return new RelationController().getSynonym(this.synId,this);
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return problem.getSynonyma();}
	public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}
}
