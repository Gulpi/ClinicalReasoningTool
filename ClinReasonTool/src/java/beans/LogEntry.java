package beans;

import java.beans.*;
import java.sql.Timestamp;

import database.DBClinReason;

public class LogEntry extends Beans{

	public static final int ADDPROBLEM_ACTION = 1;
	public static final int DELPROBLEM_ACTION = 2;
	public static final int CHGCOURSETIME_ACTION = 3; 
	//....
	
	/**
	 * action triggered by user, e.g. add a problem,... (see static definitions above)
	 */
	private int action;
	private long id;
	private Timestamp creationDate; //automatically set in the database
	private long sessionId; 
	private long patIllScriptId; //necessary? We have the sessionId, but might be quicker to access.
	/**
	 * E.g. Id of the problem or diagnosis added/removed,... new value for courseOfTime
	 */
	private long sourceId;
	private long oldValue;
	
	public LogEntry(){}
	public LogEntry(int action, long sessionId, long sourceId){
		this.action = action; 
		this.sessionId = sessionId;
		this.sourceId = sourceId;
	}
	
	public int getAction() {return action;}
	public void setAction(int action) {this.action = action;}
	public Timestamp getCreationDate() {return creationDate;}
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public long getOldValue() {return oldValue;}
	public void setOldValue(long oldValue) {this.oldValue = oldValue;}	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	
	public void save(){
		new DBClinReason().saveBean(this);
	}
		
}
