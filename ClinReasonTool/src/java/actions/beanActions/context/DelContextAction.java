package actions.beanActions.context;

import java.beans.Beans;

import beans.ContextContainer;
import beans.LogEntry;
import beans.context.Context;
import beans.context.ContextFactor;
import database.DBContext;
import util.CRTLogger;
import util.StringUtilities;

public class DelContextAction {
	private ContextContainer cc;
	
	public DelContextAction(ContextContainer cc) {
		this.cc = cc;
	}
	
	/**
	 * We look for the actor with the given id in the ContextContainer and if 
	 * found, we delete it from the container and the database. 
	 * @param idStr
	 */
	public void delete(long id) {
		if (id<=0) return;
		try {
			Context c = cc.getContextById(id);
			if(c !=null) {
				cc.removeContextFromList(c);
				new DBContext().deleteAndCommit(c);	
			}
		}
		catch (Exception e) {
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}

	public void notifyLog(ContextFactor cf) {
		new LogEntry(LogEntry.DEL_CONTEXT_ACTION, cf.getId(), cf.getListItemId()).save();
	}
}
