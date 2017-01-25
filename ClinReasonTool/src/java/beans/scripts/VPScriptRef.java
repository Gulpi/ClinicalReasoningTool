package beans.scripts;

/**
 * Contains information about the relation between a VP and the script.
 * currently the vpId is the parentId of the script, but this is not unique across systems...
 * @author ingahege
 *
 */
public class VPScriptRef {

	private long internalId;
	/**
	 * needed for displaying VP name for the charts
	 */
	private String vpName; 
	
	private long vpId; 
	
	private int systemId;
	
	/**
	 * This is the vpId for the illnessScripts, which is unique across different systems.
	 */
	private String parentId;

	public VPScriptRef(){}
	public VPScriptRef(String parentId, String vpName, int systemId, String vpId){
		this.parentId = parentId;
		this.vpName = vpName;
		this.systemId = systemId;
		//todo convert vpId and store as well - really necessary?
		
	}
	public long getVpId() {return vpId;}
	public long getInternalId() {return internalId;}
	public void setInternalId(long internalId) {this.internalId = internalId;}
	public void setVpId(long vpId) {this.vpId = vpId;}
	public String getVpName() {return vpName;}
	public void setVpName(String vpName) {this.vpName = vpName;}
	public int getSystemId() {return systemId;}
	public void setSystemId(int systemId) {this.systemId = systemId;} 
	
	public String getParentId() {return parentId;}
	public void setParentId(String parentId) {this.parentId = parentId;}
	
	public boolean equals(Object o){
		if(o instanceof VPScriptRef){
			if(((VPScriptRef) o).getSystemId() == systemId && ((VPScriptRef) o).getVpId()==vpId) return true;
		}
		return false;
	}
	
}
