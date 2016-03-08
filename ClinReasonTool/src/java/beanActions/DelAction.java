package beanActions;

import beans.relation.Relation;

public interface DelAction {

	void save();
	/**
	 * A log entry for the move action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	
	void delete();
}
