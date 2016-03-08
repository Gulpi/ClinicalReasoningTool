package beanActions;

import beans.relation.Relation;

public interface MoveAction {
	/**
	 * A log entry for the move action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	
	void save();
	
	void delete(String id);
	
}
