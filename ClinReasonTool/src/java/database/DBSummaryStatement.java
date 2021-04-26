package database;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import beans.relation.summary.SummaryStatement;
import beans.scripts.IllnessScriptInterface;
import beans.scripts.PatientIllnessScript;
import util.CRTLogger;

public class DBSummaryStatement extends DBClinReason {
    public List<SummaryStatement> readTrainingSummaryStatementsForScoring() {
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(SummaryStatement.class,"SummaryStatement");
    	criteria.add(Restrictions.eq("type", new Integer(IllnessScriptInterface.TYPE_EXPERT_CREATED)));
    	List<SummaryStatement> summaryStatements = criteria.list();
    	return summaryStatements;   
    }

}
