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
    scope: "green",
    /*connectorStyle: { strokeStyle: color2, lineWidth: 6 },*/
    connector: ["Bezier", { curviness: 15 } ],
    maxConnections: 10,
    Anchors: ["TopCenter", "TopCenter"],
    isTarget: true,
    dropOptions: myDropOptions,
    deleteEndpointsOnDetach:true
};

var expendpoint = {
	    endpoint: ["Dot", { radius: 1 }],
	    paintStyle: { fillStyle: color2 },
	    isSource: true,
	    cssClass: 'exp-endpoint',	    
	    connector: ["Bezier", { curviness: 15 } ],
	    maxConnections: 10,
	    isTarget: true,
	    dropOptions: myDropOptions,
	    deleteEndpointsOnDetach:false
	};


/*var con=info.connection;
var arr=jsPlumb.select({source:con.sourceId,target:con.targetId});
if(arr.length>1){
   jsPlumb.detach(con);*/
   
function createConnection(cnxId, sourceId, targetId, learner, exp, expWeight, learnerWeight){
	if(/*!expFeedback &&*/ learner=="0" && exp=="1"){
		createExpConnection(cnxId, sourceId, targetId, expWeight, learnerWeight)
		return;
	}

	if(instance.getEndpoints==null || instance.getEndpoints(sourceId)==undefined || instance.getEndpoints(targetId)==undefined)
		return;
	var epSource = instance.getEndpoints(sourceId)[0];
	var epTarget = instance.getEndpoints(targetId)[0];
	var color = getWeightToColor(learnerWeight);
	//alert(color);
	var cnx = instance.connect({
		source:epSource, 
		target:epTarget,
		deleteEndpointsOnDetach:false,
		connectorStyle: { strokeStyle: color, lineWidth: 6 }
		//title: 'Click to change or delete'
	});
	$(cnx).attr('id', cnxId);
	//$(cnx).attr('title', 'hallo');
	//cnx.setLabel('Click to change or delete');
	cnx.setPaintStyle({strokeStyle:color}); //color depending on the weight!!!
}

function createExpConnection(cnxId, sourceId, targetId, expWeight, learnerWeight){
	/*if(instance.getEndpoints==null || instance.getEndpoints(sourceId)==undefined || instance.getEndpoints(targetId)==undefined)
		return;*/
	var src = $("#"+sourceId); 
	var tgt = $("#"+targetId);
	var src0 = $("#"+sourceId)[0];
	var tgt0 = $("#"+targetId)[0];
	if($("#"+sourceId)[0]==undefined ||$("#"+targetId)[0]==undefined) 
		return;
	
	var ep = instance.getEndpoints(sourceId);
	var ep2 = instance.getEndpoints(targetId);
	//if(ep==undefined){ //item has no endpoints yet, so we add them
		instance.addEndpoint(sourceId, { anchor:dynamicAnchors }, expendpoint);
	//}
	//if(ep2==undefined){ //item has no endpoints yet, so we add them
		instance.addEndpoint(targetId, { anchor:dynamicAnchors }, expendpoint);
	//}
	var epSourceArr = instance.getEndpoints(sourceId);
	var epTargetArr = instance.getEndpoints(targetId);
		
	var epSource = epSourceArr[epSourceArr.length-1];
	var epTarget = epTargetArr[epTargetArr.length-1];
	var color = getWeightToColorExp(expWeight);
	//alert(color);
	var cnx = instance.connect({
		source:epSource, 
		target:epTarget,
		deleteEndpointsOnDetach:false,
		connectorStyle: { strokeStyle: color, lineWidth: 6 }
	});
	if(cnx!=undefined){
		$(cnx).attr('id', "exp_"+cnxId);
		cnx.setPaintStyle({strokeStyle:color}); //color depending on the weight!!!
		cnx.addClass("jsplumb-exp-connector-hide");
		cnx.addClass("jsplumb-exp-connector");
		cnx.removeClass("jsplumb-connector");
	}
	//cnx.hide();
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
	var sourceId = conn.sourceId; 
	var targetId = conn.targetId;
	//getConnectionBySourceAndTargetId(sourceId, targetId)
 	sendAjax(sourceId, addConnectionCallback, "addConnection", targetId);
}

/**
 * 
 * @param id = sourceId
 * @param name = targetId
 */
function addConnectionCallback(sourceId, cnxId, targetId){
	if(sourceId==null || sourceId<0 || targetId==null || targetId<0) return;
	var cnx = getConnectionBySourceAndTargetId(sourceId, targetId);
	$(cnx).attr('id', cnxId);
	//$("[id='cnxsform:hiddenCnxButton']").click();
}

/*function reInitCnxsCallback(id, cnxId){
	//we have to draw the connections again....
	//alert($("#jsonConns").html());
	alert(cnxId);
	instance.detachEveryConnection();
	//initConnections();
}*/

function openConnContext(cnxId){
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
	cnx.setPaintStyle({strokeStyle:color});
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

function getAllConnectionBySourceAndTargetId(sourceId, targetId){
	var cns = instance.getConnections('*');
	var cnxArr = new Array();
	var counter = 0;
	for(var i =0; i<cns.length; i++){
		cnx = cns[i];
		if(cnx.sourceId==sourceId && cnx.targetId==targetId){
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
	switch(weight){
	case 6: return "#990000"; //speaks against (red)
	case 5: return "#999999"; /*"#009933"*/; //highly related (black)
	case 4: return "#999999"; /*"#00e64d"*/; //somewhat related (dark grey)
	case 3: return "#e6e6e6"; /*"#e6ffee"*/; //slightly related (light grey)
	case 8: return "#006699"; //hierarchy
	case "6": return "#990000";
	case "5": return "#999999";
	case "4": return "#999999";
	case "3": return "#e6e6e6";
	case "8": return "#006699";
	default: 
		return "#999999";
	}
}

function getWeightToColorExp(weight){
	switch(weight){
	case 6: return "#990000"; //speaks against (red)
	case 5: return "#009933"; /*"#009933"*/; //highly related (black)
	case 4: return "#00e64d"; /*"#00e64d"*/; //somewhat related (dark grey)
	case 3: return "#e6ffee"; /*"#e6ffee"*/; //slightly related (light grey)
	case 8: return "#006699"; 
	default: 
		return "#00e64d";
	}
	
}

function toogleCnxDisplay(){
	toggleCnxStatus();
	initCnxDisplay();
}

function initCnxDisplay(){
	var cnxStatus = getCnxStatus();
	if(cnxStatus==1 || cnxStatus=="1") showCnx();
	else if(cnxStatus==0 || cnxStatus=="0") hideCnx();
	//handleCnxDisplay();
}

function hideCnx(){
	$(".jsplumb-connector").addClass("jsplumb-connector-hide");
	$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-show"); //hide expert cnx as well
	$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-hide");
	$("#cnxtoggle").attr("title", showCnxTitle);
	$("#cnxtoggle").removeAttr("checked");
	//$("#cnxtogglei").removeClass("fa-conn_on");
	//$("#cnxtogglei").addClass("fa-conn_off");
}

function showCnx(){
	$("#cnxtoggle").attr("title", hideCnxTitle);
	$("#cnxtoggle").attr("checked", "checked");
	//$("#cnxtogglei").removeClass("fa-conn_off");
	//$("#cnxtogglei").addClass("fa-conn_on");
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
	//var arr = jsPlumb.getConnections({sourceId:con.sourceId,targetId:con.targetId});
	var arr = getAllConnectionBySourceAndTargetId(con.sourceId, con.targetId);
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