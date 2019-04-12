package beans.error;

import java.util.List;

import beans.relation.RelationDiagnosis;

/**
 * What recently has been seen is more likely to be diagnosed later on. 
 * For detection of this error we look into the previous illness scripts of the learner. 
 * @author ingahege
 *
 */
public class AvailabilityBias extends MyError{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Number of recent scripts we include into the determination of an availability bias.
	 */
	public static final int NUM_SCRIPTS = 10;
	public static final int NUM_DAYS = 5;
	
	public AvailabilityBias(){
		setType(MyError.TYPE_AVAILABILITY);
		setDiscr(String.valueOf(MyError.TYPE_AVAILABILITY));
	}
	
	public AvailabilityBias(long parentId, int stage, int confidence, List<RelationDiagnosis> finals){
		setType(MyError.TYPE_AVAILABILITY);
		setDiscr(String.valueOf(MyError.TYPE_AVAILABILITY));
		setPatIllScriptId(parentId);
		setStage(stage);
		if(finals!=null && finals.get(0)!=null)
			setSourceId(finals.get(0).getId());
		setConfidence(confidence);
	}
	
	//public String getDescription() {return "What has recently been seen, is more likely.";}
	//public String getName() { return "Availability Bias"; }

	/* (non-Javadoc)
	 * @see beans.error.MyError#getType()
	 */
	public int getType() { return TYPE_AVAILABILITY;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_AVAILABILITY);}

}
