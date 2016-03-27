package actions.beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import beans.relation.RelationManagement;
import database.DBClinReason;

public class AddMngAction implements AddAction, Scoreable{
	
	private PatientIllnessScript patIllScript;
	
	public AddMngAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	

	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(java.beans.Beans)
	 */
	public void save(Beans b) { new DBClinReason().saveAndCommit(b);}

	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation rel) {
		LogEntry le = new LogEntry(LogEntry.ADDMNG_ACTION, patIllScript.getSessionId(), rel.getSourceId());
		le.save();
	}

	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String, java.lang.String)
	 */
	public void add(String idStr, String name) {
		long id = Long.valueOf(idStr.trim());
		addMng(id, name, -1, -1);
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
		
		addMng(id, name, (int)x, (int)y);
	}
	
	private void addMng(long id, String name, int x, int y){
		if(patIllScript.getMngs()==null) patIllScript.setMngs(new ArrayList<RelationManagement>());
		RelationManagement relMng = new RelationManagement(id, patIllScript.getId());		
		if(patIllScript.getMngs().contains(relMng)){
			createErrorMessage("Problem already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		relMng.setOrder(patIllScript.getMngs().size());
		if(x<0 && y<0){//setDefault x,y for problem
			Point p = calculateNewItemPosInCanvas();
			relMng.setX(p.x);
			relMng.setY(p.y);
		}
		else{ //problem has been created from the concept map, therefore we have a position
			relMng.setX(x);
			relMng.setY(y);
		}
		patIllScript.getMngs().add(relMng);
		relMng.setManagement(new DBClinReason().selectListItemById(id));
		save(relMng);
		notifyLog(relMng);
		triggerScoringAction(relMng);				
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getProblems()!=null || !patIllScript.getMngs().isEmpty()){
			y = patIllScript.getProblems().size() * 25; //CAVE max y! 
		}
		return new Point(RelationManagement.DEFAULT_X,y);
	}

	@Override
	public void triggerScoringAction(Beans rel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createErrorMessage(String summary, String details, Severity sev) {
		// TODO Auto-generated method stub
		
	}

}
