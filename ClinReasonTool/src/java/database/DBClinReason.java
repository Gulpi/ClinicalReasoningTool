package database;

import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.*;
import beans.PatientIllnessScript;

public class DBClinReason extends HibernateUtil{

    public String save(Object o)
    {
    	return save(o, getSession());
    }
    /**
     * Saves an object to the database (decides whether an update or insert has to
     * be done). In case of an error, an error message will be returned 
     * @param o Object to save
     * @return errormessage or null
     */   
    public String save(Object o, Session s) 
    {
    	if (s.getTransaction() == null || !s.getTransaction().isActive()) {
    		Transaction tx = beginTransaction(s);
    	}
        try {
            s.setFlushMode(FlushMode.COMMIT);   //commit will be done after insert!            
            s.saveOrUpdate(o);
            return null; //sucessful saved
        }
        catch (org.hibernate.exception.ConstraintViolationException ve)
        {
        	rollBackTx();
        	 //Logger.serious("DBClinReason.save() ", "Exception1: " + Utility.stackTraceToString(ve));
        	return "Not unique!";
        }
        catch (Exception x) {
            //Logger.serious("DBClinReason.save() ", "Exception2: " + Utility.stackTraceToString(x));
            rollBackTx();
            return "Error while saving!";
        }
        finally { }      
    }
    
    public String saveAndCommit(Object o){
    	Session s = getSession();
    	
        try {
        	beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            s.saveOrUpdate(o);
            HibernateUtil.commitTransaction(s);
            return null; //"Erfolgreich gespeichert";
        }
        
        catch(Exception e){
        	// Logger.serious("DBCourse.saveAndCommit() ", "Exception: " + Utility.stackTraceToString(e));
        	 rollBackTx();
        	 return "Fehler beim Speichern!";
        }
    }
    
    public PatientIllnessScript selectPatIllScriptBySessionId(long sessionId){
    	Criteria criteria = HibernateUtil.getSession().createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("sessionId", new Long(sessionId)));
    	PatientIllnessScript patis = (PatientIllnessScript) criteria.uniqueResult();
    	return patis;
    }
    
	/**
	 * Save the Object to the database.
	 * @param bean
	 */
	public void saveBean(Object bean) {
		Session s = getSession();
		beginTransaction(s);
		save(bean,s);
		commitTransaction(s);     
	}
}
