package beans.relation;
import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

import org.apache.commons.lang3.StringUtils;

import controller.ConceptMapController;
import controller.GraphController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;
/**
 * connects a Diagnosis object to a (Patient)IllnessScript object with some attributes.
 * @author ingahege
 */
public class RelationDiagnosis extends Relation implements Rectangle, Serializable {

	private static final long serialVersionUID = 1L;
	public static final int REL_TYPE_FINAL = 1; //final diagnosis (for PIS only)
	public static final int REL_TYPE_DDX = 2; //differential diagnosis 
	public static final int REL_TYPE_COMPL = 3; //complication of IS diagnosis 
	public static final int REL_TYPE_RELATED = 4; //otherwise related diagnosis 
	public static final int DEFAULT_X = 165; //default x position of problems in canvas
	//public static final String COLOR_DEFAULT = "#ffffff";
	//public static final String COLOR_RED = "#990000";
	public static final int TIER_NONE = 0; //I do not now
	public static final int TIER_NOTLIKELY = 3; //Clinically low likelihood
	public static final int TIER_LIKELY = 2; //Clinically moderate likelihood
	public static final int TIER_MOSTLIKELY = 1; //Clinically high likelihood
	public static final int TIER_FINAL = 4; //Final diagnosis

	//private long id;
	/**
	 * diagnosis id
	 */
	//private long listItemId; 
	/**
	 * (Patient)Illnesscript
	 */
	//private long destId; 
	
	//private int order;
		
	/**
	 * -1 = not stated, todo: define levels here (slider with Percentage?)
	 */
	private int confidence = -1; //we need levels here (only for PIS)

	/**
	 * how likely is this diagnosis, from unlikely to final diagnosis
	 * see tier definitions above
	 */
	private int tier = -1; 
	
	/**
	 * diagnoses: doNotMiss/lethal/important favorite
	 */
	//private int value = -1; //key finding,...
	
	//private Timestamp creationDate;
	//private int x;
	//private int y;
	/**
	 * Backgroundcolor of the diagnosis rectangle
	 */
	//private String color; //default: #ffffff
	private ListItem diagnosis;
	/**
	 * Must-not-miss
	 */
	private int mnm = 0;
	//private int stage;
	/**
	 * In case the learner has selected the not the main item, but a synonyma, we save the id here.
	 * We do not need the object, since it is already stored in the ListItem 
	 */
	//private long synId;
	
	public RelationDiagnosis(){}
	public RelationDiagnosis(long lisItemId, long destId, long synId){
		this.setListItemId(lisItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}
	//public long getListItemId() { return listItemId;}
	//public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	//public long getId() {return id;}
	//public void setId(long id) {this.id = id;}
	//public long getDestId() {return destId;}
	//public void setDestId(long destId) {this.destId = destId;}
	//public int getOrder() {return order;}
	//public void setOrder(int order) {this.order = order;}
	public int getTier() {return tier;}
	public void setTier(int tier) {this.tier = tier;}
	public ListItem getDiagnosis() {return diagnosis;}
	public void setDiagnosis(ListItem diagnosis) {this.diagnosis = diagnosis;}	
	/*public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	*/
	//public String getColor() {return color;}
	//public void setColor(String color) {this.color = color;}	
	/*public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}*/
	
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ return GraphController.PREFIX_DDX+this.getId();}
	public int getMnm() {return mnm;}
	public void setMnm(int mnm) {this.mnm = mnm;}
	public boolean isMnM(){
		if(mnm==1) return true;
		return false;
	}
	/* (non-Javadoc)
	 * @see beans.relation.Rectangle#toJson()
	 */
	/*public String toJson(){
		return new RelationController().getRelationToJson(this);

	}*/
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationDiagnosis && ((RelationDiagnosis)o).getListItemId()==getListItemId() && ((RelationDiagnosis)o).getDestId()==getDestId())
				return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_DDX;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return diagnosis.getName();}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getListItem()
	 */
	public ListItem getListItem() {return diagnosis;}
	/*public Synonym getSynonym(){
		return new RelationController().getSynonym(getSynId(),this);
	}*/
	//public long getSynId() {return synId;}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return diagnosis.getSynonyma();}
	/*public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}*/
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(getSynId()<=0) return diagnosis.getName();
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
