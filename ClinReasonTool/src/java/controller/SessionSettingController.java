package controller;

import beans.user.SessionSetting;
import database.DBUser;

/**
 * Handles everything around loading and applying SessionSetting objects. Each userId - vpId combination has a 
 * sessionSetting object...
 * @author ingahege
 *
 */
public class SessionSettingController {
	static private SessionSettingController instance = new SessionSettingController();
	static public SessionSettingController getInstance() { return instance; }
	
	/**
	 * We check whether the parent VP system has included some session settings we might need to consider. 
	 * If not we use the default settings. 
	 * We try to get the sessionSetting from the database to make sure that if user has started a session, he can continue with 
	 * the same settings.
	 */
	public SessionSetting initSessionSettings(String vpId, long userId){
		DBUser dbu = new DBUser();
		SessionSetting sessSetting = dbu.selectSessionSettingByUserAndVPId(userId, vpId);
		if(sessSetting!=null){
			sessSetting.setExpHintDisplayed(false); //we set it here to false, to make sure that it is displayed even if learner has stopped the session before
			return sessSetting;
		}
		
		sessSetting = new SessionSetting(vpId , userId);
		initSessSetting(sessSetting);
		dbu.saveAndCommit(sessSetting);
		return sessSetting;
	}
	
	/**
	 * Check which settings are defined by a parent VP system and put them into the SessionSetting object.
	 * @param sessSetting
	 */
	private void initSessSetting(SessionSetting sessSetting){
		sessSetting.setExpFeedbackMode(AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_EXP_FB_MODE, 0));
		sessSetting.setPeerFeedbackMode(AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_PEER_FB_MODE, 0));
		sessSetting.setDdxMode(AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_DDX_MODE, 0));

		//....
	}
}	

