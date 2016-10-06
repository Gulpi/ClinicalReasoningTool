package controller;

import java.util.*;

import actions.beanActions.AddAction;
import beans.relation.Relation;
import beans.user.User;
import database.DBList;
import model.ListItem;
import model.Synonym;

public class RelationController {
	
	public Synonym getSynonym(long synId, Relation rel){
		if(synId<=0 || rel.getSynonyma()==null) return null; 
		Iterator<Synonym> it = rel.getSynonyma().iterator();
		while(it.hasNext()){
			Synonym syn = it.next();		
			if(syn.getId() == rel.getSynId()) return syn;
		}
		return null; //TODO Error handling, this should not happen!
	}
	
	public void initAdd(String idStr, String name, String xStr, String yStr, AddAction aa, Locale scriptLoc){
		long id;
		ListItem li = null;
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());

		
		if(idStr.startsWith(Synonym.SYN_VERTEXID_PREFIX)){ //synonym selected
			//type = AddAction.ADD_TYPE_SYNITEM;
			id = Long.valueOf(idStr.substring(Synonym.SYN_VERTEXID_PREFIX.length()));
			Synonym syn = new DBList().selectSynonymById(id);
			li = getListItemById(syn.getListItemId(), scriptLoc);
			aa.addRelation(/*syn.getListItemId(), name*/li, (int)x, (int)y, id); //then we add a synonym
			return;
		}
		
		id = Long.valueOf(idStr.trim());
		li = getListItemById(id, scriptLoc);
		aa.addRelation(/*id, name*/li, (int)x, (int)y, -1);
		
	}
	
	private ListItem getListItemById(long id, Locale loc){
		if(id==-99){ //then user has selected own entry and we have to save this entry into the database:
			String entry = AjaxController.getInstance().getRequestParamByKeyNoDecrypt("orgname");
			User u = NavigationController.getInstance().getCRTFacesContext().getUser();
			if(u!=null) u.getUserSetting().setDisplayOwnEntryWarn(false);
			
			return new DBList().saveNewEntry(entry, loc);
		}
		else return new DBList().selectListItemById(id);
	}
	
}
