package database;

import org.apache.commons.collections.FastArrayList;
import org.hibernate.cfg.*;
import org.hibernate.criterion.*;
import org.hibernate.stat.Statistics;
import org.hibernate.*;

import java.lang.reflect.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 * Contains all necessary methods to start Hibernate and get and return a Session. 
 * 
 * @author inga
 *@deprecated
 */
public class HibernateUtil 
{
	static HibernateSession instance = new HibernateSession();
	
	public static void setAdd_all_classes(boolean b) {
		instance.add_all_classes = b;
	}
	
	static public void initHibernate() {
		instance.initHibernate();
	}
	
	static public void resetHibernate() {
		instance.resetHibernate();
	}
	

	public static void main(String args [])
	{
		HibernateUtil.initHibernate();
	}
	
	/**
	 * @return Returns the session factory. If null, an init-methode will be called first.
	 */
	static public SessionFactory getFactory() 
	{ 
		return instance.getFactory();
	}
	
	static public Configuration getConfiguration() {
		return instance.getConfiguration();
	}
	
	public static Configuration getOrCreateConfiguration() {
		return instance.getOrCreateConfiguration();
	}


	/*static public Session getSession()
	{
		return instance.getInternalSession(Thread.currentThread(), false);
	}*/

	


	/**
	 * We look into the session, whether there is a transaction active. 
	 * If not, we begin a transaction 
	 */
	static public Transaction beginTransaction() {
		return instance.beginTransaction();
	}
	
	/**
	 * We look into the session, whether there is a transaction active. 
	 * If not, we begin a transaction 
	 */
	static public Transaction beginTransaction(Session s) {
		return instance.beginTransaction(s);
	}

	/**
	 * made empty and moved functionality into reallyRemoveSessions!
	 * 
	 * @param t
	 */
	static public void removeSessions(Thread t){
		instance.internalRemoveSessions(t);
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 */
	static public void commitTransaction()
	{
		instance.commitTransaction();
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 * 
	 * with possible thrown Exception!!!!
	 */
	static public void commitTransactionWithException()
	{
		instance.commitTransactionWithException();
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 */
	static public void commitTransaction(Session s)
	{
		instance.commitTransaction(s);
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 * 
	 * with possible thrown Exception!!!!
	 */
	static public void commitTransactionWithException(Session s)
	{
		instance.commitTransactionWithException(s);
	}

	/**
	 * try to rollback a transaction.
	 */
	static public void rollBackTx()
	{
		instance.rollBackTx();
	}

	/**
	 * removeSessions(Thread t) is empty now!!!
	 * moved the complete functionality into this new method for backward compatibility.
	 * 
	 * This method should NOT be called directly, or only if you know what you do....
	 * 
	 * This method will be called by the request handling mechanism at the end of a request (WWWServlet)
	 * 
	 * @param t
	 */
	static public void reallyRemoveSessions(Thread t) {
		instance.reallyRemoveSessionsInternal(t);
	}

}