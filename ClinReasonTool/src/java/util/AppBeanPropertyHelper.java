package util;

import java.util.List;

import application.AppBean;
import net.casus.util.StringUtilities;

/**
 * some helpers accessing the AppBean properties for some types
 * 
 * @author admin
 *
 */
public class AppBeanPropertyHelper {

	/**
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	static public String[] getArray(String prefix, String type, String[] default_value) {
		return AppBeanPropertyHelper.getArray(prefix+type, default_value);
	}
	
	/**
	 * @param key
	 * @param default_value
	 * @return
	 */
	static public String[] getArray(String key, String[] default_value) {
		String array_string = AppBean.getProperty(key,null);
		if (array_string == null || array_string.length()==0) {
			return default_value;
		}
	
		return net.casus.util.StringUtilities.getStringArrayFromString(array_string, ",");
	}

	/**
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	public static List<String> getStringList(String prefix, String type, List<String> default_value) {
		return AppBeanPropertyHelper.getStringList(prefix+type, default_value);

	}
	
	/**
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static List<String> getStringList(String key, List<String> default_value) {
		String list_string = AppBean.getProperty(key,null);
		if (list_string == null || list_string.length()==0) {
			return default_value;
		}
	
		return net.casus.util.StringUtilities.getStringListFromString(list_string, ",");
	}

	/**
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	public static int getInt(String prefix, String type, int default_value) {
		return AppBeanPropertyHelper.getInt(prefix+type, default_value);

	}
	
	/**
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static int getInt(String key, int default_value) {
		String int_string = AppBean.getProperty(key,null);
		if (int_string == null || int_string.length()==0) {
			return 0;
		}
	
		return net.casus.util.StringUtilities.getIntegerFromString(int_string, default_value);
	}
	

	/**
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	public static long getLong(String prefix, String type, long default_value) {
		return AppBeanPropertyHelper.getLong(prefix+type, default_value);

	}
	
	/**
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static long getLong(String key, long default_value) {
		String int_string = AppBean.getProperty(key,null);
		if (int_string == null || int_string.length()==0) {
			return 0;
		}
	
		return net.casus.util.StringUtilities.getLongFromString(int_string, default_value);
	}

	/**
	 * @param prefix
	 * @param type
	 * @param default_value
	 * @return
	 */
	public static boolean getBoolean(String prefix, String type, boolean default_value) {
		return AppBeanPropertyHelper.getBoolean(prefix+type, default_value);

	}
	
	/**
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static boolean getBoolean(String key, boolean default_value) {
		String boolean_string = AppBean.getProperty(key,null);
		if (boolean_string == null || boolean_string.length()==0) {
			return false;
		}
	
		return net.casus.util.StringUtilities.getBooleanFromString(boolean_string, default_value);
	}

}
