package beans.search;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * based on a view that contains all listentries of all expert scripts, to allow for a quick search...
 * @author ingahege
 *
 */
@ManagedBean(name = "searchResult", eager = true)
@SessionScoped
public class SearchResult implements Serializable {

	private String vpId;
	private String name;
	private long sourceId;
	
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	
	/**
	 * @return vpId without the system identifier (e.g. 12345 instead of 12345_2)
	 */
	public String getTruncVpId(){
		return vpId.substring(0,vpId.indexOf("_"));
	}
	
	
}
