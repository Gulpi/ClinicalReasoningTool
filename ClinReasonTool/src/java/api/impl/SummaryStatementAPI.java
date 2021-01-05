package api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import actions.scoringActions.ScoringSummStAction;
import api.ApiInterface;
import beans.scoring.PeerContainer;
import beans.scoring.ScoreBean;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;
import controller.PeerSyncController;
import database.DBClinReason;
import net.casus.util.CasusConfiguration;
import net.casus.util.StringUtilities;
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
public class SummaryStatementAPI implements ApiInterface {

	
	public SummaryStatementAPI() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new HashMap();
		
		long id = StringUtilities.getLongFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"), -1);
		if (id > 0) {
			PatientIllnessScript userPatientIllnesScript = new DBClinReason().selectLearnerPatIllScript(id, "id");
			ScoringSummStAction action = new ScoringSummStAction();
			ScoreBean scoreBean = action.scoreAction(userPatientIllnesScript, userPatientIllnesScript.getCurrentStage());
			resultObj.put("scoreBean", scoreBean);
		}
		else {
			resultObj.put("errorMsg", "userPatientIllnesScriptID invalid! " + id);
		}
		
		
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
		return result;
	}
}
