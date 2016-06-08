package beans.scripts;

import java.io.Serializable;
import java.util.*;
import database.DBClinReason;

/**
 * all scripts of the user, needed for the overview/portfolio page to display a list. 
 * TODO: we only need id and a name, so maybe we do not have to load the full objects? or get 
 * them from view?
 * We load it from the portfolio view and also here (in case user does not come from portfolio)
 */
public class PatIllScriptContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	private long userId;
	private List<PatientIllnessScript> scriptsOfUser;
	
	public PatIllScriptContainer(long userId){
		this.userId = userId;
	}
	
	public void loadScriptsOfUser(){
		if(scriptsOfUser==null) scriptsOfUser = new DBClinReason().selectPatIllScriptsByUserId(userId);
	}
}
