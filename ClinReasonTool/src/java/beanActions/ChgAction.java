package beanActions;

import beans.relation.Relation;

public interface ChgAction {
	/**
	 * A log entry for the move action is created and saved in a Log object
	 */
	void notifyLog(Relation rel, long newId);
	
	void save(Relation rel);
}
