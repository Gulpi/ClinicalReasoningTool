package api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.AbstractAPIImpl;
import controller.ScriptCopyController;
import net.casus.util.CasusConfiguration;
import net.casus.util.Utility;
import util.CRTLogger;

/**
 * simple JSON Webservice for simple API JSON framework
 * 
 * <base>/crt/src/html/api/api.xhtml?impl=peerSync
 * should either start a new thread, or return basic running thread data!
 *
 * @author Gulpi (=Martin Adler)
 */
public class PreCheckAPI extends AbstractAPIImpl {
	
	public PreCheckAPI() {
	}
	
	@Override
	public String handle() {
		String result = "";
		Map resultObj = new HashMap();
 		List<Map> props = new ArrayList<Map>();
		try {
			String in_case = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_case");
			String dst_lang = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_dst_lang");
			
			String checkResult = new ScriptCopyController().checkExpMap(in_case, dst_lang);
			resultObj.put("status", "ok");
			resultObj.put("result", checkResult!=null ? checkResult:"" );
			ObjectMapper mapper = new ObjectMapper();
			try {
				if (CasusConfiguration.getGlobalBooleanValue("SummaryStatementAPI.prettyJSON", true)) {
					result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultObj);
				}
				else {
					result = mapper.writeValueAsString(resultObj);
				}
			} catch (JsonProcessingException e) {
				result = e.getMessage();
			}
			
			CRTLogger.out("PreCheckAPI.handle in_case:" + in_case + "; dst_lang:" + dst_lang + " -> result:" + result, CRTLogger.LEVEL_PROD);
		} catch (Exception e) {
			CRTLogger.out("PreCheckAPI.handle x:" + Utility.stackTraceToString(e), CRTLogger.LEVEL_PROD);
		}
		return result;
	}
}
