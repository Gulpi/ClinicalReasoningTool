package database;

import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.*;

import beans.LogEntry;

public class DBLog  extends DBClinReason {

	
	/**
	 * We get all log entries for a patientIllnessScript and return them in a list
	 * @param patIllScriptId
	 * @return
	 */
	public List<LogEntry> getLogsForScript(long patIllScriptId){
		Session s = instance.getInternalSession(Thread.currentThread(), false);
		Criteria criteria = s.createCriteria(LogEntry.class, "LogEntry");
		criteria.add(Restrictions.eq("patIllscriptId", new Long(patIllScriptId)));
		return (List<LogEntry>) criteria.list();
	}
	
	/**
	 * Get the number of attempts for submitting a final diagnosis
	 * @param s
	 * @param patIllScriptId
	 * @return
	 */
	public int getNumOfFinalDiagnosisAttempts( long patIllScriptId) {
		Session s = instance.getInternalSession(Thread.currentThread(), false);
		Criteria criteria = s.createCriteria(LogEntry.class, "LogEntry");
		criteria.add(Restrictions.eq("patIllscriptId", new Long(patIllScriptId)));
		criteria.add(Restrictions.eq("action", new Integer(LogEntry.SUBMITDDX_ACTION)));
		List<LogEntry> attempts = criteria.list();
		if(attempts==null) return 0;
		return attempts.size();
		
	}
}
