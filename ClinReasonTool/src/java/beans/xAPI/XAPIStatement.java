package beans.xAPI;

import java.io.Serializable;

//import static beans.xAPI.XAPIStatement.PLATFORM;

import java.util.*;

import com.google.gson.JsonElement;

import beans.user.User;
import gov.adlnet.xapi.model.*;

public class XAPIStatement extends Statement implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String HOMEPAGE = "https://crt.casus.net";
	private static final String PLATFORM = "Concept mapping tool";
	private boolean sent = false;
	private int type;

	protected void initStatement(String vpId){
		this.setId(UUID.randomUUID().toString());
		setContext(vpId);	
	}
	
	protected void setActor(User user){
		Agent agent = new Agent(); 
		agent.setAccount(new Account(user.getExtUserId(), HOMEPAGE));
		this.setActor(agent);
	}
	
	protected void setContext(String vpId){
		Context context = new Context();
		context.setPlatform(PLATFORM);
		ContextActivities ca = new ContextActivities();
		Activity parent = new Activity();
		parent.setId(vpId);
		ArrayList<Activity> list = new ArrayList<Activity>();
		list.add(parent);
		ca.setParent(list);
		context.setContextActivities(ca);
		this.setContext(context);
	}
	
	/* (non-Javadoc)
	 * @see beans.xAPI.ActionStatement#hasBeenSent()
	 */
	public boolean hasBeenSent() {return sent;}

	/* (non-Javadoc)
	 * @see beans.xAPI.ActionStatement#getResponse()
	 */
	public String getResponse() {
		if(this.getResult()==null) return null;
		return this.getResult().getResponse();
	}
	
	/*public JsonElement serialize(){
		return this.serialize();
	}*/
	
	protected void setVerb(String verb){
		Verb v = new Verb();
		v.setId(verb);
		this.setVerb(v);
		//interacted for any adding to the map
		//?? for summary statement
	}
	
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}

}
