package api.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import beans.relation.summary.SummaryStElem;
import beans.relation.summary.SummaryStNumeric;
import beans.relation.summary.SummaryStatement;
import beans.relation.summary.SummaryStatementSQ;
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
			PatientIllnessScript expScript = (PatientIllnessScript) new DBClinReason().selectExpertPatIllScriptByVPId(userPatientIllnesScript.getVpId());
			expScript.getSummStStage();
			
			ScoreBean scoreBean = new ScoreBean(userPatientIllnesScript, userPatientIllnesScript.getSummStId(), ScoreBean.TYPE_SUMMST, userPatientIllnesScript.getStage());
			if(expScript!=null && expScript.getSummSt()!=null){
				ScoringSummStAction action = new ScoringSummStAction();
				st = new SummaryStatementController().initSummStRating(expScript, userPatientIllnesScript, action);	
				action.doScoring(st, expScript.getSummSt());
			}
			
			if (st != null) {
				resultObj.put("status", "ok");
				this.addSummaryStatementToResultObj(resultObj, userPatientIllnesScript, st);
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
	
	// --------- helper ------------------------------------------------------------------------
	
	void addToResultObj(Map resultObj, String key, Object value) {
		resultObj.put(key, value != null ? value : "-");
	}
	
	void addSummaryStatementToResultObj(Map resultObj, PatientIllnessScript userPatientIllnesScript, SummaryStatement st) {
		this.addToResultObj(resultObj, "userPatientIllnesScript.id", userPatientIllnesScript.getId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.userId", userPatientIllnesScript.getUserId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.vpId", userPatientIllnesScript.getVpId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.stage", userPatientIllnesScript.getStage());
		
		this.addToResultObj(resultObj, "SummaryStatement.text", st.getText());
		this.addToResultObj(resultObj, "SummaryStatement.lang", st.getLang());
		this.addToResultObj(resultObj, "SummaryStatement.analyzed", st.isAnalyzed());
		
		//this.addToResultObj(resultObj, "SummaryStatement.sqHits", st.getSqHits() );
		//this.addToResultObj(resultObj, "SummaryStatement.itemHits", st.getItemHits());
		
		this.addToResultObj(resultObj, "SummaryStatement.sqScore", st.getSqScore());
		this.addToResultObj(resultObj, "SummaryStatement.sqScoreBasic", st.getSqScoreBasic());
		this.addToResultObj(resultObj, "SummaryStatement.sqScorePerc", st.getSqScorePerc());
		
		this.addToResultObj(resultObj, "SummaryStatement.transformationScore", st.getTransformationScore());
		this.addToResultObj(resultObj, "SummaryStatement.transformScorePerc", st.getTransformScorePerc());
		
		this.addToResultObj(resultObj, "SummaryStatement.narrowingScore", st.getNarrowingScore());
		this.addToResultObj(resultObj, "SummaryStatement.personScore", st.getPersonScore());

		//this.addToResultObj(resultObj, "SummaryStatement.units", st.getUnits());
		this.addToResultObj(resultObj, "SummaryStatement.transformNum", st.getTransformNum());

		this.addToResultObj(resultObj, "SummaryStatement.accuracyScore", st.getAccuracyScore());
		this.addToResultObj(resultObj, "SummaryStatement.globalScore", st.getGlobalScore());
	}
}
