package actions.beanActions;

import java.beans.Beans;
import javax.faces.application.FacesMessage.Severity;

import beans.relation.Relation;

public interface AddAction{
	public static final int ADD_TYPE_MAINITEM = 1;
	public static final int ADD_TYPE_SYNITEM = 2;
	
	void save(Beans b);
	/**
	 * A log entry for the add action is created and saved in a Log object
	 */
	void notifyLog(Relation rel);
	/**
	 * Called when an item is added thru the list view
	 * @param id
	 * @param name
	 */
	void add(String id, String name);
	/**
	 * called when item is added thru the concept map (then we have a position of the item in the map)
	 * @param idStr either an id or syn_id (for a synonym)
	 * @param name
	 * @param xStr  (e.g. "199.989894") -> we have to convert it into int
	 * @param yStr
	 */
	void add(String idStr, String name, String xStr, String yStr);
	//void initScoreCalc(Relation rel);
	void createErrorMessage(String summary, String details, Severity sev);
	void updateGraph(Relation rel);
	//void add(long id, String name, int x, int y);
	void addRelation(long id, String name, int x, int y, long synId);
}
