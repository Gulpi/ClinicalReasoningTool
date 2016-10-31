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
	private static final int MIN_LEVEN_DISTANCE = 4; //if we have a level 1 similarity the item is not included
	private static final int MAX_FUZZY_DISTANCE = 38; //if we have a level 1 similarity the item is not included
	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

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
	
	public static long[] getLongArrFromString(String str, String del){
		if(str==null || str.isEmpty()) return null;
		String[] strarr = StringUtils.splitByWholeSeparator(str, del);
		if(strarr==null || strarr.length==0) return null;
		long[] longarr = new long[strarr.length];
		for(int i=0; i<strarr.length; i++){
			longarr[i] = Long.valueOf(strarr[i]).longValue();
		}
		return longarr;
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

		item1 = item1.replace("-", " ");
		item2 = item2.replace("-", " ");
		item1 = item1.replace(",", "");
		item2 = item2.replace(",", "");
		item1 = item1.replace("'", "");
		item2 = item2.replace("'", "");
		item1 = item1.replace("ö", "oe");
		item2 = item2.replace("ö", "oe");
		item1 = item1.replace("ä", "ae");
		item2 = item2.replace("ä", "ae");
		item2 = item2.replace("ß", "ss");
		item1 = item1.replace("ß", "ss");
		item1 = item1.replace("ü", "ue");
		item2 = item2.replace("ü", "ue");
		if(isMatchBasedOnLevelAndFuzzy(item1, item2, loc)) return true;
		
		//if we have multiple words we split them and compare them separately
		String[] item1Arr = item1.trim().split(" ");
		String[] item2Arr = item2.trim().split(" ");
		//if(/*item1Arr.length!=item2Arr.length ||*/ (item1Arr.length==1 || item2Arr.length==1)) return false; //TODO: we might still compare them?
			//we go thru each item and compare it with the items2
			boolean[] isMatch;
			if(item1Arr.length>=item2Arr.length)
				isMatch = runThrugStringArr(item1Arr, item2Arr, loc);			
			else  isMatch = runThrugStringArr(item2Arr, item1Arr, loc);
			boolean isFinallyMatch = true;
			for(int i=0; i<isMatch.length; i++){
				if(!isMatch[i]){
					isFinallyMatch = false; //if one entry is false (no Match) we return false.
					break;
				}
			}
			if(isFinallyMatch) return isFinallyMatch;
			//check for something like: "Streptokokkenpneumonie" / "Penumonie, Streptokokken"
			if((item1Arr.length==1 && item2Arr.length==2)){
				String item2_1Str = item2Arr[0].trim() +  item2Arr[1].trim();
				if(isMatchBasedOnLevelAndFuzzy(item1, item2_1Str, loc)) return true;
				String item2_2Str = item2Arr[1].trim() +  item2Arr[0].trim();
				if(isMatchBasedOnLevelAndFuzzy(item1, item2_2Str, loc)) return true;				
			}
			if((item2Arr.length==1 && item1Arr.length==2)){
				String item1_1Str = item1Arr[0].trim() +  item1Arr[1].trim();
				if(isMatchBasedOnLevelAndFuzzy(item2, item1_1Str, loc)) return true;
				String item1_2Str = item1Arr[1].trim() +  item1Arr[0].trim();
				if(isMatchBasedOnLevelAndFuzzy(item2, item1_2Str, loc)) return true;				
			}
			return false;
			
	}
	
	/**
	 * @param item1Arr
	 * @param item2Arr
	 * @param loc
	 * @return
	 */
	private static boolean[] runThrugStringArr(String[] item1Arr, String[] item2Arr, Locale loc){
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		boolean[] isMatch = new boolean[item1Arr.length];
		for(int i=0; i<item1Arr.length; i++){
			String item1_1 = item1Arr[i];
			sb1.append(item1_1+"#");
			innerLoop:
			for(int k=0; k<item2Arr.length; k++){
				String item2_2 = item2Arr[k];
				sb2.append(item2_2+"#");
				int leven2 = StringUtils.getLevenshteinDistance(item1_1, item2_2);
				int fuzzy2 = StringUtils.getFuzzyDistance(item1_1, item2_2, loc);
				if(leven2<MIN_LEVEN_DISTANCE || fuzzy2>=MAX_FUZZY_DISTANCE){ //then the are similar
					isMatch[i] = true;
					break innerLoop;
				}
			}
		}
		return isMatch;
	}
	
	private static boolean isMatchBasedOnLevelAndFuzzy(String s1, String s2, Locale loc){
		int leven = StringUtils.getLevenshteinDistance(s1, s2);
		int fuzzy = StringUtils.getFuzzyDistance(s1.toLowerCase(), s2.toLowerCase(), loc);
		
		//compare Strings as they are:
		//if(s1.equals("Abdomen, Acute") || item2.equals("Abdomen, Acute"))
		//	System.out.println("Leven: " + leven + ", fuzzy: " + fuzzy + " Item1: " + item1 + " Item2: " + item2);
		if(leven ==2 && fuzzy>20 && fuzzy<MAX_FUZZY_DISTANCE)
			System.out.println(s1 + ", " + s2 + ", leven " + leven + ", fuzzy: " + fuzzy);

		if(s1.length()>3 && leven == 1 && fuzzy > 1) return true; 
		if(leven == 2 && fuzzy > 30) return true; 
		
		if(leven < MIN_LEVEN_DISTANCE && fuzzy>=MAX_FUZZY_DISTANCE/*&& firstLetterItem1.equalsIgnoreCase(firstLetterItem2)*/){
			return true; //these items are not added
		}
		/*if(fuzzy>=MAX_FUZZY_DISTANCE){
			return true;			
		}*/
		return false;
		
	}
	
	public static byte[] hexToBytes(String hex) {return hexToBytes(hex.toCharArray()); }
	  
	public static byte[] hexToBytes(char[] hex) {
	    int length = hex.length / 2;
	    byte[] raw = new byte[length];
	    for (int i = 0; i < length; i++) {
	      int high = Character.digit(hex[i * 2], 16);
	      int low = Character.digit(hex[i * 2 + 1], 16);
	      int value = (high << 4) | low;
	      if (value > 127)
	        value -= 256;
	      raw[i] = (byte) value;
	    }
	    return raw;
	}
	
	/**
	 * converts byte array to hex string representation
	 * @param in_bytes
	 * @return
	 */
	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int i=0; i<bytes.length;i++) {
			int b = bytes[i];
		    b &= 0xff;
		    sb.append(HEXDIGITS[b >> 4]);
		    sb.append(HEXDIGITS[b & 15]);
		    //sb.append(' ');
		}
		return sb.toString();
	}
}
