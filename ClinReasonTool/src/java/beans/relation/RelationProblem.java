package beans.relation;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.*;

import beans.graph.VertexInterface;
import controller.ConceptMapController;
import model.ListItem;

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
	private long step; //when was item created, e.g. cardId of case
	
	private ListItem problem;
	
	public RelationProblem(){}
	public RelationProblem(long listItemId, long destId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
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
	public void setProblem(ListItem problem) {this.problem = problem;}		
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ return ConceptMapController.PREFIX_PROB+this.getId();}
	
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
		StringBuffer sb = new StringBuffer();		
		sb.append("{\"label\":\""+this.getProblem().getName()+"\",\"shortlabel\":\""+this.getProblem().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+"}");		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see beans.graph.VertexInterface#getVertexId()
	 */
	public long getVertexId() {
		return this.getProblem().getItem_id();
	}
	
	/* (non-Javadoc)
	 * @see beans.graph.VertexInterface#getVertextype()
	 */
	public int getVertextype() {
		return TYPE_PROBLEM;
	}
	
	/* (non-Javadoc)
	 * @see beans.graph.VertexInterface#getLabel()
	 */
	public String getLabel(){
		return problem.getName();
	}
}
