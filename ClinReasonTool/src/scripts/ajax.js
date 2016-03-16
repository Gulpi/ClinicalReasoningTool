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
		  data: { type: type, id: id, session_id: sessId, name: name }
		})
	  .done(function( response ) {
		  handleResponse(response, callback, name);		
	  });	
}

function sendAjaxCM(id, callback, type, name, x, y){
	$.ajax({
		  method: "POST",
		  url: "tabs_ajax.xhtml",
		  data: { type: type, id: id, session_id: sessId, name: name, x: x, y: y}
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