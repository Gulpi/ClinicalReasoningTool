package properties;

import java.util.*;
import java.io.*;
import java.util.Locale;

import controller.NavigationController;
import util.CRTLogger;


/** Class for managing intl resources */
public class IntlConfiguration
{
	private static Properties p_de = new Properties();
	private static Properties p_en = new Properties();
	private String propFilenameDE = "properties/intlproperties_de.properties";
	private String propFilenameEN = "properties/intlproperties.properties";

	public IntlConfiguration(){
		loadProperties("en"); //default
		loadProperties("de");
	}
	
	private void loadProperties(String lang){
		try{
			if(lang=="de"){
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilenameDE);
				if(inputStream!=null)
					p_de.load(inputStream);
			}
			if(lang=="en"){
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilenameEN);
				if(inputStream!=null)
					p_en.load(inputStream);
			}
		}
		catch(Exception e){
			CRTLogger.out("Exception", CRTLogger.LEVEL_ERROR);
		}
	}
	
	public static String getValue(String key, Locale loc){		 
		if(loc.getLanguage().equals("de")) return p_de.getProperty(key);
		return p_en.getProperty(key);
	}
	
	public static String getValue(String key){	
		return getValue(key, NavigationController.getLocale());
	}
}
