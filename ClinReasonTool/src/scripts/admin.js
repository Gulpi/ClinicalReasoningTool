/**
 * make changes to an expert script available to users by removing the current script version from cache.
 */
function removeExpScriptFromCache(vpId){
	sendAjaxAdmin(vpId, doNothing, "removeExpScriptFromCache", vpId);
}

function doNothing(){}