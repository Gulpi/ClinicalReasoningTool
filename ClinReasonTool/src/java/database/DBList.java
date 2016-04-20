package database;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import model.ListItem;
import model.Synonym;

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
}
