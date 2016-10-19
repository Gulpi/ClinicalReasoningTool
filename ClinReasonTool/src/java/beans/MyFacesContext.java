package beans;

import java.util.Locale;

import beans.graph.Graph;
import beans.scoring.ScoreContainer;
import beans.scripts.PatientIllnessScript;

public interface MyFacesContext {

	public Graph getGraph();
	public PatientIllnessScript getPatillscript();
	public void initSession();
	public void reset();
	public ScoreContainer getScoreContainer();
	public Locale getLocale();
}
