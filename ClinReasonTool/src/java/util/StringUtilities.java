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
		int leven = StringUtils.getLevenshteinDistance(item1, item2);
		int fuzzy = StringUtils.getFuzzyDistance(item1, item2, loc);
		//String firstLetterItem1 = item1.substring(0,1);
		//String firstLetterItem2 = item2.substring(0,1);
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
		/*if(fuzzy==11 && leven==11){
			System.out.println("Leven: " + leven + ", fuzzy: " + fuzzy + " Item1: " + item1 + " Item2: " + item2);
		}*/
		
		//if we have multiple words we split them and compare them separately
		//if(item1.trim().length()>1 && item2.trim().length()>1){
		String[] item1Arr = item1.trim().split(" ");
		String[] item2Arr = item2.trim().split(" ");
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		
		if(item1Arr.length!=item2Arr.length || (item1Arr.length==1 || item2Arr.length==1)) return false; //TODO: we might still compare them?
			//we go thru each item and compare it with the items2
			boolean[] isMatch = new boolean[item1Arr.length];
			//boolean isMatch = true;
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
			boolean isFinallyMatch = true;
			for(int i=0; i<isMatch.length; i++){
				if(!isMatch[i]) isFinallyMatch = false; //if one entry is false (no Match) we return false.
				break;
			}
			//System.out.println("boolean: "+  isFinallyMatch +" ,Item1Arr: "+ sb1.toString() + " , Item2Arr: " + sb2.toString());
			return isFinallyMatch;
		//}
		
		/*else{
			String item1NoBlanks = StringUtils.remove(item1, " ");
			String item2NoBlanks = StringUtils.remove(item2, " ");
			int leven2 = StringUtils.getLevenshteinDistance(item1, item2);
			if(leven2<=3) System.out.println("leven: ("+leven2+")" + item1NoBlanks + " - " + item2NoBlanks);
		}*/
		//if(leven==2 && firstLetterItem1.equalsIgnoreCase(firstLetterItem2)) return leven;
		// if(leven==3 && firstLetterItem1.equalsIgnoreCase(firstLetterItem2)) return leven;
		//return false;
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
