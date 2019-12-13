package beans.scoring;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import controller.AjaxController;

/**
 * Configures the charts that are displayed to the learner (or instructor) 
 * 
 * @author ingahege
 *
 */
@ManagedBean(name = "chartConf", eager = true)
@RequestScoped
public class ChartConfigurator {
	public static final int DISPLAY_ALLCHARTS = 0;//default -> display all charts
	private static final int PEER_DISPLAY_ON = 1;//default -> display all charts

	/**
	 * see definitions in LearningAnalyticsContainer
	 */
	private int typeOfChart = DISPLAY_ALLCHARTS;
	/**
	 * sm (we have only a small window e.g. in a dashboard, mobile?) or lg (we have a full screen or similar)
	 * depending on this we show/hide information
	 */
	private String widgetSize = "lg";
	
	private int displayPeers = PEER_DISPLAY_ON;
	
	public ChartConfigurator(){
		setTypeOfChart();
		setWidgetSize();
		setDisplayPeer();
	}


	public int getTypeOfChart() {return typeOfChart;}

	private void setTypeOfChart() {
		typeOfChart = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_CHARTTYPE, -1);
		if(typeOfChart<0) typeOfChart = DISPLAY_ALLCHARTS;		 
	}
		
	public String getWidgetSize() {return widgetSize;}
	private void setWidgetSize() {
		widgetSize = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_CHARTSIZE);
		if(!isValidWidgetSize())widgetSize = "lg";
		widgetSize = widgetSize.toLowerCase();
	}
	
	private void setDisplayPeer(){
		setDisplayPeer(AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_CHARTPEER, -1));
	}

	private void setDisplayPeer(int dp){
		if(dp > -1) displayPeers = dp;
	}
	public int getDisplayPeers() {
		int dp = AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_CHARTPEER, -1);
		if(dp > -1) displayPeers = dp;
		return displayPeers;
	}
	
	public String getDisplayPeersChecked() {
		if(displayPeers==PEER_DISPLAY_ON) return "checked='checked'";
		return "no";
	}

	public int getDisplayProblemChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_PROBLEM_IDENT);}
	public int getDisplayDDXChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_DDX_IDENT);}
	public int getDisplayTestChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_INVESTIGATION);}
	public int getDisplayMngChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_THERAP_INTERVENTION);}
	public int getDisplaySumStChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_REPRESENTATION);}
	public int getDisplayOverallChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_OVERALL);}
	
	/**
	 * 1= show complete individual map; 0 or -1 show in a step-through mode.
	 * @return
	 */
	public int getIndivReportsDisplayMode() {
		return AjaxController.getInstance().getIntRequestParamByKey(AjaxController.REQPARAM_REPORTS_DISPLAYMODE, -1);

	}
	private int displayChart(int type){
		if(typeOfChart==DISPLAY_ALLCHARTS || DISPLAY_ALLCHARTS==type) return 2; 
		if(typeOfChart==type) return 1;
		return 0;
	}
	
	private boolean isValidWidgetSize(){
		if(widgetSize==null || widgetSize.equals("")) return false;
		if(widgetSize.equalsIgnoreCase("sm") || widgetSize.equalsIgnoreCase("lg")) return true;
		return false;
	}
		
}
