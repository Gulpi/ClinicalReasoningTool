package beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContextWrapper;

import controller.AjaxController;
import controller.IllnessScriptController;
import controller.NavigationController;
import util.CRTLogger;

/**
 * Handling the portfolio display (currently we need its request scoped to be able to register 
 * changes to the request params (e.g. user_id) 
 * @author ingahege
 * @deprecated (?)
 */
@ManagedBean(name = "port", eager = true)
@ViewScoped
public class Portfolio implements Serializable{

	private static final long serialVersionUID = 1L;
	//TODO we have now these two variables as duplicates here and in the CRTFacesContext....
	private long userId;
	//private List<PatientIllnessScript> scriptsOfUser;
	
	public Portfolio(){
		CRTFacesContext crtContext = new NavigationController().getCRTFacesContext();
		setUserId();
		if(userId>0 ){
			//loadAndSetScriptsOfUser();
			//crtContext.loadAndSetScriptsOfUser(); //this loads all scripts, we do not necessarily have to do that here, only if overview page is opened!
			if(crtContext!=null){
				//crtContext.setUserId(userId);
				crtContext.initScriptContainer();
				new NavigationController().removePatIllScript();
			}
		}
	}
	
	//public List<PatientIllnessScript> getScriptsOfUser() {
	//	return null; //scriptsOfUser;
		/*if(new NavigationController().getCRTFacesContext()!=null) 
			return new NavigationController().getCRTFacesContext().getScriptsOfUser();	
		return null;*/
	//}
	
	private void setUserId(){
		if(userId>0) return;
		String setUserIdStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_USER);
		if(setUserIdStr!=null) this.userId = (Long.valueOf(setUserIdStr).longValue());
		else{
			CRTLogger.out("Userid is null", CRTLogger.LEVEL_ERROR);
			//FacesContextWrapper.getCurrentInstance().addMessage("",new FacesMessage(FacesMessage.SEVERITY_ERROR, "userid is null",""));
		}
	}
	public long getUserId(){ return userId;}
	
	/*private void loadAndSetScriptsOfUser(){
		scriptsOfUser = new IllnessScriptController().loadScriptsOfUser();
	}*/

}
