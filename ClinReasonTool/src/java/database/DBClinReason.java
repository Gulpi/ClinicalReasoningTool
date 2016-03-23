package database;

import java.beans.Beans;
import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.*;
import beans.PatientIllnessScript;
import model.ListItem;
import util.StringUtilities;

public class DBClinReason extends HibernateUtil{
    
    /**
     * saves an object into the database. Object has to have a hibernate mapping!
     * @param o
     */
    public void saveAndCommit(Object o){
    	Session s = getSession();
    	
        try {
        	beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            s.saveOrUpdate(o);
            HibernateUtil.commitTransaction(s);
        }
        
        catch(Exception e){
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	 rollBackTx();
        }
    }
    
    /**
     * Saves/updates a collection of objects into the database
     * @param o
     */
    public void saveAndCommit(Collection o) {
    	Session s = getSession();
        try {
        	beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);   //commit will be done after insert!
            //tx = s.beginTransaction();
            Iterator it = o.iterator();
            while(it.hasNext())
            {
            	s.saveOrUpdate(it.next());
            }
            HibernateUtil.commitTransaction(s);
        }
        catch (Exception e) {
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
            rollBackTx();
        }
        finally { }      
    }
    /**
     * Delete a collection from the database
     * @param o
     */
    public void deleteAndCommit(Collection c){
    	Session s = getSession();   	
        try {
        	beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            Iterator it = c.iterator();
            while(it.hasNext())
            {
            	s.delete(it.next());
            }
            HibernateUtil.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	 rollBackTx();
        }   	
    }  
    
    /**
     * Delete a Bean from the database
     * @param o
     */
    public void deleteAndCommit(Object o){
    	Session s = getSession();   	
        try {
        	beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            s.delete(o);
            HibernateUtil.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	 rollBackTx();
        }   	
    }
    
    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptBySessionId(long sessionId){
    	Criteria criteria = HibernateUtil.getSession().createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("sessionId", new Long(sessionId)));
    	return (PatientIllnessScript) criteria.uniqueResult();
    }

    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptById(long id){
    	Criteria criteria = HibernateUtil.getSession().createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("id", new Long(id)));
    	return (PatientIllnessScript) criteria.uniqueResult();
    }
    /**
     * Select the PatientIllnessScripts for the userId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectPatIllScriptsByUserId(long userId){
    	Criteria criteria = HibernateUtil.getSession().createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	return (List<PatientIllnessScript>) criteria.list();
    }
    
    /**
     * Select the ListItem with the given id from the database.
     * @param id
     * @return ListItem or null
     */
    public ListItem selectListItemById(long id){
    	Criteria criteria = HibernateUtil.getSession().createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("item_id", new Long(id)));
    	return (ListItem) criteria.uniqueResult();
    }
    

}
