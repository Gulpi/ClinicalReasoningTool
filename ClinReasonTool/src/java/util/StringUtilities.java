package util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.*;

import controller.JsonCreator;

import java.io.*;
import java.util.Locale;

/**
 * Any kind of utilities we need for string handling
 * @author ingahege
 *
 */
public class StringUtilities {
	public static final int MIN_LEVEN_DISTANCE = 4; //if we have a level 1 similarity the item is not included
	public static final int MAX_FUZZY_DISTANCE = 38; //if we have a level 1 similarity the item is not included

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
	
	/**
	 * We compare two strings and calculate the levensthein disctance. If it is lower than the accepted distance 
	 * and starts with the same letter, we return the levensthein distance. 
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static boolean similarStrings(String item1, String item2, Locale loc){
		int leven = StringUtils.getLevenshteinDistance(item1, item2);
		int fuzzy = StringUtils.getFuzzyDistance(item1, item2, loc);
		String firstLetterItem1 = item1.substring(0,1);
		String firstLetterItem2 = item2.substring(0,1);
		if(item1.equals("Abdomen, Acute") || item2.equals("Abdomen, Acute"))
			System.out.println("Leven: " + leven + ", fuzzy: " + fuzzy + " Item1: " + item1 + " Item2: " + item2);

		//if(leven==4 && firstLetterItem1.equalsIgnoreCase(firstLetterItem2)) System.out.println("Levensthein: ("+leven+") "+ item1 + " - " + item2);
		if(leven < MIN_LEVEN_DISTANCE /*&& firstLetterItem1.equalsIgnoreCase(firstLetterItem2)*/){
			//System.out.println("not added: " + item1 + " - " + item2);
			return true; //these items are not added
		}
		if(fuzzy>=MAX_FUZZY_DISTANCE){
			//System.out.println("Leven: " + leven + ", fuzzy: " + fuzzy + " Item1: " + item1 + " Item2: " + item2);
			return true;			
		}
		if(fuzzy==11 && leven==11){
			System.out.println("Leven: " + leven + ", fuzzy: " + fuzzy + " Item1: " + item1 + " Item2: " + item2);
		}
		/*else{
			String item1NoBlanks = StringUtils.remove(item1, " ");
			String item2NoBlanks = StringUtils.remove(item2, " ");
			int leven2 = StringUtils.getLevenshteinDistance(item1, item2);
			if(leven2<=3) System.out.println("leven: ("+leven2+")" + item1NoBlanks + " - " + item2NoBlanks);
		}*/
		//if(leven==2 && firstLetterItem1.equalsIgnoreCase(firstLetterItem2)) return leven;
		// if(leven==3 && firstLetterItem1.equalsIgnoreCase(firstLetterItem2)) return leven;
		return false;
	}
}
