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
	sendAjaxUrl(id, callback, type, name, "tabs_ajax.xhtml");
}


/**
 * This is for calls not directly related to the patientIllnessScript, but for the context
 * we start an ajax call with changed params. We also include always the session id!
 * id = id of the problem/diagnosis,...
 * callback = function to call when coming back from ajax cal
 * type = method name to call server-side to handle the call.
 * name = name of problem, diagnosis,...
 */
function sendAjaxContext(id, callback, type, name){
	sendAjaxUrl(id, callback, type, name, "tabs_ajax2.xhtml");
}

function sendAjaxUrl(id, callback, type, name, url){
	$.ajax({
		  method: "POST",
		  url: url,
		  data: { type: type, id: id, session_id: 1, name: name, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	
}

function sendAjaxCM(id, callback, type, name, x, y){
	$.ajax({
		  method: "POST",
		  url: "tabs_ajax.xhtml",
		  data: { type: type, id: id, session_id: 1, name: name, x: x, y: y, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {	
		  handleResponse(response, callback, name);
	  });	
}

/* display msg and call callback function*/
function handleResponse(response, callback, name){
	 displayErrorMsg(response);
	 var id2 =  $(response).find('id').text();
	 var isOk =  $(response).find('ok').text();
	 //TODO we might need shortnam here (for tooltip in map)s
	 if(isOk=="1") callback(id2, name);
	
}

function displayErrorMsg(response){
	 var msg =  $(response).find('msg').text();
	 var formId =  $(response).find('formId').text();
	 $("#msg_"+formId).html(msg);
}

/* callback function if there is nothing to do */
function doNothing(){}

/*
 * We store the currently open tab in a cookie, to be able to reopen the same tab after a reload. 
 */
function switchTab(tabidx){	
	Cookies.set('tab', tabidx);
	$("#msg").html("");
	if(tabidx==6) updateGraph();
	
	if(expFeedback==true || peerFeedback==true){ //we only have to trigger a call if the expert feedback is on 
		sendAjaxContext(tabidx, doNothing, "toogleExpFeedback", tabidx);
		//TODO peerfeedback...
	}
	
}

function getCurrentTab(){	
	var currTab = Cookies.get('tab');
	if(currTab=="" || currTab=="undefined" || currTab==undefined){
		currTab = 0;
	}
	return currTab;
}

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
     