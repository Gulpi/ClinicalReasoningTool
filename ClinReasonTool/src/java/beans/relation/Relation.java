package beans.relation;
import java.awt.Point;
import java.beans.Beans;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.graph.MultiVertex;
import beans.graph.VertexInterface;
import controller.FeedbackController;
import controller.NavigationController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;
import properties.IntlConfiguration;

//import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The relation of an object
 * to an (Patient) IllnessScript. 
 * @author ingahege
 */
public abstract class Relation extends Beans implements Rectangle{

	public static final int TYPE_PROBLEM = 1;
	public static final int TYPE_DDX = 2;
	public static final int TYPE_TEST = 3;
	public static final int TYPE_MNG = 4;
	public static final int TYPE_CNX = 5;
	public static final int IS_SYNDROME = 1;
	public static final int IS_SYNDROME_PART = 2;
	
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
	 * the API), minimum 2 stages (before & after diagnosis submission)
	 */
	private int stage;
	
	private int order;
	private Timestamp creationDate;
	/**
	 * To allow negative findings (e.g. No fever), we need a prefix indicator.
	 */
	private String prefix;
	
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
	 * if isSyndrome is set, we add the related items here...
	 */
	private Set<RelationSyndrome> syndromItems;
	/**
	 * =PatientIllnessScriptId or IllnessScriptId
	 * @return
	 */
	public long getDestId(){return destId;} 
	public void setDestId(long destId){this.destId = destId;}	
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	/**
	 * if we have the box layout, we have to add some px to the y...
	 * @return
	 */
	public int getBoxY() {
		return y + addToY;
	}
	public void setY(int y) {this.y = y;}	
	public abstract int getRelationType();
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}		
	public String getPrefix() {return prefix;}
	public void setPrefix(String prefix) {this.prefix = prefix;}
	
	/**
	 * Currently all prefixes are a negation, if expanding this to e.g. include qualifiers such as "acute/chronic" etc.
	 * we would have to expand the prefix with a qualifier.
	 * @return
	 */
	public boolean hasPrefix(){
		if(prefix!=null && !prefix.trim().equals("") && !prefix.trim().equals("0")) return true;
		return false;
	}
	/**
	 * When during a session was the item added (e.g. on which card number, if provided by 
	 * the API), minimum 2 stages (before & after diagnosis submission)
	 */
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}

	public abstract Set<Synonym> getSynonyma();
	public long getSynId() {return synId;}
	public void setSynId(long synId) {this.synId = synId;}
	public Set<RelationSyndrome> getSyndromItems() {return syndromItems;}
	public void setSyndromItems(Set<RelationSyndrome> syndromItems) {this.syndromItems = syndromItems;}

	public abstract String getIdWithPrefix();
	
	public abstract String getLabelOrSynLabel();
	
	public void setXAndY(Point p){
		this.setX(p.x);
		this.setY(p.y);
	}
	/**
	 * If the user has chosen a synonym of the main ListItem we return it here, otherwise null.
	 * @return
	 */
	public Synonym getSynonym(){ return new RelationController().getSynonym(getSynId(),this);}
	public boolean getIsSynonyma(){
		if(synId>0) return true;
		return false;
	}
	public boolean getIsExpertHierarchyItem(){
		String expLabel = getExpItemLabel();
		if(expLabel==null || expLabel.isEmpty() || expLabel.trim().equals("")) return false;
		return true;
	}
	
	public String getShortLabelOrSynShortLabel(){
		return StringUtils.abbreviate(getLabelOrSynLabel(), ListItem.MAXLENGTH_NAME);
	}
	/**
	 * @return 0 (incorrect), 1 (correct), or 2 (partly correct)
	 */
	public int getScore(){ 
		return new ScoringController().getExpScore(this.getRelationType(), this.getListItemId());
	}

	public String getFeedback(){ return new FeedbackController().getItemFeedback(this.getRelationType(), this.getListItemId());}
	public String getExpItemLabel(){ return new FeedbackController().getExpItemLabel(this.getRelationType(), this.getListItemId());}

	/**
	 * Is the learner allowed to change it?
	 * yes if he has chosen a synonym or a different hierarchy
	 * @return
	 */
	public boolean getChgAllowed(){
		//if(synId>0) return true;	//we allow changes for synonyma 
		return new FeedbackController().isChgAllowed(this.getRelationType(), this.getListItemId()); //and diff. hierarchies
		//return false;
	}

	public String getPeerPercentage(){return new ScoringController().getPeerPercentageForAction(getRelationType(), getDestId(), getListItemId());}
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() { try {
			return getLabelOrSynLabel()+ "("+getId()+")";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * If a node has a syndrome-connection to a syndrome it is part of it and shall be displayed in a different
	 * color.
	 * @return 0(not a syndrome part) | 1 (is part of a syndrome)
	 */
	private int getIsSyndromePart(){
		//if(this.getIsSyndrome()==1) return 0; //if it is a syndrome it cannot be a child!
		Graph g = NavigationController.getInstance().getMyFacesContext().getGraph();
		MultiVertex mv = g.getVertexByIdAndType(this.getListItemId(), this.getRelationType());
		if(mv==null) return 0;
		Set<MultiEdge> cnxs = g.getExplicitExpertEdges(mv);
		if(cnxs==null || cnxs.isEmpty()) return 0;
		Iterator<MultiEdge> it = cnxs.iterator();
		while(it.hasNext()){
			MultiEdge me = it.next();
			if(me.getExpertWeight()==MultiEdge.WEIGHT_SYNDROME) return Relation.IS_SYNDROME_PART;
		}
		return 0;
	}
	
	/**
	 * Node is a syndrome (has components), e.g. shock is a combination of hypotension, tachycardia, paleness,... 
	 * Typically this is related to problems/findings, but it could also be that we have such a component-relation
	 * for the other node types. Therefore, we have it here in the super class. 
	 * 0 = not syndrome-related, 1 = is syndrome, 2 = is part of syndrome 
	 * @return
	 */
	public int getIsSyndrome(){
		if(this.getListItem().getIsSyndrome()==Relation.IS_SYNDROME) return Relation.IS_SYNDROME;
		return getIsSyndromePart();
		
	}

}
