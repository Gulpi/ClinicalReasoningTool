package beanActions;

import java.beans.Beans;

import beans.relation.Relation;

public interface ChgAction {
	/**
	 * A log entry for the change action is created and saved in a Log object
	 */
	void notifyLog(Beans rel, long newId);
	
	void save(Beans rel);
}
