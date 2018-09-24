package beans.scripts;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import beans.error.AvailabilityBias;
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
	/**
	 * all scripts of the user ordered desc by creation date
	 */
	private List<PatientIllnessScript> scriptsOfUser;
	
	public PatIllScriptContainer(long userId){
		this.userId = userId;
	}
	
	public void loadScriptsOfUser(){
		if(scriptsOfUser==null) scriptsOfUser = new DBClinReason().selectActivePatIllScriptsByUserId(userId);
	}
	
	public List<PatientIllnessScript> getScriptsOfUser(){return scriptsOfUser;}	
	public long getUserId() {return userId;}

	/**
	 * Can be used to determine an availability error based on the last num scripts... 
	 * @param num number of scripts to return
	 * @param cutoff optional cutoff date after which we do not include the scripts
	 * @return
	 */
	public List<PatientIllnessScript> getLastCompletedScripts(int num/*, Timestamp cutoff*/){
		long cutoffMillies = System.currentTimeMillis() - 1000*3600*24*AvailabilityBias.NUM_DAYS;
		Timestamp cutoff = new Timestamp(cutoffMillies);
		if(scriptsOfUser==null || scriptsOfUser.isEmpty()) return null;
		List<PatientIllnessScript> lastSubmittedScripts = new ArrayList<PatientIllnessScript>();
		Iterator<PatientIllnessScript> it = scriptsOfUser.iterator();
		int counter = 0;
		while(it.hasNext()){			
			PatientIllnessScript pi = it.next();	
			if(counter==num) break;
			if(cutoff!=null && pi.getLastAccessDate()!=null && pi.getLastAccessDate().before(cutoff)) break;
			
			if(pi.getSubmitted()){				
				lastSubmittedScripts.add(pi);
				counter ++;
			}
		}
		return lastSubmittedScripts;		
	}
	
	/*public PatientIllnessScript getScriptById(String vpId){
		if(scriptsOfUser==null || scriptsOfUser.isEmpty()) return null;
		Iterator<PatientIllnessScript> it = scriptsOfUser.iterator();
		while(it.hasNext()){			
			PatientIllnessScript pi = it.next();
			if(pi.getVpId().equals(vpId)) return pi;
		}
		return null;
	}*/
}
