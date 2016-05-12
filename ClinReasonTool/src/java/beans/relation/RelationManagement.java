package beans.relation;

import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import controller.ConceptMapController;
import controller.GraphController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;

public class RelationManagement extends Relation implements Serializable{
	
	public static final int DEFAULT_X = 245; //325; //default x position of problems in canvas

	private static final long serialVersionUID = 1L;
	private ListItem management;

	
	public RelationManagement(){}
	public RelationManagement(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}
		
	public ListItem getManagement() {return management;}
	public void setManagement(ListItem management) {this.management = management;}
	public String getIdWithPrefix(){ return GraphController.PREFIX_MNG+this.getId();}

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
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return management.getSynonyma();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(getSynId()<=0) return management.getName();
		else return getSynonym().getName();
	}
}
