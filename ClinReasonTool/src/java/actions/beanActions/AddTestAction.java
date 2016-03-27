package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.feedbackActions.FeedbackCreator;
import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationTest;
import database.DBClinReason;

public class AddTestAction implements AddAction, Scoreable, FeedbackCreator{
	
	private PatientIllnessScript patIllScript;
	
	public AddTestAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(java.beans.Beans)
	 */
	public void save(Beans b) { new DBClinReason().saveAndCommit(b);}

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		LogEntry le = new LogEntry(LogEntry.ADDTEST_ACTION, patIllScript.getSessionId(), rel.getSourceId());
		le.save();
	}

	/* (non-Javadoc)
	 * @see actions.beanActions.AddAction#add(java.lang.String, java.lang.String)
	 */
	public void add(String idStr, String name) {
		long id = Long.valueOf(idStr.trim());
		addTest(id, name, -1, -1);
	}
	
	/**
	 * @param idStr
	 * @param name
	 * @param xStr (e.g. "199.989894") -> we have to convert it into int
	 * @param yStr
	 */
	public void add(String idStr, String name, String xStr, String yStr){ 
		long id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());
		
		addTest(id, name, (int)x, (int)y);
	}
	
	private void addTest(long id, String name, int x, int y){
		if(patIllScript.getTests()==null) patIllScript.setTests(new ArrayList<RelationTest>());
		RelationTest relTest = new RelationTest(id, patIllScript.getId());		
		if(patIllScript.getTests().contains(relTest)){
			createErrorMessage("Test already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		relTest.setOrder(patIllScript.getTests().size());
		if(x<0 && y<0){//setDefault x,y for problem
			Point p = calculateNewItemPosInCanvas();
			relTest.setX(p.x);
			relTest.setY(p.y);
		}
		else{ //problem has been created from the concept map, therefore we have a position
			relTest.setX(x);
			relTest.setY(y);
		}
		patIllScript.getTests().add(relTest);
		relTest.setTest(new DBClinReason().selectListItemById(id));
		save(relTest);
		notifyLog(relTest);
		triggerScoringAction(relTest);				
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getTests()!=null || !patIllScript.getTests().isEmpty()){
			y = patIllScript.getTests().size() * 25; //CAVE max y! 
		}
		return new Point(RelationTest.DEFAULT_X,y);
	}

	@Override
	public void triggerScoringAction(Beans rel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createErrorMessage(String summary, String details, Severity sev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void triggerFeedbackAction() {
		// TODO Auto-generated method stub
		
	}

}
