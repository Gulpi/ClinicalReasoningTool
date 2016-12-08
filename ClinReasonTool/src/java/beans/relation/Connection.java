package beans.relation;

import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;

import beans.graph.MultiEdge;

/**
 * Connection in the concept map between Relation objects, e.g. ProblemRelation -> DiagnosisRelation. 
 * CAVE: start- and targetIds are the ids of the Relations NOT the listItems! 
 * @author ingahege
 *
 */
public class Connection extends Beans implements Serializable, Comparable{
	
	public static final int WEIGHT_NONE = MultiEdge.WEIGHT_NONE;	
	public static final int WEIGHT_SLIGHTLY_RELATED = MultiEdge.WEIGHT_SLIGHTLY_RELATED;
	public static final int WEIGHT_SOMEWHAT_RELATED = MultiEdge.WEIGHT_SOMEWHAT_RELATED;
	public static final int WEIGHT_HIGHLY_RELATED = MultiEdge.WEIGHT_HIGHLY_RELATED;
	public static final int WEIGHT_SPEAKS_AGAINST = MultiEdge.WEIGHT_SPEAKS_AGAINST;
	public static final int WEIGHT_HIERARCHY = MultiEdge.WEIGHT_EXPLICIT_HIERARCHY;
	

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
	/**
	 * currently only used for learning analytics purposes for learner scripts, but might be used later on for 
	 * other purposes (e.g. display connection not automatically if source and target are visible, but only after the 
	 * given stage)...
	 */
	private int stage = -1;
	/**
	 * if we have multiple endpoints for item boxes we have to store the idx of the start point used for this connection
	 */
	private int startEpIdx = 0;
	/**
	 * if we have multiple endpoints for item boxes we have to store the idx of the target point used for this connection
	 */
	private int targetEpIdx = 0;
	
	public Connection(){}
	public Connection(long startId, long targetId, long illScriptId, int startType, int targetType, int stage){
		this.startId = startId;
		this.targetId = targetId;
		this.illScriptId = illScriptId;
		this.startType = startType;
		this.targetType = targetType;
		this.stage = stage;
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
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}	
	public int getStartEpIdx() {return startEpIdx;}
	public void setStartEpIdx(int startEpIdx) {this.startEpIdx = startEpIdx;}
	public int getTargetEpIdx() {return targetEpIdx;}
	public void setTargetEpIdx(int targetEpIdx) {this.targetEpIdx = targetEpIdx;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o!=null){
			if(o instanceof Connection && ((Connection)o).getId()==this.id) return true;
			if(o instanceof Connection && ((Connection)o).getStartId()==this.startId && ((Connection)o).getTargetId()==this.targetId) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(o instanceof Connection){
			Connection cnx = (Connection) o;
			if(this.getId() < cnx.getId()) return -1;
			if(this.getId() > cnx.getId()) return 1;
			if(this.getId() == cnx.getId()) return 0;
			
		}		
		return 0;
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
