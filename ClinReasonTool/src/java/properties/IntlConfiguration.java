package properties;

import java.io.*;
import java.util.*;
import controller.LocaleController;
import util.CRTLogger;


/** Class for managing intl resources */
public class IntlConfiguration
{
	private static Properties p_de = new Properties();
	private static Properties p_en = new Properties();
	private static Properties p_pl = new Properties();

	private String propFilenameDE = "properties/intlproperties_de.properties";
	private String propFilenameEN = "properties/intlproperties.properties";
	private String propFilenamePL = "properties/intlproperties_pl.properties";


	public IntlConfiguration(){
		loadProperties("en"); //default
		loadProperties("de");
		loadProperties("pl");
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
			if(lang=="pl"){
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilenamePL);
				if(inputStream!=null)
					p_pl.load(inputStream);
			}
		}
		catch(Exception e){
			CRTLogger.out("Exception", CRTLogger.LEVEL_ERROR);
		}
	}
	
	public static String getValue(String key, Locale loc){		 
		if(loc.getLanguage().equalsIgnoreCase("de")) return p_de.getProperty(key);
		if(loc.getLanguage().equalsIgnoreCase("pl")) return p_pl.getProperty(key);
		return p_en.getProperty(key);
	}
	
	public static String getValue(String key){	
		return getValue(key, LocaleController.getLocale());
	}
}
