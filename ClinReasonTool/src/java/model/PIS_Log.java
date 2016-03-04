package model;
import java.sql.Timestamp;
import java.util.*;

/**
 * In this class all changes to a Patient IllnessScript are tracked
 * @author ingahege
 *
 */
public class PIS_Log {
	public static final int TYPE_UPDATE_SUMMST = 1; //summary statement updated
	public static final int TYPE_SUBMIT_SUMMST = 2; //(final) summary statement submitted
	public static final int TYPE_UPDATE_PROBLEM = 3; //problem updated (if we allow editing)
	public static final int TYPE_UPDATE_DIAGNOSIS = 4; //diagnosis updated (if we allow editing)	
	public static final int TYPE_DELETE_PROBLEM = 5;
	public static final int TYPE_DELETE_DIAGNOSIS = 6;
	
	private long patientIllnessScriptId = -1;
	/**
	 * What has been done? see static TYPE definitions
	 */
	private int type = -1; 
	/**
	 * When was it done (only learner PIS)
	 */
	private Timestamp date;
	
	/**
	 * at which card/type of card was change made
	 */
	private int stepInProcess = -1;
	
	/**
	 * The text before the update process
	 */
	private String oldText;
}
