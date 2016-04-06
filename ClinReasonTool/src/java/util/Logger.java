package util;

public class Logger {
	public static final int LEVEL_TEST = 1;
	public static final int LEVEL_PROD = 2;
	public static int currentLogLevel = LEVEL_TEST; //TODO get from properties
	
	public static void out(String s){
		System.out.println(s);
	}
	
	public static void out(String s,int level){
		if(currentLogLevel==LEVEL_TEST) System.out.println(s); //then we alwas print logs
		else if(level==currentLogLevel) System.out.println(s);
	}
}
