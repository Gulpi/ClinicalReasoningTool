package model;
import java.sql.Timestamp;
import java.util.*; 
/**
 * Summary Statement of the author or learner for a VP. There might be multiple Summary Statements for a case (changed
 * at distinct steps), all changes, variants are saved in the PIS_Log object.
 * @author ingahege
 *
 */
public class SummaryStatement {

	private String text; 
	private long id = -1; //shall we link to the current SummaryStatementAnswerView?
	private Timestamp creationDate;
	
}
