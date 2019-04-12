package beans.error;

import java.util.List;

import beans.relation.RelationDiagnosis;

/**
 * Tendency to look for confirming evidence of a hypothesis -> need to look for disconfirming evidence!
 *  -> how can we recognize that???? 
 * @author ingahege
 *
 */
public class ConfirmationBias extends MyError{

	public ConfirmationBias(){
		setType(MyError.TYPE_CONFIRMATION);
		setDiscr(String.valueOf(MyError.TYPE_CONFIRMATION));
	}
	
	public ConfirmationBias(long parentId, int stage, int confidence, List<RelationDiagnosis> finals){
		setType(MyError.TYPE_CONFIRMATION);
		setDiscr(String.valueOf(MyError.TYPE_CONFIRMATION));
		setPatIllScriptId(parentId);
		setStage(stage);
		if(finals!=null && finals.get(0)!=null)
			setSourceId(finals.get(0).getId());
		setConfidence(confidence);
	}
	
	//public String getDescription() {return "Tendency to look for confirming evidence of a hypothesis";}
	//public String getName() { return "Conformation Bias";}
	public int getType() {return TYPE_CONFIRMATION;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_CONFIRMATION);}


}
