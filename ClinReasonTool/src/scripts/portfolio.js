/*
 * currently used for the prototype 
 */
function createNewScript(){
	alert("Currently not implemented. Will enable students to create their own scripts independent from cases, e.g. for real patients.")
	//sendAjax(-1, "doRedire, type, name){
	
}

/*show/display peer performance in charts*/
function togglePeers(){
	var chartPeer = 0; 
	if($("#peerToggleBox").prop('checked')) chartPeer = 1;
	//alert(al);
	if($("#peerToggleBox").val()=="checked") chartPeer = 1;
	
	location.href="charts.xhtml?chart_size="+widgetSize+"&chart_type="+chartTypeDisplay+"&chart_peer="+chartPeer;
	//sendAjaxCharts(id, togglePeersCallBack, "togglePeer", name);
}

function togglePeersCallBack(){
	
}