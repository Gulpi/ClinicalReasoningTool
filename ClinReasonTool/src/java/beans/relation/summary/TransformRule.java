package beans.relation.summary;

/**
 * Rules we can apply for calculating the transformation of a summary statement.
 * @author ingahege
 *
 */
public class TransformRule {
	public static final int TYPE_PREFIX = 1; //e.g. "tachy"...
	public static final int TYPE_SUFFIX = 2; //e.g. .."penia"
	public static final int TYPE_FINDING = 3; //marked in clinreason_list, e.g. fever, diarrhea,...
	private long id;
	private String name; 
	private int type;
	private String lang;
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public String getLang() {return lang;}
	public void setLang(String lang) {this.lang = lang;} 	
	
}
