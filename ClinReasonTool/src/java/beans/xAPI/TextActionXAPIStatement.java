package beans.xAPI;

import java.util.*;

import beans.relation.summary.SummaryStatement;
import beans.scoring.ScoreBean;
import beans.user.User;
import gov.adlnet.xapi.model.*;
import properties.IntlConfiguration;

/**
 * xAPI statement for text creation, such as the summary statement.
 * @author ingahege
 *
 */
public class TextActionXAPIStatement extends XAPIStatement{
	private static final long serialVersionUID = 1L;
	public static final String VERB = "reported"; //composed?
	public static final String INTERACT_TYPE = "long-fill-in";
		
	public TextActionXAPIStatement(SummaryStatement sumst, User user, String vpId){
		initStatement(vpId);
		super.setActor(user);
		setType(ScoreBean.TYPE_SUMMST);
		setVerb(VERB);
		setResult(sumst.getText());
		//this.itemId = sumst.getId();
		setActivity(getType());
	}
	
	
	/**
	 * creates an object with an activity and activitiyDefinition.
	 * @param item
	 */
	private void setActivity(int relType){
		Activity act = new Activity();
		ActivityDefinition actDef = new ActivityDefinition();
		actDef.setType(INTERACT_TYPE);
		actDef.setInteractionType(INTERACT_TYPE);
		//InteractionComponent iac = new InteractionComponent();
		act.setDefinition(actDef);
		
		HashMap<String, String> names = new HashMap<String, String>();
		names.put("en", IntlConfiguration.getValue("scoretype."+ relType, new Locale("en")));
		names.put("de", IntlConfiguration.getValue("scoretype."+ relType, new Locale("de")));

		actDef.setName(names);
		this.setObject(act);
	
	}
	
	public void setResult(String text){
		Result result = new Result();
		result.setResponse(text);
		this.setResult(result);
	}
}
