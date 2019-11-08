package beans.helper.export;

import java.util.*;

import beans.error.MyError;
import beans.scoring.LearningAnalyticsBean;
import beans.scripts.PatientIllnessScript;
import controller.NavigationController;
import database.DBClinReason;
import net.casus.util.table.*;
import properties.IntlConfiguration;
/**
 * CAVE: This class extends CASUS code!!! 
 * Table to display/export a basic PatientIllnessScript as Excel or other Table formats.  (does not contain all the items)
 * Columns are:
 * Creation_date, vp name, scores for categories, number of added items in categories, errors, summary statement, 
 * solution shown by system, number of submitted final diagnoses, confidence
 */
public class BasicPatIllscriptTable extends DynamicTable {
	private List<PatientIllnessScript> pisList;
	
	public BasicPatIllscriptTable(List<PatientIllnessScript> g) {
		super.init();
		if(g==null || g.isEmpty() ) return;
		this.pisList = g;
		initTable();	
	}
	
	/**
	 * display an individual map
	 * @param p
	 */
	public BasicPatIllscriptTable(PatientIllnessScript p) {
		super.init();
		if(p==null) return;
		this.pisList =  new ArrayList();
		pisList.add(p);
		initTable();	
	}
	
	private void initTable() {
		initColumnHeaders();
		addRows();
	}
	
	private void initColumnHeaders() {
		String[] columns = new String[]{"id", IntlConfiguration.getValue("reports.header.vpname"),  IntlConfiguration.getValue("reports.header.date"),  IntlConfiguration.getValue("reports.header.confidence"), IntlConfiguration.getValue("reports.header.probscore"),IntlConfiguration.getValue("reports.header.ddxscore"), IntlConfiguration.getValue("reports.header.tstscore"), IntlConfiguration.getValue("reports.header.mngscore"), 
				IntlConfiguration.getValue("reports.header.probnum"), IntlConfiguration.getValue("reports.header.ddxnum"),  IntlConfiguration.getValue("reports.header.tstnum"),  IntlConfiguration.getValue("reports.header.mngnum"),  IntlConfiguration.getValue("reports.header.cnxnum"), IntlConfiguration.getValue("reports.header.summstscore"), IntlConfiguration.getValue("reports.header.sumst"),
				/*IntlConfiguration.getValue("reports.header.finalcrd"),*/ IntlConfiguration.getValue("reports.header.error1"),IntlConfiguration.getValue("reports.header.error2"), IntlConfiguration.getValue("reports.header.error3"), IntlConfiguration.getValue("reports.numownfinal")}; 
		super.setColumnHeader(columns);
	}
	
	/**
	 * add rows and tableCells from the PatientIllnessScript and LearningAnalysticsBean to the Table
	 */
	private void addRows() {
		for (int i=0;i<pisList.size(); i++) {
			int row = i+1;
			PatientIllnessScript p = pisList.get(i);
			LearningAnalyticsBean  analyticsBean = new NavigationController().getCRTFacesContext().getLearningAnalytics();
			if(analyticsBean==null) analyticsBean = new LearningAnalyticsBean(); //avoid a NullPointerException!
			
			super.addTableCell( new TableCell(p.getId(), 0, row));
			super.addTableCell( new TableCell(p.getVPName(), 1, row));
			super.addTableCell( new TableCell(p.getCreationDate(), 2, row));
			super.addTableCell( new TableCell(p.getConfidence(), 3, row));
			super.addTableCell( new TableCell(analyticsBean.getProblemScore(), 4, row));
			super.addTableCell( new TableCell(analyticsBean.getDDXScore(), 5, row));
			super.addTableCell( new TableCell(analyticsBean.getTestScore(), 6, row));
			super.addTableCell( new TableCell(analyticsBean.getMngScore(), 7, row));
			if(p.getProblems()!=null) super.addTableCell( new TableCell(p.getProblems().size(), 8, row));
			else super.addTableCell( new TableCell(0, 8, row));
			if(p.getDiagnoses()!=null) super.addTableCell( new TableCell(p.getDiagnoses().size(), 9, row));
			else super.addTableCell( new TableCell(0, 9, row));
			if(p.getTests()!=null) super.addTableCell( new TableCell(p.getTests().size(), 10, row));
			else super.addTableCell( new TableCell(0, 10, row));
			if(p.getMngs()!=null) super.addTableCell( new TableCell(p.getMngs().size(), 11, row));
			else super.addTableCell( new TableCell(0, 11, row));			
			if(p.getConns()!=null) super.addTableCell( new TableCell(p.getConns().size(), 12, row));
			else super.addTableCell( new TableCell(0, 12, row));			
			super.addTableCell( new TableCell(analyticsBean.getLastSummStScore().getOrgScoreBasedOnExp(),13, row));
			if(p.getSummSt()!=null) super.addTableCell( new TableCell(p.getSummSt().getText(),14, row));
			else super.addTableCell(new TableCell("",14, row));
			//super.addTableCell( new TableCell(p.getStage(), 15, row));
			super.addTableCell( new TableCell(p.hasError(MyError.TYPE_PREMATURE_CLOUSRE), 15, row));
			super.addTableCell( new TableCell(p.hasError(MyError.TYPE_AVAILABILITY), 16, row));
			super.addTableCell( new TableCell(p.hasError(MyError.TYPE_CONFIRMATION), 17, row));		
			super.addTableCell( new TableCell(new DBClinReason().getNumOfFinalDiagnosisAttempts(p.getId()), 18, row));	
		}
	}	
}
