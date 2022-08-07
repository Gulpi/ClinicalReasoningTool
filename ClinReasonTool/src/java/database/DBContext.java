package database;

import java.beans.Beans;
import java.util.List;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import beans.context.*;
import util.*;

public class DBContext{
	static HibernateSession instance = new HibernateSession();

    /**
     * get all actors added for a VP by a user
     * @param userId
     * @param vpId
     * @return
     */
    public List<Actor> selectActorsByUserIdAndVpId(long userId, long vpId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Actor.class,"Actor");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	criteria.add(Restrictions.eq("vpId", new Long(vpId)));
    	criteria.addOrder(Order.asc("order"));
    	List<Actor> actors =   criteria.list();
   	
    	s.close();
    	return actors;
    }
    
    public List<Actor> selectExpertActorsByVpId(long vpId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Actor.class,"Actor");
    	criteria.add(Restrictions.eq("type", new Integer(2)));
    	criteria.add(Restrictions.eq("vpId", new Long(vpId)));
    	criteria.addOrder(Order.asc("order"));
    	List<Actor> actors =   criteria.list();   	
    	s.close();
    	return actors;
    }
    
    
    public List<Context> selectCtxtsByUserIdAndVpId(long userId, long vpId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Context.class,"Context");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	criteria.add(Restrictions.eq("vpId", new Long(vpId)));
    	criteria.addOrder(Order.asc("order"));
    	List<Context> ctxts =   criteria.list();
   	
    	s.close();
    	return ctxts;
    }
    
    public List<Context> selectExpertCtxtsByVpId(long vpId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Context.class,"Context");
    	criteria.add(Restrictions.eq("type", new Integer(2)));
    	criteria.add(Restrictions.eq("vpId", new Long(vpId)));
    	criteria.addOrder(Order.asc("order"));
    	List<Context> ctxts =   criteria.list();   	
    	s.close();
    	return ctxts;
    }
    
    public void saveAndCommit(Beans b) {
    	new DBClinReason().saveAndCommit(b);
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
            instance.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBContext.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }  
        finally{
    	    s.flush();
    	    s.close();
        }
    }
}
