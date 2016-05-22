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
	public List<Relation> getProblems(){
		List<MultiVertex> l = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_PROBLEM, currStage);
		if(l==null || l.isEmpty()) return null;
		
		List<Relation> expRels = new ArrayList<Relation>();
		Iterator<MultiVertex> it = l.iterator();
		while(it.hasNext()){
			expRels.add(it.next().getExpertVertex());
		}
		return expRels;			
	}
	
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
}
