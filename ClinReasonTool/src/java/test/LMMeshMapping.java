package test;

/**
 * mapping of the "old" longmenu lists to the mesh list 
 * @author ingahege
 *
 */
public class LMMeshMapping {

	private long id; //dummy id
	private String lmName;
	private String meshName;
	private long meshId;
	private long lmId;
	public String getLmName() {
		return lmName;
	}
	public void setLmName(String lmName) {
		this.lmName = lmName;
	}
	public String getMeshName() {
		return meshName;
	}
	public void setMeshName(String meshName) {
		this.meshName = meshName;
	}
	public long getMeshId() {
		return meshId;
	}
	public void setMeshId(long meshId) {
		this.meshId = meshId;
	}
	public long getLmId() {
		return lmId;
	}
	public void setLmId(long lmId) {
		this.lmId = lmId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
