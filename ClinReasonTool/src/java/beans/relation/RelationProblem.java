package beans.relation;

import java.io.Serializable;
import java.util.*;

import controller.GraphController;
import model.ListItem;
import model.Synonym;

/**
 * Relation between an (Patient-)IllnessScript and a problem. We need this to specify a problem, e.g. whether it is 
 * almost proving a diagnosis or rarely occurs with a diagnosis.
 * We might need more qualifiers,...
 * @author ingahege
 *
 */
public class RelationProblem extends Relation implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final int QUALIFIER_RARE = 0; 
	public static final int QUALIFIER_MEDIUM = 1;
	public static final int QUALIFIER_OFTEN = 2;
	public static final int DEFAULT_X = 15; //80; //default x position of problems in canvas
	public static final int FIND_PROTOTYPICAL = 1;
	public static final int FIND_NONPROTOTYPICAL = 2;
	
	/**
	 * problems: key-finding, other,... (?)
	 */
	private int value; //key finding,...
	
	/**
	 * how often is a problem prevalent in a diagnosis (rare, medium, often)
	 */
	private int qualifier;
	
	//private Timestamp creationDate;
	
	private ListItem problem;
	
	/**
	 * Expert can add information about whether a problem or finding is (non-) prototypical for the final diagnoses
	 * of the patient. This information is considered when checking for Representativeness errors.
	 */
	private int prototypical = -1;
	/**
	 * If a user has selected a synonym instead of the main item, we store this here in addition to the main item.
	 */
	
	public RelationProblem(){}
	public RelationProblem(long listItemId, long destId, long synId){
		this.setListItemId(listItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}
	public ListItem getProblem() {return problem;}
	public ListItem getListItem() {return getProblem();}
	public void setProblem(ListItem problem) {this.problem = problem;}		
	
	public String getIdWithPrefix(){return GraphController.PREFIX_PROB+this.getId();}
	
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_PROBLEM;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return problem.getName();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){	
		String postStr = "";
		if(this.getIsSyndrome()==1) postStr = " (Syndrome)";
		if(getSynId()<=0) return problem.getName() + postStr;
		else return getSynonym().getName() + postStr;
	}
	
	public int getPrototypical() {return prototypical;}
	public void setPrototypical(int prototypical) {this.prototypical = prototypical;}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return problem.getSynonyma();}
	
}
