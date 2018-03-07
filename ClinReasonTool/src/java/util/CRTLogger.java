package util;

import org.apache.log4j.*;

public class CRTLogger {
	public static final int LEVEL_TEST = 1;
	public static final int LEVEL_PROD = 2;
	public static final int LEVEL_ERROR = 3;
	public static int currentLogLevel = LEVEL_TEST; //TODO get from properties
	private static  Logger logger = Logger.getLogger(CRTLogger.class);
	/**
	 * Write the string into the console and into a logfile (crt.log)
	 * @param s
	 * @param level
	 */
	public static void out(String s,int level){
		try{
		    if(level == LEVEL_TEST && currentLogLevel==LEVEL_TEST) {
		    	logger.debug(s); 
		    }
		    if(level == LEVEL_PROD) {
		    	logger.info(s);
		    }
		    if(level == LEVEL_ERROR) {
		    	logger.error(s);
		    }
		}
		catch(Exception e){
			//TODO
		}
	}
}
