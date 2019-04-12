package beans.error;

import java.util.List;

import beans.relation.RelationDiagnosis;

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
	
	public Anchoring(long parentId, int stage, int confidence, List<RelationDiagnosis> finals){
		setType(MyError.TYPE_ANCHORING);
		setDiscr(String.valueOf(MyError.TYPE_ANCHORING));
		setPatIllScriptId(parentId);
		setStage(stage);
		if(finals!=null && finals.get(0)!=null)
			setSourceId(finals.get(0).getId());
		setConfidence(confidence);
	}
	

	/* (non-Javadoc)
	 * @see beans.error.MyError#getType()
	 */
	public int getType() { return TYPE_ANCHORING;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_ANCHORING);}
}
