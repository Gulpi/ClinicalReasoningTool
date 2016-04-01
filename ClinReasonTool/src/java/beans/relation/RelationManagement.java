package beans.relation;

import java.beans.Beans;
import java.io.Serializable;

import controller.ConceptMapController;
import model.ListItem;

public class RelationManagement extends Beans implements Relation, Rectangle, Serializable{
	
	public static final int DEFAULT_X = 200; //default x position of problems in canvas

	private static final long serialVersionUID = 1L;
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
	
	private ListItem management;
	
	public RelationManagement(){}
	public RelationManagement(long listItemId, long destId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
	}
		
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}
	public ListItem getManagement() {return management;}
	public void setManagement(ListItem management) {this.management = management;}
	public void setId(long id) {this.id = id;}	
	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	public long getDestId() {return destId;}
	public void setDestId(long destId) {this.destId = destId;}
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}
	public long getId() {return id;}
	public String getIdWithPrefix(){ return ConceptMapController.PREFIX_MNG+this.getId();}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationManagement && ((RelationManagement)o).getListItemId()==this.listItemId && ((RelationManagement)o).getDestId()==this.destId)
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Rectangle#toJson()
	 */
	public String toJson(){
		StringBuffer sb = new StringBuffer();		
		sb.append("{\"label\":\""+this.getManagement().getName()+"\",\"shortlabel\":\""+this.getManagement().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+"}");		
		return sb.toString();
	}
	

	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_MNG;}	

	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return management.getName();}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getListItem()
	 */
	public ListItem getListItem() {return management;}
	
}
