package controller;

import java.util.*;

import beans.LogEntry;
import database.DBLog;
/**
 * handles everything around LogEntries
 * @author ingahege
 *
 */
public class LogEntryController {

	static private LogEntryController instance = new LogEntryController();
	static public LogEntryController getInstance() { return instance; }
	
	public List<LogEntry> getLogEntriesForScript(long scriptId){
		return new DBLog().getLogsForScript(scriptId);
	}
	
	
}
