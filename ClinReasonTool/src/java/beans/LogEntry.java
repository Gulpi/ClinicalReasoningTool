package beans;

import java.beans.*;
import java.sql.Timestamp;

import controller.NavigationController;
import database.DBClinReason;

/**
 * We save all the actions in a log table. 
 * TODO: we need a second variable for some actions, such as addCnx, move
 * @author ingahege
 *
 */
public class LogEntry extends Beans{

	public static final int ADDPROBLEM_ACTION = 1;
	public static final int ADDTEST_ACTION = 17;
	public static final int ADDMNG_ACTION = 18;
	public static final int ADDDIAGNOSIS_ACTION = 5;
	public static final int ADDEPI_ACTION = 29;
	public static final int ADDCONNECTION_ACTION = 8; 

	public static final int DELPROBLEM_ACTION = 2;
	public static final int DELDIAGNOSIS_ACTION = 6;
	public static final int DELMNG_ACTION = 15;
	public static final int DELTEST_ACTION = 16;
	public static final int DELCONNECTION_ACTION = 9; 
	public static final int DELEPI_ACTION = 30;
	public static final int DELCNXAFTERSTARTNODE_ACTION = 11; //a connection is deleted because the related start point has been deleted
	public static final int DELCNXAFTERTARGETNODE_ACTION = 12; //a connection is deleted because the related target point has been deleted
	
	public static final int CHGPROBLEM_ACTION = 10; 
	public static final int CHGDDX_ACTION = 13; 
	public static final int CHGDDXMNM_ACTION = 14; //change MnM flag
	public static final int CHGDDXTIER_ACTION = 35; //change tier flag

	public static final int CHGCOURSETIME_ACTION = 3; 
	public static final int CHGTEST_ACTION = 21; 
	public static final int CHGMNG_ACTION = 22; 
	public static final int CHGEPI_ACTION = 31;
	public static final int CHGCNXWEIGHT_ACTION = 33;
	
	public static final int MOVEPROBLEM_ACTION = 4; 	
	public static final int MOVETEST_ACTION = 19; 
	public static final int MOVEMNG_ACTION = 20; 
	public static final int MOVEDIAGNOSIS_ACTION = 7; 
	public static final int DRAGDROP_ACTION = 34;
	
	public static final int CRTPATILLSCRIPT_ACTION = 23;
	public static final int CLOSEPATILLSCRIPT_ACTION = 24;
	
	public static final int CREATESUMMST_ACTION = 25;
	public static final int UPDATESUMMST_ACTION = 26;
	public static final int CREATENOTE_ACTION = 27;
	public static final int UPDATENOTE_ACTION = 28;
	public static final int SUBMITDDX_ACTION = 32;
	
	public static final int FEEDBACK_ON_ACTION = 36;
	public static final int FEEDBACK_OFF_ACTION = 37;
	public static final int CHG_CONFIDENCE_ACTION = 38;
	public static final int CHG_COURSEOFTIME_ACTION = 39;
	
	
	/**
	 * action triggered by user, e.g. add a problem,... (see static definitions above)
	 */
	private int action;
	/**
	 * Id of the log entry (internal purposes)
	 */
	private long id;
	private Timestamp creationDate; //automatically set in the database
	private long patIllscriptId; 
	private long sessionId;
	//private long patIllScriptId; //necessary? We have the sessionId, but might be quicker to access.
	/**
	 * E.g. Id of the problem or diagnosis added/removed,... new value for courseOfTime
	 */
	private long sourceId;
	/**
	 * e.g. orderNr for problem/diagnosis or targetId for Cnx 
	 */
	private long sourceId2;
	
	private int stage;
	//private long oldValue;
	
	public LogEntry(){}
	public LogEntry(int action, long patIllscriptId, long sourceId){
		this.action = action; 
		this.patIllscriptId = patIllscriptId;
		this.sourceId = sourceId;
	}
	
	public LogEntry(int action, long patIllscriptId, long sourceId, long sourceId2){
		this.action = action; 
		this.patIllscriptId = patIllscriptId;
		this.sourceId = sourceId;
		this.sourceId2 = sourceId2;
	}
	
	public int getAction() {return action;}
	public void setAction(int action) {this.action = action;}
	public Timestamp getCreationDate() {return creationDate;}
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	//public long getOldValue() {return oldValue;}
	//public void setOldValue(long oldValue) {this.oldValue = oldValue;}	
	
	public long getId() {return id;}
	public long getSessionId() {return sessionId;}
	public void setSessionId(long sessionId) {this.sessionId = sessionId;}
	public void setId(long id) {this.id = id;}	
	public long getPatIllscriptId() {return patIllscriptId;}
	public void setPatIllscriptId(long patIllscriptId) {this.patIllscriptId = patIllscriptId;}	
	public long getSourceId2() {return sourceId2;}
	public void setSourceId2(long sourceId2) {this.sourceId2 = sourceId2;}	
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	private void setStageBasedOnCurrentStage(){
		stage = new NavigationController().getCRTFacesContext().getPatillscript().getCurrentStage();
	}
	public void save(){
		setStageBasedOnCurrentStage();
		new DBClinReason().saveAndCommit(this);
	}
		
}
