package beans.context;

import java.beans.Beans;
import java.util.Iterator;

import beans.list.ListItem;
import beans.list.Synonym;
import beans.scoring.ScoreBean;
import controller.ScoringController;

/**
 * contextual factors such as time pressure, patient behaviour, emotions, time of day,...
 * @author ingahege
 *
 */
public class Context extends Beans implements ContextFactor{

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
	private ListItem contextItem;
	private long synId;
	/**
	 * 1 = learner, 2 = expert /author
	 */
	private int type;
	
	public Context() {}
	public Context(long userId, long vpId, long listItemId, int stage) {
		this.userId = userId; 
		this.vpId = vpId;
		this.listItemId = listItemId;
		this.stage = stage;
	}
	
	public Context(long userId, long vpId, long listItemId, int stage, long synId) {
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
	public ListItem getContextItem() {return contextItem;}
	public void setContextItem(ListItem contxtItem) {this.contextItem = contxtItem;}
	public long getSynId() {return synId;}
	public void setSynId(long synId) {this.synId = synId;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	
	private Synonym getSynonym(){
		if(synId<=0 ||contextItem.getSynonyma()==null) return null; 
		Iterator<Synonym> it = contextItem.getSynonyma().iterator();
		while(it.hasNext()){
			Synonym syn = it.next();		
			if(syn.getId() == this.synId) return syn;
		}
		return null; //TODO Error handling, this should not happen!
	}

	
	public String getName() {
		if(contextItem!=null && this.synId<=0) return contextItem.getName(); //a main list item
		Synonym syn = getSynonym();
		if(syn!=null) return syn.getName();		
		return "";
	}
	
	
	public int getScore(){ 
		return new ScoringController().getExpScore(ScoreBean.TYPE_ADD_CONTEXT, this.getListItemId());
	}
	


	public boolean equals(Object o) {
		if(o ==null || !(o instanceof Context)) return false;
		Context c = (Context) o;
		if(c.getId()==id) return true; 
		if(c.getListItemId()==listItemId) return true;
		
		return false;
	}
	
	
}
