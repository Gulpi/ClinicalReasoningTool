package api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import actions.scoringActions.ScoringSummStAction;
import api.ApiInterface;
import application.AppBean;
import beans.relation.summary.SummaryStatement;
import beans.scoring.ScoreBean;
import beans.scripts.PatientIllnessScript;
import controller.JsonCreator;
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
	AppBean appBean = null;
	ReScoreThread thread = null;
	ReScoreThread lastThread = null;
	
	public SummaryStatementAPI() {
	}
	
	/**
	 * needs to be initialized, to be available alos from Threads, which do NOT have a Faces context!!!
	 * @return
	 */
	public AppBean getAppBean(){
		if (appBean == null) {
			ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			appBean = (AppBean) context.getAttribute(AppBean.APP_KEY);
		}
		
		return appBean;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new TreeMap();
		
		try {
			this.getAppBean();
			ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			new JsonCreator().initJsonExport(context);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
				PatientIllnessScript userPatientIllnesScript = new DBClinReason().selectPatIllScriptById(id);
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
					thread.setType(StringUtilities.getIntegerFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("type"), PatientIllnessScript.TYPE_LEARNER_CREATED));
					thread.setStartDate(StringUtilities.getDateFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("start_date"), null));
					thread.setEndDate(StringUtilities.getDateFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("end_date"), null));
					thread.setLoadNodes(StringUtilities.getBooleanFromString((String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("load_nodes"), false));
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
		
		PatientIllnessScript expScript = getAppBean().addExpertPatIllnessScriptForVpId(userPatientIllnesScript.getVpId());
		expScript.getSummStStage();
		
		ScoreBean scoreBean = new ScoreBean(userPatientIllnesScript, userPatientIllnesScript.getSummStId(), ScoreBean.TYPE_SUMMST, userPatientIllnesScript.getCurrentStage());
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
		this.addToResultObj(resultObj, "UserPatientIllnesScript.id", userPatientIllnesScript.getId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.userId", userPatientIllnesScript.getUserId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.vpId", userPatientIllnesScript.getVpId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.stage", userPatientIllnesScript.getCurrentStage());
	}
	
	void addSummaryStatementToResultObj(Map resultObj, PatientIllnessScript userPatientIllnesScript, SummaryStatement st) {
		this.addToResultObj(resultObj, "UserPatientIllnesScript.id", userPatientIllnesScript.getId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.userId", userPatientIllnesScript.getUserId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.vpId", userPatientIllnesScript.getVpId());
		this.addToResultObj(resultObj, "UserPatientIllnesScript.stage", userPatientIllnesScript.getCurrentStage());
		
		this.addToResultObj(resultObj, "SummaryStatement.text", st.getText());
		this.addToResultObj(resultObj, "SummaryStatement.lang", st.getLang());
		this.addToResultObj(resultObj, "SummaryStatement.analyzed", st.isAnalyzed());
		this.addToResultObj(resultObj, "SummaryStatement.creationDate", st.getCreationDate());
		
		//this.addToResultObj(resultObj, "SummaryStatement.sqHits", st.getSqHits() );
		//this.addToResultObj(resultObj, "SummaryStatement.itemHits", st.getItemHits());
		
		this.addToResultObj(resultObj, "SummaryStatement.sqScore", st.getSqScore());
		this.addToResultObj(resultObj, "SummaryStatement.sqScoreBasic", st.getSqScoreBasic());
		this.addToResultObj(resultObj, "SummaryStatement.sqScorePerc", st.getSqScorePerc());
		
		this.addToResultObj(resultObj, "SummaryStatement.transformationScore", st.getTransformationScore());
		this.addToResultObj(resultObj, "SummaryStatement.transformScorePerc", st.getTransformScorePerc());
		
		this.addToResultObj(resultObj, "SummaryStatement.narrowingScore", st.getNarrowingScore());
		this.addToResultObj(resultObj, "SummaryStatement.narr1Score", st.getNarr1Score());
		this.addToResultObj(resultObj, "SummaryStatement.narr2Score", st.getNarr2Score());
		this.addToResultObj(resultObj, "SummaryStatement.narrowingScoreNew", st.getNarrowingScoreNew());
		
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
		int type =  PatientIllnessScript.TYPE_LEARNER_CREATED;
		Date startDate = null;
		Date endDate = null;
		SummaryStatementAPI ctrl = null;
		List<Map> results = new ArrayList<Map>();
		boolean loadNodes = false;
		
		int count = -1;
		int idx = -1;

		@Override
		public void run() {
			 try{
				 List<PatientIllnessScript> userPatientIllnesScripts = new DBClinReason().selectLearnerPatIllScriptsByNotAnalyzedSummSt(max, startDate, endDate, type, loadNodes);
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
							Map result1 = new TreeMap();
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

		public boolean isLoadNodes() {
			return loadNodes;
		}

		public void setLoadNodes(boolean loadNodes) {
			this.loadNodes = loadNodes;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
		
		
	}
}
