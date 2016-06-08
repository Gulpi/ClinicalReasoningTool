package database;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import beans.scripts.*;

public class DBEditing extends DBClinReason{

	   /**
     * Select the PatientIllnessScript of the expert, which is identified by the parentId and type from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectAllExpertPatIllScripts(){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	//criteria.add(Restrictions.eq("parentId", new Long(parentId)));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_EXPERT_CREATED)));
    	List<PatientIllnessScript> patIllScripts = criteria.list();
    	s.close();
    	/*if(patIllScript!=null)
    		patIllScript.setSummSt(loadSummSt(patIllScript.getSummStId()));*/
    	return patIllScripts;  	
    }
    
    public PatientIllnessScript selectExpertPatIllScriptById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("id", new Long(id)));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_EXPERT_CREATED)));
    	PatientIllnessScript patIllScript = (PatientIllnessScript) criteria.uniqueResult();
    	s.close();
    	if(patIllScript!=null)
    		patIllScript.setSummSt(loadSummSt(patIllScript.getSummStId()));
    	return patIllScript;  	
    }
}
