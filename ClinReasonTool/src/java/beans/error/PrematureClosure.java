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
public class PrematureClosure extends Error{
	//do we need any specifics here?
	
	public PrematureClosure(){
		setType(Error.TYPE_PREMATURE_CLOUSRE);
	}
	
	/* (non-Javadoc)
	 * @see beans.error.Error#getType()
	 */
	public long getType() {
		return Error.TYPE_PREMATURE_CLOUSRE;
	}
	

}
