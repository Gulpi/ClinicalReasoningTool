package beans;

import java.util.*;

import javax.faces.bean.SessionScoped;

import beans.graph.*;
import beans.relation.Relation;

/**
 * A view object (no database storage) for displaying the expert's script. 
 * underlying structure for getter is the graph
 * @author ingahege
 *
 */
@SessionScoped
public class ExpPatientIllnessScript {

	private Graph g;
	private int currStage;
	
	public ExpPatientIllnessScript(Graph g, int stage){
		this.g = g;
		this.currStage = stage;		
	}
	
	/**
	 * get all findings/problems of the expert that have NOT been selected by the learner
	 * @return list of Relations or null
	 */
	public List<Relation> getProblems(){ return getList(Relation.TYPE_PROBLEM);	}

	/**
	 * get all diagnoses of the expert that have NOT been selected by the learner
	 * We might need this separately because of additional parameters in the RelationDiagnosis object
	 * @return list of Relations or null
	 */
	public List<Relation> getDiagnoses(){
		List<MultiVertex> l = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_DDX, currStage);
		if(l==null || l.isEmpty()) return null;
		
		List<Relation> expRels = new ArrayList<Relation>();
		Iterator<MultiVertex> it = l.iterator();
		while(it.hasNext()){
			expRels.add(it.next().getExpertVertex());
		}
		return expRels;			
	}

	/**
	 * get all test items of the expert that have NOT been selected by the learner
	 * @return list of Relations or null
	 */
	public List<Relation> getTests(){ return getList(Relation.TYPE_TEST);		}
	
	/**
	 * get all management items of the expert that have NOT been selected by the learner
	 * @return list of Relations or null
	 */
	public List<Relation> getMngs(){ return getList(Relation.TYPE_MNG);}
	
	/**
	 * get all items of given type that have not been selected by the learner, but the expert
	 * @param type
	 * @return
	 */
	private List<Relation> getList(int type){
		List<MultiVertex> l = g.getVerticesByTypeAndStageExpOnly(type, currStage);
		if(l==null || l.isEmpty()) return null;
		
		List<Relation> expRels = new ArrayList<Relation>();
		Iterator<MultiVertex> it = l.iterator();
		while(it.hasNext()){
			expRels.add(it.next().getExpertVertex());
		}
		return expRels;			
	}
}
