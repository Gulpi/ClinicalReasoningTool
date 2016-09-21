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

import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import controller.ConceptMapController;
import controller.GraphController;
import controller.NavigationController;
import controller.RelationController;
import controller.ScoringController;
import model.ListItem;
import model.Synonym;
import properties.IntlConfiguration;
/**
 * connects a Diagnosis object to a (Patient)IllnessScript object with some attributes.
 * @author ingahege
 */
public class RelationDiagnosis extends Relation implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int REL_TYPE_FINAL = 1; //final diagnosis (for PIS only)
	public static final int REL_TYPE_DDX = 2; //differential diagnosis 
	public static final int REL_TYPE_COMPL = 3; //complication of IS diagnosis 
	public static final int REL_TYPE_RELATED = 4; //otherwise related diagnosis 
	public static final int DEFAULT_X = 5; //165; //default x position of problems in canvas
	//public static final String COLOR_DEFAULT = "#ffffff";
	//public static final String COLOR_RED = "#990000";
	public static final int TIER_NONE = 0; //I do not now
	public static final int TIER_NOTLIKELY = 3; //Clinically low likelihood
	public static final int TIER_LIKELY = 2; //Clinically moderate likelihood
	public static final int TIER_MOSTLIKELY = 1; //Clinically high likelihood
	public static final int TIER_FINAL = 4; //Final diagnosis
	public static final int TIER_RULEDOUT = 5;
	public static final int TIER_WORKINGDDX = 6;
	private static final String COLOR_MNM = "#FF0000";
	private static final String COLOR_RULEDOUT =  "1";//"#cccccc";
	private static final String COLOR_DEFAULT = "2";//"#ffffff";
	private static final String COLOR_WORKINGDDX = "3";//"#cce6ff";
	private static final String COLOR_FINAL = "4";//"#80bfff";
	private static final String COLOR_BGDEFAULT = "5";

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
	private int finalDiagnosis = -1;

	private ListItem diagnosis;
	/**
	 * Must-not-miss
	 */
	private int mnm = 0;
	
	/**
	 * stage at which a diagnosis has been ruled out, -1/0: not ruled out
	 */
	private int ruledOut;
	
	/**
	 * Current working diagnosis (most likely one at current stage)
	 */
	private int workingDDX;

	
	public RelationDiagnosis(){}
	public RelationDiagnosis(long lisItemId, long destId, long synId){
		this.setListItemId(lisItemId);
		this.setDestId(destId);
		if(synId>0) setSynId(synId);
	}

	public int getTier() {return tier;}
	
	/**
	 * We only display the expert's final diagnosis after the user has submitted a diagnosis.... 
	 * @return
	 */
	public int getExptier(){
		if(tier!=TIER_FINAL) return tier; //everything except finals are displayed... 
		PatientIllnessScript learnerscript = new NavigationController().getCRTFacesContext().getPatillscript();
		if(learnerscript.getSubmitted()) return TIER_FINAL;
		return 0;
	}
	
	public void setTier(int tier) {this.tier = tier;}
	public ListItem getDiagnosis() {return diagnosis;}
	public void setDiagnosis(ListItem diagnosis) {this.diagnosis = diagnosis;}	
	public String getShortLabelOrSynShortLabel(){return StringUtils.abbreviate(getLabelOrSynLabel(), ListItem.MAXLENGTH_NAME-2);}

	//public int getSubmittedStage() {return submittedStage;}
	//public void setSubmittedStage(int submittedStage) {this.submittedStage = submittedStage;}
	
	//public int getRuledOut() {return ruledOut;}
	public boolean isRuledOutBool(){
		if(ruledOut>0) return true;
		return false;
	}
	
	public int getRuledOut(){return ruledOut;}
	public void setRuledOut(int ruledOut) {this.ruledOut = ruledOut;}
	public void setRuledOutAtCurrStage() {
		PatientIllnessScript patillscript = NavigationController.getInstance().getPatientIllnessScript();
		if(patillscript!=null){
			this.ruledOut = patillscript.getStageForAction();
			/*if(patillscript.isExpScript())
				this.ruledOut = patillscript.getStage();
			else this.ruledOut = patillscript.getCurrentStage();*/
		}
	}
	
	
	
	public int getFinalDiagnosis() {return finalDiagnosis;}
	public void setFinalDiagnosis(int stage) {
		this.finalDiagnosis = stage;
		if(stage>0) tier = TIER_FINAL; //backward compatibility....
	}
	/*public void setFinalDiagnosis(){
		tier = TIER_FINAL;
	}*/
	public boolean isFinalDDX(){
		if(tier==TIER_FINAL || finalDiagnosis>0) return true;
		return false;
	}	
	private void setWorkingDDXAtCurrStage(){
		PatientIllnessScript patillscript = NavigationController.getInstance().getPatientIllnessScript();
		if(patillscript!=null){
			//if(patillscript.isExpScript())
				this.workingDDX = patillscript.getStageForAction();
			//else this.workingDDX = patillscript.getCurrentStage();
		}
		
	}
	
	public void toggleRuledOut(){
		if(ruledOut>0) ruledOut = -1;
		else setRuledOutAtCurrStage();
	}
	
	public void toggleWorkingDDX(){
		if(workingDDX>0) workingDDX = -1;
		else setWorkingDDXAtCurrStage();
	}
	
	public String getIdWithPrefix(){ return GraphController.PREFIX_DDX+this.getId();}
	public int getMnm() {
		return mnm;}
	public void setMnm(int mnm) {this.mnm = mnm;}
	public boolean isMnM(){
		if(mnm==1) return true;
		return false;
	}


		
	public boolean isWorkingDDXBool() {
		if(workingDDX>0) return true;
		return false;
	}
	
	public int getWorkingDDX(){return workingDDX;}
	
	public void setWorkingDDX(int workingDDX) {this.workingDDX = workingDDX;}
	
	public String getColor(){		
		boolean expEdit = NavigationController.getInstance().isExpEdit();
		if(expEdit){ 
			PatientIllnessScript learnerscript = new NavigationController().getCRTFacesContext().getPatillscript();
			if(isRuledOutBool() && ruledOut<=learnerscript.getStage()) return COLOR_RULEDOUT;
			return COLOR_DEFAULT;//"#000000";
		}
		else{
			if(isRuledOutBool()) return COLOR_RULEDOUT;
			return COLOR_DEFAULT;//"#000000";//COLOR_DEFAULT;
		}
	}
	
	
	/**
	 * color of the expert boxes for the learner feedback. 
	 * if a ddx has been ruled out by an expert it is displayed as ruled out from the stage on where it has been ruled out.  
	 * @return
	 */
	/*public String getExpColor(){	
		PatientIllnessScript learnerscript = new NavigationController().getCRTFacesContext().getPatillscript();
		if(isRuledOutBool() && ruledOut<=learnerscript.getCurrentStage()) return COLOR_RULEDOUT;
		return COLOR_DEFAULT; //"#000000";
	}*/
	
	/**
	 * Background color of boxes for learner boxes and expert edit boxes. 
	 * For expert edit we consider the actual stage (not may stage!), to make it easier to display the changes of 
	 * the script during the scenario.
	 * @return
	 */
	/*public String getBackgroundColor(){		
		boolean expEdit = NavigationController.getInstance().isExpEdit();
		//if the current script is created as an expert script we display the colors only from the stage on in which they have been assigned.
		if(expEdit){ 
			PatientIllnessScript learnerscript = new NavigationController().getCRTFacesContext().getPatillscript();
			if(isFinalDDX() && learnerscript.getSubmittedStage()<=learnerscript.getStage()) return COLOR_FINAL;
			if(workingDDX<=0) return COLOR_BGDEFAULT;

			if(workingDDX<=learnerscript.getStage()) return COLOR_WORKINGDDX;
			return COLOR_BGDEFAULT;
		}
		else{
			if(isFinalDDX()) return COLOR_FINAL;
			if(workingDDX>0) return COLOR_WORKINGDDX;			
			return COLOR_BGDEFAULT;
		}
	}*/

	/**
	 * background color of the expert boxes for the learner feedback. 
	 * if a ddx has been defined as working ddx it is displayed as such from the (max) stage on where it has been made. 
	 *  final diagnoses are only displayed after user has passed the maxDDXSubmission stage (after that he must have
	 *  made a final diagnosis). 
	 * @return
	 */
	/*public String getExpBackgroundColor(){		
		//if the current script is created as an expert script we display the colors only from the stage on in which they have been assigned.
		PatientIllnessScript learnerscript = new NavigationController().getCRTFacesContext().getPatillscript();
		//if(isFinalDiagnosis() && learnerscript.getSubmittedStage()<=learnerscript.getStage()) return COLOR_FINAL;
		
		if(isFinalDDX() && learnerscript.getCurrentStage()>learnerscript.getMaxSubmittedStage()) return COLOR_FINAL;
		if(workingDDX<=0) return COLOR_DEFAULT;
		if(workingDDX<=learnerscript.getCurrentStage()) return COLOR_WORKINGDDX;
		return COLOR_DEFAULT;			
	}*/
	
	public int getExpCssClass(){		
		//if the current script is created as an expert script we display the colors only from the stage on in which they have been assigned.
		PatientIllnessScript learnerscript = NavigationController.getInstance().getCRTFacesContext().getPatillscript();
		Graph g = NavigationController.getInstance().getCRTFacesContext().getGraph();
		MultiVertex mv = g.getVertexByIdAndType(this.getListItemId(), Relation.TYPE_DDX);
		if(mv==null || mv.getExpertVertex()==null) return -1;
		RelationDiagnosis expRel = (RelationDiagnosis) mv.getExpertVertex();
		//if(isFinalDiagnosis() && learnerscript.getSubmittedStage()<=learnerscript.getStage()) return COLOR_FINAL;
		
		if(expRel.isFinalDDX() && (learnerscript.getCurrentStage()>learnerscript.getMaxSubmittedStage() || learnerscript.getSubmitted())) return TIER_FINAL;
		if(isRuledOutBool() && ruledOut<=learnerscript.getCurrentStage()) return TIER_RULEDOUT;
		if(workingDDX<=learnerscript.getCurrentStage()) return TIER_WORKINGDDX;
		return -1;
		
	}
	
	public int getCssClass(){
		boolean expEdit = NavigationController.getInstance().isExpEdit();
		if(expEdit){
			
		}
		else{
			if(isFinalDDX()) return TIER_FINAL;
			if(isRuledOutBool()) return TIER_RULEDOUT;
			if(isWorkingDDXBool()) return TIER_WORKINGDDX;
		}
		return TIER_NONE;
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
	
	/**
	 * return the score if this ddx has been selected as a final diagnosis.  
	 * @return
	 */
	public String getFinalDDXScore(){ 
		return new ScoringController().getIconForFinalDDXScore(this.getListItemId());
	}
	
	/**
	 * return the score if this ddx has been selected as a final diagnosis.  
	 * @return
	 */
	public String getExp(){ 
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		MultiVertex mvertex = g.getVertexByIdAndType(this.getListItemId(), Relation.TYPE_DDX);
		if(mvertex==null || mvertex.getLearnerVertex()==null) return ""; //should not happen
		//only return something if learner has chosen it as a final ddx:
		boolean isLearnerFinal = ((RelationDiagnosis)mvertex.getLearnerVertex()).getTier() == RelationDiagnosis.TIER_FINAL;
		if(isLearnerFinal && mvertex.getExpertVertex()==null) 
			return IntlConfiguration.getValue("ddx.nodiff");
		try{
			RelationDiagnosis expRel = (RelationDiagnosis) mvertex.getExpertVertex();
			//if(expRel==null) return IntlConfiguration.getValue("ddx.tierdescr.noexp"); //expert does not have item
			if(isLearnerFinal) return IntlConfiguration.getValue("ddx.tierdescr."+expRel.getTier()); //String.valueOf(expRel.getTier());
		}
		catch(Exception e){return "";} //can happen if expert has this item not as a diagnosis but as a something else....	
		return "";
	}
	
	/**
	 * is currently an expert script edited? If so we display a link to mark a diagnosis as final.
	 * @return
	 */
	public boolean getIsExpEdit(){return NavigationController.getInstance().isExpEdit();}

}
