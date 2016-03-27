package actions.beanActions;

import java.beans.Beans;
import javax.faces.application.FacesMessage.Severity;

import beans.relation.Relation;

public interface AddAction{

	void save(Beans b);
	/**
	 * A log entry for the add action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	void add(String id, String name);
	//void initScoreCalc(Relation rel);
	void createErrorMessage(String summary, String details, Severity sev);
}
