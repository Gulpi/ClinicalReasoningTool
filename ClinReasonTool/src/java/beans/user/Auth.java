package beans.user;

import java.io.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import beans.AdminFacesContext;
import beans.CRTFacesContext;
import controller.AjaxController;
import database.DBUser;
import util.CRTLogger;
import util.Encoder;
import util.StringUtilities;

@ManagedBean
@ViewScoped
public class Auth implements Serializable{
    private String username;
    private String password;
    private String originalURL;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        //String tmp = ((HttpServletRequest) externalContext.getRequest()).getRequestDispatcher();
        originalURL = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);

        if (originalURL == null) {
            originalURL = externalContext.getRequestContextPath() + "/src/html/edit/exp_scripts.xhtml";
        } else {
            String originalQuery = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_QUERY_STRING);

            if (originalQuery != null) {
                originalURL += "?" + originalQuery;
            }
        }
    }

    //private UserService userService;

    public void login() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        
        try {
        	User user = new DBUser().selectUserByLogin(username);
        	if(user!=null && compareEncrPwd(password, user.getPassword())){
        		AdminFacesContext cnxt =  (AdminFacesContext) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AdminFacesContext.CRT_FC_KEY);
        		if(cnxt!=null) cnxt.setUser(user);
        		externalContext.getSessionMap().put("user", user);
        		externalContext.redirect(originalURL);
        	}
        	else handleError();
        } 
        catch (Exception e) {
        	CRTLogger.out("Auth.login(): " + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
            handleError();
        }
    }
    
    /** 
 	 * Handle unknown username/password in request.login().
     */
    private void handleError(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	context.addMessage(null, new FacesMessage("Unknown login/Pwd"));
    }
    
    /**
     * We compare the entered password (non-encrypted) with the encrypted password stored in the database.
     * @param enteredPwd
     * @param encPwd
     * @return
     */
    private boolean compareEncrPwd(String enteredPwd, String encPwd){
    	if(enteredPwd==null || enteredPwd.trim().equals("")) return false;
    	//if(encPwd==null || encPwd.trim().equals("")) return false;
    	try{
	    	String encEnteredPwd = Encoder.getInstance().encodeQueryParam(enteredPwd);
	    	if(encEnteredPwd.equals(encPwd)) return true;
	    	return false;
    	}
    	catch(Exception e){
    		return false;
    	}  	
    }

    public void logout() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
}
