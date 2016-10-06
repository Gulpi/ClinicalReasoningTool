package controller;

import beans.user.User;
import database.DBUser;

public class UserController {

	
	private User createAndSaveUser(int systemId, String extUserId){
		User u = new User(systemId, extUserId);
		u.getUserSetting().initNewUser();
		new DBUser().saveAndCommit(u);
		
		return u;
	}
	
	public User getUser(int systemId, String extUserId){
		DBUser dbu = new DBUser();
		User u = dbu.selectUserByExternalId(extUserId, systemId);
		if(u!=null) return u;
		return createAndSaveUser(systemId, extUserId);
 	}
	
}
