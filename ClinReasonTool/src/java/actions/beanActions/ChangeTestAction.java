package actions.beanActions;

import java.beans.Beans;

import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.*;
import database.DBClinReason;
import database.DBList;
import model.ListItem;

/**
 * A ProblemRelation is changed and a different Problem object attached, id remains the same, so, no
 * other changes necessary.
 * @author ingahege
 *
 */
public class ChangeTestAction implements ChgAction, Scoreable{

	private PatientIllnessScript patIllScript;
	
	public ChangeTestAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void changeTest(String oldTestIdStr, String newTestIdStr){
		long oldTestId = Long.valueOf(oldTestIdStr.trim());
		long newTestId = Long.valueOf(newTestIdStr.trim());
		changeTest(oldTestId, newTestId);
	}
	
	public void changeTest(long newProbId, long probRel){
		RelationTest testToChg = patIllScript.getTestById(probRel);
		ListItem oldTest = new DBList().selectListItemById(testToChg.getListItemId());
		ListItem newTest = new DBList().selectListItemById(newProbId);
		if(testToChg!=null && newTest!=null && oldTest!=null){
			notifyLog(testToChg, newProbId);
			testToChg.setTest(newTest);
			testToChg.setListItemId(newTest.getItem_id());
			save(testToChg);		
		}
		//else -> error...
	}
	
	public void notifyLog(Beans testToChg, long newTestId){
		LogEntry le = new LogEntry(LogEntry.CHGTEST_ACTION, patIllScript.getId(), ((Relation)testToChg).getListItemId(), newTestId);
		le.save();
	}
	
	public void save(Beans rel){
		new DBClinReason().saveAndCommit(rel);
	}

	@Override
	public void triggerScoringAction(Beans beanToScore) {
		// TODO Auto-generated method stub
		
	}
}
