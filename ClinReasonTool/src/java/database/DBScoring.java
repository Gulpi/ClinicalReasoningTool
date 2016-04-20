package database;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import beans.scoring.FeedbackBean;
import beans.scoring.ScoreBean;

public class DBScoring extends DBClinReason {

    public Map<Long, ScoreBean> selectScoreBeansByPatIllScriptId(long patIllScriptId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(ScoreBean.class,"ScoreBean");
    	criteria.add(Restrictions.eq("patIllScriptId", new Long(patIllScriptId)));
    	List<ScoreBean> l = criteria.list();
    	if(l==null || l.isEmpty()) return null;
    	Map<Long, ScoreBean> scores = new HashMap<Long, ScoreBean>();
    	for(int i=0; i<l.size(); i++){
    		scores.put(l.get(i).getScoredItem(), l.get(i));
    	}
    	return scores;
    }
    
    public Map<Integer, List<FeedbackBean>> selectFeedbackBeansByPatIllScriptId(long patIllScriptId){
    	Session s = instance.getInternalSession(Thread.currentThread(), false);
    	Criteria criteria = s.createCriteria(FeedbackBean.class,"FeedbackBean");
    	criteria.add(Restrictions.eq("patIllScriptId", new Long(patIllScriptId)));
    	List<FeedbackBean> l = criteria.list();
    	if(l==null || l.isEmpty()) return null;
    	Map<Integer, List<FeedbackBean>> feedbackBeans = new HashMap<Integer, List<FeedbackBean>>();
    	for(int i=0; i<l.size(); i++){
    		FeedbackBean fb = l.get(i);
    		List<FeedbackBean> stageList = new ArrayList();
    		if(feedbackBeans.get(new Integer(fb.getStage()))!=null){
    			stageList = feedbackBeans.get(new Integer(fb.getStage()));
    		}
    		stageList.add(fb);
    		feedbackBeans.put(new Integer(fb.getStage()), stageList);
    		
    			//feedbackBeans.put(new Integer(fb.getStage()), l.get(i));
    	}
    	return feedbackBeans;
    }
    
}
