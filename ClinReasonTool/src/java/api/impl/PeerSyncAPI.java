package api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.ApiInterface;
import beans.scoring.PeerContainer;
import controller.PeerSyncController;
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
public class PeerSyncAPI implements ApiInterface {
	PeerContainer peers = new PeerContainer();
	PeerSyncAPIThread thread = null;
	PeerSyncAPIThread lastThread = null;
	
	public PeerSyncAPI() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new HashMap();
		PeerSyncAPIThread mythread = thread;
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
			if (mythread == null) {
				thread = new PeerSyncAPIThread();
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
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (CasusConfiguration.getGlobalBooleanValue("LearningAnalytics1.prettyJSON", true)) {
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
	public void fillStatus(@SuppressWarnings("rawtypes") Map resultObj, PeerSyncAPIThread mythread) {
		resultObj.put("result", "running");
		resultObj.put("started", mythread.getStarted().toString());
		resultObj.put("sync_max", mythread.getCtrl().getSync_idx());
		resultObj.put("sync_idx", mythread.getCtrl().getSync_idx());
		resultObj.put("query_max", mythread.getMax());
		resultObj.put("query_start_date", mythread.getStartDate());
		resultObj.put("query_end_date", mythread.getEndDate());
	}
	
	
	
	class PeerSyncAPIThread extends Thread {
		PeerSyncController ctrl;
		Date started = new Date();
		int max = 100;
		Date startDate = null;
		Date endDate = null;

		@Override
		public void run() {
			 try{
				 ctrl =  new PeerSyncController(peers);
				 ctrl.sync(max,startDate,endDate);
			 }
			 catch(Exception e){
				 CRTLogger.out("PeerSyncAPIThread(): " +Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			 } 
			 
			 lastThread = thread;
			 thread = null;
		}

		public Date getStarted() {
			return started;
		}

		public void setStarted(Date started) {
			this.started = started;
		}

		public PeerSyncController getCtrl() {
			return ctrl;
		}

		public void setCtrl(PeerSyncController ctrl) {
			this.ctrl = ctrl;
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
		
		
	}
}
