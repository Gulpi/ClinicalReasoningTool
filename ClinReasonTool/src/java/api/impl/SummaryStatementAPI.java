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
import application.AppBean;
import beans.relation.summary.SummaryStatement;
import beans.scoring.LearningAnalyticsBean;
import beans.scoring.PeerContainer;
import beans.scoring.ScoreBean;
import beans.scoring.ScoreContainer;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;
import controller.PeerSyncController;
import controller.SummaryStatementController;
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
			SummaryStatement st = null;
			PatientIllnessScript userPatientIllnesScript = new DBClinReason().selectLearnerPatIllScript(id, "id");
			PatientIllnessScript expScript = AppBean.getExpertPatIllScript(userPatientIllnesScript.getVpId());
			expScript.getSummStStage();
			
			ScoreBean scoreBean = new ScoreBean(userPatientIllnesScript, userPatientIllnesScript.getSummStId(), ScoreBean.TYPE_SUMMST, userPatientIllnesScript.getStage());
			if(expScript!=null && expScript.getSummSt()!=null){
				ScoringSummStAction action = new ScoringSummStAction();
				st = new SummaryStatementController().initSummStRating(expScript, userPatientIllnesScript, action);	
				
			}
			
			if (st != null) {
				resultObj.put("status", "ok");
				resultObj.put("SummaryStatement", st);

			}
			else {
				resultObj.put("status", "error");
				resultObj.put("errorMsg", "no SummaryStatement object ?");
			}
		}
		else {
			resultObj.put("status", "error");
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
