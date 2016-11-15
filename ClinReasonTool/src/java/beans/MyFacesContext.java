package beans;

import java.util.Locale;

import beans.graph.Graph;
import beans.scoring.ScoreContainer;
import beans.scripts.PatientIllnessScript;
import beans.user.User;

public interface MyFacesContext {

	public Graph getGraph();
	public PatientIllnessScript getPatillscript();
	public void initSession();
	public void reset();
	public ScoreContainer getScoreContainer();
	public Locale getLocale();
	public User getUser();
	public boolean isView();
}
