package beanActions;

import java.awt.Point;
import java.beans.Beans;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import beans.*;
import beans.relation.*;
import database.DBClinReason;
import errors.WrongProblemError;
import scoringActions.ProblemScoring;

public class AddProblemAction implements AddAction{

	private PatientIllnessScript patIllScript;
	
	public AddProblemAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#add(java.lang.String)
	 */
	public void add(String idStr, String name){ 
		//addProblem(idStr, name);
		long id = Long.valueOf(idStr.trim());
		addProblem(id, name, -1, -1);
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
		
		addProblem(id, name, (int)x, (int)y);
	}
	
	/*private void addProblem(String idStr, String name){
		long id = Long.valueOf(idStr.trim());
		addProblem(id, name);
	}*/
	
	private void addProblem(long id, String name, int x, int y){
		if(patIllScript.getProblems()==null) patIllScript.setProblems(new ArrayList<RelationProblem>());
		RelationProblem relProb = new RelationProblem(id, patIllScript.getId());		
		if(patIllScript.getProblems().contains(relProb)){
			createErrorMessage("Problem already assigned.","optional details", FacesMessage.SEVERITY_WARN);
			return;
		}
		relProb.setOrder(patIllScript.getProblems().size());
		if(x<0 && y<0){//setDefault x,y for problem
			Point p = calculateNewItemPosInCanvas();
			relProb.setX(p.x);
			relProb.setY(p.y);
		}
		else{ //problem has been created from the concept map, therefore we have a position
			relProb.setX(x);
			relProb.setY(y);
		}
		patIllScript.getProblems().add(relProb);
		relProb.setProblem(new DBClinReason().selectListItemById(id));
		save(relProb);
		notifyLog(relProb);
		initScoreCalc(relProb);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#createErrorMessage(java.lang.String, java.lang.String, javax.faces.application.FacesMessage.Severity)
	 */
	public void createErrorMessage(String summary, String details, Severity sev){
		 // MyFacesContextFactory factory = (MyFacesContextFactory) FactoryFinder.getFactory("MyFacesContextFactory");
		 // CRTFacesContext facesContext = factory.getFacesContextBySessionId(patIllScript.getSessionId());
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		facesContext.addMessage("",new FacesMessage(sev, summary,details));
	}
	
	/**
	 * we calculate a position for the new item. 
	 * TODO: we could check whether the position is already taken,or others are vacant due to deleting of others
	 * @return
	 */
	private Point calculateNewItemPosInCanvas(){
		int y = 5;
		if(patIllScript.getProblems()!=null || !patIllScript.getProblems().isEmpty()){
			y = patIllScript.getProblems().size() * 25; //CAVE max y! 
		}
		return new Point(RelationProblem.DEFAULT_X,y);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){ new DBClinReason().saveAndCommit(b); }
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relProb){
		LogEntry le = new LogEntry(LogEntry.ADDPROBLEM_ACTION, patIllScript.getSessionId(), relProb.getSourceId());
		le.save();
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#initScoreCalc(beans.relation.Relation)
	 */
	public void initScoreCalc(Relation relProb){
		try{
			new ProblemScoring().calcScoreForAddProblem(patIllScript, relProb);
		}
		catch(WrongProblemError wpe){}
	}
}
