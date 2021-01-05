package api.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import api.impl.PeerSyncAPI.PeerSyncAPIThread;
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
	ReScoreThread thread = null;
	ReScoreThread lastThread = null;
	
	public SummaryStatementAPI() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new HashMap();
		
		ReScoreThread mythread = thread;
		String status = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("status");

		if (StringUtilities.isValidString(status) && status.equalsIgnoreCase("true")) {
			if (mythread != null) {
				fillStatus(resultObj, mythread);
			}
			else if (lastThread != null) {
				fillStatus(resultObj, lastThread);
			}
			else {
				resultObj.put("result", "no status available!");
			}
		}
		else {
			long id = StringUtilities.getLongFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"), -1);
			if (id > 0) {
				PatientIllnessScript userPatientIllnesScript = new DBClinReason().selectLearnerPatIllScript(id, "id");
				SummaryStatement st = this.handleByPatientIllnessScript(userPatientIllnesScript);
				
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
				if (mythread == null) {
					thread = new ReScoreThread();
					thread.setCtrl(this);
					thread.setMax(StringUtilities.getIntegerFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("max"), 100));
					thread.setStartDate(StringUtilities.getDateFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("start_date"), null));
					thread.setEndDate(StringUtilities.getDateFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("end_date"), null));
					mythread = thread;
					mythread.start();
					
					resultObj.put("result", "started");
				}
				else {
					fillStatus(resultObj, mythread);
				}
			}
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
	
	@SuppressWarnings("unchecked")
	public void fillStatus(@SuppressWarnings("rawtypes") Map resultObj, ReScoreThread mythread) {
		resultObj.put("result", "running");
		resultObj.put("started", mythread.getStarted().toString());
		if (mythread.getFinished() != null) {
			resultObj.put("finished", mythread.getFinished().toString());
		}
		resultObj.put("sync_max", mythread.getCount());
		resultObj.put("sync_idx", mythread.getIdx());
		
		resultObj.put("query_max", mythread.getMax());
		resultObj.put("query_start_date", mythread.getStartDate());
		resultObj.put("query_end_date", mythread.getEndDate());
		
		String details = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("details");
		if (details != null && details.equalsIgnoreCase("true")) {
			resultObj.put("details", mythread.getResults());
		}
	}
	
	public SummaryStatement handleByPatientIllnessScript(PatientIllnessScript userPatientIllnesScript) {
		SummaryStatement st = null;
		PatientIllnessScript expScript = (PatientIllnessScript) new DBClinReason().selectExpertPatIllScriptByVPId(userPatientIllnesScript.getVpId());
		expScript.getSummStStage();
		
		ScoreBean scoreBean = new ScoreBean(userPatientIllnesScript, userPatientIllnesScript.getSummStId(), ScoreBean.TYPE_SUMMST, userPatientIllnesScript.getRawStage());
		if(expScript!=null && expScript.getSummSt()!=null){
			ScoringSummStAction action = new ScoringSummStAction();
			st = new SummaryStatementController().initSummStRating(expScript, userPatientIllnesScript, action);	
			action.doScoring(st, expScript.getSummSt());
		}
		
		return st;
	}
	
	// --------- helper ------------------------------------------------------------------------
	
	void addToResultObj(Map resultObj, String key, Object value) {
		resultObj.put(key, value != null ? value : "-");
	}
	
	void addSummaryStatementToResultObj(Map resultObj, PatientIllnessScript userPatientIllnesScript) {
		this.addToResultObj(resultObj, "userPatientIllnesScript.id", userPatientIllnesScript.getId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.userId", userPatientIllnesScript.getUserId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.vpId", userPatientIllnesScript.getVpId());
		this.addToResultObj(resultObj, "userPatientIllnesScript.stage", userPatientIllnesScript.getStage());
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
	
	class ReScoreThread extends Thread {
		Date started = new Date();
		Date finished = null;
		int max = 100;
		Date startDate = null;
		Date endDate = null;
		SummaryStatementAPI ctrl = null;
		List<Map> results = new ArrayList<Map>();
		
		int count = -1;
		int idx = -1;

		@Override
		public void run() {
			 try{
				 List<PatientIllnessScript> userPatientIllnesScripts = new DBClinReason().selectLearnerPatIllScriptsByNotAnalyzedSummSt(max, startDate, endDate);
				 if (userPatientIllnesScripts != null) {
					 this.count = userPatientIllnesScripts.size();
					 this.idx = 0;
					 Iterator<PatientIllnessScript> it = userPatientIllnesScripts.iterator();
					 while (it.hasNext()) {
						 PatientIllnessScript userPatientIllnesScript = it.next();
						 idx++;
						 try {
							SummaryStatement st = ctrl.handleByPatientIllnessScript(userPatientIllnesScript);
							 if (st != null) {
								Map result1 = new HashMap();
								ctrl.addSummaryStatementToResultObj(result1, userPatientIllnesScript, st);
								results.add(result1);
							 }
						} catch (Throwable e) {
							Map result1 = new HashMap();
							ctrl.addSummaryStatementToResultObj(result1, userPatientIllnesScript);
							ctrl.addToResultObj(result1, "exception", Utility.stackTraceToString(e));
							results.add(result1);
						}
					 }
				 }
			 }
			 catch(Exception e){
				 CRTLogger.out("PeerSyncAPIThread(): " +Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			 } 
			 
			 this.setFinished(new Date());
			 lastThread = thread;
			 thread = null;
		}

		public Date getStarted() {
			return started;
		}

		public void setStarted(Date started) {
			this.started = started;
		}
		
		public Date getFinished() {
			return finished;
		}

		public void setFinished(Date finished) {
			this.finished = finished;
		}		

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public SummaryStatementAPI getCtrl() {
			return ctrl;
		}

		public void setCtrl(SummaryStatementAPI ctrl) {
			this.ctrl = ctrl;
		}

		public List<Map> getResults() {
			return results;
		}

		public void setResults(List<Map> results) {
			this.results = results;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getIdx() {
			return idx;
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}
		
		
	}
}
