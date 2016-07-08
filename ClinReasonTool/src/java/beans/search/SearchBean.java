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
	private String vpId;
	private Locale loc;
	private long scriptId;
	private int type; //expert or learner script
	private long userId;
	
	public SearchBean(){}
	public SearchBean(String searchterm, long matchedId, String vpId, long scriptId, int type, long userId){
		this.searchTerm = searchterm;
		this.meshIdOfSSearchTerm = matchedId;
		this.vpId = vpId;
		this.scriptId = scriptId;
		this.userId = userId;
	}
	
	public String getSearchTerm() {return searchTerm;}
	public void setSearchTerm(String searchTerm) {this.searchTerm = searchTerm;}
	public long getMeshIdOfSSearchTerm() {return meshIdOfSSearchTerm;}
	public void setMeshIdOfSSearchTerm(long meshIdOfSSearchTerm) {this.meshIdOfSSearchTerm = meshIdOfSSearchTerm;}
	public String getParentId() {return vpId;}
	public void setParentId(String vpId) {this.vpId = vpId;}
	public Locale getLoc() {return loc;}
	public void setLoc(Locale loc) {this.loc = loc;}
	public long getScriptId() {return scriptId;}
	public void setScriptId(long scriptId) {this.scriptId = scriptId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}	
}
