package beans.relation;

import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import beans.scoring.ScoreContainer;
import controller.GraphController;
import controller.NavigationController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;

/**
 * Relation between an (Patient-)IllnessScript and an epidemiology/exposure condition. (e.g. smoking or age,...)
 * @author ingahege
 *
 */
public class RelationEpi extends Relation implements Rectangle, Serializable{

	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_X = 5; //default x position of problems in canvas
	
	//private long id;
	/**
	 * can be problem, test, management, diagnosis
	 */
	//private long listItemId; 
	
	/**
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	//private long synId;
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
	//private int value; //key finding,...
	
	/**
	 * how often is a problem prevalent in a diagnosis (rare, medium, often)
	 */
	//private int qualifier;
	
	//private Timestamp creationDate;
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimun 2 stages (before & after diagnosis submission)
	 */
	//private int stage; //when was item created, e.g. cardId of case
	
	private ListItem epi;
	/**
	 * If a user has selected a synonym instead of the main item, we store this here in addition to the main item.
	 */
	
	public RelationEpi(){}
	public RelationEpi(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}
	//public long getListItemId() {return listItemId;}
	//public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	//public long getDestId() {return destId;}
	//public void setDestId(long destId) {this.destId = destId;}	
	//public long getId() {return id;}
	//public void setId(long id) {this.id = id;}	
	//public int getOrder() {return order;}
	//public void setOrder(int order) {this.order = order;}	
	public ListItem getEpi() {return epi;}
	public ListItem getListItem() {return getEpi();}
	public void setEpi(ListItem epi) {this.epi = epi;}		
	/*public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	*/	
	/*public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}	*/
	//public long getSynId() {return synId;}
	//public void setSynId(long synId) {this.synId = synId;}
	
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ 
		/*if(synId<=0)*/ return GraphController.PREFIX_EPI+this.getId();
		//return ConceptMapController.PREFIX_PROB+synId;
	}
	
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationEpi && ((RelationEpi)o).getListItemId()==getListItemId() && ((RelationEpi)o).getDestId()==getDestId())
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_EPI;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return epi.getName();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(getSynId()<=0) return epi.getName();
		else return getSynonym().getName();
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getShortLabelOrSynShortLabel()
	 */
	public String getShortLabelOrSynShortLabel(){		
		return StringUtils.abbreviate(getLabelOrSynLabel(), ListItem.MAXLENGTH_NAME);
	}
	
	public Synonym getSynonym(){
		return new RelationController().getSynonym(getSynId(),this);
	}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return epi.getSynonyma();}
	/*public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}*/
	
	/*public String getScore(){
		return new ScoringController().getIconForScore(this.getListItemId());
		//sreturn "icon-ok2";
	}*/
}
