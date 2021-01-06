package beans.relation;

import java.io.Serializable;
import java.util.*;
import controller.GraphController;
import beans.list.*;
/**
 * connects a pathophysiology  object to a (Patient)IllnessScript object with some attributes.
 * @author ingahege
 */
public class RelationPatho extends Relation implements Serializable {
	
	private ListItem patho;
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_X = 15; //165; //245; //default x position of problems in canvas

	
	public RelationPatho(){}
	public RelationPatho(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}
	public ListItem getPatho() {return patho;}
	public ListItem getListItem() {return getPatho();}
	public void setPatho(ListItem patho) {this.patho = patho;}		
		
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_PATHO;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return patho.getName();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){	
		String postStr = "";
		
		if(getSynId()<=0) return patho.getName() + postStr;
		else return getSynonym().getName() + postStr;
	}

	public Set<Synonym> getSynonyma() {
		return patho.getSynonyma();
	}
	@Override
	public String getIdWithPrefix() {
		return GraphController.PREFIX_PATHO+this.getId();
	}
}