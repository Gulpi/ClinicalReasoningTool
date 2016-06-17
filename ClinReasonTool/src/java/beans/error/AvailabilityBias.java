package beans.error;

/**
 * What recently has been seen is more likely to be diagnosed later on. 
 * For detection of this error we look into the previous illness scripts of the learner. 
 * @author ingahege
 *
 */
public class AvailabilityBias extends MyError{

	/**
	 * Number of recent scripts we include into the determination of an availability bias.
	 */
	public static final int NUM_SCRIPTS = 10;
	
	public AvailabilityBias(){
		setType(MyError.TYPE_AVAILABILITY);
		setDiscr(String.valueOf(MyError.TYPE_AVAILABILITY));
	}
	
	public AvailabilityBias(long parentId, int stage){
		setType(MyError.TYPE_AVAILABILITY);
		setDiscr(String.valueOf(MyError.TYPE_AVAILABILITY));
		setPatIllScriptId(parentId);
		setStage(stage);
	}
	
	public String getDescription() {return "What has recently been seen, is more likely.";}
	public String getName() { return "Availability Bias"; }

	/* (non-Javadoc)
	 * @see beans.error.MyError#getType()
	 */
	public int getType() { return TYPE_AVAILABILITY;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_AVAILABILITY);}

}
