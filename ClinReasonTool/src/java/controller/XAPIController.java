package controller;

import beans.CRTFacesContext;
import beans.relation.Relation;
import beans.relation.SummaryStatement;
import beans.user.User;
import beans.xAPI.*;
import gov.adlnet.xapi.model.Statement;
import util.*;

/**
 * Controller for creating and sending xAPI statements, statements are collected in the StatementContainer
 * @author ingahege
 *
 */
public class XAPIController {
	static private XAPIController instance = new XAPIController();
	static public XAPIController getInstance() { return instance; }
	
	
	public void sendStatementContainer(StatementContainer sc){
		if(sc==null || sc.getStatements()==null) return; //error log
		for(int i=0; i<sc.getStatements().size(); i++){
			Statement s = (Statement) sc.getStatements().get(i);
			CRTLogger.out("JSON: " + s.serialize().toString(), CRTLogger.LEVEL_TEST);
		}
		
	}
	
	public static void testXAPI(){
		AddActionXAPIStatement aast = new AddActionXAPIStatement(new User(), "dyspnea", Relation.TYPE_PROBLEM, "12345");
		aast.addResponseToResult("fever");
		aast.addResponseToResult("cough");
		String s = aast.serialize().toString();
		CRTLogger.out("statement:" + s, CRTLogger.LEVEL_TEST);
	}
	
	/**
	 * We look whether for the given action a statement has been created, if not, we create it, otherwise we update it.
	 * we also trigger the update of the collected statement (or create it)
	 */
	public void addOrUpdateAddStatement(Relation rel){
		try{
			CRTFacesContext crtContext = NavigationController.getInstance().getCRTFacesContext();
			StatementContainer sc = crtContext.getPatillscript().getStmtContainer();
			AddActionXAPIStatement aas = new AddActionXAPIStatement(rel, crtContext.getUser(), sc.getVpId());
			sc.addStatement(aas, crtContext.getUser());
		}
		catch (Exception e){
			CRTLogger.out("XAPIController.addOrUpdateStatement:" + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
	}
	
	public void addTextActionStatement(SummaryStatement sumst){
		CRTFacesContext crtContext = NavigationController.getInstance().getCRTFacesContext();
		StatementContainer sc = crtContext.getPatillscript().getStmtContainer();
		TextActionXAPIStatement tast = new TextActionXAPIStatement(sumst, crtContext.getUser(),sc.getVpId());
		sc.addStatement(tast);
	}
	
	public void removeXAPIAddActionStatement(Relation rel){
		try{
			CRTFacesContext crtContext = NavigationController.getInstance().getCRTFacesContext();
			StatementContainer sc = crtContext.getPatillscript().getStmtContainer();
			sc.removeActionStatement(rel.getRelationType(), rel.getLabelOrSynLabel());
			//sc.
		}
		catch(Exception e){
			CRTLogger.out("XAPIController.removeXAPIAddActionStatement:" + StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);

		}
	}
	

}
