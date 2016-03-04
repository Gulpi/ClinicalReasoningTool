package util;
import java.io.*;

/**
 * Any kind of utilities we need for string handling
 * @author ingahege
 *
 */
public class StringUtilities {

	/**
	 * Counts the number of a pattern within a string.
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static int countInString(String s, String pattern) {
		if (!StringUtilities.isValidString(s)) return 0;
		int result = 0;
		int pos = -1;
		while ((pos = s.indexOf(pattern,pos+1)) != -1) {
			result++;
		}
		
		return result;
	}
	
	/**
	 * s is a valid string if it is not null and not an empty string
	 * @param s
	 * @return
	 */
	public static boolean isValidString(String s) {
		return (s != null && s.compareTo("") != 0);
	}
	
	/** Returns the stack trace for the given Thowable as string.
     * if there's a TRACE_LIMIT, the stacktrace will be cut after this line!!
	 *
	 * @param throwable exception to create the stacktrace for */
	public static String stackTraceToString(Throwable throwable)
	{
		StringWriter stack;	// tmp
			
		stack = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stack));
		
		String s = stack.toString();
		stack = null;
		
		if ("javax.servlet.http.HttpServlet.service" != null) {
			try {
				LineNumberReader lnr = new LineNumberReader(new StringReader(s));
				stack = new StringWriter();
				PrintWriter pw = new PrintWriter(stack);
				String line = null;
				boolean ownCode = true;
				while (ownCode && (line=lnr.readLine()) != null) {
					if (line.indexOf("javax.servlet.http.HttpServlet.service") != -1) ownCode = false;
					pw.println(line);
				}
				s = stack.toString();
				stack = null;
			}
			catch(Exception x) { 
			}
		}		
		return s;		
	}
}
