package beans.error;

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
	
	public Representativeness(long parentId, int stage){
		setType(MyError.TYPE_REPRESENTATIVENESS);
		setDiscr(String.valueOf(MyError.TYPE_REPRESENTATIVENESS));
		setPatIllScriptId(parentId);
		setStage(stage);
	}
	
	/* (non-Javadoc)
	 * @see beans.error.Error#getType()
	 */
	public int getType() {return MyError.TYPE_REPRESENTATIVENESS; }
	public String getDiscr() {return String.valueOf(MyError.TYPE_REPRESENTATIVENESS);}
	public String getDescription(){ return "Tendency to be guided by prototypical features of disease and miss atypical variants";}
	public String getName(){return "Representativeness";}

}
