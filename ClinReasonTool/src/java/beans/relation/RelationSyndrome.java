package beans.relation;

import beans.list.*;
/**
 * @author ingahege
 * ListItems that belong to a syndrome (e.g. ProblemRelation)
 */
/**
 * @author ingahege
 *
 */
public class RelationSyndrome {

	/**
	 * relation id (=parent)
	 */
	private long syndromeId;
	
	/**
	 * unique id of the relation
	 */
	private long id;
	
	/**
	 * item that belongs to a syndrome.
	 */
	private long listItemIdChild;
	
	private ListItem listItem;

	public long getSyndromeId() {return syndromeId;}
	public void setSyndromeId(long syndromeId) {this.syndromeId = syndromeId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getListItemIdChild() {return listItemIdChild;}
	public void setListItemIdChild(long listItemIdChild) {this.listItemIdChild = listItemIdChild;}
	public ListItem getListItem() {return listItem;}
	public void setListItem(ListItem listItem) {this.listItem = listItem;}
	
	
}
