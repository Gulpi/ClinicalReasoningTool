package beans.xAPI;

import java.util.*;

import beans.user.User;
import controller.XAPIController;
import util.CRTLogger;
import util.StringUtilities;

/**
 * Container for all xAPI statements for a user - vp session. 
 * 
 * @author ingahege
 *
 */
public class StatementContainer {

	/**
	 * all Statements (singel statement for each action)
	 */
	private List<XAPIStatement> statements;
	/**
	 * Here we store statements that contain all items of the same type (e.g. problems) in one statement
	 */
	private List<AddActionStatement> collStatements;
	private long userId;
	private String extUserId;
	private String vpId;

	public StatementContainer(long userId, String vpId, int systemId){
		this.userId = userId;
		this.vpId = vpId;
	}
	
	public List<XAPIStatement> getStatements() {return statements;}
	//public void setStatements(List<ActionStatement> statements) {this.statements = statements;}
	
	/**
	 * Looks whether for the given type (see Relation type definitions) a statement exists and returns it. 
	 * @param type
	 * @return
	 */
	/*public ActionStatement getStatementByType(int type){
		if(statements==null) return null;
		for(int i=0; i<statements.size(); i++){
			if(statements.get(i).getType()==type) return statements.get(i);
		}
		return null;
	}*/
	
	public void addStatement(AddActionStatement st, User user){
		if (statements==null) statements = new ArrayList<XAPIStatement>();
		statements.add((XAPIStatement) st);
		updateAddCollStatement(st, user);
	}
	
	public void addStatement(TextActionXAPIStatement st){
		if (statements==null) statements = new ArrayList<XAPIStatement>();
		statements.add(st);
		//updateAddCollStatement(st, user);
	}
	
	public void removeActionStatement(int type, String term){
		if (statements==null || statements.isEmpty()) return;
		//remove single statement:
		for(int i=0; i< statements.size(); i++){
			XAPIStatement as = statements.get(i);
			if(as.getType() == type && as.getResponse().equals(term)){
				statements.remove(as);
				return;
			}				
		}
		//remove entry from coll. statement:
		AddActionStatement st = getCollStatementByType(type);
		if(st!=null){
			st.deleteResponseFromResult(term);
			logStatement((XAPIStatement) st);
		}
	}
	
	private AddActionStatement getCollStatementByType(int type){
		if(collStatements==null || collStatements.isEmpty()) return null;
		for(int i=0; i<collStatements.size(); i++){
			AddActionStatement as = collStatements.get(i);
			if(as.getType()==type) return as;
		}
		return null;
	}
	
	/**
	 * update the collective AddActionStatement for the given type
	 * @param type
	 * @param term
	 */
	private void updateAddCollStatement(AddActionStatement st, User u){
		AddActionStatement as = getCollStatementByType(st.getType());
		if(as==null){
			as = new AddActionXAPIStatement(u, st.getResponse(), st.getType(), vpId);
			addStatementToCollStatements(as);
		}
		else as.addResponseToResult(st.getResponse());
		logStatement((XAPIStatement) as);
	}
	
	
	private void addStatementToCollStatements(AddActionStatement as){
		if(collStatements==null)
			collStatements = new ArrayList<AddActionStatement>();
		collStatements.add(as);
	}
	
	private void logStatement(XAPIStatement s){
		try{
			String st = s.serialize().toString();
			CRTLogger.out("statement:" + st, CRTLogger.LEVEL_TEST);
		}
		catch(Exception e){
			CRTLogger.out("logStatement " + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}

	public String getVpId() {return vpId;}
	
	
	
}
