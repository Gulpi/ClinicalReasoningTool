package beanActions;

import java.beans.Beans;

import beans.relation.Relation;

public interface AddAction {

	void save(Beans b);
	/**
	 * A log entry for the add action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	void add(String id, String name);
	void initScoreCalc(Relation rel);
}
