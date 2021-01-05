package beans.relation.summary;

import java.io.Serializable;

import net.casus.util.nlp.spacy.SpacyDocToken;

/**
 * A SummaryStatement object can have multiple SummaryStatementSQ objects attached to it once it has 
 * been analyzed. A SummaryStatementSQ object stores ths relation to semantic qualifiers included in the 
 * summaryStatement text. 
 * @author ingahege
 *
 */
public class SummaryStatementSQ implements Serializable{

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
	private int position;
	
	private SpacyDocToken spacyMatch;
	/**
	 * we store here if we have found an opposite SQ in the expert statement (for the same reference term)
	 */
	private boolean expHasOpposite = false;
	
	public SummaryStatementSQ(){}
	public SummaryStatementSQ(long summStId, int sqId, String text){
		this.summStId = summStId;
		this.sqId = sqId;
		this.text = text;
	}
	
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
	public boolean isExpHasOpposite() {return expHasOpposite;}
	public void setExpHasOpposite(boolean expHasOpposite) {this.expHasOpposite = expHasOpposite;}
	
	public boolean isSpacyMatch() {
		if(spacyMatch!=null) return true;
		return false;
		}
	public void setSpacyMatch(SpacyDocToken spacyMatch) {this.spacyMatch = spacyMatch;}
	public SpacyDocToken getSpacyMatch(){return spacyMatch;} 
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof SummaryStatementSQ){
			SummaryStatementSQ ssq = (SummaryStatementSQ) o;
			if(this.id>0 && this.id == ssq.getId()) return true;
			if(this.sqId!=-1 && ssq.getSummStId() == this.summStId && ssq.getSqId()==this.sqId) return true;
			if(ssq.getSummStId() == this.summStId && this.position == ssq.getPosition()) return true;
			if(ssq.getSummStId() == this.summStId && (this.text.contains(ssq.getText()) || ssq.getText().contains(text))) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return text;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/*public int compareTo(Object o) {
		if(o instanceof SummaryStatementSQ){
			SummaryStatementSQ sq = (SummaryStatementSQ) o;
			if(sq.getId()>this.id) return 1;
			if(sq.getId()<this.id) return -1;
		}
		return 0;
	}*/
	
}
