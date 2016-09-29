package controller;

import java.util.Locale;

import javax.faces.context.FacesContext;

import application.AppBean;
import beans.CRTFacesContext;

/**
 * Handles the setting/getting of the Locale. We have two kinds of LOcale - the language of the script and the language of the navigation elements. 
 * Currently we consider the locale of the script (which we get from the VP system) also as locale for navigation elements.
 * @author ingahege
 *
 */
public class LocaleController {

	static private LocaleController instance = new LocaleController();
	static public LocaleController getInstance() { return instance; }
	private static Locale defaulLoc = new Locale("en");
	
	public static Locale getLocale(){
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		return cnxt.getLocale();
		
		//return getLocale(cnxt);
	}
	
	/*public static Locale getLocale(CRTFacesContext cnxt){
		if(cnxt==null || cnxt.getViewRoot()==null || cnxt.getViewRoot().getLocale()==null || cnxt.getViewRoot().getLocale()==null) 
			return setLocale();
		Locale loc = cnxt.getViewRoot().getLocale();
		return cnxt.getViewRoot().getLocale();
	}*/
	
    /*private void setLocale(Locale loc) {
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		cnxt.setLocale(loc);
    }*/
    
    public Locale getScriptLocale(){
    	String locStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_SCRIPTLOC);
    	if(isAcceptedLocale(locStr)) return new Locale(locStr);
    	return getLocale();
    }
    
    public static Locale setLocale(){
		Locale loc = null; 
		if(FacesContext.getCurrentInstance().getViewRoot()!=null) 
			loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(loc==null) loc = new Locale(AppBean.DEFAULT_LOCALE);
		
		String locStr = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_LOC);
		if(isAcceptedLocale(locStr)) loc = new Locale(locStr);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(loc);
		//CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		//if(cnxt!=null && cnxt.getViewRoot()!=null) cnxt.getViewRoot().setLocale(loc);

		return loc;
    }
    
    /*public void setScriptLocale(Locale loc){
    	setLocale(loc);
    }*/
    
    private static boolean isAcceptedLocale(String locStr){
		if(locStr!=null && !locStr.trim().equals("")){ 
			for(int i=0; i<AppBean.ACCEPTED_LOCALES.length; i++){
			if(locStr.equals(AppBean.ACCEPTED_LOCALES[i]))
					return true;
			}
		}
		return false;
    }
}
