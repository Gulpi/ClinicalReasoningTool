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
	//feedback setting?
	
}
