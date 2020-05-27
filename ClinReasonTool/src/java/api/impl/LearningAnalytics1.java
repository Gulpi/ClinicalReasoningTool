package api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.ApiInterface;
import database.HibernateUtil;
import net.casus.util.CasusConfiguration;
import net.casus.util.StringUtilities;
import net.casus.util.Utility;
import net.casus.util.database.HQLQuery;
import util.CRTLogger;

/**
 * Sample ApiInterface implemenation for demo
 * 
 * hqlQuery6 & 
http://m7.instruct.de:8080/crt/src/html/api/api.xhtml?impl=la&in_case=1151758_2&problems=1&diagnosis=1&management=1&tests=1&correct=1&in_ext_user=222112,208121,223276,200091,209297,222264,222369,222114,222116,225227,221513,210188,220797,237601,222181,224097,220937,246452,15739,237514,239659,221464,222205,247015,207402,226450,246352,225702,205557,208856,245105,239713,227639,240699,238449,244817,207979,210603,221674,210179,221353,182021,225339,210892,226187,222321,194601,226526,210660,232640,222224,194435,207496,223647,206186,207938,221599,226266,248619,208803,210575,210577,225513,224764,207747,210403,226461,223316,198792,226430,223352,223373,246143,223572,219310,236537,237454,202436,241491,241493,209244,160784,71500,177661,227408,234415,151796,234478,7875,156696,7900,200233,219986,232473,231054,231203,95846,231329
http://m7.instruct.de:8080/crt/src/html/api/api.xhtml?impl=la&in_case=909355_2&in_ext_user=7900,11138,
 * 
 * 
 * @author Gulpi (=Martin Adler)
 */
public class LearningAnalytics1 implements ApiInterface {

	@Override
	public String handle() {
		long startms = System.currentTimeMillis();
		CasusConfiguration.addGlobalProperties("LearningAnalytics");
		long endms = System.currentTimeMillis();
		System.out.println("init "+(endms-startms) + "ms");
		
		long query_startms = endms;
		String result = "";
		Map resultObj = new HashMap();
 		List<Map> props = new ArrayList<Map>();
		try {
			String in_case = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_case");
			String in_ext_user = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_ext_user");
			String in_local = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_locale");
			Locale locale = Locale.ENGLISH;
			if (StringUtilities.isValidString(in_local)) {
				locale = new Locale(in_local);
			}
			
			Map cell_query_parameter = new HashMap();
			cell_query_parameter.put("in_case", in_case);
			List<String> myList = StringUtilities.getStringListFromString(in_ext_user, ",");
			cell_query_parameter.put("in_ext_user", myList);
			
			HQLQuery hqlQuery = new HQLQuery("LearningAnalytics1.hqlQuery.count", "", HibernateUtil.impl);
			hqlQuery.setQuery_parameter(cell_query_parameter);
			hqlQuery.query();	
			int my_count = hqlQuery.getCount();
			resultObj.put("count", Integer.valueOf(my_count));
			
			this.handleSection(cell_query_parameter, "LearningAnalytics1.hqlQuery5" , "LearningAnalytics1.hqlQuery6", props, "problems", locale);
			this.handleSection(cell_query_parameter, "LearningAnalytics1.hqlQuery1" , "LearningAnalytics1.hqlQuery2", props, "diagnosis", locale);
			this.handleSection(cell_query_parameter, "LearningAnalytics1.hqlQuery7" , "LearningAnalytics1.hqlQuery8", props, "tests", locale);
			this.handleSection(cell_query_parameter, "LearningAnalytics1.hqlQuery3" , "LearningAnalytics1.hqlQuery4", props, "management", locale);
			this.handleSection(cell_query_parameter, "LearningAnalytics1.hqlQuery9" , "LearningAnalytics1.hqlQuery10", props, "correct", locale);
			this.handleSummaryStatements(cell_query_parameter, props, "summaryStatements", locale);
		} catch (HibernateException e) {
			resultObj.put("result", "error");
			resultObj.put("error", Utility.stackTraceToString(e));
		}
		
		try {
			endms = System.currentTimeMillis();
			
			resultObj.put("result", "ok");
			resultObj.put("query-time-ms", Long.toString(endms-query_startms));
			ObjectMapper mapper = new ObjectMapper();
			resultObj.put("data", props);
			
			startms = endms;
			if (CasusConfiguration.getGlobalBooleanValue("LearningAnalytics1.prettyJSON", true)) {
				result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultObj);
			}
			else {
				result = mapper.writeValueAsString(resultObj);
			}
			endms = System.currentTimeMillis();
			CRTLogger.out("LearningAnalytics1.handle " +  (endms-startms), CRTLogger.LEVEL_PROD);
		} catch (JsonProcessingException e) {
			result = "{ result=\"error\", \"" + e.getMessage() + "\" }";
		}
		return result;
	}

	public void handleSummaryStatements(Map cell_query_parameter, List<Map> props, String key, Locale locale) {
		String key_enabled = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(key);
		int enabled = StringUtilities.getIntegerFromString(key_enabled, -1);
		
		String key_fetchmax = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(key + "_fetchmax");
		int fetchMax = StringUtilities.getIntegerFromString(key_fetchmax, 100);
		CRTLogger.out("LearningAnalytics1.handleSummaryStatements: key_enabled(" + key + "):"  + key_enabled + "," + enabled, CRTLogger.LEVEL_PROD);
		if (enabled != 1) {
			CRTLogger.out("LearningAnalytics1.handleSummaryStatements: return", CRTLogger.LEVEL_PROD);
			return;
		}
		
		Map item = new HashMap();
		props.add(item);
		
		item.put("key", key);
		item.put("type", "textlist");
		
		HQLQuery hqlQuery = new HQLQuery("LearningAnalytics1.hqlQuerySummaryStatement", "", HibernateUtil.impl);
		if (fetchMax>0) {
			hqlQuery.setCursormode(1);
			hqlQuery.setFetchMax(fetchMax);
		}
		hqlQuery.setQuery_parameter(cell_query_parameter);
		hqlQuery.query();		
		
		List<Properties> myresult = hqlQuery.resultsToPropertiesList(hqlQuery.getReturnAliases());
		if (myresult != null) {
			item.put("data", myresult);
		}
	}

	public void handleSection(Map cell_query_parameter, String hqlKey1, String hqlKey2, List<Map> props, String key, Locale locale) {
		String key_enabled = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(key);
		int enabled = StringUtilities.getIntegerFromString(key_enabled, -1);
		CRTLogger.out("LearningAnalytics1.handleSection: key_enabled(" + key + "):"  + key_enabled + "," + enabled, CRTLogger.LEVEL_PROD);
		if (enabled != 1) {
			CRTLogger.out("LearningAnalytics1.handleSection: return", CRTLogger.LEVEL_PROD);
			return;
		}
		
		Map item = new HashMap();
		props.add(item);
		
		item.put("key", key);
		item.put("type", "wordcloud");
		
		HQLQuery hqlQuery = new HQLQuery(hqlKey1, "", HibernateUtil.impl);
		hqlQuery.setQuery_parameter(cell_query_parameter);
		hqlQuery.query();
		String[] returnAliases = hqlQuery.getReturnAliases();
		
		hqlQuery.copyToQuery(hqlKey2, "", 
				CasusConfiguration.getGlobalIntegerValue("LearningAnalytics1.listItemId1.Idx", 0), 
				CasusConfiguration.getGlobalIntegerValue("LearningAnalytics1.listItemId1.Idx", 0), 
				CasusConfiguration.getIntegerArrayValue("LearningAnalytics1.srcCopyCols", "1"), 
				CasusConfiguration.getIntegerArrayValue("LearningAnalytics1.dstCopyCols", "2"));
		
		List<Properties> myresult = hqlQuery.resultsToPropertiesList(returnAliases);
		if (myresult != null) {
			item.put("data", myresult);
		}
		
	}

}
