var color2 = "#c2c2c2";

var myDropOptions = {
        tolerance: "touch",
        hoverClass: "dropHover",
        activeClass: "dragActive"
    };

var endpoint = {
    endpoint: ["Dot", { radius: 5 }],
    paintStyle: { fillStyle: color2 },
    isSource: true,
    //scope: "green",
    //connectorStyle: { strokeStyle: color2, lineWidth: 6 },
    connector: ["Bezier", { curviness: 15 } ],
    maxConnections: 10,
    //Anchors: ["TopCenter", "TopCenter"],
    isTarget: true,
    //dropOptions: myDropOptions,
    deleteEndpointsOnDetach:true
};
var expendpoint = {
	    endpoint: ["Dot", { radius: 0.1 }],
	    paintStyle: { fillStyle: color2 },
	    isSource: true,
	    //scope: "green",
	    //connectorStyle: { strokeStyle: color2, lineWidth: 6 },
	    cssClass: 'exp-endpoint',
	    connector: ["Bezier", { curviness: 15 } ],
	    maxConnections: 10,
	    //Anchors: ["TopCenter", "TopCenter"],
	    isTarget: true,
	    //dropOptions: myDropOptions,
	    deleteEndpointsOnDetach:false	
		
}


/*var con=info.connection;
var arr=jsPlumb.select({source:con.sourceId,target:con.targetId});
if(arr.length>1){
   jsPlumb.detach(con);*/
   
function createConnection(cnxId, sourceId, targetId, learner, exp, expWeight, learnerWeight, startEpIdx, targetEpIdx){

	if(/*!expFeedback &&*/ learner=="0" && exp=="1"){
		createExpConnection(cnxId, sourceId, targetId, expWeight, learnerWeight, startEpIdx, targetEpIdx)
		return;
	}
	if(instance.getEndpoints==null || instance.getEndpoints(sourceId)==undefined || instance.getEndpoints(targetId)==undefined)
		return;
	
	var epSource = getEndpointForCnx(sourceId, startEpIdx);//instance.getEndpoints(sourceId)[startEpIdx];
	var epTarget =  getEndpointForCnx(targetId, targetEpIdx); // instance.getEndpoints(targetId)[targetEpIdx];
	var color = getWeightToColor(learnerWeight);
	var lw = getWeightToLineWidth(learnerWeight);
	//alert(color);
	var cnx = instance.connect({
		source:epSource, 
		target:epTarget,
		/*deleteEndpointsOnDetach:false,*/
		paintStyle: { strokeStyle: color, lineWidth: lw }
		//title: 'Click to change or delete'
	});
	$(cnx).attr('id', cnxId);
	//$(cnx).attr('title', 'hallo');
	//cnx.setLabel('Click to change or delete');
	//cnx.setPaintStyle({strokeStyle:color, lineWidth:lw}); //color depending on the weight!!!
}

function getEndpointForCnx(itemId, epIdx){
	var eps = instance.getEndpoints(itemId);
	if(eps==null) return;
	if(epIdx=="0"){ //default
		return eps[0];
	}
	for(var i=0; i<eps.length;i++){
		if(eps[i].id==epIdx+"_"+itemId) return eps[i];
	}
}

function createExpConnection(cnxId, sourceId, targetId, expWeight, learnerWeight, startEpIdx, targetEpIdx){
	if($("#"+sourceId)[0]==undefined ||$("#"+targetId)[0]==undefined) 
		return;

	createEndpointsForItems(sourceId, expendpoint);
	createEndpointsForItems(targetId, expendpoint);

	var epSource = getEndpointForCnx(sourceId, startEpIdx);//instance.getEndpoints(sourceId)[startEpIdx];
	var epTarget =  getEndpointForCnx(targetId, targetEpIdx); // instance.getEndpoints(targetId)[targetEpIdx];

	var color = getWeightToColorExp(expWeight);
	var lw = getWeightToLineWidth(expWeight);
	//alert(color);
	var cnx = instance.connect({
		source:epSource, 
		target:epTarget,
		id:"exp_"+cnxId,
		/*deleteEndpointsOnDetach:false,*/
		connectorStyle: { strokeStyle: color, lineWidth: lw }
	});
	if(cnx!=undefined){
		$(cnx).attr('id', "exp_"+cnxId);
		cnx.setPaintStyle({strokeStyle:color, lineWidth: lw}); //color depending on the weight!!!
		if(!isOverallExpertOn() || !isOverallCnxOn()){
			//cnx.addClass("jsplumb-exp-connector-show");
		//}
		//else{
			cnx.addClass("jsplumb-exp-connector-hide");
		}
		cnx.addClass("jsplumb-exp-connector");
		cnx.removeClass("jsplumb-connector");
	}
}

function delConnection(){
	var cnxId = $("#conn_id").html();	
	var cnx = getConnectionById(cnxId);
	jsPlumb.detach(cnx);
	$("#conn_id").html('');
	$("#connContext" ).dialog( "close" );
	sendAjax(cnxId, doNothing, "delConnection", "");
}
/**
 * a connection is added to the canvas, we save the start- and endpoint and label.
 */
function addConnection(conn){
	//var eps = conn.endpoints;
	var epSource = conn.sourceEndpoint.id;//instance.getEndpoints(conn.id);
	var epTarget = conn.targetEndpoint.id;
	if(epSource==epTarget) return;
	var epSourceIdx = epSource.charAt(0);
	var epTargetIdx = epTarget.charAt(0);
	
	var sourceId = conn.sourceId; 
	var targetId = conn.targetId;
	sendAjaxCnx(sourceId, addConnectionCallback, "addConnection", targetId, epSourceIdx, epTargetIdx);
}

/**
 * 
 * @param id = sourceId
 * @param name = targetId
 */
function addConnectionCallback(sourceId, cnxId, targetId){
	if(sourceId==null || sourceId<0 || targetId==null || targetId<0) return; //cnx with same source & target
	var cnx = getConnectionBySourceAndTargetId(sourceId, targetId);
	$(cnx).attr('id', cnxId);
	var color = getWeightToColor(2);
	var lw = getWeightToLineWidth(2);

	cnx.setPaintStyle({strokeStyle:color, lineWidth:lw});
	//$("[id='cnxsform:hiddenCnxButton']").click();
}

function openConnContext(cnxId){
	if(!cnxId.startsWith("cnx_")) return; //click on endpoint/anchor
	clearErrorMsgs();
	$("#connContext").dialog( "option", "width", ['200'] );
	$("#connContext").dialog( "option", "height", ['200'] );
	$("#connContext").dialog( "option", "title", chgCnxDialogTitle);
	$("#connContext").dialog( "option", "buttons", [ ] );
	$("#connContext" ).dialog( "open" );
	$("#conn_id").html(cnxId);
	$("#connContext").show();
}



function chgConnectionWeight(weight){
	var cnxId = $("#conn_id").html();	
	var cnx = getConnectionById(cnxId);
	var color = getWeightToColor(weight);
	var lw = getWeightToLineWidth(weight);
	cnx.setPaintStyle({strokeWidth:lw, strokeStyle:color});
	$("#conn_id").html('');
	$("#connContext" ).dialog( "close" );
	sendAjax(cnxId, doNothing, "chgConnection", weight);	
}

function getConnectionById(cnxId){
	var cns = instance.getConnections('*');
	for(var i =0; i<cns.length; i++){
		cnx = cns[i];
		if(cnx.id==cnxId) return cnx;
	}	
}
/**
 * get the connection with the given source- and targetId. 
 * @param sourceId
 * @param targetId
 * @returns {___anonymous_cnx}
 */
function getConnectionBySourceAndTargetId(sourceId, targetId){
	var cns = instance.getConnections('*');
	for(var i =0; i<cns.length; i++){
		cnx = cns[i];
		if(cnx.sourceId==sourceId && cnx.targetId==targetId) return cnx;
	}	
	//TODO:
	//return jsPlumb.select({source:sourceId,target:targetId});
}

/**
 * looks whether a learner connections is already made between the source and target. If so return 
 * it. We need this as an array, because the cnx the learner currently tries to make is also already
 * registered. So, array length is always >=1! 
 * @param sourceId
 * @param targetId
 * @returns {Array}
 */
function getAllLearnerConnectionBySourceAndTargetId(sourceId, targetId){
	var cns = instance.getConnections('*');
	var cnxArr = new Array();
	var counter = 0;
	for(var i =0; i<cns.length; i++){
		cnx = cns[i];
		if(cnx.sourceId==sourceId && cnx.targetId==targetId && !cnx.id.startsWith("exp")){
			//alert(cnx.getParameter("class"));
			cnxArr[counter] = cnx;
			counter ++;
		}
	}
	return cnxArr;
	//TODO:
	//return jsPlumb.select({source:sourceId,target:targetId});
}

/* get color of connection based on the weight */
function getWeightToColor(weight){
	//return "#006699";
	switch(weight){
	case 6: return "#990000"; //speaks against (red)
	case 5: return "#004466"; //"#999999"; //"#009933"; //highly related (black)
	//case 4: return "#999999"; //"#00e64d"; //somewhat related (dark grey)
	case 3: return "#b3e6ff"; //"#e6e6e6"; //"#e6ffee"; //slightly related (light grey)
	case 8: return "#006699"; //hierarchy
	case "6": return "#990000";
	case "5": return "#004466";
	//case "4": return "#999999";
	case "3": return "#b3e6ff";
	case "8": return "#006699";
	default: 
		return "#006699"; //"#999999";
	}
}

var default_linewidth = 3;
var strong_linewidth = 4;
var light_linewidth = 2;
/**
 * NOT WORKING, WHY?????
 * @param weight
 * @returns {Number}
 */
function getWeightToLineWidth(weight){
	switch(weight){
	case 6: return default_linewidth; 		//speaks against (red)
	case 5: return strong_linewidth; 		//"#009933"; //highly related (black)
	//case 4: return 4; 	//"#00e64d"; //somewhat related (dark grey)
	case 3: return light_linewidth; 		//"#e6ffee"; //slightly related (light grey)
	case 8: return default_linewidth; 
	case 2: return default_linewidth; 		//default
	case "6": return default_linewidth; 	//speaks against (red)
	case "5": return strong_linewidth; 	//"#009933"; //highly related (black)
	//case "4": return 4; 	//"#00e64d"; //somewhat related (dark grey)
	case "3": return light_linewidth; 	//"#e6ffee"; //slightly related (light grey)
	case "8": return default_linewidth; 
	case "2": return default_linewidth; 	//default
	default: 
		return default_linewidth;
	}
}

function getWeightToColorExp(weight){
	return "#006699";
	/*
	switch(weight){
	case 6: return "#990000"; //speaks against (red)
	case 5: return "#009933"; //"#009933"; //highly related (black)
	case 4: return "#00e64d"; //"#00e64d"; //somewhat related (dark grey)
	case 3: return "#e6ffee"; //"#e6ffee"; //slightly related (light grey)
	case 8: return "#006699"; 
	default: 
		return "#00e64d";
	}
	*/
}

function toogleCnxDisplay(){
	toggleCnxStatus();
	initCnxDisplay();
}

function initCnxDisplay(){
	var cnxStatus = getCnxStatus();
	if(cnxStatus==1 || cnxStatus=="1") showCnx();
	else if(cnxStatus==0 || cnxStatus=="0") hideCnx();
}

function hideCnx(){
	$(".jsplumb-connector").addClass("jsplumb-connector-hide");
	$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-show"); //hide expert cnx as well
	$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-hide");
	$("#cnxtoggle").attr("title", showCnxTitle);
	$("#cnxtoggle").removeAttr("checked");
}

function showCnx(){
	$("#cnxtoggle").attr("title", hideCnxTitle);
	$("#cnxtoggle").attr("checked", "checked");
	$(".jsplumb-connector").removeClass("jsplumb-connector-hide");
	//if expert on -> display expert cnx: TODO
	if(isOverallExpertOn()){
		$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-show");
		$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-hide");
	}
	$(".jsplumb-connector").show();
}

function isDuplicateCnx(info, errormsg){
	//first we check whether there has already been made a connection between the source and target. 
	//If so display an error msg
	var con=info.connection;
	var arr = getAllLearnerConnectionBySourceAndTargetId(con.sourceId, con.targetId);
	if(arr.length>1){
	    jsPlumb.detach(con);
	    var msgSpanId = getErrorMsgSpanByTargetId(con.targetId);
	    $("#"+msgSpanId).html(errormsg);
	    return true;
	 }
	return false;
}
/**
 * we display the error message for connections in the box of the target item
 * @param targetId
 */
function getErrorMsgSpanByTargetId(targetId){
	var targetStart = targetId.substring(0,3);
	switch(targetStart){
	case "fdg":
		return "msg_probform";	
	case "ddx":
		return "msg_ddxform";
	case "mng":
		return "msg_mngform";
	case "tst":
		return "msg_testform";
		default: return "";
	}
}
/**
 * when an action has been performed we have to update the cnxs as well. It can be that we have to repaint the exp 
 * cnxs if the learner has added an element that is correct and has an expert cnx (then the id of the element changes).
 */
function reInitCnxsCallback(data){
	 var status = data.status; 
	 if(isCallbackStatusSuccess(data)){
    	fireAddConnection = false;
    	var cnxs = instance.getConnections();
    	if(cnxs!=null){
        	for(i=0; i<cnxs.length;i++){
        		var tmpCnx = cnxs[i];
        		if(tmpCnx.id.startsWith("exp_"))
        			instance.detach(tmpCnx);
        	}
    	}
    	reInitExpConnections();
    	fireAddConnection = true;        	
    }
}

function isOverallCnxOn(){
	if($("#cnxtoggle").prop("checked"))
		return true;
	return false;
}