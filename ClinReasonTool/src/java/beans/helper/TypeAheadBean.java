package beans.helper;

import beans.scripts.PatientIllnessScript;
import controller.AjaxController;
import controller.NavigationController;
import database.DBClinReason;

/**
 * History of what a user has typed in before selecting an item from the list (or canceling)
 * useful for list improvement and maybe also learning analytics (how much does a learner try & error)
 * @author ingahege
 *
 */
public class TypeAheadBean {

	private long userId;
	/**
	 * What the user has typed in into the search field before selecting something from the list or canceling the action. 
	 * each type/delete step is seperated by "#". 
	 */
	private String typeaheadText;
	private String vpId; 
	private long scriptId;
	/**
	 * the id of the finally selected item or -1 if nothing has been selected.
	 */
	private long finalItemId = -1;
	/**
	 * internal id to avoid complex composite id...
	 */
	private long id;
	
	/**
	 * Problem, DDX, ... (see definition in Relation class)
	 */
	private int type;
	
	
	public TypeAheadBean(long finalItem, int type){
		this.finalItemId = finalItem;
		PatientIllnessScript patillscript = NavigationController.getInstance().getPatientIllnessScript();
		if(patillscript!=null){
			this.userId = patillscript.getUserId();
			this.scriptId = patillscript.getId();
			this.vpId = patillscript.getVpId();
		}
		this.typeaheadText = AjaxController.getInstance().getRequestParamByKey("typehistory");
		this.type = type;
		
	}
	/**
	 * Constructor when search from list was not successful, the the finalItemId is -1 and we 
	 * get the type of action (e.g. problem (=1), ddx(=2) as String value. 
	 * @param typeStr
	 */
	public TypeAheadBean(String typeStr){
		this((long)-1,Integer.valueOf(typeStr).intValue());
	}
	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public String getTypeaheadText() {return typeaheadText;}
	public void setTypeaheadText(String typeaheadText) {this.typeaheadText = typeaheadText;}
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public long getScriptId() {return scriptId;}
	public void setScriptId(long scriptId) {this.scriptId = scriptId;}
	public long getFinalItemId() {return finalItemId;}
	public void setFinalItemId(long finalItemId) {this.finalItemId = finalItemId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}		
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}

	/**
	 * Save if we have something in the typeAheadHistory...
	 */
	public void save(){
		if(getTypeaheadText()!=null && !getTypeaheadText().trim().equals(""))
			new DBClinReason().saveAndCommit(this);
	}
}
