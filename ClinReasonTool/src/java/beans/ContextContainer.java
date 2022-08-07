package beans;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import actions.beanActions.context.*;
import beans.context.*;
import controller.AjaxController;
import controller.NavigationController;

/**
 * contains all contextual factors added for a VP
 * @author ingahege
 *
 */
public class ContextContainer {
	
	public static final int TYPE_AUTHOR = 2;
	public static final int TYPE_PLAYER = 1;
	

	private List<Actor> actors;
	private List<Context> ctxts;
	//other factors...
	private long userId;
	private long vpId;
	private int currentStage =1;
	private int type = 1; //1=player, 2=authoring
	/**
	 * locale of the VP, so that actors are added in the same language
	 */
	private String locale;
	
	public ContextContainer(long userId, long vpId, String locale, int type){
		this.userId = userId;
		this.vpId = vpId;
		this.locale = locale;
		this.type = type;
	}
	public ContextContainer(long vpId, int type){
		this.vpId = vpId;
		this.type = type;
	}
	
	public List<Actor> getActors() {return actors;}	
	public void setActors(List<Actor> actors) {this.actors = actors;}	
	public List<Context> getCtxts() {return ctxts;}
	public void setCtxts(List<Context> ctxts) {this.ctxts = ctxts;}
	
	public String getLocale() {return locale;}
	public void setLocale(String locale) {this.locale = locale;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public long getVpId() {return vpId;}
	public void setVpId(long vpId) {this.vpId = vpId;}		
	
	public int getCurrentStage() {
		String stage = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_STAGE);
		if(StringUtils.isNumeric(stage))
			this.currentStage = Integer.valueOf(stage);			
		return this.currentStage;
	}
	
	public void setCurrentStage(int currentStage) {this.currentStage = currentStage;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}

	/**
	 * looks for the Actor with the given id in the actors list and returns it if found or null.
	 * @param id
	 * @return
	 */
	public Actor getActorById(long id) {
		if(actors==null || actors.isEmpty()) return null; 
		for(int i=0;i<actors.size();i++) {
			if (actors.get(i).getId()==id) return actors.get(i);
		}
		return null;
	}
	
	public Context getContextById(long id) {
		if(ctxts==null || ctxts.isEmpty()) return null; 
		for(int i=0;i<ctxts.size();i++) {
			if (ctxts.get(i).getId()==id) return ctxts.get(i);
		}
		return null;
	}
	
	public void removeActorFromList(Actor act) {
		if(actors==null || actors.isEmpty()) return; 
		actors.remove(act);
	}
	
	public void removeContextFromList(Context c) {
		if(ctxts==null || ctxts.isEmpty()) return; 
		ctxts.remove(c);
	}
	
	/**
	 * checks whether the given Actor is already in the list of actors. 
	 * @param act
	 * @return
	 */
	public boolean contains(Actor act) {
		if(actors==null || actors.isEmpty()) return false; 
		if(actors.contains(act)) return true;
		return false;
	}
	
	public boolean contains(Context c) {
		if(ctxts==null || ctxts.isEmpty()) return false; 
		if(ctxts.contains(c)) return true;
		return false;
	}
	
	public void addActor(String listItemId, String name){
		Actor act = new AddActorAction(this).add(listItemId, name, type);
		addActor(act);
	}
	
	public void addActor(Actor act){
		if(actors==null) actors = new ArrayList<Actor>();
		if(act!=null)
			actors.add(act);
	}
	
	public void addContext(String listItemId, String name){
		Context c = new AddContextAction(this).add(listItemId, name, type);
		addContext(c);
	}
	
	public void addContext(Context c){
		if(ctxts==null) ctxts = new ArrayList<Context>();
		if(c!=null)
			ctxts.add(c);
	}

	
	/**
	 * We delete the Actor with the given id from the list and from the database (if the id is valid)
	 * @param idStr
	 */
	public void delActor(String idStr) {
		if (idStr==null || idStr.isEmpty()) return;
		long id = -1;
		try {
			id = Long.parseLong(idStr);
		}
		catch(Exception e) {return;}
		if(id<=0) return;
		new DelActorAction(this).delete(id);
		if(actors!=null) {
			for(int i=0;i<actors.size();i++) {
				Actor act = actors.get(i);
				if(act.getId()==id) {
					actors.remove(act);
					return;
				}
			}
		}
	}
	
	/**
	 * We delete the Actor with the given id from the list and from the database (if the id is valid)
	 * @param idStr
	 */
	public void delContext(String idStr) {
		if (idStr==null || idStr.isEmpty()) return;
		long id = -1;
		try {
			id = Long.parseLong(idStr);
		}
		catch(Exception e) {return;}
		if(id<=0) return;
		new DelContextAction(this).delete(id);
		if(ctxts!=null) {
			for(int i=0;i<ctxts.size();i++) {
				Context c = ctxts.get(i);
				if(c.getId()==id) {
					ctxts.remove(c);
					return;
				}
			}
		}
	}
	
	
	/** 
	 * we land here from an ajax request for any actions concerning the patientIllnessScript....
	 **/
	public void ajaxResponseHandler() throws IOException {
		AjaxController.getInstance().receiveAjax(this);
	}
	
}
