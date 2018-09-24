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
	private String lang;
	/**
	 * not yet needed, but we store it already for changing later on to multiple statements/script...
	 */
	private long patillscriptId = -1;
	/**
	 * expert or learner statement(see definitions in PatientIllnessScript)
	 */
	private int type = -1;
	/**
	 * has the text been evaluated/analyzed
	 */
	private boolean analyzed;
	private String test;
	private int testNum = -1;
	private int testTransf = -1;
	private int testNumExpMatches = -1;
	private int testSQNum = -1;
	private String testSQ;
	private int testExpSQNum = -1;
	private String testExpSQ;
	/**
	 * all semantic qualifier ids that have been
	 */
	//private Map<Long, SummaryStatementSQ> sqHits;
	private List<SummaryStatementSQ> sqHits;
	
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
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public boolean isAnalyzed() {return analyzed;}
	public void setAnalyzed(boolean analyzed) {this.analyzed = analyzed;}	
	public String getLang() {return lang;}
	public void setLang(String lang) {this.lang = lang;}
	public List getSqHits(){return sqHits;}
	
	public String getTest() {return test;}
	public void setTest(String test) {this.test = test;}
	public int getTestNum() {return testNum;}
	public void setTestNum(int testNum) {this.testNum = testNum;}
	public int getTestTransf() {return testTransf;}
	public void setTestTransf(int testTransf) {this.testTransf = testTransf;}	
	public int getTestNumExpMatches() {return testNumExpMatches;}
	public void setTestNumExpMatches(int testNumExpMatches) {this.testNumExpMatches = testNumExpMatches;}
	public int getTestSQNum() {return testSQNum;}
	public void setTestSQNum(int testSQNum) {this.testSQNum = testSQNum;}
	public String getTestSQ() {return testSQ;}
	public void setTestSQ(String testSQ) {this.testSQ = testSQ;}
	public int getTestExpSQNum() {return testExpSQNum;}
	public void setTestExpSQNum(int testExpSQNum) {this.testExpSQNum = testExpSQNum;}
	public String getTestExpSQ() {return testExpSQ;}
	public void setTestExpSQ(String testExpSQ) {this.testExpSQ = testExpSQ;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SummaryStatement){
			if(((SummaryStatement) o).getId() == this.id) return true;
		}
		return false;
	}	
	
	public void setSqHits(List<SummaryStatementSQ> hits){
		this.sqHits = hits;
	}
	
	/**
	 * display the summary statement with the highlighted sematic qualifiers 
	 * We split the text, match it with the sematic qualifier hits and print the hits in a different color/bold.
	 * @return
	 */
	public String getTextSQ(){
		if(sqHits==null || sqHits.isEmpty()) return text;
		if(text ==null || text.trim().isEmpty()) return "";
		String[] splitText = text.split(" ");
		for(int i=0;i<splitText.length; i++){
			String s = splitText[i].toLowerCase().trim();
			for(int j=0; j<sqHits.size(); j++){
				if(s.contains(sqHits.get(j).getText().trim().toLowerCase())){
					splitText[i] = "<span class='sqhit'>"+s+"</span>";
				}
			}
		}
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < splitText.length; i++) {
		   strBuilder.append(splitText[i]+ " ");
		}
		return strBuilder.toString();
	}
	
/*	public String getSqHitsoString(){
		if(sqHits==null || sqHits.isEmpty()) return "";
		StringBuffer sb = new StringBuffer(500);
		for(int i=0;i<sqHits.size(); i++){
			sb.append(sqHits.get)
		}
	}*/
}
