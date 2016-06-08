package beans;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import database.DBEditing;
import beans.scripts.*;
/**
 * all expert scripts, needed for the overview/portfolio page to display a list. 
 */
@ManagedBean(name = "expport", eager = true)
@SessionScoped
public class ExpPortfolio implements Serializable{

	private static final long serialVersionUID = 1L;
	private long userId;
	private List<PatientIllnessScript> expscripts;

	public ExpPortfolio(){
		loadScripts();
	}
	
	public ExpPortfolio(long userId){
		this.userId = userId;
		loadScripts();
	}
	
	
	public List<PatientIllnessScript> getExpscripts() {return expscripts;}


	/**
	 * TODO: later on we have to consider the userId to load only scripts that are editable by the current user.
	 */
	private void loadScripts(){
		if(expscripts==null) expscripts = new DBEditing().selectAllExpertPatIllScripts();
	}
}
