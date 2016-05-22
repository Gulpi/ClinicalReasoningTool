package beans.error;

import java.util.*;

import javax.faces.bean.SessionScoped;

/**
 * wrong diagnoses submitted before case has ended
 * Criteria: 
 * 1. score = 0
 * 2. stage of exp final diagnosis is later than leaners stage
 * @author ingahege
 *
 */
@SessionScoped
public class PrematureClosure extends MyError{
	//do we need any specifics here?
	
	public PrematureClosure(){
		setType(MyError.TYPE_PREMATURE_CLOUSRE);
		setDiscr(String.valueOf(MyError.TYPE_PREMATURE_CLOUSRE));
	}
	
	public PrematureClosure(long parentId, int stage){
		setType(MyError.TYPE_PREMATURE_CLOUSRE);
		setDiscr(String.valueOf(MyError.TYPE_PREMATURE_CLOUSRE));
		setPatIllScriptId(parentId);
		setStage(stage);
	}
	
	/* (non-Javadoc)
	 * @see beans.error.Error#getType()
	 */
	public int getType() {
		return MyError.TYPE_PREMATURE_CLOUSRE;
	}
	public String getDiscr() {return String.valueOf(MyError.TYPE_PREMATURE_CLOUSRE);}
	public String getDescription(){ return "Accepting a diagnosis too early .... etc...";}
	public String getName(){return "Premature Closure";}

	

}
