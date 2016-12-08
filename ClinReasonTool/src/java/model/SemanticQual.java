package model;

/**
 * Semantic qualifiers (e.g. acute/chronic upper/lower ....) for analyzing the summary statements
 * 
 * @author ingahege
 *
 */
public class SemanticQual {

	private String lang;
	private String qualifier;
	private int id;
	private int category;
	private int deleteFlag = 0;
	
	public String getLang() {return lang;}
	public void setLang(String lang) {this.lang = lang;}
	public String getQualifier() {return qualifier;}
	public void setQualifier(String qualifier) {this.qualifier = qualifier;}
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public int getCategory() {return category;}
	public void setCategory(int category) {this.category = category;}
	public int getDeleteFlag() {return deleteFlag;}
	public void setDeleteFlag(int deleteFlag) {this.deleteFlag = deleteFlag;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o !=null && o instanceof SemanticQual){
			SemanticQual ssq = (SemanticQual) o;
			if(ssq.getId() == this.id) return true;
		}
		return false;
	}}
