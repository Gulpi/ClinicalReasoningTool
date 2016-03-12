package beanActions;

import java.util.*; 
import beans.relation.Relation;
import database.DBClinReason;

public class ActionHelper {

	/**
	 * after deleting an item from the list the list has to be reordered and all items saved. 
	 */
	public void reOrderItems(List items){
		if(items==null || items.isEmpty()) return;
		for(int i=0; i<items.size(); i++){
			Relation rel = (Relation) items.get(i);
			rel.setOrder(i);
		}
		//new DBClinReason().saveAndCommit(items);
	}
}
