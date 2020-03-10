package beans.relation.summary;

import beans.list.ListItem;
import net.casus.util.nlp.spacy.SpacyDocToken;

public class SummaryStElem {

	private long id;
	private long summStId;
	/**
	 * Reference to the Mesh entry
	 */
	private ListItem listItem;
	/**
	 * matching terms with the expert's summary statement 
	 */
	private boolean expertMatch = false;
	/**
	 * matching terms with elements from the expert's illnessScript
	 * -1 / 0 = no match, 1=finding match, 2 = ddx match, 3 = test match, 4 = therapy match
	 */
	private int expertScriptMatch = -1; 
	
	/**
	 * we store here the position of the element in the expert statement for later accuracy testing
	 */
	private int expertMatchIdx;
	/**
	 * if the match is a synonym we store the string here, but save the parent listItem
	 */
	private String synonymStr = null;
	
	/**
	 * start position of the word in the text (wordcount, 0-based)
	 */
	//private int startPos;
	
	/**
	 * start position/index within the text 
	 */
	private int startIdx;
	
	/**
	 * end position of the word in the text (wordcount, 0-based)
	 */
	//private int endPos;

	/**
	 * 1=prefix, 2=suffix, 3= transformed finding (e.g. fever)
	 */
	private int transform = 0; 
	
	private String type; //currently used for person
	
	public SummaryStElem(ListItem li ){
		this.listItem = li;
		if(this.listItem.isTransformation()) 
			this.setTransform(TransformRule.TYPE_FINDING);
	}
	
	/**
	 * somehow there is a mismatch between the idx in the token and the "real" index, that is why we have it here as
	 * separate parameter....
	 * @param tok
	 * @param startIdx
	 */
	public SummaryStElem(SpacyDocToken tok, int startIdx){
		this.startIdx = startIdx;
		this.type = tok.getLabel();
		this.synonymStr = tok.getToken();
	}
	
	public ListItem getListItem() {return listItem;}
	public void setListItem(ListItem listItem) {this.listItem = listItem;}
	public boolean isExpertMatch() { return expertMatch;}
	public void setExpertMatch(boolean expertMatch) {this.expertMatch = expertMatch;} 
	public String getSynonymStr() {return synonymStr;}
	public void setSynonymStr(String synonymStr) {this.synonymStr = synonymStr;}	
	public int getExpertScriptMatch() {return expertScriptMatch;}
	public void setExpertScriptMatch(int expertScriptMatch) {this.expertScriptMatch = expertScriptMatch;}	
	public int getTransform() {return transform;}
	public void setTransform(int transform) {this.transform = transform;}
	public int getStartIdx() {return startIdx;}
	public void setStartIdx(int startIdx) {this.startIdx = startIdx;}	
	public String getType() {return type;}
	public void setType(String type) {this.type = type;}	
	public int getExpertMatchIdx() {return expertMatchIdx;}
	public void setExpertMatchIdx(int expertMatchIdx) {this.expertMatchIdx = expertMatchIdx;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getSummStId() {return summStId;}
	public void setSummStId(long summStId) {this.summStId = summStId;}

	public boolean isPerson(){
		if(type!=null && type.equals(SpacyDocToken.LABEL_PERSON)) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(synonymStr!=null) return synonymStr;
		else return listItem.getName();
	}
	
	public boolean equals(Object o){
		if(o instanceof SummaryStElem){
			SummaryStElem se = (SummaryStElem) o;
			if(se.getListItem()!=null && this.getListItem()!=null && se.getListItem().getListItemId() == this.getListItem().getListItemId()) return true;
			if(se.getStartIdx()==this.startIdx) return true;
		}
		return false;
	}
	
	public boolean isFinding(){
		if(listItem==null) return false; 
		return listItem.isFinding();
	}
	public boolean isAnatomy(){
		if(listItem==null) return false; 
		return listItem.isAnatomy();
	}
	public boolean isDiagnosis(){
		if(listItem==null) return false; 
		return listItem.isDiagnosis();
	}
	public boolean isTher(){
		if(listItem==null) return false; 
		return listItem.isTher();
	}
	public boolean isTest(){
		if(listItem==null) return false; 
		return listItem.isTest();
	}
	//public int getStartPos() {return startPos;}
	//public void setStartPos(int startPos) {this.startPos = startPos;}
	//public int getEndPos() {return endPos;}
	//public void setEndPos(int endPos) {this.endPos = endPos;}
	
	public boolean isCountry(){
		if(listItem!=null && listItem.getFirstCode().startsWith("Z01")) return true; 
		return false;
	}
	
	/*private void checkTransformationElem(){
		if(this.listItem.isTransformation()) 
			this.setTransform(TransformRule.TYPE_FINDING);
		else{
			if(SummaryStatementController.)
			
		}
	}*/
	/*public int getType(){
		if(listItem==null) return -1;
		if(listItem.get)
	}*/
	
}
