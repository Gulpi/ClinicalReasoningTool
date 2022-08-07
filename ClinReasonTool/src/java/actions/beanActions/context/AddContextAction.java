package actions.beanActions.context;

import java.beans.Beans;

import javax.faces.application.FacesMessage.Severity;

import actions.scoringActions.Scoreable;
import actions.scoringActions.ScoringContextAction;
import beans.ContextContainer;
import beans.LogEntry;
import beans.context.*;
import beans.list.*;
import database.DBContext;
import database.DBList;

public class AddContextAction implements Scoreable{

	private ContextContainer cc;
	
	//public AddActorAction() {}
	public AddContextAction(ContextContainer cc) {
		this.cc = cc;
	}

	public void triggerScoringAction(Beans beanToScore, boolean isJoker) {
		// TODO Auto-generated method stub
		
	}

	private void save(Beans b) {
		new DBContext().saveAndCommit(b);		
	}

	public void notifyLog(ContextFactor cf) {
		new LogEntry(LogEntry.ADD_CONTEXT_ACTION, cf.getId(), cf.getListItemId()).save();
	}

	/**
	 * @param id
	 * @param name (in case user has added something new that is not in the list yet.
	 */
	public Context add(String id, String name, int type) {
		if(cc==null) return null;
		long listItemId = -1;
		ListItem li = null;
		long synId = -1;
		//synonym to be added
		if(id!=null && id.startsWith(Synonym.SYN_VERTEXID_PREFIX)) {
			synId = Long.parseLong(id.substring(Synonym.SYN_VERTEXID_PREFIX.length()));
			Synonym syn = new DBList().selectSynonymById(synId);
			listItemId = syn.getListItemId();
			li = new DBList().selectListItemById(listItemId);
		}
		//org item to be added
		else if(id!= null) {
			listItemId = Long.parseLong(id);
			li = new DBList().selectListItemById(listItemId);
		} 
		
		//new item to be created in list
		else if (id==null || id.isEmpty()) {
			li =  new DBList().saveNewEntry(name, cc.getLocale());
			listItemId = li.getListItemId();
		}
		
		
		Context c = new Context(cc.getUserId(), cc.getVpId(), listItemId, cc.getCurrentStage(),synId);
		c.setContextItem(li);
		c.setType(cc.getType());
		c.setOrder(getOrderNrOfNewContext());
		//check whether actor already added! 
		if(cc.contains(c)) return null;
		this.save(c);
		
		notifyLog(c);	
		if(type==ContextContainer.TYPE_PLAYER) new ScoringContextAction().scoreAddContextAction(c);
		return c;
	}
	
	/**
	 * we get the new orderNr for a new Actor object
	 * As numbers are not updated if something is deleted, we need to get the next hights number.  
	 * @return
	 */
	private int getOrderNrOfNewContext() {
		if(cc==null || cc.getCtxts()==null || cc.getCtxts().size()==0) return 0;
		int maxOrderNr = 0;
		for(int i=0;i<cc.getCtxts().size();i++) {
			Context c = cc.getCtxts().get(i);
			if(c.getOrder()>maxOrderNr) maxOrderNr = c.getOrder();
		}
		return maxOrderNr+1;
	}
	

	public void createErrorMessage(String summary, String details, Severity sev) {
		// TODO Auto-generated method stub
		
	}


}
