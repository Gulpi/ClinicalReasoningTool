package controller;

import java.util.Locale;

import javax.faces.context.FacesContext;

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
		if(cnxt==null) return defaulLoc;
		return cnxt.getViewRoot().getLocale();
	}
	
    public void setLocale(Locale loc) {
		CRTFacesContext cnxt =  (CRTFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.CRT_FC_KEY);
		cnxt.getViewRoot().setLocale(loc);

    }
    
    public void setScriptLocale(Locale loc){
    	setLocale(loc);
    }
}
