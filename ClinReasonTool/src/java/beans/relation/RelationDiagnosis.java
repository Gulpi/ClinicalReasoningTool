package beans.relation;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import controller.ConceptMapController;
import model.ListItem;
/**
 * connects a Diagnosis object to a (Patient)IllnessScript object with some attributes.
 * @author ingahege
 */
public class RelationDiagnosis extends Beans implements Relation, Rectangle, Serializable {

	private static final long serialVersionUID = 1L;
	public static final int REL_TYPE_FINAL = 1; //final diagnosis (for PIS only)
	public static final int REL_TYPE_DDX = 2; //differential diagnosis 
	public static final int REL_TYPE_COMPL = 3; //complication of IS diagnosis 
	public static final int REL_TYPE_RELATED = 4; //otherwise related diagnosis 
	public static final int DEFAULT_X = 70; //default x position of problems in canvas
	public static final String COLOR_DEFAULT = "#ffffff";
	public static final String COLOR_RED = "#990000";
	private long id;
	/**
	 * diagnosis id
	 */
	private long sourceId; 
	/**
	 * (Patient)Illnesscript
	 */
	private long destId; 
	
	private int order;
		
	/**
	 * -1 = not stated, todo: define levels here (slider with Percentage?)
	 */
	private int confidence = -1; //we need levels here (only for PIS)

	private int type = -1; //see definitions above
	
	/**
	 * diagnoses: doNotMiss/lethal/important favorite
	 */
	private int value = -1; //key finding,...
	
	//private Timestamp creationDate;
	private int x;
	private int y;
	/**
	 * Backgroundcolor of the diagnosis rectangle
	 */
	private String color; //default: #ffffff
	private ListItem diagnosis;
	private int mnm = 0;

	public RelationDiagnosis(){}
	public RelationDiagnosis(long sourceId, long destId){
		this.setSourceId(sourceId);
		this.setDestId(destId);
	}
	public long getSourceId() { return sourceId;}
	public void setSourceId(long sourceId) {this.sourceId = sourceId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getDestId() {return destId;}
	public void setDestId(long destId) {this.destId = destId;}
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public ListItem getDiagnosis() {return diagnosis;}
	public void setDiagnosis(ListItem diagnosis) {this.diagnosis = diagnosis;}	
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}	
	public String getColor() {return color;}
	public void setColor(String color) {this.color = color;}
	//public Timestamp getCreationDate() {return creationDate;}
	//public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}
	public String getIdWithPrefix(){ return ConceptMapController.PREFIX_DDX+this.getId();}
	public int getMnm() {return mnm;}
	public void setMnm(int mnm) {this.mnm = mnm;}
	public boolean isMnM(){
		if(mnm==1) return true;
		return false;
	}
	/* (non-Javadoc)
	 * @see beans.relation.Rectangle#toJson()
	 */
	public String toJson(){
		StringBuffer sb = new StringBuffer();
		sb.append("{\"label\":\""+this.getDiagnosis().getName()+"\",\"shortlabel\":\""+this.getDiagnosis().getShortName()+"\",\"id\": \""+getIdWithPrefix()+"\",\"x\": "+this.x+",\"y\":"+this.y+",\"color\": \""+this.color+"\"}");		
		return sb.toString();
	}
	
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof RelationDiagnosis && ((RelationDiagnosis)o).getSourceId()==this.sourceId && ((RelationDiagnosis)o).getDestId()==this.destId)
				return true;
		}
		return false;
	}
}
