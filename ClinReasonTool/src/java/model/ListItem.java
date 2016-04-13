package model;

import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.util.*;


/**
 * We have one list in the database containing all entries for diagnoses, problems etc. ListItem models one entry. 
 * @author ingahege
 *
 */
public class ListItem implements Serializable{

	public static final int MAXLENGTH_NAME = 15;
	private static final long serialVersionUID = 1L;
	private String name; //MH
	private String mesh_id; //UI
	private long item_id = -1;
	private String firstCode; //MN
	private String category; //PA
	private int level;
	private String note; //MS
	private String item_description; //AN
	private String source; //e.g. MESH
	private Set synonyma; //ENTRY
	private Set otherCodes; //MN 
	private Locale language; //en, de,...
	private String mesh_ec; //EC
	private String itemType; //D=Diagnosis, ...
	
	public ListItem(){}
	public ListItem(String lang,String source, String name){
		this.language = new Locale(lang);
		this.source = source;
		this.name = name;
	}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getMesh_id() {return mesh_id;}
	public void setMesh_id(String mesh_id) {this.mesh_id = mesh_id;}
	public long getItem_id() {return item_id;}
	public void setItem_id(long item_id) {this.item_id = item_id;}
	public String getFirstCode() {return firstCode;}
	public void setFirstCode(String firstCode) {this.firstCode = firstCode;}
	public String getCategory() {return category;}
	public void setCategory(String category) {this.category = category;}
	public int getLevel() {return level;}
	public void setLevel(int level) {this.level = level;}
	public String getNote() {return note;}
	public void setNote(String note) {this.note = note;}
	public String getItem_description() {return item_description;}
	public void setItem_description(String item_description) {this.item_description = item_description;}
	public String getSource() {return source;}
	public void setSource(String source) {this.source = source;}
	public Set<Synonym> getSynonyma() {return synonyma;}
	public void setSynonyma(Set<Synonym> synonyma) {this.synonyma = synonyma;}
	public Set<String> getOtherCodes() {return otherCodes;}
	public void setOtherCodes(Set<String> otherCodes) {this.otherCodes = otherCodes;}
	public Locale getLanguage() {return language;}
	public void setLanguage(Locale language) {this.language = language;}
	public String getMesh_ec() {return mesh_ec;}
	public void setMesh_ec(String mesh_ec) {this.mesh_ec = mesh_ec;}
	public String getItemType() {return itemType;}
	public void setItemType(String itemType) {this.itemType = itemType;}	

	
	public String getShortName(){ 
		return StringUtils.abbreviate(this.name, MAXLENGTH_NAME);
		/*if(this.name==null || this.name.length()<=MAXLENGTH_NAME) return name;
		String shortName = name.substring(0, MAXLENGTH_NAME) + "..";
		return shortName;*/
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.name + ", Id: " + this.item_id;
	}
	
	public boolean equals(Object o){
		if(o instanceof ListItem && ((ListItem)o).getItem_id()==item_id) return true;
		return false;
	}
	
}
