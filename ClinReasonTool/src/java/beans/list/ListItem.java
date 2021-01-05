package beans.list;

import org.apache.commons.lang3.StringUtils;

import controller.SummaryStatementController;
import util.CRTLogger;
import util.StringUtilities;

import java.io.Serializable;
import java.util.*;


/**
 * We have one list in the database containing all entries for diagnoses, problems etc. ListItem models one entry. 
 * @author ingahege
 *
 */
public class ListItem implements Serializable, ListInterface, Comparable{

	public static final int MAXLENGTH_NAME = 15;
	private static final long serialVersionUID = 1L;
	private String name; //MH
	private String nameLower = null;
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
	private boolean transformation = false;
	
	private Boolean isFindingCache = null;
	private Boolean isDiagnosisCache = null;
	private Boolean isTestCache = null;
	private Boolean isTherapyCache = null;
	private Boolean isAnatomyCache = null;

	/**
	 * name of the listItem with replace special chars and in lower case
	 */
	private transient String replName;
	
	public static final String TYPE_OWN = "PRIVATE";
	public static final String TYPE_ADDED = "ADDED";
	public static final String TYPE_MESH = "MESH";
	/**
	 * if true, we do not export this item (including synonyma) into the json list.
	 */
	private boolean ignored = false;
	
	/**
	 * we can have items that are composed of several findings, e.g. shock (hypotension, tachcardia, acidosis,...)
	 * 0: is not a syndrome, 1 = is a syndrome (can be extended....) 
	 */
	private int isSyndrome = 0;
	
	public ListItem(){}
	public ListItem(String lang,String source, String name){
		this.language = new Locale(lang);
		this.source = source;
		this.name = name;
		this.replName = StringUtilities.replaceChars(name.toLowerCase());
	}
		
	public boolean isIgnored() {return ignored;}
	public void setIgnored(boolean ignored) {this.ignored = ignored;}
	public String getName() {return name;}
	public String getNameLower() {
		if (this.nameLower == null && this.name != null) {
			this.nameLower = this.name.toLowerCase();
		}
		return this.nameLower;
	}
	public void setName(String name) {
		this.name = name;
		this.replName = StringUtilities.replaceChars(name.toLowerCase());
	}
	public String getMesh_id() {return mesh_id;}
	public void setMesh_id(String mesh_id) {this.mesh_id = mesh_id;}
	public long getItem_id() {return item_id;}
	public long getListItemId() {return item_id;}
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
	public int getIsSyndrome() {return isSyndrome;}
	public void setIsSyndrome(int isSyndrome) {this.isSyndrome = isSyndrome;}
	public boolean isSynonym(){return false;}	
	public boolean isTransformation() {return transformation;}
	public void setTransformation(boolean transformation) {this.transformation = transformation;}
	public String getReplName() {return replName;}
	
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
	 * otherCodes with each other and return the closest disctance. If no hierarchy relation is there, we return -99.
	 * Relation of child <-> parent is shown by negative/positive return value.
	 * @param li
	 * @return
	 */
	public int getHierarchyDiff(ListItem li){
		if(this.equals(li)) return -99;
		int codeDiff = getHierarchyDiff(li.getFirstCode());
		//code does not match and there are no other codes:
		if(codeDiff == -99 && (li.getOtherCodes()==null || li.getOtherCodes().isEmpty()) && (this.getOtherCodes()==null || this.getOtherCodes().isEmpty())) return -99;
		//code does match and there are no other codes (that might be closer)
		if(codeDiff > -99 && (li.getOtherCodes()==null || li.getOtherCodes().isEmpty()) && (this.getOtherCodes()==null || this.getOtherCodes().isEmpty())) return codeDiff/4;
		Iterator<String> it = li.getOtherCodes().iterator();
		while(it.hasNext()){
			int diff2 = getHierarchyDiff(it.next());
			if(Math.abs(diff2)<Math.abs(codeDiff) || codeDiff==-99) codeDiff = diff2;
		}
		//divide by 4 because distance for mesh codes is always 3 numbers and a dot.
		return codeDiff/4;
	}
	
	public int getAbsHierarchyDiff(ListItem li){
		int codeDiff = getHierarchyDiff(li);
		if(codeDiff==-99) return -99;
		//divide by 4 because distance for mesh codes is always 3 numbers and a dot.
		return Math.abs(codeDiff);
	}
		
	/**
	 * calculates a difference between a given code with the firstCode and otherCodes of this ListItem.
	 * @param code
	 * @return
	 */
	private int getHierarchyDiff(String code){
		if(code==null || firstCode==null) return -99;
		int diff = -99;
		if(!code.equals(firstCode) && (code.startsWith(firstCode) || firstCode.startsWith(code))){
			//we look into the distance of the codes: 
			diff = /*Math.abs*/(firstCode.length() - code.length());
		}
		if(diff>-99 || this.getOtherCodes()==null || this.getOtherCodes().isEmpty()) return diff;
		
		Iterator it = this.getOtherCodes().iterator();
		while(it.hasNext()){
			String otherCode = (String) it.next();
			if(!code.equals(otherCode) && (code.startsWith(otherCode) || otherCode.startsWith(code))){
				int diff2 = /*Math.abs*/(otherCode.length() - code.length());
				if(Math.abs(diff2)<Math.abs(diff) || diff==-99) diff = diff2;
			}			
		}
		return diff;
	}
	
	public String getIdForJsonList(){
		return String.valueOf(item_id);
	}

	public int compareTo(Object o) {
		if(o instanceof ListInterface){
			ListInterface li = (ListInterface) o;
			return name.compareToIgnoreCase(li.getName());
		}
		return 0;
	}
	

	/**
	 * returns true if the ListItem belongs to the mesh category C23 (Pathological Conditions, Signs and Symptoms) either 
	 * with the first code or with other codes.
	 * @return
	 */
	public boolean isFinding(){
		if (isFindingCache != null) return isFindingCache.booleanValue();
		
		if (firstCode!=null && (firstCode.startsWith("C23") || firstCode.startsWith("F01"))) {
			isFindingCache = Boolean.TRUE;
			return true;
		}
		//CRTLogger.out("", 1);
		//otherCodes we have only for English mesh terms, therefore we have to get the ones from the matching English term
		if((otherCodes == null || otherCodes.isEmpty()) && !this.language.getLanguage().equalsIgnoreCase("en"))
			setOtherCodes(); 
		if (otherCodes!=null){
			Iterator it = otherCodes.iterator();
			while(it.hasNext()){
				String code = (String) it.next();
				if(code.startsWith("C23")) {
					isFindingCache = Boolean.TRUE;
					return true;
				}
			}
		}
		
		isFindingCache = Boolean.FALSE;
		return false; 
	}
	
	public boolean isAnatomy(){
		if (isAnatomyCache != null) return isAnatomyCache.booleanValue();
		
		if (firstCode!=null && firstCode.startsWith("A")) {
			isAnatomyCache = Boolean.TRUE;
			return true;
		}
		/*if (otherCodes!=null){
			Iterator it = otherCodes.iterator();
			while(it.hasNext()){
				String code = (String) it.next();
				if(code.startsWith("C23")) return true;
			}
		}*/
		
		isAnatomyCache = Boolean.FALSE;
		return false; 
	}
	
	public boolean isDiagnosis(){
		if (isDiagnosisCache != null) return isDiagnosisCache.booleanValue();

		if(!isFinding()){
			if (firstCode!=null && firstCode.startsWith("C")) {
				isDiagnosisCache = Boolean.TRUE;
				return true;
			}
			//if (firstCode!=null && firstCode.startsWith("C14")) return true;			
		}
		
		isDiagnosisCache = Boolean.FALSE;
		return false;
	}
	
	public boolean isTest(){
		if (isTestCache != null) return isTestCache.booleanValue();
		if(!isFinding()){
			if (firstCode!=null && firstCode.startsWith("E01")) {
				isTestCache = Boolean.TRUE;
				return true;
			}
			//if (firstCode!=null && firstCode.startsWith("C14")) return true;			
		}
		
		isTestCache = Boolean.FALSE;
		return false;
	}
	public boolean isTher(){
		if (isTherapyCache != null) return isTherapyCache.booleanValue();
		
		if(!isFinding()){
			boolean my_result = false;
			if (firstCode!=null && firstCode.startsWith("E02")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("E03")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("E04")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("E07")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("D01")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("D02")) my_result = true;
			if (firstCode!=null && firstCode.startsWith("D27")) my_result = true;
			if (my_result == true) {
				isTherapyCache = Boolean.TRUE;
				return true;
			}
			//if (firstCode!=null && firstCode.startsWith("C14")) return true;			
		}
		
		isTherapyCache = Boolean.FALSE;
		return false;
	}
	
	/**
	 * if the language is NOT English we have to get the otherCodes from the main English term. 
	 * TODO: We could update the database with the otherCodes, so with time this should reduce the searching 
	 * we have to do....
	 */
	public void setOtherCodes(){
		List<ListItem> enList = SummaryStatementController.getListItemsByLang("en");
		for(int i=0; i<enList.size(); i++){
			ListItem li = enList.get(i);
			if(li==null && li.getFirstCode()==null)
				CRTLogger.out("", 1);
			if(li!=null && li.getFirstCode()!=null && li.getFirstCode().equalsIgnoreCase(firstCode)){
				this.otherCodes = li.getOtherCodes();
				break;
			}
		}
	}
}
