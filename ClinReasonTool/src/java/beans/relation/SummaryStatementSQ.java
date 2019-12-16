package beans.relation;

/**
 * A SummaryStatement object can have multiple SummaryStatementSQ objects attached to it once it has 
 * been analyzed. A SummaryStatementSQ object stores ths relation to semantic qualifiers included in the 
 * summaryStatement text. 
 * @author ingahege
 *
 */
public class SummaryStatementSQ {

	/**
	 * id of the summary statement that contains the semantic qualifier
	 */
	private long summStId;
	/**
	 * id of the semantic qualifier
	 */
	private int sqId;
	/**
	 * to avoid composite ids (and to allow duplicates), we generate a unique id.
	 */
	private long id;
	/**
	 * We store the semantic qualifier here, just to make it easier to analyze.
	 */
	private String text;
	
	/**
	 * This is the whole word that contains the semantic qualifier
	 */
	private String textMatch;
	
	/**
	 * position of the first char of the semantic qualifier in the text we have a match with
	 */
	private int position = -1;
	
	public long getSummStId() {return summStId;}
	public void setSummStId(long summStId) {this.summStId = summStId;}
	public int getSqId() {return sqId;}
	public void setSqId(int sqId) {this.sqId = sqId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}	
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}		
	public String getTextMatch() {return textMatch;}
	public void setTextMatch(String textMatch) {this.textMatch = textMatch;}	
	public int getPosition() {return position;}
	public void setPosition(int position) {this.position = position;}
	
	public SummaryStatementSQ(){}
	public SummaryStatementSQ(long summStId, int sqId, String text){
		this.summStId = summStId;
		this.sqId = sqId;
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof SummaryStatementSQ){
			SummaryStatementSQ ssq = (SummaryStatementSQ) o;
			if(ssq.getSummStId() == this.summStId && ssq.getSqId()==this.sqId) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return text;
	}
	
}
