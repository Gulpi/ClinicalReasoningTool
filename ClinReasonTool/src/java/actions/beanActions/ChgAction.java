package actions.beanActions;

import java.beans.Beans;

import beans.relation.Relation;

public interface ChgAction {
	/**
	 * A log entry for the change action is created and saved in a Log object
	 */
	void notifyLog(Beans rel, long newId);
	
	void save(Beans rel);
	
	/**
	 * If the learner has changed an item (ie switched from a synonym to the main listItem we have to 
	 * -> we do not have to update, since only the Relation within a Vertex is changed
	 * @param rel
	 */
	//void updateGraph(Relation rel);
}
