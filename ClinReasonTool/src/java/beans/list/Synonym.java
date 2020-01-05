package beans.list;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import util.StringUtilities;

public class Synonym implements Serializable, ListInterface, Comparable{

	private String name;
	private long id;
	/**
	 * the id of the ListItem object the synonym is referring to.fg
	 */
	private long listItemId;
	private Locale language;
	private String source; //"MESH" or "ADDED" if manually added, currently just in database, no mapping necessary
	private boolean ignored = false;
	private boolean transformation = false;
	/**
	 * Synonyma can be scored differently if they are not 100%the same as the main listItem. For example the 
	 * synonym "Shortness of Breath" should be scored less than "Dyspnea". This has to be done manually in the database 
	 * on a case-basis over time. What shall we choose for default value?
	 */
	private float ratingWeight = 1; 
	/**
	 * name of the synonym with replace special chars and in lower case
	 */
	private transient String replName;
	public static final String SYN_VERTEXID_PREFIX = "syn_";

	public Synonym(){}
	public Synonym(Locale loc, String name){
		this.language = loc; 
		this.name = name;
		this.replName = StringUtilities.replaceChars(name.toLowerCase());
	}
	
	public String getName() {return name;}
	public void setName(String name) {
		this.name = name;
		this.replName = StringUtilities.replaceChars(name.toLowerCase());
	}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}	
	public Locale getLanguage() {return language;}
	public void setLanguage(Locale language) {this.language = language;}	
	public float getRatingWeight() {return ratingWeight;}
	public void setRatingWeight(float ratingWeight) {this.ratingWeight = ratingWeight;}	
	public boolean isIgnored() {return ignored;}
	public void setIgnored(boolean ignored) {this.ignored = ignored;}
	public boolean isSynonym(){return true;}	
	public boolean isTransformation() {return transformation;}
	public void setTransformation(boolean transformation) {this.transformation = transformation;}	
	public String getReplName() {return replName;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof Synonym && ((Synonym) o).getId()==id) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.name + ", Id: " + this.id;
	}
	
	public String getShortName(){ 
		return StringUtils.abbreviate(this.name, ListItem.MAXLENGTH_NAME);
	}

	public String getIdForJsonList() {
		return Synonym.SYN_VERTEXID_PREFIX+id;
	}
	
	public int compareTo(Object o) {
		if(o instanceof ListInterface){
			ListInterface li = (ListInterface) o;
			return name.compareToIgnoreCase(li.getName());
		}
		return 0;
	}
	
	public String getItemType(){return "Syn";}
	
}
