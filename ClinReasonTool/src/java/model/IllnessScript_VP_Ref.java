package model;
/**
 * Each IllnessScript can be associated to mutiple cases, and one case can have multiple IllnessScripts. 
 * 
 * @author ingahege
 * @deprecated
 */
public class IllnessScript_VP_Ref {

	private final int LEVEL_MAIN = 1; //main diagnosis
	private final int LEVEL_SIDE = 2; //related diagnosis, but not main
	
	/**
	 * 1 = after early cues (1st card), 2 = after investigations (after tests card finished), 3 = after 
	 * diagnosis has been established, 4 = after management
	 */
	private int stage = -1;
	private long illnessScriptId = -1; //IS or PIS (?) at stage 4 (or even 3) it is an IS, before PIS
	private long caseId = -1;
	/**
	 * is it the main diagnosis of the case or a kind of "side" diagnosis related to the case
	 */
	private int level = -1;
	
	public long getIllnessScriptId() {return illnessScriptId;}
	public void setIllnessScriptId(long illnessScriptId) {this.illnessScriptId = illnessScriptId;}
	public long getCaseId() {return caseId;}
	public void setCaseId(long caseId) {this.caseId = caseId;}
	public int getLevel() {return level;}
	public void setLevel(int level) {this.level = level;}	
	
}
