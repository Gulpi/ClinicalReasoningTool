package beanActions;

import java.util.*;
import java.beans.Beans;

import beans.relation.Relation;

/**
 * The order of the problems was changed, so we reorder the problems list accordingly and save the 
 * new order to the database.
 * @author ingahege
 *
 */
public interface MoveAction {
	/**
	 * A log entry for the move action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	
	void save(List l);
	
	/**
	 * reorder the problems list 
	 * @param newOrderStr (format: selProb=12654&selProb=578&selProb=4655&selProb=717&selProb=6327)
	 */
	void reorder(String newOrderStr);
	
}
