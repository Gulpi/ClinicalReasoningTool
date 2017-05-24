package database;



/**
 * Contains all necessary methods to start Hibernate and get and return a Session. 
 * 
 * @author inga
 *
 */
public class HibernateUtil 
{
	static HibernateSession instance = new HibernateSession();
	
	static public void initHibernate() {
		instance.initHibernate();
	}
	
	static public void resetHibernate() {
		instance.resetHibernate();
	}
	
	
	/**
	 * @return Returns the session factory. If null, an init-methode will be called first.
	 */
/*	static public SessionFactory getFactory() 
	{ 
		return instance.getFactory();
	}
	
	static public Configuration getConfiguration() {
		return instance.getConfiguration();
	}
	
	public static Configuration getOrCreateConfiguration() {
		return instance.getOrCreateConfiguration();
	}*/




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