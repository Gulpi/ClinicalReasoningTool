package controller;

import java.util.Iterator;

import actions.beanActions.AddAction;
import beans.relation.Rectangle;
import beans.relation.Relation;
import database.DBClinReason;
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
	
	public String getRelationToJson(Relation rel){
		StringBuffer sb = new StringBuffer();
		if(rel.getSynId()<=0)
			sb.append("{\"label\":\""+rel.getListItem().getName()+"\",\"shortlabel\":\""+rel.getListItem().getShortName()+"\",\"id\": \""+rel.getIdWithPrefix()+"\",\"x\": "+((Rectangle)rel).getX()+",\"y\":"+((Rectangle)rel).getY()+"}");		
		else{
			sb.append("{\"label\":\""+rel.getSynonym().getName()+"\",\"shortlabel\":\""+rel.getSynonym().getShortName()+"\",\"id\": \""+rel.getIdWithPrefix()+"\",\"x\": "+((Rectangle)rel).getX()+",\"y\":"+((Rectangle)rel).getY()+"}");		

		}
		return sb.toString();
	}
	
	public void initAdd(String idStr, String name, String xStr, String yStr, AddAction aa){
		long id;
		int type = AddAction.ADD_TYPE_MAINITEM;
		if(idStr.startsWith(Synonym.SYN_VERTEXID_PREFIX)){
			type = AddAction.ADD_TYPE_SYNITEM;
			id = Long.valueOf(idStr.substring(Synonym.SYN_VERTEXID_PREFIX.length()));
		}
		else id = Long.valueOf(idStr.trim());
		float x = Float.valueOf(xStr.trim());
		float y = Float.valueOf(yStr.trim());
		
		if(type==AddAction.ADD_TYPE_MAINITEM) aa.addRelation(id, name, (int)x, (int)y, -1);
		else{
			//we have to find the parent id of the synonym.
			Synonym syn = new DBClinReason().selectSynonymById(id);
			aa.addRelation(syn.getListItemId(), name, (int)x, (int)y, id); //then we add a synonym
		}
	}
}
