package database;

import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import beans.search.SearchResult;

public class DBSearch extends DBClinReason{
	
	/**
	 * @param itemIds
	 * @param searchTerm
	 * @param userId
	 * @return Map: key=parentId, value=List of SearchBean objects for this parentId
	 */
	/*public Map<Long, List<SearchBean>> selectSearchBeansForItemIds(List<Long> itemIds, String searchTerm, long userId){
		if (itemIds==null || itemIds.isEmpty()) return null;
		Session s = instance.getInternalSession(Thread.currentThread(), false);
		List l = new ArrayList();
		//List ddx = selectScriptsForItemIds(itemIds, "script.selByRelDDX", userId);
		//if(ddx!=null) l.addAll(ddx);
		List probs = selectScriptsForItemIds(itemIds, "script.selByRelProb", userId, s);
		if(probs!=null) l.addAll(probs);
		//List tests = selectScriptsForItemIds(itemIds, "script.selByRelTest", userId);
		//if(tests!=null) l.addAll(tests);
		//List mngs = selectScriptsForItemIds(itemIds, "script.selByRelMng", userId);
		//if(mngs!=null) l.addAll(mngs);
		
		if(l==null || l.isEmpty()) return null;
		Map<Long, List<SearchBean>> sbs = new HashMap<Long, List<SearchBean>>();
		for(int i=0; i<l.size(); i++){
			Object[] row = (Object[]) l.get(i);

			SearchBean sb = new SearchBean(searchTerm, -1, (String) row[1], ((Long) row[0]).longValue(), ((Integer) row[2]).intValue(), userId);
			List<SearchBean> list = new ArrayList<SearchBean>();
			if(sbs.get(new Long(sb.getParentId()))!=null){
				list = sbs.get(new Long(sb.getParentId()));			
			}
			list.add(sb);
			sbs.put(new Long(sb.getParentId()), list);
		}
		return sbs;
	}*/

	public List<SearchResult> selectScriptsForSearchTerm(String searchTerm){
		Session s = instance.getInternalSession(Thread.currentThread(), false);
      	Criteria criteria = s.createCriteria(SearchResult.class,"SearchResult");
    	criteria.add(Restrictions.ilike("name", searchTerm.toLowerCase()));
    	return criteria.list();
	}
	
	

}
