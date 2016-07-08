package beans.user;

/**
 * A user object 
 * @author ingahege
 *
 */
public class User {

	private long userId;
	/**
	 * id a user has in an external system
	 */
	private String extUserId;
	/**
	 * system the externalId is from (TODO could be more than one)
	 */
	private int systemId;
	
	public User(){}
	public User(int systemId, String extUserId){
		this.systemId = systemId;
		this.extUserId = extUserId;
	}
	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public String getExtUserId() {return extUserId;}
	public void setExtUserId(String extUserId) {this.extUserId = extUserId;}
	public int getSystemId() {return systemId;}
	public void setSystemId(int systemId) {this.systemId = systemId;} 	
	
}
