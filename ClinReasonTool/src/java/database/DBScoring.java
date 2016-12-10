package database;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import beans.scoring.FeedbackBean;
import beans.scoring.LearningBean;
import beans.scoring.PeerBean;
import beans.scoring.ScoreBean;

public class DBScoring extends DBClinReason {

    public List<ScoreBean> selectScoreBeansByPatIllScriptId(long patIllScriptId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ScoreBean.class,"ScoreBean");
    	criteria.add(Restrictions.eq("patIllScriptId", new Long(patIllScriptId)));
    	criteria.addOrder(Order.asc("stage"));

    	List<ScoreBean> l = criteria.list();
    	return l;
    }
    
    /**
     * Gets the ScroeBean objects for the given scriptIds and deletFlag=0
     * @param scriptIds
     * @return
     */
    public List<ScoreBean> selectScoreBeansByPatIllScriptIds(List<Long> scriptIds){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ScoreBean.class,"ScoreBean");
    	criteria.add(Restrictions.in("patIllScriptId", scriptIds));
    	criteria.add(Restrictions.eq("deleteFlag", new Boolean(false)));
    	List<ScoreBean> l = criteria.list();
    	return l;
    }
    
    public List<ScoreBean> selectScoreBeansByUserId(long userId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ScoreBean.class,"ScoreBean");
    	criteria.add(Restrictions.eq("userId", new Long(userId)));
    	criteria.add(Restrictions.eq("deleteFlag", new Boolean(false)));
    	List<ScoreBean> l = criteria.list();
    	return l;
    }
    
    public LearningBean selectLearningBeanByScriptId(long scriptId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(LearningBean.class,"LearningBean");
    	criteria.add(Restrictions.eq("patIllScriptId", new Long(scriptId)));
    	return (LearningBean) criteria.uniqueResult();
    }
    
    
    public Map<Integer, List<FeedbackBean>> selectFeedbackBeansByPatIllScriptId(long patIllScriptId){
    	if(patIllScriptId<0) return null;
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(FeedbackBean.class,"FeedbackBean");
    	criteria.add(Restrictions.eq("patIllScriptId", new Long(patIllScriptId)));
    	List<FeedbackBean> l = criteria.list();
    	if(l==null || l.isEmpty()) return null;
    	Map<Integer, List<FeedbackBean>> feedbackBeans = new HashMap<Integer, List<FeedbackBean>>();
    	for(int i=0; i<l.size(); i++){
    		FeedbackBean fb = l.get(i);
    		List<FeedbackBean> stageList = new ArrayList<FeedbackBean>();
    		if(feedbackBeans.get(new Integer(fb.getStage()))!=null){
    			stageList = feedbackBeans.get(new Integer(fb.getStage()));
    		}
    		stageList.add(fb);
    		feedbackBeans.put(new Integer(fb.getStage()), stageList);   		
    	}
    	return feedbackBeans;
    }

   
    
    /**
     * We get ALL PeerBean objects for all scripts and put them into a map with the parentId as key. 
     * @return
     */
    public Map<String, List<PeerBean>> selectAllPeerBeans(){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(PeerBean.class,"PeerBean");
    	List<PeerBean> list =  criteria.list();
    	if(list==null || list.isEmpty()) return null;
    	Map<String, List<PeerBean>> m = new HashMap<String, List<PeerBean>>();
    	for(int i=0; i<list.size(); i++){
    		PeerBean pb = list.get(i);
    		List<PeerBean> beans = m.get(pb.getVpId()); 
    		if(beans==null) beans = new ArrayList<PeerBean>();
    		beans.add(pb);
    		m.put(pb.getVpId(), beans);
    	}
    	return m;
    }
    
}
