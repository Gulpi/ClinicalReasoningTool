package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import beans.search.SearchBean;
import database.DBList;
import database.DBSearch;
import model.ListItem;

/**
 * We can search for scripts of a user (learner or editor) based on a search term
 * The results can be used to directly filter the display or returned as an xml file for VP- or portfolio systems.
 * @author ingahege
 *
 */
public class SearchController {
	
	private static final int MINLENGTH_SEARCHTERM = 3;

	//TODO: we need a module id later on, to decide which scripts the user has access to...
	/**
	 * We have to load the json file for the given locale and scan it for the term.
	 * @param searchTerm
	 * @param userId
	 * @return
	 */
	public Map<Long, List<SearchBean>> doSearch(String searchTerm, long userId, Locale loc){
		if(StringUtils.isEmpty(searchTerm) || searchTerm.length()<=MINLENGTH_SEARCHTERM) return null; 
		/*String jsonFile = JsonCreator.getMeshJsonFileNameByLoc(loc);
		List<Long> matchingMeshIds = new ArrayList<Long>();
		String line;
		try{
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));
			 while ((line = br.readLine()) != null) {
				 if(StringUtils.containsIgnoreCase(line, searchTerm)){
					 long mechId = getMeshIdFromLine(line)
					 matchingMeshIds.add(getMeshIdFromLine(line));
				 }
			 }
			 br.close();
		}
		catch(Exception e){}*/
		List<ListItem> items = new DBList().selectListItemBySearchTerm(searchTerm, loc);
		//if(items==null || items.isEmpty()) return null; //nothing found that matches the searchterm..
		return getSearchBeansForItems(items, searchTerm, userId);
		//we might return if we have too many items found here....
		//if(matchingMeshIds==null || matchingMeshIds.isEmpty()) return null; //nothing found that matches the searchterm..
		
		//Map<Long, List<SearchBean>> searchHits = new HashMap<Long, List<SearchBean>>();

		//return searchHits;
	}
	
	private Map<Long, List<SearchBean>> getSearchBeansForItems(List<ListItem> items, String searchTerm, long userId){
		//TODO get from a view??? 
		if(items==null || items.isEmpty()) return null;
		List<Long> itemIds = new ArrayList<Long>();
		for(int i=0; i<itemIds.size(); i++){
			itemIds.add(items.get(i).getItem_id());
		}
		DBSearch dbs = new DBSearch();
		return dbs.selectSearchBeansForItemIds(itemIds, searchTerm, userId);
		
	}
	
}
