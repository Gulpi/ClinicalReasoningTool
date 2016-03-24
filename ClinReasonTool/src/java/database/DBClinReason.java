package database;

import java.beans.Beans;
import java.util.*;

import javax.faces.context.FacesContext;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.*;

import beans.CRTFacesContext;
import beans.PatientIllnessScript;
import model.ListItem;
import util.StringUtilities;

public class DBClinReason /*extends HibernateUtil*/{
	static HibernateSession instance = new HibernateSession();
	//static SessionFactory sessionFactory = new SessionFactory();//new Configuration().configure().buildSessionFactory();
	
    /**
     * saves an object into the database. Object has to have a hibernate mapping!
     * @param o
     */
    public void saveAndCommit(Object o){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	//PatientIllnessScript ps = (PatientIllnessScript) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.PATILLSCRIPT_KEY);
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);     
            s.saveOrUpdate(o);
            instance.commitTransaction(s);
        }
        
        catch(Exception e){
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
        finally{
        	    s.flush();
        	    s.close();
        }
    }
    
    /*public void saveAndCommit(Object o1, Object o2){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	//Session s = sessionFactory.getCurrentSession();
    	
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);     
            s.saveOrUpdate(o1);
            s.saveOrUpdate(o2);
            instance.commitTransaction(s);
        }
        
        catch(Exception e){
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
    }*/
    
    /**
     * saves an object into the database. Object has to have a hibernate mapping!
     * @param o
     */
    public void updateAndCommit(Object o){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);     
            s.update(o);
            instance.commitTransaction(s);
        }
        
        catch(Exception e){
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
        finally{
    	    s.flush();
    	    s.close();
        }
    }
    
    /**
     * Saves/updates a collection of objects into the database
     * @param o
     */
    public void saveAndCommit(Collection o) {
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);   //commit will be done after insert!
            //tx = s.beginTransaction();
            Iterator it = o.iterator();
            while(it.hasNext())
            {
            	s.saveOrUpdate(it.next());
            }
            instance.commitTransaction(s);
        }
        catch (Exception e) {
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
        finally{
    	    s.flush();
    	    s.close();
        }
    }
    /**
     * Delete a collection from the database
     * @param o
     */
    public void deleteAndCommit(Collection c){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);   	
        try {
        	instance.beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            Iterator it = c.iterator();
            while(it.hasNext())
            {
            	s.delete(it.next());
            }
            instance.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }   
        finally{
    	    s.flush();
    	    s.close();
        }
    }  
    
    /**
     * Delete a Bean from the database
     * @param o
     */
    public void deleteAndCommit(Object o){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);   		
        try {
        	instance.beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            s.delete(o);
            HibernateUtil.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }  
        finally{
    	    s.flush();
    	    s.close();
        }
    }
    
    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptBySessionId(long sessionId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("sessionId", new Long(sessionId)));
    	PatientIllnessScript ps =  (PatientIllnessScript) criteria.uniqueResult();
    	s.close();
    	return ps;
    }

    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("id", new Long(id)));
    	PatientIllnessScript ps = (PatientIllnessScript) criteria.uniqueResult();
    	s.close();
    	return ps;
    }
    /**
     * Select the PatientIllnessScripts for the userId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectPatIllScriptsByUserId(long userId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	return  criteria.list();
    }
    
    /**
     * Select the ListItem with the given id from the database.
     * @param id
     * @return ListItem or null
     */
    public ListItem selectListItemById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("item_id", new Long(id)));
    	ListItem li = (ListItem) criteria.uniqueResult();
    	s.close();
    	return li;
    }
    

}
