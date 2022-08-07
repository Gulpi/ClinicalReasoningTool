package controller;

import java.util.Locale;

import application.AppBean;
import beans.ContextContainer;
import beans.user.User;
import database.DBContext;
import net.casus.util.Utility;
import util.CRTLogger;

public class ContextController {
	
	static private ContextController instance = new ContextController();
	static public ContextController getInstance() { return instance; }

	public ContextContainer initExpertContainer(long vpId) {
		ContextContainer expContainer = new ContextContainer(vpId, 2);
		expContainer.setActors(new DBContext().selectExpertActorsByVpId(vpId));
		expContainer.setCtxts(new DBContext().selectExpertCtxtsByVpId(vpId));
		return expContainer;
	}
	
	/**
	 * we load or initiate the context factors the author has already added
	 */
	/*public boolean getInitContextContainer() {
		long vpId = Long.parseLong(AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_VP));
		if(contxts!=null && contxts.getVpId()==vpId) return true; //already loaded
		
		contxts = new ContextContainer(vpId, 2);
		contxts.setActors(new DBContext().selectExpertActorsByVpId(vpId));
		contxts.setCtxts(new DBContext().selectExpertCtxtsByVpId(vpId));
		return true;
	}*/
	public ContextContainer initContextContainer(ContextContainer contxts, User user, Locale locale, int type) {
		try {
			long vpId = Long.parseLong(AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_VP));
			
	
			if(contxts!=null && contxts.getUserId()==user.getExtUserIdLong() && contxts.getVpId() == vpId) return contxts; //already loaded
			//not yet loaded, so create and try to load from database:
			contxts = new ContextContainer(user.getExtUserIdLong(), vpId, locale.getLanguage(), type);
			
			contxts.setActors(new DBContext().selectActorsByUserIdAndVpId(user.getExtUserIdLong(), vpId));
			contxts.setCtxts(new DBContext().selectCtxtsByUserIdAndVpId(user.getExtUserIdLong(), vpId));
			AppBean.addExpertContextsForVpId(vpId);
			return contxts;
		}
		catch(Exception e) {
			CRTLogger.out(Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return null;
		}
	}
}
