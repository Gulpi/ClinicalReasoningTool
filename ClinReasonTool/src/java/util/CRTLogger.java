package util;

import org.apache.log4j.*;

public class CRTLogger {
	public static final int LEVEL_TEST = 1;
	public static final int LEVEL_PROD = 2;
	public static final int LEVEL_ERROR = 3;
	public static int currentLogLevel = LEVEL_TEST; //TODO get from properties
	private static final String logfile = "logs/crt.log";
	
	/**
	 * Write the string into the console and into a logfile (crt.log)
	 * @param s
	 * @param level
	 */
	public static void out(String s,int level){
		try{
		    Layout layout = new PatternLayout("%-5p [%t]: %m%n");
		    Logger logger = Logger.getLogger(logfile);
		    logger.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
		    logger.addAppender(new FileAppender(layout, logfile));
		    if(level == LEVEL_TEST && currentLogLevel==LEVEL_TEST) 
		    	logger.info(s); 
		    if(level == LEVEL_PROD) logger.info(s);
		    if(level == LEVEL_ERROR) logger.error(s);
		    //logger.info("Hello again...");  
		}
		catch(Exception e){
			//TODO
		}
	}
}
