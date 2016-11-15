package beans;

import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import application.AppBean;
import beans.scoring.PeerBean;
import beans.scripts.*;
import controller.AjaxController;
import controller.NavigationController;
import controller.ReportController;

/**
 * @author ingahege
 *
 */
@ManagedBean(name = "reportbean", eager = true)
@SessionScoped
public class ReportBean {
	/**
	 * key = vpId, value=learner scripts for this vpId
	 */
	private Map<String, List<PatientIllnessScript>> scriptMap;

	public ReportBean(){
		getAllVPs();
	}
	/**
	 * get all VP refs to display a select list of VPs (1. step of indiv. reports)
	 * @return
	 */
	public List<VPScriptRef> getAllVPs(){
		Map<String, VPScriptRef> m = (Map<String, VPScriptRef>) AppBean.getVpScriptRefs();
		List<VPScriptRef> list = new ArrayList();
		
		if(m==null || m.isEmpty()) return null;
		Iterator it = m.values().iterator();
		while(it.hasNext())
			list.add((VPScriptRef) it.next());
		//Collection l = m.values();
		return list;
		//return (List<VPScriptRef>) m.values();
	}
	
	public List<PatientIllnessScript> getLearnerScripts(){
		String vpId = AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_REPORTS_VP);
		return getLearnerScripts(vpId);
	}
	/**
	 * Select learner scripts based on a previously selected VPId and return the list (2. step of indiv.reports)
	 * @return
	 */
	public List<PatientIllnessScript> getLearnerScripts(String vpId){
		//String vpId = AjaxController.getInstance().getRequestParamByKey(AjaxController.REQPARAM_REPORTS_VP);
		if(vpId==null || vpId.equals("") || vpId.equals("-1")) return null;
		if(scriptMap!=null && scriptMap.get(vpId)!=null && !scriptMap.get(vpId).isEmpty()) return scriptMap.get(vpId); //already loaded once....so just return		
		List<PatientIllnessScript> scripts = ReportController.getInstance().getLearnerScriptsForVPId(vpId);
		if(scriptMap==null) scriptMap = new TreeMap<String,List<PatientIllnessScript>>();
		scriptMap.put(vpId, scripts);
		return scripts;

	}
	
	public PatientIllnessScript getSelectedLearnerScript(){
 		String vpId = AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_REPORTS_VP);
		return getSelectedLearnerScript(vpId);
	}
	
	/**
	 * User has selected VPId and script id, so we get the script and return it for display
	 * @return
	 */
	public PatientIllnessScript getSelectedLearnerScript(String vpId){
		if(scriptMap==null) return null;
		long id = AjaxController.getInstance().getLongRequestParamByKey(AjaxController.REQPARAM_REPORTS_SCRIPT_ID);
		if(id<0) return null;
		List<PatientIllnessScript> scripts = scriptMap.get(vpId);
		if(scripts==null) return null;
		for(int i=0; i<scripts.size(); i++){
			if (scripts.get(i).getId()==id){
				NavigationController.getInstance().getAdminFacesContext().setPatillscript(scripts.get(i));
				return scripts.get(i);
			}
		}
		return null;
	}
	
	
	public String getSelVpId(){
		return  AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_REPORTS_VP);
	}
	
	public String getSelScriptId(){
		return AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_REPORTS_SCRIPT_ID);
	}
	
	/**
	 * @return PeerBean for currently selected vpId to display in reports
	 */
	public List<PeerBean> getPeerBeanByVpId(){
 		String vpId = AjaxController.getInstance().getRequestParamByKeyNoDecrypt(AjaxController.REQPARAM_REPORTS_VP);
 		if(vpId==null || vpId.equals("") || vpId.equals("-1")) return null;
		return AppBean.getPeers().getPeerBeans(vpId);
	}
}
