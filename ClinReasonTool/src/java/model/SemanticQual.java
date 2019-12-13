package model;

/**
 * Semantic qualifiers (e.g. acute/chronic upper/lower ....) for analyzing the summary statements
 * based on Connell KJ, Bordage G, Chang RW. Assessing Clinicians’ Quality of Thinking and Semantic Competence: 
 * A Training Manual. Chicago: University of Illinois at Chicago, Northwestern University Medical School, Chicago; 1998. 
 * 
 * @author ingahege
 *
 */
public class SemanticQual {

	private String lang;
	private String qualifier;
	private int id;
	/**
	 * see static definitions of categories 1-11.
	 */
	private int category;
	private int deleteFlag = 0;
	public static final int CATEGORY_PATIENT = 1; //patient characteristic (male, tall,...)
	public static final int CATEGORY_SETTING = 2; //exercise-induced,...
	public static final int CATEGORY_ONSET = 3; //e.g. acute, recurrence,...
	public static final int CATEGORY_DURATION = 4;  //e.g. always, slowly,...
	public static final int CATEGORY_COURSE = 5; //e.g. initial, reversible,...
	public static final int CATEGORY_LOCATION = 6; //e.g. anterior, external,...
	public static final int CATEGORY_QUALITY = 7; //e.g. benign, dry,...
	public static final int CATEGORY_QUANTITY = 8; //unique, mono
	public static final int CATEGORY_SEVERITY = 9; //urgent, mild
	public static final int CATEGORY_AGGR_REL = 10; //aggravating/relieving, e.g. lying down, horizontal
	public static final int CATEGORY_ASSOCIATED = 11; //local, complementary
	
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
