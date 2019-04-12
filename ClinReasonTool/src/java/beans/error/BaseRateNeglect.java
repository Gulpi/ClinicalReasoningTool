package beans.error;

import java.util.List;

import beans.relation.RelationDiagnosis;

/**
 * tendency to ignore the true rate of a disease and pursue rare and more exotic diagnoses. 
 * @author ingahege
 *
 */
public class BaseRateNeglect extends MyError{

	public BaseRateNeglect(){
		setType(MyError.TYPE_BASERATE);
		setDiscr(String.valueOf(MyError.TYPE_BASERATE));
	}
	
	public BaseRateNeglect(long parentId, int stage, int confidence, List<RelationDiagnosis> finals){
		setType(MyError.TYPE_BASERATE);
		setDiscr(String.valueOf(MyError.TYPE_BASERATE));
		setPatIllScriptId(parentId);
		setStage(stage);
		if(finals!=null && finals.get(0)!=null)
			setSourceId(finals.get(0).getId());
		setConfidence(confidence);
	}
	
	/*public String getDescription() {
		return "Tendency to ignore the true rate of a disease and pursue rare and more exotic diagnoses";
	}

	public String getName() { return "BaseRateNeglect";}*/
	public int getType() {return TYPE_BASERATE;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_BASERATE);}


}
