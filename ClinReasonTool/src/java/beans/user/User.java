package beans.user;

import javax.faces.bean.SessionScoped;

import util.CRTLogger;
import util.Encoder;
import util.StringUtilities;

/**
 * A user object. We create it when a user comes from a VP system or when an admin logs in to edit an expert script.
 * @author ingahege
 *
 */
@SessionScoped
public class User {

	private long userId;
	/**
	 * id a user has in an external system
	 */
	private String extUserId;
	
	private String extUserId2; //decrypted external userId
	
	/**
	 * system the externalId is from (TODO could be more than one)
	 */
	private int systemId;
	
	/**
	 * currently only needed for login of admins/exp script editors
	 */
	private String userName;
	/**
	 * currently only needed for login of admins/exp script editors
	 */
	private String password;
	
	private boolean admin = false;
	
	/**
	 * if true, this user is allowed to edit or create expert scripts
	 */
	private boolean editor = false;

	private UserSetting userSetting = new UserSetting();
	
	public User(){}
	public User(int systemId, String extUserId){
		this.systemId = systemId;
		this.extUserId =extUserId;
		this.extUserId2 = decodeUserId(extUserId);
	}
	
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public String getExtUserId() {return extUserId;}
	public void setExtUserId(String extUserId) {this.extUserId = extUserId;}
	public int getSystemId() {return systemId;}
	public void setSystemId(int systemId) {this.systemId = systemId;}
	public boolean isEditor() {return editor;}
	public void setEditor(boolean editor) {this.editor = editor;}
	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	public UserSetting getUserSetting() {return userSetting;}
	public void setUserSetting(UserSetting userSetting) {this.userSetting = userSetting;}	
	public String getExtUserId2() {return extUserId2;}
	public void setExtUserId2(String extUserId2) {this.extUserId2 = extUserId2;}	
	public boolean isAdmin() {return admin;}
	public void setAdmin(boolean admin) {this.admin = admin;}
	
	private String decodeUserId(String extUserId){
		try{
			return new Encoder().decodeQueryParam(extUserId);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e) , CRTLogger.LEVEL_ERROR);
			return extUserId;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof User){
			if(this.getUserId() == ((User)o).getUserId()) return true;
		}
		return false;
	}
}
