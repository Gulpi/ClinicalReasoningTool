package beans.search;

import java.util.Locale;

/**
 * SearchBeans are created for each hit of a search query. It contains the search term, type of hit (e.g. problem, dxx,...),  
 * percentage of match,...
 * @author ingahege
 *
 */
public class SearchBean {

	private String searchTerm; 
	private long meshIdOfSSearchTerm; 
	private long parentId;
	private Locale loc;
	private long scriptId;
	private int type; //expert or learner script
	
	
	public SearchBean(){}
	public SearchBean(String searchterm, long matchedId, long parentId, long scriptId, int type){
		this.searchTerm = searchterm;
		this.meshIdOfSSearchTerm = matchedId;
		this.parentId = parentId;
		this.scriptId = scriptId;
	}
	
	public String getSearchTerm() {return searchTerm;}
	public void setSearchTerm(String searchTerm) {this.searchTerm = searchTerm;}
	public long getMeshIdOfSSearchTerm() {return meshIdOfSSearchTerm;}
	public void setMeshIdOfSSearchTerm(long meshIdOfSSearchTerm) {this.meshIdOfSSearchTerm = meshIdOfSSearchTerm;}
	public long getParentId() {return parentId;}
	public void setParentId(long parentId) {this.parentId = parentId;}
	public Locale getLoc() {return loc;}
	public void setLoc(Locale loc) {this.loc = loc;}
	public long getScriptId() {return scriptId;}
	public void setScriptId(long scriptId) {this.scriptId = scriptId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	
	
	
}
