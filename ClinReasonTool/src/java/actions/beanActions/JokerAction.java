package actions.beanActions;

import java.util.*;

import javax.faces.application.FacesMessage;

import application.ErrorMessageContainer;
import beans.scripts.*;
import beans.graph.Graph;
import beans.graph.MultiVertex;
import beans.relation.Relation;
import controller.NavigationController;

/**
 * All kinds of jokers we offer the learner, such as adding a finding, test,....
 * @author ingahege
 *
 */
public class JokerAction {

	private PatientIllnessScript patIllScript;

	public JokerAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	
	public void addJoker(String type){
		if(type==null || type.equals("")) return;
		if(type.equals("1")) addFdgJoker();
		else if(type.equals("2")) addDDXJoker();
		else if(type.equals("3")) addTestJoker();
		else if(type.equals("4")) addMngJoker();
	}
	/**
	 * Add a finding the expert has added 
	 */
	private void addFdgJoker(){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> expsVertices = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_PROBLEM, patIllScript.getCurrentStage());
		MultiVertex jokerVertex = chooseItem(expsVertices);
		if(jokerVertex==null){ //no more fdgs to be added at this stage
			new ErrorMessageContainer().addErrorMessage("probform", "Expert added no additional findings a this stage", "", FacesMessage.SEVERITY_ERROR);
			return;
		}
		new AddProblemAction(patIllScript).addRelation(jokerVertex.getExpertVertex().getListItemId(), jokerVertex.getExpertVertex().getPrefixStr(), -1, -1, -1, true);
	}
	
	/**
	 * Add a differential the expert has added 
	 */
	private void addDDXJoker(){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> expsVertices = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_DDX, patIllScript.getCurrentStage());
		MultiVertex jokerVertex = chooseItem(expsVertices);
		if(jokerVertex==null){ //no more fdgs to be added at this stage
			new ErrorMessageContainer().addErrorMessage("ddxform", "Expert added no additional differentials a this stage", "", FacesMessage.SEVERITY_ERROR);
			return;
		}
		new AddDiagnosisAction(patIllScript).addRelation(jokerVertex.getExpertVertex().getListItemId(), jokerVertex.getExpertVertex().getPrefixStr(), -1, -1, -1, true);
	}
	
	/**
	 * Add a test the expert has added 
	 */
	private void addTestJoker(){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> expsVertices = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_TEST, patIllScript.getCurrentStage());
		MultiVertex jokerVertex = chooseItem(expsVertices);
		if(jokerVertex==null){ //no more fdgs to be added at this stage
			new ErrorMessageContainer().addErrorMessage("testform", "Expert added no additional tests a this stage", "", FacesMessage.SEVERITY_ERROR);
			return;
		}
		new AddTestAction(patIllScript).addRelation(jokerVertex.getExpertVertex().getListItemId(), jokerVertex.getExpertVertex().getPrefixStr(), -1, -1, -1, true);
	}
	
	/**
	 * Add a management option the expert has added 
	 */
	private void addMngJoker(){
		Graph g = new NavigationController().getCRTFacesContext().getGraph();
		List<MultiVertex> expsVertices = g.getVerticesByTypeAndStageExpOnly(Relation.TYPE_MNG, patIllScript.getCurrentStage());
		MultiVertex jokerVertex = chooseItem(expsVertices);
		if(jokerVertex==null){ //no more fdgs to be added at this stage
			new ErrorMessageContainer().addErrorMessage("mngform", "Expert added no additional therapies a this stage", "", FacesMessage.SEVERITY_ERROR);
			return;
		}
		new AddMngAction(patIllScript).addRelation(jokerVertex.getExpertVertex().getListItemId(), jokerVertex.getExpertVertex().getPrefixStr(), -1, -1, -1, true);
	}
	
	
	private MultiVertex chooseItem(List<MultiVertex> expsVertices){
		if(expsVertices==null || expsVertices.isEmpty()) return null;
		//for now we pick randomly, but we could add a algorithm here based on the connections to the 
		// final ddx and picks of the peers....
		if(expsVertices.size()==1) return expsVertices.get(0);
		int myRandomPick = new Random().nextInt(expsVertices.size());
		return expsVertices.get(myRandomPick);
	}
}
