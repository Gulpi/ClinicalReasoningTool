package database;

import java.beans.Beans;
import java.util.*;

import javax.faces.context.FacesContext;
import javax.management.relation.Relation;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.*;

import beans.CRTFacesContext;
import beans.IllnessScript;
import beans.PatientIllnessScript;
import beans.SummaryStatement;
import beans.relation.RelationDiagnosis;
import beans.relation.RelationProblem;
import beans.scoring.ScoreBean;
import controller.IllnessScriptController;
import model.ListItem;
import model.Synonym;
import util.StringUtilities;

public class DBClinReason /*extends HibernateUtil*/{
	static HibernateSession instance = new HibernateSession();
	//static SessionFactory sessionFactory = new SessionFactory();//new Configuration().configure().buildSessionFactory();
	
    /**
     * saves an object into the database. Object has to have a hibernate mapping!
     * @param o
     */
    public void saveAndCommit(Object o){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	//PatientIllnessScript ps = (PatientIllnessScript) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(CRTFacesContext.PATILLSCRIPT_KEY);
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);     
            s.saveOrUpdate(o);
            instance.commitTransaction(s);
        }
        
        catch(Exception e){
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
        finally{
        	    s.flush();
        	    s.close();
        }
    }
        
    /**
     * Saves/updates a collection of objects into the database
     * @param o
     */
    public void saveAndCommit(Collection o) {
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
        try {
        	instance.beginTransaction();
            s.setFlushMode(FlushMode.COMMIT);   //commit will be done after insert!
            //tx = s.beginTransaction();
            Iterator it = o.iterator();
            while(it.hasNext())
            {
            	s.saveOrUpdate(it.next());
            }
            instance.commitTransaction(s);
        }
        catch (Exception e) {
        	System.out.println("DBClinReason.saveAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }
        finally{
    	    s.flush();
    	    s.close();
        }
    }
    /**
     * Delete a collection from the database
     * @param o
     */
    public void deleteAndCommit(Collection c){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);   	
        try {
        	instance.beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            Iterator it = c.iterator();
            while(it.hasNext())
            {
            	s.delete(it.next());
            }
            instance.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }   
        finally{
    	    s.flush();
    	    s.close();
        }
    }  
    
    /**
     * Delete a Bean from the database
     * @param o
     */
    public void deleteAndCommit(Object o){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);   		
        try {
        	instance.beginTransaction(s);
            s.setFlushMode(FlushMode.COMMIT);     
            s.delete(o);
            instance.commitTransaction(s);
        }        
        catch(Exception e){
        	System.out.println("DBClinReason.deleteAndCommit(), Exception: " + StringUtilities.stackTraceToString(e));
        	instance.rollBackTx();
        }  
        finally{
    	    s.flush();
    	    s.close();
        }
    }
    
    /**
     * Select the PatientIllnessScript for the parentId from the database. 
     * @param parentId
     * @return IllnessScript or null
     */
    public List<IllnessScript> selectIllScriptByParentId(long parentId){
    	return selectIllScripts(parentId, "parentId");
    }
    
    /**
     * Select the PatientIllnessScript for the parentId (e.g. vpId) from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<IllnessScript> selectIllScriptByDiagnosisId(long diagnosisId){
    	return selectIllScripts(diagnosisId, "diagnosisId");
    }
 
    /**
     * Select the IllnessScript for a given list of problems   
     * @param sessionId
     * @return PatientIllnessScript or null
     * TODO
     */
    public List<IllnessScript> selectIllScriptByProblems(List<RelationProblem> probs){
    	return null; //we need a matching algorithm here....
    }
    
    public List<IllnessScript> selectIllScriptByDiagnoses(List ddx){
    	if(ddx==null) return null; 
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(IllnessScript.class,"IllnessScript");
    	Long[] ids = new IllnessScriptController().getListItemsFromRelationList(ddx);
    	if(ids!=null){
    		criteria.add(Restrictions.in("diagnosisId", ids));
    		return criteria.list();
    	}
    	else return null;
    }
    /**
     * Select the IllnessScript
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    private List<IllnessScript> selectIllScripts(long id, String identifier){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(IllnessScript.class,"IllnessScript");
    	criteria.add(Restrictions.eq(identifier, new Long(identifier)));
    	List<IllnessScript> l =  (List<IllnessScript>) criteria.list();
    	s.close();
    	return l;
    }
    
    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptBySessionId(long sessionId){
    	return selectLearnerPatIllScript(sessionId, "sessionId");
    }
    
    /**
     * Select the PatientIllnessScript of the expert, which is identified by the parentId and type from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectExpertPatIllScript(long parentId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("parentId", new Long(parentId)));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_EXPERT_CREATED)));
    	PatientIllnessScript patIllScript =  (PatientIllnessScript) criteria.uniqueResult();
    	s.close();
    	if(patIllScript!=null)
    		patIllScript.setSummSt(loadSummSt(patIllScript.getSummStId()));
    	return patIllScript;  	
    }
   
    
    /**
     * Select the PatientIllnessScript for the sessionId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptById(long id){
    	return selectLearnerPatIllScript(id, "id");
    }
    
    private PatientIllnessScript selectLearnerPatIllScript(long id, String identifier){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq(identifier, new Long(id)));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_LEARNER_CREATED)));
    	PatientIllnessScript patIllScript =  (PatientIllnessScript) criteria.uniqueResult();
    	s.close();
    	if(patIllScript!=null)
    		patIllScript.setSummSt(loadSummSt(patIllScript.getSummStId()));
    	return patIllScript;
    }

    /**
     * Select the PatientIllnessScripts for the userId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectPatIllScriptsByUserId(long userId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	//CAVE summStatement not loaded!
    	return  criteria.list();
    }
    
    /**
     * Select the PatientIllnessScripts for the userId and parentId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public PatientIllnessScript selectPatIllScriptsByUserIdAndParentId(long userId, long parentId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	criteria.add(Restrictions.eq("parentId", new Long(parentId)));
    	PatientIllnessScript patIllScript =  (PatientIllnessScript) criteria.uniqueResult();
    	if(patIllScript!=null)
    		patIllScript.setSummSt(loadSummSt(patIllScript.getSummStId()));
    	return patIllScript;
    }
	private SummaryStatement loadSummSt(long id){
		if(id<=0) return null;
		Session s = instance.getInternalSession(Thread.currentThread(), false);
		Criteria criteria = s.createCriteria(SummaryStatement.class,"SummaryStatement");
		criteria.add(Restrictions.eq("id", new Long(id)));
		return (SummaryStatement) criteria.uniqueResult();
	}
    
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
    
    /*public List selectSynonyma(){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Synonym.class,"Synonym");
    	criteria.add(Restrictions.eq("id", new Long(1)));
    	List li = criteria.list();
    	s.close();
    	return li;
    }*/ 
    
    public Synonym selectSynonymById(long id){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(Synonym.class,"Synonym");
    	criteria.add(Restrictions.eq("id", new Long(id)));
    	Synonym li = (Synonym) criteria.uniqueResult();
    	s.close();
    	return li;
    }
    
    
    public Map<Long, ScoreBean> selectScoreBeansByPatIllScriptId(long patIllScriptId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ScoreBean.class,"ScoreBean");
    	criteria.add(Restrictions.eq("patIllnessScriptId", new Long(patIllScriptId)));
    	List<ScoreBean> l = criteria.list();
    	if(l==null || l.isEmpty()) return null;
    	Map<Long, ScoreBean> scores = new HashMap<Long, ScoreBean>();
    	for(int i=0; i<l.size(); i++){
    		scores.put(l.get(i).getScoredItem(), l.get(i));
    	}
    	return scores;
    }
    

}
