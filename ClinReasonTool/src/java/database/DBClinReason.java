package database;

import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;

import beans.scripts.*;
import beans.relation.RelationProblem;
import beans.relation.SummaryStatement;
import controller.IllnessScriptController;
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
        	    s.evict(o);
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
    	    s.evict(o);
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
     * Beware: summStatement not loaded!
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectPatIllScriptsByUserId(long userId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	criteria.addOrder(Order.desc("lastAccessDate"));
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
    
    /**
     * Selects all PatientIllnessScripts that can be included into the peer responses. 
     * Only scripts that have not yet been added (peerSync=0), scripts that have submitted a diagnosis (submittedStage>0),
     * and learner scripts (not expert ones) are considered and selected.
     * @return PatientIllnessScript or null
     */
    public List<PatientIllnessScript> selectLearnerPatIllScriptsByPeerSync(){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	criteria.add(Restrictions.eq("peerSync", new Boolean(false)));
    	criteria.add(Restrictions.gt("submittedStage", 0));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_LEARNER_CREATED)));
    	return criteria.list();
    }
    
    /**
     * Select the PatientIllnessScripts for the userId and parentId from the database. 
     * @param sessionId
     * @return PatientIllnessScript or null
     */
    /*public List<PatientIllnessScript> selectLearnerPatIllScriptsByDate(){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PatientIllnessScript.class,"PatientIllnessScript");
    	//criteria.add(Restrictions.eq("peerSync", new Boolean(false)));
    	criteria.add(Restrictions.eq("type", new Integer(PatientIllnessScript.TYPE_LEARNER_CREATED)));
    	//TODO for peer calculation, we have to add a date range otherwise this is too much....
    	return criteria.list();
    }*/
    
	protected SummaryStatement loadSummSt(long id){
		if(id<=0) return null;
		Session s = instance.getInternalSession(Thread.currentThread(), false);
		Criteria criteria = s.createCriteria(SummaryStatement.class,"SummaryStatement");
		criteria.add(Restrictions.eq("id", new Long(id)));
		return (SummaryStatement) criteria.uniqueResult();
	}
}
