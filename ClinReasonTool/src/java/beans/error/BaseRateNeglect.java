package beans.error;

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
	
	public BaseRateNeglect(long parentId, int stage){
		setType(MyError.TYPE_BASERATE);
		setDiscr(String.valueOf(MyError.TYPE_BASERATE));
		setPatIllScriptId(parentId);
		setStage(stage);
	}
	
	public String getDescription() {
		return "Tendency to ignore the true rate of a disease and pursue rare and more exotic diagnoses";
	}

	public String getName() { return "BaseRateNeglect";}
	public int getType() {return TYPE_BASERATE;}
	public String getDiscr() {return String.valueOf(MyError.TYPE_BASERATE);}


}
