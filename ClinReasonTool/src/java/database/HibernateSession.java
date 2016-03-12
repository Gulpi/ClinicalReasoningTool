package database;

import java.util.*;

import org.apache.commons.collections.FastArrayList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * non static reusable version of hibernateUtil static class, this class does the real work....
 * 
 * @author admin
 *
 */
public class HibernateSession {
	/**
	 * SessionFactory provides the sessions for the application. Each session has a database
	 * connection.
	 */
	private SessionFactory factory = null;
	private Configuration cfg = null;
	private Properties alternativeProperties = null;
	/**
	 * turn logging of stacktraces per session get / put on or off -> performance critical!!!!
	 */
	//private boolean logstacktrace = false;
	/**
	 * replaced by threadLocal implementation, now used for documentation only when logstacktrace is true
	 */
	private Map sessions; 
	
	/**
	 * new implementation with thread local.... will replace sessions
	 */
	private ThreadLocal<List> threadLocal = new ThreadLocal<List>();
	
	/**
	 * new implementation with thread local.... will replace sessions
	 */
	private ThreadLocal<List> threadExceptionList = new ThreadLocal<List>();
	
	/**
	 * for faster test scenarios i can limit class addition...
	 */
	public boolean add_all_classes = true;
	
	public HibernateSession() {
		
	}
	
	public HibernateSession(Properties p) {
		alternativeProperties = p;
	}
	
	/**
	 * @return Returns the session factory. If null, an init-methode will be called first.
	 */
	public SessionFactory getFactory() 
	{ 
		if(factory==null)
			initHibernate();

		return factory;
	}
	
	public void resetHibernate() {
		try { 
			cfg = null;
			factory = null;
		}
		catch(Throwable th) {
			//ok
		}
	}
	
	/**
	 * Initializing Hibernate. Configurationfile is loaded and the SessionFactory is 
	 * built.
	 * Here all classes matching database tables have to be added!!!!
	 */
	public void initHibernate()
	{
		try { 

			if (cfg == null) cfg = createNewConfiguration();
			

			//load classes here....
			cfg.addClass(model.ListItem.class);
			cfg.addClass(beans.PatientIllnessScript.class);	
			cfg.addClass(beans.relation.RelationProblem.class);	
			cfg.addClass(beans.relation.RelationDiagnosis.class);	
			cfg.addClass(beans.LogEntry.class);
			//addAutoHSQLDBConfig();
			
		   
			if (factory==null) {
				//init_setHibernateDialect();
				addAlternativeProperties();
			}
				
			try {factory = cfg.buildSessionFactory();} 
			finally{}
		}
		finally{}

	}
	

	private void addAlternativeProperties() {
		if (this.alternativeProperties != null) {
			Iterator it = alternativeProperties.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				Object value = alternativeProperties.get(key);
				if (key != null && value != null && value instanceof String && ((String) value).equalsIgnoreCase("<null>")) {
					cfg.getProperties().remove(key);
					//Logger.info("HibernateSession.addAlternativeProperties ", "cfg.getProperties().remove=" + key);
				}
				else if (key != null && value != null){
					cfg.setProperty(key.toString(), value.toString());
					//Logger.info("HibernateSession.addAlternativeProperties ", "cfg.setProperty(" + key + ")=" + value);
				}
			}
		}
	}

	/*private void addAutoHSQLDBConfig() {
		if (CasusConfiguration.getGlobalBooleanValue("HibernateSession.overwriteDSSettingWithContextHsqldbSettings", false)) {
			//Logger.info("HibernateSession.initHibernate ", "HibernateSession.overwriteDSSettingWithContextHsqldbSettings!!!");

			cfg.setProperty("hibernate.connection.datasource", "");
			cfg.getProperties().remove("hibernate.connection.datasource");
			cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
			cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
			cfg.setProperty("hibernate.connection.username", "sa");
			cfg.setProperty("hibernate.connection.password", "");
			
			String tmpDBSUffix = CasusConfiguration.getGlobalValue( "Hsqldb.defaultAbsUrlPath","../webapps/pp/hsqldb/casus3");
			String pathdelimiter = Character.toString(File.separatorChar);
			tmpDBSUffix = StringUtilities.replace(tmpDBSUffix, "/", pathdelimiter);
			cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:" + tmpDBSUffix);
			Logger.info("HibernateSession.initHibernate ", "cfg.setProperty(hibernate.connection.url)=" + cfg.getProperty("hibernate.connection.url"));
		
			try {
				String dbName = CasusConfiguration.getGlobalValue( "Hsqldb.defaultAbsUrlPath","../webapps/pp/hsqldb/casus3");
				dbName = StringUtilities.replace(dbName, "/", pathdelimiter);
				File myDB = new File(dbName);
				String[] files = myDB.getParentFile().list(new HTMLConfigurableFileFilter("true file;" + myDB.getName() + ".%"));
				List myList = new ArrayList();
				for (int i=0; files != null&& i<files.length;i++) {
					myList.add(new File(myDB.getParentFile(),files[i]));
				}
				File rmt_db = new File(myDB.getParentFile(),myDB.getName() +  "_rmt_tmp.zip");
				String rmtUrl = CasusConfiguration.getGlobalValue( "Hsqldb.remoteUrl");
				if (StringUtilities.isValidString(rmtUrl)) {

					int httpresult = MyHttpClient.getInstance().getToFile( rmtUrl, null, rmt_db, null);
					if (httpresult == 200) {
						if (myList != null && myList.size()>0) {
							DateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS",Locale.ENGLISH);
							String timeStamp = dateformat.format(new Date());
							File bu_db = new File(myDB.getParentFile(),myDB.getName() +  "_" + timeStamp + ".zip");
							Logger.info("HibernateSession.initHibernate ", "myFile=" + bu_db.getAbsolutePath());
							IOUtilities.createZipFile(bu_db , myList);
						}
						List<File> extractList = IOUtilities.extractZipFile(rmt_db, myDB.getParentFile());
						if (extractList != null) {
							Iterator<File> it = extractList.iterator();
							StringBuffer sb = new StringBuffer();
							while(it.hasNext()) {
								File myFile = it.next(); 
								Logger.info("HibernateSession.initHibernate ", "myFile=" + myFile.getAbsolutePath());
								 sb.append(myFile.getName());
								 sb.append("\n");
							}
							CasusConfiguration.addGlobalKey("Hsqldb.loadedRmtDB", sb.toString());
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	





	/**
	 * 
	 */
	/*private void init_setHibernateDialect() {
		if (net.casus.util.CasusConfiguration.getGlobalBooleanValue("HibernateSession.setDialectFromDSUrl",true)) {
			try {
		        Context initContext = new InitialContext();
		        Context envContext  = (Context)initContext.lookup("java:/comp/env");
		        
		        //org.apache.tomcat.dbcp.dbcp2.BasicDataSource
		        DataSource  ds = (DataSource)envContext.lookup(net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.Name","jdbc/myoracle"));
		        Logger.info("HibernateSession.init_setHibernateDialect", "ds class:" + (ds!=null?ds.getClass():"null"));
		        
		        if (ds.getClass().toString().equals("org.apache.tomcat.dbcp.dbcp.BasicDataSource")) {
		        	org.apache.tomcat.dbcp.dbcp.BasicDataSource bds = (org.apache.tomcat.dbcp.dbcp.BasicDataSource) ds;
		        	String dialect = null;
		        	if (bds.getDriverClassName() != null && bds.getDriverClassName().toLowerCase().contains("hsqldb")) {
		        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.hsqldb","org.hibernate.dialect.HSQLDialect");
		        	}
		        	else if (bds.getDriverClassName() != null && bds.getDriverClassName().toLowerCase().contains("oracle")) {
		        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.oracle","org.hibernate.dialect.OracleDialect");
		        	}
		        	else if (bds.getDriverClassName() != null && bds.getDriverClassName().toLowerCase().contains("mysql")) {
		        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.mysql","org.hibernate.dialect.MySQLDialect");
		        	}
        			Logger.info("init_setHibernateDialect", "dialect:" + dialect);
	        		if (dialect != null) {
	        			cfg.setProperty("hibernate.dialect",dialect);
	        		}
	        	}
		        else {
		        	Method m = JavaReflectionUtilities.getMethodByName(ds.getClass(), "getDriverClassName", null, JavaReflectionUtilities.EMPTY_PARAMETER_LIST);
		        	Object driverNameObj = m.invoke(ds, JavaReflectionUtilities.NO_PARAMETERS);
	        		Logger.info("init_setHibernateDialect", "driverNameObj:" + driverNameObj);
	        		if (driverNameObj != null) {
		        		String dialect = null;
		        		String driverName = driverNameObj.toString().toLowerCase();
		        		if (driverName.contains("hsqldb")) {
			        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.hsqldb","org.hibernate.dialect.HSQLDialect");
			        	}
			        	else if (driverName.contains("oracle")) {
			        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.oracle","org.hibernate.dialect.OracleDialect");
			        	}
			        	else if (driverName.contains("mysql")) {
			        		dialect = net.casus.util.CasusConfiguration.getGlobalValue("Database.DataSource.HibernateDialect.mysql","org.hibernate.dialect.MySQLDialect");
			        	}
		        		Logger.info("init_setHibernateDialect", "dialect:" + dialect);
		        		if (dialect != null) {
		        			cfg.setProperty("hibernate.dialect",dialect);
		        		}
		        	}
		        }
		    }
		    catch(Throwable x) {
		        Logger.serious("HibernateSession.init_setHibernateDialect", "x:" + Utility.stackTraceToString(x));
		    }
		}
		
		try {
			cfg.addSqlFunction("greatest", new VarArgsSQLFunction("greatest(", ",", ")"));
			cfg.addSqlFunction("least", new VarArgsSQLFunction("least(", ",", ")"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/**
	 * TODO please comment!!!!
	 * @param key
	 */
	/*private void addClasses(String key) {
		// Add other intl configuartions:
		String addClasses = net.casus.util.CasusConfiguration.getGlobalValue(key);
		Logger.important("HibernateSession.initHibernate","HibernateSession.addClasses(" + key + "): " + addClasses);
		if (add_all_classes && StringUtilities.isValidString(addClasses)) {
			try {
				String[] addClasses_array = StringUtilities.getStringArrayFromString(addClasses, ",");
				for (int i = 0; i < addClasses_array.length; i++) {
					try {
						Class myClass = Class.forName(addClasses_array[i]);
						cfg.addClass(myClass);
						Logger.important("HibernateSession.initHibernate","HibernateUtil.addClasses: " + myClass.getName());
					}
					catch(Exception x) {
						Logger.serious("HibernateUtil.initHibernate","X: " + Utility.stackTraceToString(x));
					}
				}
			}
			catch(Exception langX) {
			}
		}
	}*/
	
	/**
	 * This method adds classes specified by CasusConfiguration key name to the hibernate configuration!
	 * 
	 * Must be performed BEFORE cfg.buildSessionFactory() (in initHibernate())
	 * 
	 * @param inClassName
	 */
	/*public void addClassesByConfigurationPrefix2HibCfg(String conf_keyname) {
		String s= net.casus.util.CasusConfiguration.getGlobalValue(conf_keyname);
		if (StringUtilities.isValidString(s)) {
			StringTokenizer st = new StringTokenizer (s,",");
			while (st.hasMoreTokens()){
				String className = st.nextToken();
				this.addClassByName2HibCfg(className);
			}
		}
	}*/

	/**
	 * This method adds a class specified by full class name to the hibernate configuration!
	 * 
	 * Must be performed BEFORE cfg.buildSessionFactory() (in initHibernate())
	 * 
	 * @param inClassName
	 */
	/*public void addClassByName2HibCfg(String inClassName) {
		try {
			Class myClass = Class.forName(inClassName);
			if (myClass != null) {
				this.getConfiguration().addClass(myClass);
			}
		}
		catch(Exception x) {
			Logger.serious("HibernateSession.addClassByName2HibCfg ", "x:" + Utility.stackTraceToString(x));
		}
	}*/
	
	public Configuration getConfiguration() {
		return cfg;
	}
	
	public Configuration getOrCreateConfiguration() {
		if (cfg == null) {
			cfg = createNewConfiguration();
		}
		return cfg;
	}
	
	public Configuration createNewConfiguration() {
		return new Configuration();
	}
	
	public Map getAllSessions() {
		return new HashMap(sessions);
	}
	
	/**
	 * opens and returns a session from the session factory.
	 * @return Session
	 */
	public Session getInternalSession(Thread t, boolean debug)
	{
		SessionFactory f = this.getFactory();
		Session s = null;
		if(t!=null) s = getInternalSessionByThread(t);

		if(s==null)
		{
			s = f.openSession();
			//Logger.debug ("HibernateSession.getSession","t " + t.hashCode() + ", s:" + s.hashCode() + "," + s.isOpen());
			addSession(s,Thread.currentThread());
		}
		else {
			//Logger.debug ("HibernateSession.getSession","found s: t " + t.hashCode() + ", s:" + s.hashCode() + "," + s.isOpen());
		}
		return  s;             
	}

	public Session getInternalSessionByThread(Thread t)
	{
		List sessionList = null;
		
		sessionList = threadLocal.get();
		if(sessionList!=null)
		{
			for(int i=0; i<sessionList.size();i++)
			{
				Session s = (Session) sessionList.get(i);
				if(s!=null && s.isOpen()) {
					//Logger.debug ("HibernateSession.getSessionByThread","return t:" + t.hashCode() + ", s:" + s.hashCode() + ", " + s.isOpen());
					return s;
				}
				else {
					if (s == null) {
						//Logger.important("HibernateSession.getSessionByThread","t:" + t.hashCode() + ", s==null");
					}
					else {
						//Logger.important ("HibernateSession.getSessionByThread","t:" + t.hashCode() + ", s:" + s.hashCode() + ", " + s.isOpen());
						if (s.isOpen() == false) {
						}
					}
				}
			}
		}
		else {
			//Logger.debug ("HibernateSession.getSessionByThread","t:" + t.hashCode() + ", sessionList==null");
		}
		
		
		return null;
	}
	
	/**
	 * if a session has been opened it will be written into the session HashMap with 
	 * the current Thread as key.
	 * @param s
	 * @param t
	 */
	private void addSession(Session s, Thread t)
	{
		List sessionList;
		//logstacktrace = CasusConfiguration.getGlobalBooleanValue("HibernateUtil.logstacktrace", false);
		if (sessions==null) {sessions = new HashMap();
		}

		if(threadLocal.get()==null)
		{
			sessionList = new ArrayList();
			sessionList.add(s);
			{
				List logstacktraceList = new ArrayList();
				//StringObjectHashKey v = new StringObjectHashKey(logstacktrace ? Utility.stackTraceToString(new Exception("HibernateSession.addSession")) : "HibernateSession.addSession",s);
				//logstacktraceList.add(v);
				sessions.put(t,logstacktraceList);
			}
			threadLocal.set(sessionList);
		}
		else
		{
			sessionList = threadLocal.get();
			if(!sessionList.contains(s)) {
				sessionList.add(0,s);
				{
					List logstacktraceList = (List) sessions.get(t);
					//StringObjectHashKey v = new StringObjectHashKey(logstacktrace ? Utility.stackTraceToString(new Exception("HibernateSession.addSession")) : "HibernateSession.addSession",s);
					//logstacktraceList.add(v);
				}
			}
			
			if (sessionList.size()>1) {
				//Utility.dummyX("HibernateSession.addSession -> WARNING multiple hibsession per thread!! t:" + t.hashCode());
				try {
					for(int i=0; i<sessionList.size();i++)
					{
						Session loop = (Session) sessionList.get(i);
						//Logger.serious ("HibernateSession.addSession","t:" + t.hashCode() + ", loop:" + loop.hashCode() + ", " + loop.isOpen());
						//if(loop!=null && loop.isOpen())
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void reallyRemoveSessionsInternal(Thread t)
	{
		try{    
			if(sessions!=null)
			{
				List sessionList = null;
				sessionList = threadLocal.get();
				int sessionListSize = 0;
				if(sessionList!=null && (sessionListSize = sessionList.size())>0)
				{
					for(int i=0; i<sessionListSize;i++)
					{
						Session s = (Session) sessionList.get(i);
						//Logger.debug ("HibernateSession.reallyRemoveSessions","t " + t.hashCode() + ", s:" + s.hashCode());
						try {
							if(s!=null && s.isOpen()) {
								s.clear();
							}
						}
						catch(Throwable x) {
							// ok end of lifecycle
							//Logger.info ("HibernateSession.reallyRemoveSessions","x1:" + x.getMessage());
						}
						
						try {
							if(s!=null && s.isOpen()) {
								s.close();
							}
						}
						catch(Throwable x) {
							// ok end of lifecycle
							//Logger.info ("HibernateSession.reallyRemoveSessions","x2:" + x.getMessage());
						}
						
						try {
							if(s!=null && !s.isOpen() && s.isConnected()) {
								s.disconnect();
							}
						}
						catch(Throwable x) {
							// ok end of lifecycle
							//Logger.info ("HibernateSession.reallyRemoveSessions","x3:" + x.getMessage());
						}
						
						Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
						
					}
					sessionList.clear();
					//Logger.debug ("HibernateSession.reallyRemoveSessions","sessions.size():" + sessions.size());
					Map mysessions = sessions;
					threadLocal.remove();
					sessions.remove(t);
					threadExceptionList.remove();
					//Logger.debug ("HibernateSession.reallyRemoveSessions","sessions.size():" + sessions.size());
					int mysize = sessions.size();
					if (mysize < 0) {
						//Logger.info ("HibernateSession.reallyRemoveSessions","huh? sessions.size():" + sessions.size());
					}
				}  
				else {
					threadLocal.remove();
					sessions.remove(t);
					threadExceptionList.remove();
				}
			}
		}
		catch (Exception e)
		{
			//Logger.serious("HibernateSession.reallyRemoveSessions() ", "Exception: " + Utility.stackTraceToString(e));
		}
	}
	
	public void internalRemoveSessions(Thread t){
		
	}
	
	/**
	 * We look into the session, whether there is a transaction active. 
	 * If not, we begin a transaction 
	 */
	public Transaction beginTransaction() {
		return beginTransaction(this.getInternalSession(Thread.currentThread(),false));
	}
	
	/**
	 * We look into the session, whether there is a transaction active. 
	 * If not, we begin a transaction 
	 */
	public Transaction beginTransaction(Session s) {
		Transaction tx = null;
		try {
			Transaction mytx = s.getTransaction();
			if (mytx != null && mytx.isActive()) {
				tx = mytx;
				//Logger.debug("HibernateSession.beginTransaction()","Session= " + s.hashCode() + ", tx= " + tx.hashCode() + "(is active)");
			}
			else {
				tx = s.beginTransaction();
				//tx.setTimeout(CasusConfiguration.getGlobalIntegerValue("HibnerateSession.beginTransaction.timeout", 120));
				//Logger.debug("HibernateSession.beginTransaction()","Session= " + s.hashCode() + ", tx= " + tx.hashCode());
			}
		}
		catch(Exception x) 
		{
			//Logger.serious("HibernateSession.beginTransaction()","Exception= " + Utility.stackTraceToString(x));
		}
		return tx;
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 */
	public void commitTransaction()
	{
		try {
			commitTransaction(getInternalSession(Thread.currentThread(),false));
		}
		catch(Exception x) 
		{
			//Logger.serious("HibernateSession.commitTransaction()","Exception= " + Utility.stackTraceToString(x));
		}
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 * 
	 * with possible thrown Exception!!!!
	 */
	public void commitTransactionWithException()
	{
		commitTransactionWithException(getInternalSession(Thread.currentThread(),false));
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 */
	public void commitTransaction(Session s)
	{
		Transaction tx = null;
		try {			
			//Logger.debug("HibernateSession.commitTransaction()","Session= " + s.hashCode());

			tx = s.getTransaction();
			if (tx != null && tx.isActive()) {
				//Logger.debug("HibernateSession.commitTransaction()","tx= " + tx.hashCode());
				tx.commit();
			}
			else {
				s.flush();
			}
		}
		catch(Exception x) 
		{
			//Logger.serious("HibernateSession.commitTransaction()","Exception= " + Utility.stackTraceToString(x));
		}
	}

	/**
	 * We look into the session, whether there is a transaction active.
	 * If yes, we commit it.
	 * 
	 * with possible thrown Exception!!!!
	 */
	public void commitTransactionWithException(Session s)
	{
		Transaction tx = s.getTransaction();
		if (tx != null) 
			tx.commit();

	}

	/**
	 * try to rollback a transaction.
	 */
	public void rollBackTx()
	{
		try{
			Session s = getInternalSession(Thread.currentThread(),false);
			Transaction tx = s.getTransaction();
			if (tx!=null && tx.isActive()) tx.rollback();
		}
		catch(Exception e){
			//Logger.serious("HibernateSession.rollBackTx()", "rollback failed:" + e.toString());
		}
	}
	
	public List getMyThreadExceptionList() {
		return threadExceptionList.get();
	}
	
	public void removeMyThreadExceptionList() {
		threadExceptionList.remove();
	}
	
	public void add2ThreadExceptionList(Throwable th) {
		if (th != null) {
			if (th instanceof NullPointerException) {
				//Logger.info("HibernateSesison.add2ThreadExceptionList","npX:" + Utility.stackTraceToString(th));
				return;
			}
			
			
			List myThreadExceptionList = threadExceptionList.get();
			if (myThreadExceptionList == null) {
				myThreadExceptionList = new FastArrayList();
				threadExceptionList.set(myThreadExceptionList);
			}
			
			if (myThreadExceptionList != null && th instanceof RuntimeException) {
				myThreadExceptionList.add(th);
			}
		}
	}

	public ThreadLocal<List> getThreadExceptionList() {
		return threadExceptionList;
	}

	public void setThreadExceptionList(ThreadLocal<List> threadExceptionList) {
		this.threadExceptionList = threadExceptionList;
	}
	
}
