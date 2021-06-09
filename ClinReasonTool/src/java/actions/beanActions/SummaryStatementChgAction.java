package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.ScoringSummStAction;
import beans.LogEntry;
import beans.relation.summary.SummaryStatement;
import beans.scripts.*;
import database.DBClinReason;
import net.casus.util.Utility;
import util.CRTLogger;


public class SummaryStatementChgAction /*extends ChgAction*/{

	private PatientIllnessScript patIllScript;
	private DBClinReason dcr = new DBClinReason();
	
	public SummaryStatementChgAction(){}
	public SummaryStatementChgAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.ChgAction#notifyLog(java.beans.Beans, long)
	 */
	public void notifyLog(Beans b, long newId) {
		LogEntry le = new LogEntry(LogEntry.CREATESUMMST_ACTION, patIllScript.getId(), newId);
		try{
			if(patIllScript.getSummSt()!=null) le.setSourceText(patIllScript.getSummSt().getText());
		}
		catch(Exception e){}
		le.save();
		
	}
	
	private void notifyLogUpdate(Beans b, long newId) {
		LogEntry le = new LogEntry(LogEntry.UPDATESUMMST_ACTION, patIllScript.getId(), newId);
		try{
			if(patIllScript.getSummSt()!=null) le.setSourceText(patIllScript.getSummSt().getText());
		}
		catch(Exception e){}
		le.save();		
	}
	
	/**
	 * @param summStId (not really needed)
	 * @param text
	 */
	public void updateOrCreateSummaryStatement(String summStId, String text){
		if(patIllScript.getSummSt()==null /*|| summStId.equals("-1")*/) createSummaryStatement(text);
		else updateSummaryStatement(text);
	}
	
	public void updateSummaryStatementStage(int stage) {
		if(this.patIllScript.getSummSt()==null && this.patIllScript.getId()<=0) //should not happen
			createSummaryStatement("");
		
		this.patIllScript.getSummSt().setStage(stage);
		save(this.patIllScript.getSummSt());
	}
	public void updateSummaryStatementStage(String stage) {
		try {
			updateSummaryStatementStage(Integer.valueOf(stage).intValue());
		}
		catch(Exception e) {
			CRTLogger.out(Utility.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	/**
	 * No summaryStatement has been created so far, so we create one, save it and attach it to the 
	 * PatientIllnessScript.
	 * @param text
	 */
	private void createSummaryStatement(String text){
		SummaryStatement sumSt = new SummaryStatement(text, patIllScript.getId());
		if(patIllScript.isExpScript()) sumSt.setStage(patIllScript.getStage());
		else sumSt.setStage(patIllScript.getCurrentStage());
		sumSt.setType(patIllScript.getType());
		sumSt.setLang(patIllScript.getLocale().getLanguage());
		save(sumSt);
		
		patIllScript.setSummSt(sumSt);
		patIllScript.setSummStId(sumSt.getId());
		patIllScript.save();
		notifyLog(patIllScript.getSummSt(), patIllScript.getSummSt().getId());
		//new ScoringSummStAction().scoreAction(patIllScript, patIllScript.getCurrentStage());
		save(sumSt); //save again, because now we have analyzed it (flag is set to true)

	}
	
	/**
	 * Summary Statement already attached to PatientIllnessScript, so we just update it. 
	 * @param text
	 */
	private void updateSummaryStatement(String text){
		if(patIllScript.getSummSt().getText()!=null && patIllScript.getSummSt().getText().equals(text)) 
			return; //nothing to do, user made no change....
		patIllScript.getSummSt().setText(text);
		//we have to make sure that we reanalyze the text and delete all SumSttSQ obejcts
		if(patIllScript.getSummSt().getSqHits()!=null) {
			new DBClinReason().deleteAndCommit(patIllScript.getSummSt().getSqHits());
			patIllScript.getSummSt().setSqHits(null);
		}
		save(patIllScript.getSummSt());
		notifyLogUpdate(patIllScript.getSummSt(), patIllScript.getSummSt().getId());
		//we no longer score it on update but at end of session!
		//new ScoringSummStAction().scoreAction(patIllScript, patIllScript.getCurrentStage());
	}
	
	/* (non-Javadoc)
	 * @see beanActions.ChgAction#save(java.beans.Beans)
	 */
	public void save(Beans b) {
		dcr.saveAndCommit(b);
		
	}

}
