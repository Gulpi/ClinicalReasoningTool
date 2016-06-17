package beans.error;

/**
 * Fixate on first impression -> leads to premature closure, can we distinguish those two??
 * 
 * @author ingahege
 *
 */
public class Anchoring extends MyError{
	
	public Anchoring(){
		setType(MyError.TYPE_ANCHORING);
		setDiscr(String.valueOf(MyError.TYPE_ANCHORING));
	}
	
	public Anchoring(long parentId, int stage){
		setType(MyError.TYPE_ANCHORING);
		setDiscr(String.valueOf(MyError.TYPE_ANCHORING));
		setPatIllScriptId(parentId);
		setStage(stage);
	}
	
	public String getDescription() {return "Fixation on first impression.";}
	public String getName() { return "Anchoring"; }

	/* (non-Javadoc)
	 * @see beans.error.MyError#getType()
	 */
	public int getType() { return TYPE_ANCHORING;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_ANCHORING);}
}
