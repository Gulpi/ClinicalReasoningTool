package actions.beanActions;

//simport beans.relation.Relation;

public interface DelAction {

	void save(Object rel);
	/**
	 * A log entry for the delete action is created and saved in a Log object
	 */
	void notifyLog(Object o);
	
	/**
	 * Delete the bean from the database.
	 * @param id
	 */
	void delete(String id);
}
