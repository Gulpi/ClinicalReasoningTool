package beans;

import java.io.Serializable;

import javax.faces.bean.SessionScoped;

/**
 * User specific settings we want to store in the database for later sessions. 
 * Other settings, such as the current open tab can be handled via cookies. 
 * @author ingahege
 *
 */
@SessionScoped
public class UserSetting implements Serializable{
	
	/**
	 * We store here when we create a new user, in order to display certain hints and explanations for him.
	 */
	private boolean isNewUser = false; 
	private boolean displayOwnEntryWarn = false;
	private boolean openHelpOnLoad = false;
	//feedback setting?

	public boolean getIsNewUser() {return isNewUser;}
	public void setIsNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}
	public boolean getDisplayOwnEntryWarn() {return displayOwnEntryWarn;}
	public void setDisplayOwnEntryWarn(boolean bool) {this.displayOwnEntryWarn = bool;}	
	public boolean getOpenHelpOnLoad() {return openHelpOnLoad;}
	public void setOpenHelpOnLoad(boolean openHelpOnLoad) {this.openHelpOnLoad = openHelpOnLoad;}
	
	public void initNewUser(){
		isNewUser = true;
		displayOwnEntryWarn = true;
		openHelpOnLoad = true;
	}
	
}
