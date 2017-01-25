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
	
	private static final long serialVersionUID = 1L;
	/**
	 * We store here when we create a new user, in order to display certain hints and explanations for him.
	 */
	private boolean isNewUser = false; 
	/**
	 * if user enters an own entry for the first time, we warn him that those own entries cannot be scored.
	 */
	private boolean displayOwnEntryWarn = false;
	/**
	 * if user opens a script for the first time, we display the open help dialog
	 */
	private boolean openHelpOnLoad = false;
	/**
	 * if user has added two different types of items for the first time we display a hint for the connections
	 */
	private boolean displayCnxHint = false;
	//feedback setting?

	public boolean getIsNewUser() {return isNewUser;}
	public void setIsNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}
	public boolean getDisplayOwnEntryWarn() {return displayOwnEntryWarn;}
	public void setDisplayOwnEntryWarn(boolean bool) {this.displayOwnEntryWarn = bool;}	
	public boolean getOpenHelpOnLoad() {return openHelpOnLoad;}
	public void setOpenHelpOnLoad(boolean openHelpOnLoad) {this.openHelpOnLoad = openHelpOnLoad;}	
	public boolean isDisplayCnxHint() {return displayCnxHint;}
	public void setDisplayCnxHint(boolean displayCnxHint) {this.displayCnxHint = displayCnxHint;}
	
	public void initNewUser(){
		isNewUser = true;
		displayOwnEntryWarn = true;
		openHelpOnLoad = true;
		displayCnxHint = true;
	}
	
}
