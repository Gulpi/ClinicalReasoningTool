package beans.relation;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.SessionScoped; 
/**
 * Summary Statement of the author or learner for a VP. There might be multiple Summary Statements for a case (changed
 * at distinct steps), all changes, variants are saved in the PIS_Log object.
 * @author ingahege
 *
 */
@SessionScoped
public class SummaryStatement extends Beans implements Serializable{

	private static final long serialVersionUID = 1L;
	private String text; 
	private long id = -1; 
	private Timestamp creationDate;
	private int stage = -1;
	/**
	 * not yet needed, but we stpre it already for changing later on to multiple statements/script...
	 */
	private long patillscriptId = -1;
	
	public SummaryStatement(){}
	public SummaryStatement(String text){
		this.text = text;
	}
	public SummaryStatement(String text, long patIllscriptId){
		this.text = text;
		this.patillscriptId = patIllscriptId;
	}
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}		
	public long getPatillscriptId() {return patillscriptId;}
	public void setPatillscriptId(long patillscriptId) {this.patillscriptId = patillscriptId;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SummaryStatement){
			if(((SummaryStatement) o).getId() == this.id) return true;
		}
		return false;
	}	
}
