package api.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

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
import database.HibernateUtil;
import net.casus.util.CasusConfiguration;
import net.casus.util.StringUtilities;
import net.casus.util.Utility;
import net.casus.util.database.HQLQuery;
import net.casus.util.io.IOUtilities;
import net.casus.util.nlp.spacy.SpacyStructureStats;
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
			String src_lang = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_src_lang");
			String dst_lang = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_dst_lang");
		} catch (HibernateException e) {
		}
		return result;
	}
}
