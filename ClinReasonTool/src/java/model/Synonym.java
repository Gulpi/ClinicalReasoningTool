package model;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class Synonym implements Serializable{

	private String name;
	private long id;
	/**
	 * the id of the ListItem object the synonym is referring to.fg
	 */
	private long listItemId;
	private Locale locale;
	private String source; //"MESH" or "ADDED" if manually added, currently just in database, no mapping necessary
	private boolean ignored = false;
	/**
	 * Synonyma can be scored differently if they are not 100%the same as the main listItem. For example the 
	 * synonym "Shortness of Breath" should be scored less than "Dyspnea". This has to be done manually in the database 
	 * on a case-basis over time. What shall we choose for default value?
	 */
	private float ratingWeight = 1; 
	public static final String SYN_VERTEXID_PREFIX = "syn_";

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}	
	public Locale getLocale() {return locale;}
	public void setLocale(Locale locale) {this.locale = locale;}	
	public float getRatingWeight() {return ratingWeight;}
	public void setRatingWeight(float ratingWeight) {this.ratingWeight = ratingWeight;}	
	public boolean isIgnored() {return ignored;}
	public void setIgnored(boolean ignored) {this.ignored = ignored;}
	
	public Synonym(){}
	public Synonym(Locale loc, String name){
		this.locale = loc; 
		this.name = name;
	}
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
	
}
