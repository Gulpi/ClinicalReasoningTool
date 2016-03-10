package beanActions;

import beans.relation.Relation;

public interface DelAction {

	void save(Relation rel);
	/**
	 * A log entry for the delete action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	
	/**
	 * Delete the bean from the database.
	 * @param id
	 */
	void delete(String id);
}
