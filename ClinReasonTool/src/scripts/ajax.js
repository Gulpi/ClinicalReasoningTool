String.prototype.startsWith = function(str) {return (this.match("^"+str)==str)}
String.prototype.trim = function() { return this.replace(/^\s+|\s+$/g,"");}

/* copied from http://jquery-howto.blogspot.com/2009/09/get-url-parameters-values-with-jquery.html
 * to access the query params we get from the hosting system.
 * access: 
 * // Get object of URL parameters
 * var allVars = $.getUrlVars();
 * // Getting URL var by its nam
 * var byName = $.getUrlVar('name');
 */
$.extend({
	  getUrlVars: function(){
	    var vars = [], hash;
	    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	    for(var i = 0; i < hashes.length; i++)
	    {
	      hash = hashes[i].split('=');
	      vars.push(hash[0]);
	      vars[hash[0]] = hash[1];
	    }
	    return vars;
	  },
	  getUrlVar: function(name){
	    return $.getUrlVars()[name];
	  }
	});

/**
 * we start an ajax call with changed params. We also include always the session id!
 * id = id of the problem/diagnosis,...
 * callback = function to call when coming back from ajax cal
 * type = method name to call server-side to handle the call.
 * name = name of problem, diagnosis,...
 */
function sendAjax(id, callback, type, name){
	clearErrorMsgs();
	sendAjaxUrl(id, callback, type, name, "", ajaxUrl);
}

function sendAjax(id, callback, type, name, typedinName){
	clearErrorMsgs();
	var confirmed = true;
	if(id=="-99" && displayOwnEntryWarn=="true"){
		displayOwnEntryWarn = "false"
		confirmed = confirm(displayOwnEntryWarnMsg);
	}
	if(confirmed){
		//if(id=="-99") displayOwnEntryWarn = "false";
		sendAjaxUrl(id, callback, type, name, typedinName, ajaxUrl);
	}
/*	else{//empty boxes -> too early
		$("#problems").val("");
		$("#ddx").val("");
		$("#tests").val("");
		$("#mng").val("");
	}*/
	
}


function sendAjaxCharts(id, callback, type, name){
	clearErrorMsgs();
	sendAjaxUrl(id, callback, type, name, "", "/crt/src/html/charts_ajax.xhtml");
}


/**
 * This is for calls not directly related to the patientIllnessScript, but for the context
 * we start an ajax call with changed params.
 * id = id of the problem/diagnosis,...
 * callback = function to call when coming back from ajax cal
 * type = method name to call server-side to handle the call.
 * name = name of problem, diagnosis,...
 */
function sendAjaxContext(id, callback, type, name){
	clearErrorMsgs();
	sendAjaxUrl(id, callback, type, name, "", "/crt/src/html/tabs_ajax2.xhtml");
}

function sendAjaxAdmin(id, callback, type, name){
	$.ajax({
		  method: "POST",
		  url: "/crt/src/html/admin/tabs_ajax_admin.xhtml",
		  data: { type: type, id: id, name: name }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	

	//sendAjaxUrl(id, callback, type, name, "/crt/src/html/admin/tabs_ajax_admin.xhtml");
}

function sendAjaxReports(id, callback, type, scriptId){
	$.ajax({
		  method: "POST",
		  url: "/crt/src/html/reports/tabs_ajax_reports.xhtml",
		  data: { type: type, r_vp_id: id, r_scriptid: scriptId }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	

	//sendAjaxUrl(id, callback, type, name, "/crt/src/html/admin/tabs_ajax_admin.xhtml");
}

function sendAjaxUrl(id, callback, type, name, orgname, url){
	$.ajax({
		  method: "POST",
		  url: url,
		  data: { type: type, id: id, name: name, orgname: orgname, script_id: scriptId, stage:currentStage, typehistory:inputhistory }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	
}
/**
 * use this function if you want to get back an html (template)
 * @param id
 * @param callback
 * @param type
 * @param name
 * @param url
 */
function sendAjaxUrlHtml(id, callback, type, name, url){
	$.ajax({
		  method: "POST",
		  url: url,
		  data: { type: type, id: id, name: name, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {
		  handleResponseHtml(response, callback, name);		
	  });
}

function sendAjaxCM(id, callback, type, name, x, y){
	$.ajax({
		  method: "POST",
		  url: ajaxUrl,
		  data: { type: type, id: id, name: name, x: x, y: y, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {	
		  handleResponse(response, callback, name);
	  });	
}

/**
 * when a connection is made we include the ids of the start- and target endpoints to be stored for the connection.
 * @param id
 * @param callback
 * @param type
 * @param name
 * @param startEpId
 * @param targetEpId
 */
function sendAjaxCnx(id, callback, type, name, startEpId, targetEpId){
	$.ajax({
		  method: "POST",
		  url: ajaxUrl,
		  data: { type: type, id: id, name: name, x: startEpId, y: targetEpId, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {	
		  handleResponse(response, callback, name);
	  });	
}

/**
 *  display msg and call callback function
 *  we have an id and id2...
 **/
function handleResponse(response, callback, name){
	 displayErrorMsg(response);
	 var id =  $(response).find('id').text();
	 var id2 =  $(response).find('id2').text();
	 var isOk =  $(response).find('ok').text();
	 //TODO we might need shortnam here (for tooltip in map)s
	 if(isOk=="1"){
		 if(id2!="" && id2!=null)
			 callback(id, id2, name); //addConnectionCallback(sourceId, cnxId, targetId){
		 else callback(id, name);
	 }
	
}

/* display msg and call callback function*/
function handleResponseHtml(response, callback, name){
	if(response.contentType=="text/xml")
		handleResponse(response, callback, name)
	else callback(response);	
}

/**
 * Error message are displayed in the error message span in the correpsonding form. 
 * E.g. id of form is "probform" and span id is "msg_probform"
 * @param response
 */
function displayErrorMsg(response){
	 var msg =  $(response).find('msg').text();
	 var formId =  $(response).find('formId').text();
	 $("#msg_"+formId).html(msg);
}

/* callback function if there is nothing to do */
function doNothing(){}


function isCallbackStatusSuccess(data){
	 var status = data.status;
	 switch (status) {
     case "begin": // Before the ajax request is sent.    	
         return false;

     case "complete": // After the ajax response is arrived.
     	//instance.removeGroup("fdg_group", true);
    	 return false;

     case "success":
    	 return true;
	 }
}
/*
 * We store the currently open tab in a cookie, to be able to reopen the same tab after a reload. 
 */
/*function switchTab(tabidx){	
	Cookies.set('tab', tabidx);
	$("#msg").html("");
	if(tabidx==6) updateGraph();
	
	if(expFeedback==true || peerFeedback==true){ //we only have to trigger a call if the expert feedback is on 
		sendAjaxContext(tabidx, doNothing, "toogleExpFeedback", tabidx);
		//TODO peerfeedback...
	}
	
}*/

/*function getCurrentTab(){	
	var currTab = Cookies.get('tab');
	if(currTab=="" || currTab=="undefined" || currTab==undefined){
		currTab = 0;
	}
	return currTab;
}*/

function storeContainerPos(type, x, y){
	if(type=="fdg_box"){
		sessionStorage.fdgx = x; 
		sessionStorage.fdgy = y; 
	}
}

/*
 * 0=off, 1 = on, default=on
 */
function getCnxStatus(){
	if(sessionStorage.cnxtoggle) return sessionStorage.cnxtoggle;
	else{
		sessionStorage.cnxtoggle = 1;
		return sessionStorage.cnxtoggle;
	}
}

function toggleCnxStatus(){
	if(sessionStorage.cnxtoggle){
		if(sessionStorage.cnxtoggle==1)  sessionStorage.cnxtoggle=0;
		else sessionStorage.cnxtoggle = 1;
	}
	else{
		sessionStorage.cnxtoggle = 0;
	}
	return sessionStorage.cnxtoggle;
}

/* storing the max height for boxes fdg/ddx and test/mng for use on next card. */ 
function getBoxFdgDDXHeight(){
	if(sessionStorage.fdgddxHeight) return sessionStorage.fdgddxHeight;
	return -1;
}

function getBoxTstMngHeight(){
	if(sessionStorage.testmngHeight) return sessionStorage.testmngHeight;
	return -1;
}

function setBoxHeight(boxId, height){
	if(boxId=="fdg_box" || boxId=="ddx_box") setBoxFdgDDXHeight(height);
	if(boxId=="tst_box" || boxId=="mng_box") setBoxTstMngHeight(height);
		
}

function setBoxFdgDDXHeight(height){
	sessionStorage.fdgddxHeight = height; 
}

function setBoxTstMngHeight(height){
	sessionStorage.testmngHeight = height; 
}

/*storing x and y position of boxes - currently not in use*/

function getContainerX(type){
	if(type=="fdg"){
		if(sessionStorage.fdgx) return sessionStorage.fdgx;
		sessionStorage.fdgx = fdgDefX;
		return sessionStorage.fdgx;
	}
	if(type=="ddx"){
		if(sessionStorage.ddxx) return sessionStorage.ddxx;
		sessionStorage.ddxx = ddxDefX;
		return sessionStorage.ddxx;
	}
	if(type=="tst"){
		if(sessionStorage.tstx) return sessionStorage.tstx;
		sessionStorage.tstx = tstDefX;
		return sessionStorage.tstx;
	}
	if(type=="mng"){
		if(sessionStorage.mngx) return sessionStorage.mngx;
		sessionStorage.mngx = mngDefX;
		return sessionStorage.mngx;
	}
	if(type=="sum"){
		if(sessionStorage.sumx) return sessionStorage.sumx;
		sessionStorage.sumx = sumDefX;
		return sessionStorage.sumx;
	}
	if(type=="pat"){
		if(sessionStorage.patx) return sessionStorage.patx;
		sessionStorage.patx = patDefX;
		return sessionStorage.patx;
	}
}

function getContainerY(type){
	if(type=="fdg"){
		if(sessionStorage.fdgy) return sessionStorage.fdgy;
		sessionStorage.fdgy = fdgDefY;
		return sessionStorage.fdgy;
	}
	if(type=="ddx"){
		if(sessionStorage.ddxy) return sessionStorage.ddxy;
		sessionStorage.ddxy = ddxDefY;
		return sessionStorage.ddxy;
	}
	if(type=="tst"){
		if(sessionStorage.tsty) return sessionStorage.tsty;
		sessionStorage.tsty = tstDefY;
		return sessionStorage.tsty;
	}
	if(type=="mng"){
		if(sessionStorage.mngy) return sessionStorage.mngy;
		sessionStorage.mngy = mngDefY;
		return sessionStorage.mngy;
	}
	if(type=="sum"){
		if(sessionStorage.sumy) return sessionStorage.sumy;
		sessionStorage.sumy = sumDefY;
		return sessionStorage.sumy;
	}
	if(type=="pat"){
		if(sessionStorage.paty) return sessionStorage.paty;
		sessionStorage.paty = patDefY;
		return sessionStorage.paty;
	}
}

/*
 * returns true if container is collapsed, else false. 
 */
function getContainerCollapsed(type){
	if(type=="fdg"){
		if(sessionStorage.fdgcollapsed)
			return sessionStorage.fdgcollapsed;		
		sessionStorage.fdgcollapsed = "false";
		return sessionStorage.fdgcollapsed;
	}	
	if(type=="ddx"){
		if(sessionStorage.ddxcollapsed)
			return sessionStorage.ddxcollapsed;
				sessionStorage.ddxcollapsed = "false";
		return sessionStorage.ddxcollapsed;
	}
	if(type=="tst"){
		if(sessionStorage.tstcollapsed)
			return sessionStorage.tstcollapsed;		
		sessionStorage.tstcollapsed = "false";
		return sessionStorage.tstcollapsed;
	}
	if(type=="mng"){
		if(sessionStorage.mngcollapsed)
			return sessionStorage.mngcollapsed;		
		sessionStorage.mngcollapsed = "false";
		return sessionStorage.mngcollapsed;
	}
	if(type=="sum"){
		if(sessionStorage.sumcollapsed)
			return sessionStorage.sumcollapsed;		
		sessionStorage.sumcollapsed = "false";
		return sessionStorage.sumcollapsed;
	}
	if(type=="pat"){
		if(sessionStorage.patcollapsed)
			return sessionStorage.patcollapsed;		
		sessionStorage.patcollapsed = "false";
		return sessionStorage.patcollapsed;
	}
}

function toggleStoredContainerCollapsed(type){
	if(type=="fdg_box"){
		if(sessionStorage.fdgcollapsed){
			if(sessionStorage.fdgcollapsed=="true")
				sessionStorage.fdgcollapsed = "false";
			else 
				sessionStorage.fdgcollapsed = "true";
		}
		else sessionStorage.fdgcollapsed = "true";
	}
	if(type=="ddx_box"){
		if(sessionStorage.ddxcollapsed){
			if(sessionStorage.ddxcollapsed=="true")
				sessionStorage.ddxcollapsed = "false";
			else 
				sessionStorage.ddxcollapsed = "true";
		}
		else sessionStorage.ddxcollapsed = "true";
	}
	if(type=="mng_box"){
		if(sessionStorage.mngcollapsed){
			if(sessionStorage.mngcollapsed=="true")
				sessionStorage.mngcollapsed = "false";
			else 
				sessionStorage.mngcollapsed = "true";
		}
		else sessionStorage.mngcollapsed = "true";
	}
	if(type=="tst_box"){
		if(sessionStorage.tstcollapsed){
			if(sessionStorage.tstcollapsed=="true")
				sessionStorage.tstcollapsed = "false";
			else 
				sessionStorage.tstcollapsed = "true";
		}
		else sessionStorage.tstcollapsed = "true";
	}
	if(type=="sum_box"){
		if(sessionStorage.sumcollapsed){
			if(sessionStorage.sumcollapsed=="true")
				sessionStorage.sumcollapsed = "false";
			else 
				sessionStorage.sumcollapsed = "true";
		}
		else sessionStorage.sumcollapsed = "true";
	}
	if(type=="pat_box"){
		if(sessionStorage.patcollapsed){
			if(sessionStorage.patcollapsed=="true")
				sessionStorage.patcollapsed = "false";
			else 
				sessionStorage.patcollapsed = "true";
		}
		else sessionStorage.patcollapsed = "true";
	}
}

/*********communication with VP system (via HTML5 cross-domain messaging) ***************/

/*
 * indicated the parent VP system whether the learner has submitted a diagnosis (1) 
 * 
 * status=-1: unknwon
 * status=0: diagnosis not submitted, but currentstage is >= maxStage until which learner should submit final diagnosis.
 * statue=1: diagnosis submitted or diagnosis submission not yet enforced (currentStage<maxStage)
 */
function postEnforceFinalDDXSubmission(isSubmitted/*, currentStage, maxStageForSubmission*/){
	var message = 1; //default 
	if(isSubmitted!="true" && parseInt(currentStage)>=parseInt(maxSubmittedStage)) message  = 0; 
	
	top.postMessage(message, "*");
}

function postFrameHeight(newHeight){
	top.postMessage(newHeight, "*");
}     

