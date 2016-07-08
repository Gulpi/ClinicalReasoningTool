package database;

import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;

import beans.user.User;


public class DBUser extends DBClinReason{

    public User selectUserByExternalId(String extUserId, int systemId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(User.class,"User");
    	criteria.add(Restrictions.eq("extUserId", extUserId));
    	criteria.add(Restrictions.eq("systemId", new Integer(systemId)));

    	return (User) criteria.uniqueResult();
    }
    
    public User selectUserById(long userId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(User.class,"User");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	return (User) criteria.uniqueResult();
    }

}
