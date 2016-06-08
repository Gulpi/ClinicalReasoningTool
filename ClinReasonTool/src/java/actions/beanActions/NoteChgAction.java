package actions.beanActions;

import java.beans.Beans;

import beans.LogEntry;
import beans.scripts.*;
import beans.Note;
import database.DBClinReason;


public class NoteChgAction /*implements ChgAction*/{

	private PatientIllnessScript patIllScript;
	private DBClinReason dcr = new DBClinReason();
	
	public NoteChgAction(){}
	public NoteChgAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.ChgAction#notifyLog(java.beans.Beans, long)
	 */
	public void notifyLog(Beans b, long newId) {
		LogEntry le = new LogEntry(LogEntry.CREATENOTE_ACTION, patIllScript.getId(), newId);
		le.save();
		
	}
	
	private void notifyLogUpdate(Beans b, long newId) {
		LogEntry le = new LogEntry(LogEntry.UPDATENOTE_ACTION, patIllScript.getId(), newId);
		le.save();		
	}
	
	/**
	 * @param summStId (not really needed)
	 * @param text
	 */
	public void updateOrCreateNote(String noteId, String text){
		if(patIllScript.getNote()==null || noteId.equals("-1")) createNote(text);
		else updateNote(text);
	}
	
	/**
	 * No summaryStatement has been created so far, so we create one, save it and attach it to the 
	 * PatientIllnessScript.
	 * @param text
	 */
	private void createNote(String text){
		Note note = new Note(text);
		save(note);
		patIllScript.setNote(note);
		patIllScript.setNoteId(note.getId());
		patIllScript.save();
		//new DBClinReason().saveAndCommit(sumSt, patIllScript);
		notifyLog(patIllScript.getNote(), patIllScript.getNote().getId());
	}
	
	/**
	 * Summary Statement already attached to PatientIllnessScript, so we just update it. 
	 * @param text
	 */
	private void updateNote(String text){
		patIllScript.getNote().setText(text);
		save(patIllScript.getNote());
		notifyLogUpdate(patIllScript.getNote(), patIllScript.getNote().getId());
	}
	
	/* (non-Javadoc)
	 * @see beanActions.ChgAction#save(java.beans.Beans)
	 */
	public void save(Beans b) {
		dcr.saveAndCommit(b);
		
	}

}
