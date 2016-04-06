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
	$.ajax({
		  method: "POST",
		  url: "tabs_ajax.xhtml",
		  data: { type: type, id: id, session_id: sessId, name: name, script_id: scriptId, stage:currentStage }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	
}

function sendAjaxCM(id, callback, type, name, x, y){
	$.ajax({
		  method: "POST",
		  url: "tabs_ajax.xhtml",
		  data: { type: type, id: id, session_id: sessId, name: name, x: x, y: y, script_id: scriptId, stage:currentStage }
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
	 $("#msg").html(msg);
}

/* callback function if there is nothing to do */
function doNothing(){}

/*
 * We store the currently open tab in a cookie, to be able to reopen the same tab after a reload. 
 */
function switchTab(tabidx){	
	Cookies.set('tab', tabidx);
	if(tabidx==6) updateGraph();
	//alert(Cookies.get('tab'));
}

function getCurrentTab(tabidx){	
	var currTab = Cookies.get('tab');
	
	if(currTab=="" || currTab=="undefined" || currTab==undefined){
		//alert(currTab);
		currTab = 0;
	}
	return currTab;
}
