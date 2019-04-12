package beans.error;

import java.util.List;

import beans.relation.RelationDiagnosis;

/**
 * Tendency to be guided by prototypical features of disease and miss atypical variants
 * @author ingahege
 *
 */
public class Representativeness extends MyError{

	public Representativeness(){
		setType(MyError.TYPE_REPRESENTATIVENESS);
		setDiscr(String.valueOf(MyError.TYPE_REPRESENTATIVENESS));
	}
	
	public Representativeness(long parentId, int stage, int confidence, List<RelationDiagnosis> finals){
		setType(MyError.TYPE_REPRESENTATIVENESS);
		setDiscr(String.valueOf(MyError.TYPE_REPRESENTATIVENESS));
		setPatIllScriptId(parentId);
		setStage(stage);
		if(finals!=null && finals.get(0)!=null)
			setSourceId(finals.get(0).getId());
		setConfidence(confidence);
	}
	
	/* (non-Javadoc)
	 * @see beans.error.Error#getType()
	 */
	public int getType() {return MyError.TYPE_REPRESENTATIVENESS; }
	public String getDiscr() {return String.valueOf(MyError.TYPE_REPRESENTATIVENESS);}
	//public String getDescription(){ return "Tendency to be guided by prototypical features of disease and miss atypical variants";}
	//spublic String getName(){return "Representativeness";}

}
