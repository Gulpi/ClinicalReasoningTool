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
	private static final String KEY_FOR_CHARTTYPE = "CHART_TYPE";
	
	public ChartConfigurator(){
		setTypeOfChart();
		setWidgetSize();
	}
	/**
	 * see definitions in LearningAnalyticsContainer
	 */
	private int typeOfChart = DISPLAY_ALLCHARTS;
	/**
	 * sm (we have only a small window e.g. in a dashboard, mobile?) or lg (we have a full screen or similar)
	 * depending on this we show/hide information
	 */
	private String widgetSize = "lg";

	public int getTypeOfChart() {return typeOfChart;}
	//public void setTypeOfChart(int typeOfChart) {this.typeOfChart = typeOfChart; }
	public void setTypeOfChart() {
		typeOfChart = new AjaxController().getIntRequestParamByKey(AjaxController.REQPARAM_CHARTTYPE);
		if(typeOfChart<0) typeOfChart = DISPLAY_ALLCHARTS;		 
	}
		
	public String getWidgetSize() {return widgetSize;}
	public void setWidgetSize() {
		widgetSize = new AjaxController().getRequestParamByKey(AjaxController.REQPARAM_CHARTSIZE);
		if(!isValidWidgetSize())widgetSize = "lg";
		widgetSize = widgetSize.toLowerCase();
	}
	public int getDisplayProblemChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_PROBLEM_IDENT);}
	public int getDisplayDDXChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_DDX_IDENT);}
	public int getDisplayTestChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_INVESTIGATION);}
	public int getDisplayMngChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_THERAP_INTERVENTION);}
	public int getDisplaySumStChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_REPRESENTATION);}
	public int getDisplayOverallChart(){ return displayChart(LearningAnalyticsContainer.CATEGORY_OVERALL);}
	
	private int displayChart(int type){
		if(typeOfChart==DISPLAY_ALLCHARTS || typeOfChart==type || DISPLAY_ALLCHARTS==type) return 1; 
		return 0;
	}
	
	private boolean isValidWidgetSize(){
		if(widgetSize==null || widgetSize.equals("")) return false;
		if(widgetSize.equalsIgnoreCase("sm") || widgetSize.equalsIgnoreCase("lg")) return true;
		return false;
	}
		
}
