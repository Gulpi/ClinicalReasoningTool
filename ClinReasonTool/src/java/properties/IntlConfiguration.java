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
	private static Properties p_sv = new Properties();
	private static Properties p_es = new Properties();

	private String propFilenameDE = "properties/intlproperties_de.properties";
	private String propFilenameEN = "properties/intlproperties.properties";
	private String propFilenamePL = "properties/intlproperties_pl.properties";
	private String propFilenameSV = "properties/intlproperties_sv.properties";
	private String propFilenameES = "properties/intlproperties_es.properties";

	public IntlConfiguration(){
		loadProperties("en"); //default
		loadProperties("de");
		loadProperties("pl");
		loadProperties("sv");
		loadProperties("es");
	}
	
	/**
	 * TODO: should be changed to: testPropertiesLoad() from CASUS--SupportLib
	 * @param lang
	 */
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
			if(lang=="sv"){
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilenameSV);
				if(inputStream!=null)
					p_sv.load(inputStream);
			}
			if(lang=="es"){
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilenameES);
				if(inputStream!=null)
					p_es.load(inputStream);
			}
		}
		catch(Exception e){
			CRTLogger.out("Exception", CRTLogger.LEVEL_ERROR);
		}
	}
	
	public static String getValue(String key, Locale loc){		 
		if(loc.getLanguage().equalsIgnoreCase("de")) return p_de.getProperty(key);
		if(loc.getLanguage().equalsIgnoreCase("pl")) return p_pl.getProperty(key);
		if(loc.getLanguage().equalsIgnoreCase("sv")) return p_sv.getProperty(key);
		if(loc.getLanguage().equalsIgnoreCase("es")) return p_es.getProperty(key);
		return p_en.getProperty(key);
	}
	
	public static String getValue(String key){	
		return getValue(key, LocaleController.getLocale());
	}
}
