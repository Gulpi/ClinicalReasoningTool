package actions.beanActions;

import java.beans.Beans;

import beans.LogEntry;
import beans.relation.SummaryStatement;
import beans.scripts.*;
import database.DBClinReason;


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
		le.save();
		
	}
	
	private void notifyLogUpdate(Beans b, long newId) {
		LogEntry le = new LogEntry(LogEntry.UPDATESUMMST_ACTION, patIllScript.getId(), newId);
		le.save();		
	}
	
	/**
	 * @param summStId (not really needed)
	 * @param text
	 */
	public void updateOrCreateSummaryStatement(String summStId, String text){
		if(patIllScript.getSummSt()==null || summStId.equals("-1")) createSummaryStatement(text);
		else updateSummaryStatement(text);
	}
	
	/**
	 * No summaryStatement has been created so far, so we create one, save it and attach it to the 
	 * PatientIllnessScript.
	 * @param text
	 */
	private void createSummaryStatement(String text){
		SummaryStatement sumSt = new SummaryStatement(text);
		save(sumSt);
		patIllScript.setSummSt(sumSt);
		patIllScript.setSummStId(sumSt.getId());
		patIllScript.save();
		//new DBClinReason().saveAndCommit(sumSt, patIllScript);
		notifyLog(patIllScript.getSummSt(), patIllScript.getSummSt().getId());
	}
	
	/**
	 * Summary Statement already attached to PatientIllnessScript, so we just update it. 
	 * @param text
	 */
	private void updateSummaryStatement(String text){
		if(patIllScript.getSummSt().getText()!=null && patIllScript.getSummSt().getText().equals(text)) 
			return; //nothing to do, user made no change....
		patIllScript.getSummSt().setText(text);
		save(patIllScript.getSummSt());
		notifyLogUpdate(patIllScript.getSummSt(), patIllScript.getSummSt().getId());
	}
	
	/* (non-Javadoc)
	 * @see beanActions.ChgAction#save(java.beans.Beans)
	 */
	public void save(Beans b) {
		dcr.saveAndCommit(b);
		
	}

}
