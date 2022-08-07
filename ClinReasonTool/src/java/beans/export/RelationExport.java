package beans.export;

/**
 * For exporting maps to Excel we have in this class everything we need for the relations/items- 
 * in one view. 
 * @author ingahege
 *
 */
public class RelationExport {

	private long patillscriptId;
	private String vpId; 
	private long sourceId; 
	private String name; 
	private int stage; 
	private int itemType;
	/**
	 * just for ddxs otherwise -1
	 */
	private int ruledOutStage;
	/**
	 * just for ddxs otherwise -1
	 */
	private int workingDDXStage; 
	/**
	 * just for ddxs otherwise -1
	 */
	private int mnmStage;
	/**
	 * just for ddxs otherwise -1
	 */
	private int finalDDXStage;
	private String prefix;
	
	
	public long getPatillscriptId() {return patillscriptId;}
	public void setPatillscriptId(long patillscriptId) {this.patillscriptId = patillscriptId;}
	public String getVpId() {return vpId;}
	public void setVpId(String vpId) {this.vpId = vpId;}
	public long getSourceId() {return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public int getRuledOutStage() {return ruledOutStage;}
	public void setRuledOutStage(int ruledOutStage) {this.ruledOutStage = ruledOutStage;}
	public int getWorkingDDXStage() {return workingDDXStage;}
	public void setWorkingDDXStage(int workingDDXStage) {this.workingDDXStage = workingDDXStage;}
	public int getMnmStage() {return mnmStage;}
	public void setMnmStage(int mnmStage) {this.mnmStage = mnmStage;}
	public String getPrefix() {return prefix;}
	public void setPrefix(String prefix) {this.prefix = prefix;}
	public int getFinalDDXStage() {return finalDDXStage;}
	public void setFinalDDXStage(int finalDDXStage) {this.finalDDXStage = finalDDXStage;}
	public int getItemType() {return itemType;}
	public void setItemType(int itemType) {this.itemType = itemType;} 
	
}
