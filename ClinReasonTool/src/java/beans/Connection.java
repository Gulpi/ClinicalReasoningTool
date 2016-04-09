package beans;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import beans.relation.Relation;
import controller.ConceptMapController;

/**
 * Connection in the concept map between Relation objects, e.g. ProblemRelation -> DiagnosisRelation. 
 * CAVE: start- and targetIds are the ids of the Relations NOT the listItems! 
 * @author ingahege
 *
 */
public class Connection extends Beans implements Serializable{


	private static final long serialVersionUID = 1L;
	/**
	 * just an internal id
	 */
	private long id;
	/**
	 * id of a Relation (e.g. a ProblemRelation) object
	 */
	private long startId; 
	/**
	 * id of a Relation (e.g. a DiagnosisRelation) object
	 */
	private long targetId;
	private long illScriptId;
	//private String label; 
	//private String color; //define default color
	private Timestamp creationDate;
	private int startType; //see definitions in ConceptMapController
	private int targetType; //see definitions in ConceptMapController
	private int weight; 
	public Connection(){}
	public Connection(long startId, long targetId, long illScriptId, int startType, int targetType){
		this.startId = startId;
		this.targetId = targetId;
		this.illScriptId = illScriptId;
		this.startType = startType;
		this.targetType = targetType;
	}
	
	public long getStartId() {return startId;}
	public void setStartId(long startId) {this.startId = startId;}
	public long getTargetId() {return targetId;}
	public void setTargetId(long targetId) {this.targetId = targetId;}
	public long getIllScriptId() {return illScriptId;}
	public void setIllScriptId(long illScriptId) {this.illScriptId = illScriptId;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public Timestamp getCreationDate() {return creationDate;}
	public void setCreationDate(Timestamp creationDate) {this.creationDate = creationDate;}			
	public int getStartType() {return startType;}
	public void setStartType(int startType) {this.startType = startType;}
	public int getTargetType() {return targetType;}
	public void setTargetType(int targetType) {this.targetType = targetType;}	
	public int getWeight() {return weight;}
	public void setWeight(int weight) {this.weight = weight;}
	
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof Connection && ((Connection)o).getId()==this.id /*&& ((Connection)o).getTargetId()==this.targetId*/)
				return true;
		}
		return false;
	}
	

	/*public String toJson(){
		//'[{"id": "cnx_1", "sourceid": "cmddx_6","targetid": "cmddx_3"}]';
		StringBuffer sb = new StringBuffer();
		ConceptMapController cmc = new ConceptMapController();
		String startIdWithPrefix = cmc.getPrefixByType(startType)+startId; 	
		String targetIdWithPrefix = cmc.getPrefixByType(targetType)+targetId;
		
		sb.append("{\"id\":\""+cmc.PREFIX_CNX + this.getId()+"\",\"sourceid\": \""+startIdWithPrefix+"\",\"targetid\": \""+targetIdWithPrefix+"\"}");		
		return sb.toString();		
	}*/
	
}
