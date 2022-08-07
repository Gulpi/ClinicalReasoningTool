package beans.context;

import java.beans.Beans;
import java.io.Serializable;
import java.util.*;
import beans.list.*;
import beans.scoring.ScoreBean;
import controller.ScoringController;

/**
 * actors as contextual factor, that are present in the VP and relevant for the CR process
 * @author ingahege
 *
 */
public class Actor extends Beans implements Serializable, ContextFactor{

	private static final long serialVersionUID = 1L;
	private long id; 
	/**
	 * actors are in the MesH list, this id points to this entry
	 */
	private long listItemId; 
	private long vpId;
	/**
	 * userId from the parent system
	 */
	private long userId;
	/**
	 * Stage / card at which the actor has been added
	 */
	private int stage;
	/**
	 * index or orderNr the actor has in the list of actors. (0-based)
	 */
	private int order;
	private ListItem actorItem;
	private long synId;
	/**
	 * 1 = learner, 2 = expert /author
	 */
	private int type;
	
	public Actor() {}
	public Actor(long userId, long vpId, long listItemId, int stage) {
		this.userId = userId; 
		this.vpId = vpId;
		this.listItemId = listItemId;
		this.stage = stage;
	}
	
	public Actor(long userId, long vpId, long listItemId, int stage, long synId) {
		this.userId = userId; 
		this.vpId = vpId;
		this.listItemId = listItemId;
		this.stage = stage;
		this.synId = synId;
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public long getListItemId() {return listItemId;}
	public void setListItemId(long listItemId) {this.listItemId = listItemId;}
	public long getVpId() {return vpId;}
	public void setVpId(long vpId) {this.vpId = vpId;}
	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public int getOrder() {return order;}
	public void setOrder(int order) {this.order = order;}
	public ListItem getActorItem() {
		return actorItem;
		
	}
	public void setActorItem(ListItem actorItem) {this.actorItem = actorItem;}		
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	public long getSynId() {return synId;}
	public void setSynId(long synId) {this.synId = synId;}
	
	/**
	 * returns the name of the listItem or the synonym
	 */
	public String getName() {
		if(actorItem!=null && this.synId<=0) return actorItem.getName(); //a main list item
		Synonym syn = getSynonym();
		if(syn!=null) return syn.getName();		
		return "";
	}
	
	private Synonym getSynonym(){
		if(synId<=0 || actorItem.getSynonyma()==null) return null; 
		Iterator<Synonym> it = actorItem.getSynonyma().iterator();
		while(it.hasNext()){
			Synonym syn = it.next();		
			if(syn.getId() == this.synId) return syn;
		}
		return null; //TODO Error handling, this should not happen!
	}
	
	public int getScore(){ 
		return new ScoringController().getExpScore(ScoreBean.TYPE_ADD_ACTOR, this.getListItemId());
	}
	

	public boolean equals(Object o) {
		if(o ==null || !(o instanceof Actor)) return false;
		Actor act = (Actor) o;
		if(act.getId()==id) return true; 
		if(act.getListItemId()==listItemId) return true;
		
		return false;
	}
	
}
