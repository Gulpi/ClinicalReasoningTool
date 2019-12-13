package beans.relation;

import beans.list.ListInterface;
import beans.list.ListItem;

public class SummaryStElem {

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
	 * if the match is a synonym we store the string here, but save the parent listItem
	 */
	private String synonymStr = null;

	public SummaryStElem(ListItem li ){
		this.listItem = li;
	}
	public ListItem getListItem() {return listItem;}
	public void setListItem(ListItem listItem) {this.listItem = listItem;}
	public boolean isExpertMatch() { return expertMatch;}
	public void setExpertMatch(boolean expertMatch) {this.expertMatch = expertMatch;} 
	public String getSynonymStr() {return synonymStr;}
	public void setSynonymStr(String synonymStr) {this.synonymStr = synonymStr;}	
	public int getExpertScriptMatch() {return expertScriptMatch;}
	public void setExpertScriptMatch(int expertScriptMatch) {this.expertScriptMatch = expertScriptMatch;}
	
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
		}
		return false;
	}
	
	public boolean isFinding(){return listItem.isFinding();}
	public boolean isAnatomy(){return listItem.isAnatomy();}
	public boolean isDiagnosis(){return listItem.isDiagnosis();}
	public boolean isTher(){return listItem.isTher();}
	public boolean isTest(){return listItem.isTest();}
	
	/*public int getType(){
		if(listItem==null) return -1;
		if(listItem.get)
	}*/
	
}
