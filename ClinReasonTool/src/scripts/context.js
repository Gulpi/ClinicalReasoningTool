/******************** actors*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addActor(listItemId, name, typedinName){
	
	if(name!=""){
		sendAjaxUrl(listItemId, actorCallBack, "addActor", name, typedinName, ajaxUrl);
	}
}

function delActor(id){	
	sendAjaxUrl(id, actorCallBack, "delActor", "", "", ajaxUrl);
}

function addContext(listItemId, name, typedinName){
	
	if(name!=""){
		sendAjaxUrl(listItemId, contextCallBack, "addContext", name, typedinName,ajaxUrl);
	}
}

function delContext(id){	
	sendAjaxUrl(id, contextCallBack, "delContext", "", "", ajaxUrl);
}


/*
** Make sure that the actors list is updated
*/
function actorCallBack(){
	$("#act_search").val("");	
	$("[id='actorsform:hiddenActButton']").click();
}

/*
** Make sure that the actors list is updated
*/
function contextCallBack(){
	$("#ctxt_search").val("");	
	$("[id='contextform:hiddenActButton']").click();
}

/**
* After clicking the hodden button -> Not sure whether we have to do something here.
 */
function updateActCallback(){
	
}