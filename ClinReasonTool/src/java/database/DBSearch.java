package database;

import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import beans.search.SearchResult;

public class DBSearch extends DBClinReason{
	

	public List<SearchResult> selectScriptsForSearchTerm(String searchTerm){
		Session s = instance.getInternalSession(Thread.currentThread(), false);
      	Criteria criteria = s.createCriteria(SearchResult.class,"SearchResult");
    	criteria.add(Restrictions.ilike("name", searchTerm.toLowerCase(), MatchMode.ANYWHERE));
    	return criteria.list();
	}
	
	

}
