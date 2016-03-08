package beanActions;

import java.beans.Beans;
import java.util.*;

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
	public void add(String problemIdStr, String name){ addProblem(problemIdStr, name);}
	
	private void addProblem(String problemIdStr, String name){
		long problemId = Long.valueOf(problemIdStr.trim());
		addProblem(problemId, name);
	}
	
	private void addProblem(long problemId, String name){
		if(patIllScript.getProblems()==null) patIllScript.setProblems(new ArrayList<RelationProblem>());
		RelationProblem relProb = new RelationProblem(problemId, patIllScript.getId());
		relProb.setOrder(patIllScript.getProblems().size());
		if(patIllScript.getProblems().contains(relProb)){
			//create error message....
			return;
		}
		patIllScript.getProblems().add(relProb);
		save(relProb);
		initScoreCalc(relProb);		
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#save(beans.relation.Relation)
	 */
	public void save(Beans b){
		//TODO save whole problems collection?
		new DBClinReason().saveBean(b);
	}
	
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
