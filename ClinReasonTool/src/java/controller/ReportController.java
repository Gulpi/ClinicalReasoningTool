package controller;

import java.util.*;

import beans.scripts.PatientIllnessScript;
import database.DBClinReason;
import net.casus.model.course.Course;
import util.Encoder;
import util.StringUtilities;

/**
 * Handles all stuff around displaying the learner's scripts, either on an individual or on an overall basis.
 * 
 * @author ingahege
 *
 */
public class ReportController {
	static private ReportController instance = new ReportController();
	static public ReportController getInstance() { return instance; }

	public List<PatientIllnessScript> getLearnerScriptsForVPId(String vpId){
		if(vpId==null) return null;
		return new DBClinReason().selectLearnerPatIllScriptsByVPId(vpId);
	}
	
	/**
	 * we get all scripts for all the vps and users in the Casus course
	 * @return
	 */
	public   List<PatientIllnessScript> getLearnerScriptsForCourse(Course c){
		if(c==null || c.getStudents()==null || c.getCases()==null) return null;
		//put all userIds as Strings into an array
		String[] extUserId = getUserIdsInCourse(c.getStudents());
		DBClinReason dbc = new DBClinReason();
		List<PatientIllnessScript> mapsForCourse = new ArrayList();
		
		Iterator it = c.getCases().keySet().iterator();
		//go thru all cases and get the scripts for all users in the array:
		while (it.hasNext()) {
			Long vpId = (Long) it.next();
			String vpIdStr = vpId.longValue()+"_2";
			List scripts = dbc.selectPatIllScriptsByExtUserIdsAndVpId(extUserId, vpIdStr);
			if(scripts!=null && !scripts.isEmpty()) {
				mapsForCourse.addAll(scripts);
			}
		}
		return mapsForCourse;
		
	}
	
	
	/**
	 * gets the encoded extUserId for all users in the map 
	 * CAVE: We might have to get the CRT userId from the database, if not all scripts have the extUserId in 
	 * the database
	 * 
	 * @param csr
	 * @return
	 */
	private String[] getUserIdsInCourse(Map csr) {
		if(csr==null || csr.isEmpty()) return null;
		String[] extUserIds = new String[csr.size()];
		Iterator it = csr.keySet().iterator();
		int counter = 0;
		while(it.hasNext()) {
			 	Long casusUserId = (Long) it.next();
			 	String extUserId  = Encoder.getInstance().encodeQueryParam(String.valueOf(casusUserId.longValue()));
			 	extUserIds[counter] = extUserId;
			 	counter++;
		} 
		return extUserIds;
	}
}
