package beans.relation;
import java.awt.Point;
import java.beans.Beans;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import beans.graph.VertexInterface;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;

//import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The relation of an object
 * to an (Patient) IllnessScript. 
 * @author ingahege
 */
public abstract class Relation extends Beans{

	public static final int TYPE_PROBLEM = 1;
	public static final int TYPE_DDX = 2;
	public static final int TYPE_TEST = 3;
	public static final int TYPE_MNG = 4;
	public static final int TYPE_CNX = 5;
	public static final int TYPE_EPI = 6;

	private long id;
	
	/**
	 * can be problem, test, management, diagnosis
	 */
	private long listItemId;
	/**
	 * (Patient)Illnesscript
	 */
	private long destId; 
	
	/**
	 * x position of the problem in the concept map canvas
	 */
	private int x;
	/**
	 * y position of the problem in the concept map canvas
	 */
	private int y;
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimun 2 stages (before & after diagnosis submission)
	 */
	private int stage;
	
	private int order;
	private Timestamp creationDate;
	
	/**
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	private long synId;

	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	public abstract ListItem getListItem();
	public abstract String getLabel();
	/**
	 * =PatientIllnessScriptId or IllnessScriptId
	 * @return
	 */
	public long getDestId(){return destId;} 
	public void setDestId(long destId){this.destId = destId;}
	//public abstract int getOrder(); 
	//spublic abstract void setOrder(int o); 	
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	
	public abstract int getRelationType();
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}	
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimun 2 stages (before & after diagnosis submission)
	 */
	//public abstract int getStage();
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}

	//public abstract Synonym getSynonym(); 
	public abstract Set<Synonym> getSynonyma();
	//public abstract long getSynId();
	public long getSynId() {return synId;}
	public void setSynId(long synId) {this.synId = synId;}

	public abstract String getIdWithPrefix();
	
	public abstract String getLabelOrSynLabel();
	//public abstract String getShortLabelOrSynShortLabel();
	
	public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}
	/**
	 * If the user has chosen a synonym of the main ListItem we return it here, otherwise null.
	 * @return
	 */
	public Synonym getSynonym(){ return new RelationController().getSynonym(getSynId(),this);}

	
	public String getShortLabelOrSynShortLabel(){return StringUtils.abbreviate(getLabelOrSynLabel(), ListItem.MAXLENGTH_NAME);}
	public String getScore(){ return new ScoringController().getIconForScore(this.getRelationType(), this.getListItemId());}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null && o instanceof Relation){
			Relation rel = (Relation)o;
			if(rel.getId()==id) return true;
			if(rel.getListItemId() == this.listItemId && rel.getRelationType() == this.getRelationType()) return true;
		}
		return false;
	}
}
