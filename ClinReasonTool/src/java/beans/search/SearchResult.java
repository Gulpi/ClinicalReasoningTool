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
	
	/**
	 * 1=problem, 2=diagonsis, 3=test,4=management
	 */
	private int itemType = -1;
	/**
	 * 0 for main item, 1 for synonyns'
	 */
	private int synonym = -1;
	/**
	 * expert(s) patient illness script id
	 */
	private long pisId = -1;
	
	/**
	 * when a synonym this would be set
	 */
	private long synId = -1;
	
	/**
	 * explicit set, so an explicit set synonym can be detected by selSynId==synId
	 */
	private long selSynId = -1;
	
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	
	public int getItemType() { return itemType; }
	public void setItemType(int itemType) { this.itemType = itemType; }
	public int getSynonym() { return synonym; }
	public void setSynonym(int synonym) { this.synonym = synonym; }
	public long getPisId() { return pisId; }
	public void setPisId(long pisId) { this.pisId = pisId; }
	
	public long getSynId() { return synId; }
	public void setSynId(long synId) { this.synId = synId; }
	public long getSelSynId() { return selSynId; }
	public void setSelSynId(long selSynId) { this.selSynId = selSynId; }
	
	/**
	 * @return vpId without the system identifier (e.g. 12345 instead of 12345_2)
	 */
	public String getTruncVpId(){
		return vpId.substring(0,vpId.indexOf("_"));
	}
	
	
}
