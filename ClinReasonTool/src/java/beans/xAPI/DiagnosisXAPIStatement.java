package beans.xAPI;

import java.util.*;

import beans.relation.RelationDiagnosis;
import beans.scoring.ScoreBean;
import beans.scripts.PatientIllnessScript;
import beans.user.User;
import gov.adlnet.xapi.model.*;
import properties.IntlConfiguration;

/**
 * xAPI Statement created when the learner submits a final diagnosis
 * **/
public class DiagnosisXAPIStatement extends XAPIStatement /* implements AddActionStatement*/{

	private static final long serialVersionUID = 1L;
	public static final String VERB = "diagnosed";
	public static final String INTERACT_TYPE = "fill-in";

	
	public DiagnosisXAPIStatement(PatientIllnessScript patillscript, User user, String vpId){
		initStatement(vpId);
		setActor(user);
		setType(ScoreBean.TYPE_FINAL_DDX);
		setActivity();
		setResult(patillscript.getFinalDiagnoses());		
		//this.itemId = rel.getListItemId();
		setVerb(VERB);
		//this.setTimestamp(new Timestamp(System.currentTimeMillis()));
	}
	
	
	/**
	 * creates an object with an activity and activitiyDefinition.
	 * @param item
	 */
	private void setActivity(){
		Activity act = new Activity();
		ActivityDefinition actDef = new ActivityDefinition();
		actDef.setType(INTERACT_TYPE);
		actDef.setInteractionType(INTERACT_TYPE);
		//InteractionComponent iac = new InteractionComponent();
		act.setDefinition(actDef);
		
		HashMap<String, String> names = new HashMap<String, String>();
		//names.put("en","Identifying problems");
		names.put("en", IntlConfiguration.getValue("scoretype." + ScoreBean.TYPE_FINAL_DDX, new Locale("en")));
		names.put("de", IntlConfiguration.getValue("scoretype." + ScoreBean.TYPE_FINAL_DDX, new Locale("de")));

		actDef.setName(names);
		this.setObject(act);
	}
	
	/**
	 * We set the term the learner has selected or entered into the result object
	 * @param term
	 */
	private void setResult(List<RelationDiagnosis> finalDDXs){
		Result result = this.getResult();
		if(finalDDXs==null || finalDDXs.isEmpty()) return; //should not happen!
		
		if(result==null) result = new Result();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<finalDDXs.size();i++){
			sb.append(finalDDXs.get(i).getLabelOrSynLabel());
		}
		result.setResponse(sb.toString());
		this.setResult(result);
		/*}
		else{
			String s = result.getResponse();
			s += "," + term;
			result.setResponse(s);
		}	*/
	}
	

}
