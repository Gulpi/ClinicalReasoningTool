package application;

import java.util.Locale;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;

import controller.AjaxController;

public class CRTViewHandler extends ViewHandlerWrapper{

	private ViewHandler parent; 

	public CRTViewHandler(ViewHandler vh){
		this.parent = vh;
	}
	public ViewHandler getWrapped() {
		return parent; //FacesContext.getCurrentInstance().getApplication().getViewHandler();
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext fc){
		Locale loc = null; 
		if(FacesContext.getCurrentInstance().getViewRoot()!=null) 
			loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(loc==null) loc = new Locale(AppBean.DEFAULT_LOCALE);
		String locStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_LOC);
		if(locStr!=null && !locStr.trim().equals("")){ 
			for(int i=0; i<AppBean.ACCEPTED_LOCALES.length; i++){
			if(locStr.equals(AppBean.ACCEPTED_LOCALES[i]))
					loc = new Locale(locStr);
			}
		}
		
		return loc;
	}
}
