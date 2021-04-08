package api.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import actions.scoringActions.ScoringSummStAction;
import api.AbstractAPIImpl;
import api.ApiInterface;
import application.AppBean;
import beans.list.ListItem;
import beans.relation.summary.SummaryStElem;
import beans.relation.summary.SummaryStatement;
import beans.relation.summary.SummaryStatementSQ;
import beans.scoring.ScoreBean;
import beans.scripts.PatientIllnessScript;
import controller.JsonCreator;
import controller.SummaryStatementController;
import database.DBClinReason;
import database.DBList;
import net.casus.util.CasusConfiguration;
import net.casus.util.StringUtilities;
import net.casus.util.Utility;
import net.casus.util.io.IOUtilities;
import net.casus.util.nlp.spacy.SpacyStructureStats;
import util.CRTLogger;

/**
 * simple JSON Webservice for simple API JSON framework
 * 
 * <base>/crt/src/html/api/api.xhtml?impl=spacy
 * should either start a new thread, or return basic running thread data!
 *
 * @author Gulpi (=Martin Adler)
 */
public class SpacyStructureStatsAPI extends AbstractAPIImpl {
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized String handle() {
		String result = null;
		@SuppressWarnings("rawtypes")
		Map resultObj = new TreeMap();
		
		try {
			// make sure appBean is there / in internal thread we don't have contexts!!
			this.init();
			
			// make sure lists are created already!
			if (SummaryStatementController.getListItemsByLang("de") == null || SummaryStatementController.getListItemsByLang("en") == null) {
				ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
				new JsonCreator().initJsonExport(context);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		SpacyStructureStats spacyStructureStats =  SpacyStructureStats.resetInstance();
		resultObj.put("spacyStructureStats.getHitMap", spacyStructureStats.getHitMap());
			
		
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
