package beans.relation;
import java.awt.Point;
import java.beans.Beans;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

import org.apache.commons.lang3.StringUtils;

import controller.ConceptMapController;
import controller.GraphController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;
/**
 * connects a Diagnosis object to a (Patient)IllnessScript object with some attributes.
 * @author ingahege
 */
public class RelationDiagnosis extends Relation implements Rectangle, Serializable {

	private static final long serialVersionUID = 1L;
	public static final int REL_TYPE_FINAL = 1; //final diagnosis (for PIS only)
	public static final int REL_TYPE_DDX = 2; //differential diagnosis 
	public static final int REL_TYPE_COMPL = 3; //complication of IS diagnosis 
	public static final int REL_TYPE_RELATED = 4; //otherwise related diagnosis 
	public static final int DEFAULT_X = 80; //165; //default x position of problems in canvas
	//public static final String COLOR_DEFAULT = "#ffffff";
	//public static final String COLOR_RED = "#990000";
	public static final int TIER_NONE = 0; //I do not now
	public static final int TIER_NOTLIKELY = 3; //Clinically low likelihood
	public static final int TIER_LIKELY = 2; //Clinically moderate likelihood
	public static final int TIER_MOSTLIKELY = 1; //Clinically high likelihood
	public static final int TIER_FINAL = 4; //Final diagnosis

	/**
	 * has this diagnosis been submitted as final the learner? If yes for certain components no more changes 
	 * can be made (?).
	 */
	//private int submittedStage;
		
	/**
	 * -1 = not stated, todo: define levels here (slider with Percentage?)
	 */
	private int confidence = -1; //we need levels here (only for PIS)

	/**
	 * how likely is this diagnosis, from unlikely to final diagnosis
	 * see tier definitions above
	 */
	private int tier = -1; 

	private ListItem diagnosis;
	/**
	 * Must-not-miss
	 */
	private int mnm = 0;

	
	public RelationDiagnosis(){}
	public RelationDiagnosis(long lisItemId, long destId, long synId){
		this.setListItemId(lisItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}

	public int getTier() {return tier;}
	public void setTier(int tier) {this.tier = tier;}
	public ListItem getDiagnosis() {return diagnosis;}
	public void setDiagnosis(ListItem diagnosis) {this.diagnosis = diagnosis;}		
	//public int getSubmittedStage() {return submittedStage;}
	//public void setSubmittedStage(int submittedStage) {this.submittedStage = submittedStage;}
	
	public String getIdWithPrefix(){ return GraphController.PREFIX_DDX+this.getId();}
	public int getMnm() {return mnm;}
	public void setMnm(int mnm) {this.mnm = mnm;}
	public boolean isMnM(){
		if(mnm==1) return true;
		return false;
	}




	/* (non-Javadoc)
	 * @see beans.relation.Relation#getRelationType()
	 */
	public int getRelationType() {return TYPE_DDX;}	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabel()
	 */
	public String getLabel(){return diagnosis.getName();}
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getListItem()
	 */
	public ListItem getListItem() {return diagnosis;}

	/* (non-Javadoc)
	 * @see beans.relation.Relation#getSynonyma()
	 */
	public Set<Synonym> getSynonyma(){ return diagnosis.getSynonyma();}
	
	/* (non-Javadoc)
	 * @see beans.relation.Relation#getLabelOrSynLabel()
	 */
	public String getLabelOrSynLabel(){		
		if(getSynId()<=0) return diagnosis.getName();
		else return getSynonym().getName();
	}
}
