/**
 * make changes to an expert script available to users by removing the current script version from cache.
 */
function removeExpScriptFromCache(vpId){
	sendAjaxAdmin(vpId, doNothing, "removeExpScriptFromCache", vpId);
}

function doNothing(){}


/**
 * admin selects a vp id so we trigger an ajax call, to get back all learner scripts for the 
 * selected vpId. 
 */
function selectScriptForVPId(){
	var vpId = $("#report_vpid").val();
	window.location.replace(window.location.pathname + "?r_vp_id="+vpId);
	//sendAjaxReports(vpId, selectScriptForVPIdCallback, "getLearnerScripts", vpId);
}

function selectScriptForVPIdCallback(response){
	//alert(response);
}

function displayScript(){
	var vpId = $("#report_vpid").val();
	var scriptId =  $("#report_scripId").val();
	sendAjaxReports(vpId, displayScriptCallback, "getSelectedLearnerScript", scriptId);

}

function displayScriptCallback(){
	$("#report_iframe").attr("src", "../view/exp_boxes_view.xhtml");

}