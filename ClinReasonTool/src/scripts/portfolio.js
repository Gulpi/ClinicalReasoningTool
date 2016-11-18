/*
 * currently used for the prototype 
 */
function createNewScript(){
	alert("Currently not implemented. Will enable students to create their own scripts independent from cases, e.g. for real patients.")
	//sendAjax(-1, "doRedire, type, name){
	
}

/*show/display peer performance in charts*/
function togglePeers(){
/*	var chartPeer = 0; 
	if($("#peerToggleBox").prop('checked')) chartPeer = 1;
	if($("#peerToggleBox").val()=="checked") chartPeer = 1;*/
	//var tst = $("#peertoggle").val();
	//if($("#peertoggle").prop("checked")=="on") 
		if(peerStatus == 1) peerStatus = 0;
		else if(peerStatus==0) peerStatus = 1;
	//if($("#peertoggle").val()=="on") 
	//	peerStatus = 1;
	//if(peerStatus==1 || peerStatus=="1") showCnx();
	//else if(peerStatus==0 || peerStatus=="0") hideCnx();

	
	if(currentChartNum==1) printLargeOverall();
	if(currentChartNum==2) printLargeFdg();
	if(currentChartNum==3) printLargeDDX();
	if(currentChartNum==4) printLargeTst();
	if(currentChartNum==5) printLargeMng();
	if(currentChartNum==6) printLargeSum();
	//location.href="charts.xhtml?chart_size="+widgetSize+"&chart_type="+chartTypeDisplay+"&chart_peer="+chartPeer;
	//sendAjaxCharts(id, togglePeersCallBack, "togglePeer", name);
}

function togglePeersCallBack(){
	
}
/**
 * user clicks on a chart thumbnail, so, we enlarge it...
 * @param typeOfChart
 */
/*function openChartLarge(chartId){

	$("#"+chartId).width(300);
	$("#"+chartId).height(200);
	$("#overallchart").width(300);
	$("#overallchart").height(200);
	for (var i = 0; i < nv.graphs.length; i++) {
        nv.graphs[i].update();
    }
	
}*/

/*function resizeHandling(){
	$('#overallchart_cont').bind('resize', function(){

        for (var i = 0; i < nv.graphs.length; i++) {
            nv.graphs[i].update();
        }

	});
}*/