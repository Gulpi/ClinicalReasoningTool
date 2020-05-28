package api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.ApiInterface;
import beans.scoring.PeerContainer;
import controller.PeerSyncController;
import net.casus.util.CasusConfiguration;
import util.CRTLogger;
import util.StringUtilities;

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
	
	public PeerSyncAPI() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new HashMap();
		PeerSyncAPIThread mythread = thread;
		
		if (mythread == null) {
			mythread = new PeerSyncAPIThread();
			mythread.start();
			
			resultObj.put("result", "started");
		}
		else {
			resultObj.put("result", "running");
			resultObj.put("started", mythread.getStarted());
			resultObj.put("sync_max", mythread.getCtrl().getSync_idx());
			resultObj.put("sync_idx", mythread.getCtrl().getSync_idx());
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
	
	class PeerSyncAPIThread extends Thread {
		PeerSyncController ctrl;
		Date started = new Date();

		@Override
		public void run() {
			 try{
				 ctrl =  new PeerSyncController(peers);
				 ctrl.sync();
			 }
			 catch(Exception e){
				 CRTLogger.out("PeerSyncAPIThread(): " + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			 } 
			 
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
		
		
	}
}
