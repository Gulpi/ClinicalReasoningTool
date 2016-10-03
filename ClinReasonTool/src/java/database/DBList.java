package database;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import model.ListItem;
import model.Synonym;
import util.CRTLogger;

/**
 * Contains all access methods for the list loading (mesh, etc)
 * @author ingahege
 *
 */
public class DBList extends DBClinReason {
 
	/**
     * Select the ListItem with the given id from the database.
     * @param id
     * @return ListItem or null
     */
    public ListItem selectListItemById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("item_id", new Long(id)));
    	ListItem li = (ListItem) criteria.uniqueResult();
    	s.close();
    	return li;
    }
    

    
    /**
     * Select the ListItem with the given meshId from the database.
     * Just needed for importing German Mesh List.
     * @param id
     * @return ListItem or null
     */
    public ListItem selectListItemByMeshId(String id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("mesh_id", id));
    	ListItem li = (ListItem) criteria.uniqueResult();
    	s.close();
    	return li;
    }
    
    /**
     * Loads ListItems for the given types. CAVE: This returns lots of items, only call during init of application 
     * or for testing!
     * @param types
     * @return
     */
    public List<ListItem> selectListItemsByTypesAndLang(Locale loc, String[] types){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.in("itemType", types));
    	criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    	criteria.add(Restrictions.eq("language", loc));
    	criteria.addOrder(Order.asc("name"));
    	List l = criteria.list();
    	s.close();
    	return l;
    }
    
    public Synonym selectSynonymById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Synonym.class,"Synonym");
    	criteria.add(Restrictions.eq("id", new Long(id)));
    	Synonym li = (Synonym) criteria.uniqueResult();
    	s.close();
    	return li;
    }
    
    /**
     * We select (based on the code of the MESH item all parent and child items. (e.g. for cough the first parent item would be 
     * "respiratory disorders). 
     *  
     * @param li
     * @return list of ListItem objects or null
     */
    public List<ListItem> selectParentAndChildListItems(ListItem li){
    	long startms = System.currentTimeMillis();
    	if(li==null) return null;

    	String code = li.getFirstCode();
    	if(code==null || code.indexOf(".")<0) return null;
    	CRTLogger.out("Start selectParentAndChildListItems " + startms + "ms", CRTLogger.LEVEL_PROD);

    	List<ListItem> items = new ArrayList<ListItem>();
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	
    	//select & add childs:
		List<ListItem> childs = selectChildListItemsByCode(code, s, li.getLanguage());
		if(childs!=null && !childs.isEmpty()) items.addAll(childs);
    	
		//select and add all parents:
		while(code.indexOf(".")>0){
			code = code.substring(0, code.lastIndexOf("."));
			if(code==null || code.trim().equals("")) break;
			ListItem l = selectListItemByCode(code, s, li.getLanguage());
			if(l!=null) items.add(l);
		}

		s.close();
		CRTLogger.out("End selectParentAndChildListItems " + (System.currentTimeMillis() - startms) + "ms", CRTLogger.LEVEL_PROD);
		return items;		
    }
    
	/**
     * Select the ListItem with the given id from the database.
     * @param id
     * @return ListItem or null
     */
    private ListItem selectListItemByCode(String code, Session s, Locale lang){    	
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("firstCode", code));
    	criteria.add(Restrictions.eq("language", lang));
    	ListItem li = (ListItem) criteria.uniqueResult();
    	return li;
    }
    
    private List<ListItem> selectChildListItemsByCode(String code, Session s, Locale lang){
       	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.like("firstCode", code+".", MatchMode.START));
    	criteria.add(Restrictions.eq("language", lang));
    	return criteria.list();
    }
    
    public List<ListItem> selectListItemBySearchTerm(String searchTerm, Locale lang){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ListItem.class,"ListItem");
    	criteria.add(Restrictions.eq("language", lang));
    	criteria.add(Restrictions.eq("ignored", false));
    	criteria.add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE));
    	return criteria.list();
    }
     
}
