package beans.xAPI;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;

import beans.relation.Relation;
import beans.user.User;
import gov.adlnet.xapi.model.*;
import properties.IntlConfiguration;

/**
 * xAPI Statement created when the learner adds an item to the concept map
 * actor = agent
 * result.response -> entered term is stored in the result
 * ActionDefiniton.name -> (e.g "Add Finding", "Add DDX",...)
 * ActionDefiniton.type -> "long-fill-in"
 * 
 * @author ingahege
 *
 */
public class AddActionXAPIStatement extends XAPIStatement implements AddActionStatement{
	public static final String VERB = "added";
	public static final String INTERACT_TYPE = "fill-in";

	private long itemId;
	
	/**
	 * @param userId (id of the actor/agent)
	 * @param term entered by the learner
	 * @param relType action carried out by the learner
	 */
	public AddActionXAPIStatement(User user, String term, int relType, String vpId){
		initStatement(vpId);
		setActor(user);
		setType(relType);
		setActivity(relType);
		setResult(term);
		setVerb(VERB);
		//this.setTimestamp(new Timestamp(System.currentTimeMillis()));
	}
	
	public AddActionXAPIStatement(Relation rel, User user, String vpId){
		initStatement(vpId);
		setActor(user);
		setType(rel.getRelationType());
		setActivity(getType());
		setResult(rel.getLabelOrSynLabel());		
		this.itemId = rel.getListItemId();
		setVerb(VERB);
		//this.setTimestamp(new Timestamp(System.currentTimeMillis()));
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
		//names.put("en","Identifying problems");
		names.put("en", IntlConfiguration.getValue("scoretype."+ relType, new Locale("en")));
		names.put("de", IntlConfiguration.getValue("scoretype."+ relType, new Locale("dej")));

		actDef.setName(names);
		this.setObject(act);
	
	}
	

	
	/**
	 * We set the term the learner has selected or entered into the result object
	 * @param term
	 */
	private void setResult(String term){
		addResponseToResult(term);
	}
	
	/**
	 * We add the new term to the response in the result object. Responses are separated by # to avoid any confusion with 
	 * strings that contain commas.
	 * @param term
	 */
	public void addResponseToResult(String term){
		Result result = this.getResult();
		if(result==null){
			result = new Result();
			result.setResponse(term);
			this.setResult(result);
		}
		else{
			String s = result.getResponse();
			s += "," + term;
			result.setResponse(s);
		}	
	}
	
	public void deleteResponseFromResult(String term){
		Result result = this.getResult();
		if(result==null) return;
		//String[] responseArr = StringUtils.split(result.getResponse(), "#");
		StringUtils.remove(result.getResponse(), term);
		
	}
			
}
