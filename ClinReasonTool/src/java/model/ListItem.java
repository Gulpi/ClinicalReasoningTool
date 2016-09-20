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
	/**
	 * if true, we do not export this item (including synonyma) into the json list.
	 */
	private boolean ignored = false;
	
	public ListItem(){}
	public ListItem(String lang,String source, String name){
		this.language = new Locale(lang);
		this.source = source;
		this.name = name;
	}
		
	public boolean isIgnored() {return ignored;}
	public void setIgnored(boolean ignored) {this.ignored = ignored;}
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
	
	/**
	 * We look whether an Item is in a higher or lower hierarchy level. We compare both, firstCode and 
	 * otherCodes with each other and return the closest disctance. If no hierarchy relation is there, we return -1.s
	 * @param li
	 * @return
	 */
	public int getHierarchyDiff(ListItem li){
		int codeDiff = getHierarchyDiff(li.getFirstCode());
		if(codeDiff > -1 || (li.getOtherCodes()==null || li.getOtherCodes().isEmpty())) return codeDiff;
		Iterator it = li.getOtherCodes().iterator();
		while(it.hasNext()){
			int diff2 = getHierarchyDiff((String)it.next());
			if(diff2<codeDiff || codeDiff==-1) codeDiff = diff2;
		}
		return codeDiff;
	}
		
	/**
	 * calculates a difference between a given code with the firstCode and otherCodes of this ListItem.
	 * @param code
	 * @return
	 */
	private int getHierarchyDiff(String code){
		if(code==null || firstCode==null) return -1;
		int diff = -1;
		/*String longerCode = code;
		String shorterCode = code;
		
		if(code.length()>firstCode.length()) shorterCode = firstCode;
		else longerCode = firstCode;*/
		
		if(!code.equals(firstCode) && (code.startsWith(firstCode) || firstCode.startsWith(code))){
			//we look into the distance of the codes: 
			diff = Math.abs(firstCode.length() - code.length());
		}
		if(diff>-1 || this.getOtherCodes()==null || this.getOtherCodes().isEmpty()) return diff;
		
		Iterator it = this.getOtherCodes().iterator();
		while(it.hasNext()){
			String otherCode = (String) it.next();
			if(!code.equals(otherCode) && (code.startsWith(otherCode) || otherCode.startsWith(code))){
				int diff2 = Math.abs(otherCode.length() - code.length());
				if(diff2<diff || diff==-1) diff = diff2;
			}			
		}
		return diff;
	}
	
}
