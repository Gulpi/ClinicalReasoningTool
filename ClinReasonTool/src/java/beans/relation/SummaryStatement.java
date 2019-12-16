package beans.relation;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.SessionScoped;

import beans.list.ListInterface;
import beans.list.ListItem;
import beans.list.Synonym; 
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
	/**
	 * all semantic qualifier ids that have been
	 */
	private List<SummaryStatementSQ> sqHits;
	private List<SummaryStElem> itemHits; //identified findings and diseases
	
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
	/**
	 * column "SQ1"
	 * @return
	 */
	public List getSqHits(){return sqHits;}
	/**
	 * add a matching listItem 
	 * @param li
	 */
	public void addItemHit(ListItem li){
		if(itemHits==null) itemHits = new ArrayList<SummaryStElem>();
		SummaryStElem el = new SummaryStElem(li);
		if(!itemHits.contains(el)) //do not add duplicates!
			itemHits.add(el);		
	}

	/**
	 * add a matching synonym.
	 * @param li
	 * @param s
	 */
	public void addItemHit(ListItem li, Synonym s){
		if(itemHits==null) itemHits = new ArrayList<SummaryStElem>();
		SummaryStElem el = new SummaryStElem(li);
		if(s!=null) el.setSynonymStr(s.getName());
		if(!itemHits.contains(el)) //do not add duplicates!
			itemHits.add(el);		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SummaryStatement){
			if(((SummaryStatement) o).getId() == this.id) return true;
		}
		return false;
	}	
	
	public List<SummaryStElem> getItemHits() {return itemHits;}
	public void setItemHits(List<SummaryStElem> itemHits) {this.itemHits = itemHits;}
	public void setSqHits(List<SummaryStatementSQ> hits){this.sqHits = hits;}
	
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
	
	/**
	 * Findings (column "Findings"
	 * @return
	 */
	public String getFindingHits(){
		if(itemHits==null) return "";
		StringBuffer fdgs = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isFinding()) fdgs.append(e.toString()+", ");
		}
		return fdgs.toString();
	}
	
	/**
	 * column "Anatomy"
	 * @return
	 */
	public String getAnatomyHits(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isAnatomy()) sb.append(e.toString()+", ");
		}
		return sb.toString();
	}
	
	/**
	 * column "Diagnoses"
	 * @return
	 */
	public String getDiagnosesHits(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isDiagnosis()) sb.append(e.toString()+", ");
		}
		return sb.toString();
	}

	/**
	 * column "Therapies"
	 * @return
	 */
	public String getTherHits(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isTher()) sb.append(e.toString()+", ");
		}
		return sb.toString();
	}
	
	/**
	 * column "Tests"
	 * @return
	 */
	public String getTestHits(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isTest()) sb.append(e.toString()+", ");
		}
		return sb.toString();
	}
	
	/**
	 * column ExpMatchSumSt
	 * @return
	 */
	public String getExpMatches(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.isExpertMatch()) sb.append(e.toString()+", ");
		}
		return sb.toString();
	}
	
	/**
	 * column ExpMatchScript
	 * @return
	 */
	public String getExpScriptMatches(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(e.getExpertScriptMatch()>0) sb.append(e.toString()+" ("+e.getExpertScriptMatch()+"), ");
		}
		return sb.toString();
	}
	
	/**
	 * column "Other"
	 * @return
	 */
	public String getOtherHits(){
		if(itemHits==null) return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<itemHits.size();i++){
			SummaryStElem e = itemHits.get(i);
			if(!e.isAnatomy() && !e.isDiagnosis() &&!e.isFinding() && !e.isTest() && !e.isTher()) 
				sb.append(e.toString());
		}
		return sb.toString();
	}
}
