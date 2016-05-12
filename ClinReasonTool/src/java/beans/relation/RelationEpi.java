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
public class RelationEpi extends Relation implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_X = 5; //default x position of problems in canvas
	
	private ListItem epi;
	
	public RelationEpi(){}
	public RelationEpi(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}

	public ListItem getEpi() {return epi;}
	public ListItem getListItem() {return getEpi();}
	public void setEpi(ListItem epi) {this.epi = epi;}		
	public String getIdWithPrefix(){ 
		/*if(synId<=0)*/ return GraphController.PREFIX_EPI+this.getId();
	}
	
	/*public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationEpi && ((RelationEpi)o).getListItemId()==getListItemId() && ((RelationEpi)o).getDestId()==getDestId())
				return true;
		}
		return false;
	}*/
	
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
}
