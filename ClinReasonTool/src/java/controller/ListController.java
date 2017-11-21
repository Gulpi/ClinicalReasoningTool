package controller;

import java.util.*;

import beans.list.*;
import database.DBList;

/**
 * handles the searching, editing, and adding of list entries from the admin interface
 * CAVE: Deleting is not possible
 * @author ingahege
 *
 */
public class ListController {
	private static final int MAX_RESULTS = 100;
	/**
	 * We store the number of results we retrieved from the search
	 */
	private int resultCount = 0;
	
	/**
	 * Select matching items from list depending on mode (see definitions in ListItem). If we find more than 
	 * MAX_RESULTS we return null, but resultCount is set to indicate that. 
	 * @param lang
	 * @param searchterm
	 * @param mode
	 * @return
	 */
	public List<ListInterface> getListItems(String lang, String searchterm, String mode){
		if (searchterm==null || searchterm.trim().equals("")) return null;
		//Do a count first to see whether selecting makes sense!
		List<ListInterface> results = new ArrayList<ListInterface>();
		//we excpet too many results, so we return:
		if(searchterm==null || searchterm.trim().equals("") || searchterm.length()<=3) return null;
		
		DBList dbl = new DBList();
		if(mode==null || mode.equals(ListItem.TYPE_OWN) || mode.equals(""))
			results = dbl.selectPrivateListItemsByLangAndTerm(lang, searchterm);
		else if(mode ==null || !mode.equals(ListItem.TYPE_OWN)){ //public list, we select main entries and synonyms
			List<ListInterface> items = dbl.selectPublicListItemsByLangAndTerm(lang, searchterm);
			List<ListInterface> syns = new DBList().selectSynonymsByLangAndTerm(lang, searchterm);
			if(items.size() > MAX_RESULTS || syns.size() > MAX_RESULTS){
				return null;
			}

			results = jointListItemsAndSynonyms(items, syns);
		}
		if(results!=null) resultCount = results.size();
		if(results==null || results.size()>MAX_RESULTS) return null;
		
		
		return results;
	}
	
	/**
	 * We have to check whether we have terms in the synonyms list that are not yet in the items list (as synonym
	 * of an item). If we have new synonyms we add them to the items list.
	 * @param items
	 * @param syns
	 * @return
	 */
	private List<ListInterface> jointListItemsAndSynonyms(List<ListInterface> items, List<ListInterface> syns){
		if(syns==null || syns.isEmpty()) return items;
		if(items==null || items.isEmpty()) return syns;
		List<ListInterface> joinedList = items;
		for(int i=0; i<syns.size();i++){
			boolean found = false;
			ListInterface syn = syns.get(i);
			for(int j=0;j<items.size();j++){
				ListItem item = (ListItem) items.get(j);
				if(item.getSynonyma()!=null && !item.getSynonyma().isEmpty()){
					Iterator it = item.getSynonyma().iterator();
					while(it.hasNext()){
						ListInterface syn2 = (ListInterface) it.next();
						if(syn.equals(syn2)){
							found = true;
							break;
						}
					}
					if(found) break;
				}
			}
			if(!found) joinedList.add(syn);
		}
		
		return joinedList;
	}
}
