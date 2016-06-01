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
    maxConnections: 3,
    isTarget: true,
    dropOptions: myDropOptions,
    deleteEndpointsOnDetach:false
};

var expendpoint = {
	    endpoint: ["Dot", { radius: 1 }],
	    paintStyle: { fillStyle: color2 },
	    isSource: true,
	    cssClass: 'exp-endpoint',	    
	    connector: ["Bezier", { curviness: 15 } ],
	    maxConnections: 3,
	    isTarget: true,
	    dropOptions: myDropOptions,
	    deleteEndpointsOnDetach:false
	};


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
	if(ep==undefined){ //item has no endpoints yet, so we add them
		instance.addEndpoint(sourceId, { anchor:dynamicAnchors }, expendpoint);
	}
	if(ep2==undefined){ //item has no endpoints yet, so we add them
		instance.addEndpoint(targetId, { anchor:dynamicAnchors }, expendpoint);
	}
	var epSource = instance.getEndpoints(sourceId)[0];
	var epTarget = instance.getEndpoints(targetId)[0];
	var color = getWeightToColor(expWeight);
	//alert(color);
	var cnx = instance.connect({
		source:epSource, 
		target:epTarget,
		deleteEndpointsOnDetach:false,
		connectorStyle: { strokeStyle: color, lineWidth: 6 }
	});
	$(cnx).attr('id', "exp_"+cnxId);
	cnx.setPaintStyle({strokeStyle:color}); //color depending on the weight!!!
	cnx.addClass("jsplumb-exp-connector-hide");
	cnx.addClass("jsplumb-exp-connector");
	cnx.removeClass("jsplumb-connector");
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
 	sendAjax(sourceId, doNothing, "addConnection", targetId);
}

function openConnContext(cnxId){
	$("#connContext").dialog( "option", "width", ['200'] );
	$("#connContext").dialog( "option", "height", ['200'] );
	$("#connContext").dialog( "option", "title", "change connection" );
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

function getWeightToColor(weight){
	switch(weight){
	case 6: return "#990000";
	case 5: return "#009933";
	case 4: return "#00e64d";
	case 3: return "#e6ffee";
	case "6": return "#990000";
	case "5": return "#009933";
	case "4": return "#00e64d";
	case "3": return "#e6ffee";
	default: 
		return "#c2c2c2";
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
	//$(".jsplumb-connector").hide();
	$("#cnxtoggle").attr("title", "Show connections");
	$("#cnxtogglei").removeClass("fa-conn_on");
	$("#cnxtogglei").addClass("fa-conn_off");
}

function showCnx(){
	$("#cnxtoggle").attr("title", "Hide connections");
	$("#cnxtogglei").removeClass("fa-conn_off");
	$("#cnxtogglei").addClass("fa-conn_on");
	$(".jsplumb-connector").removeClass("jsplumb-connector-hide");
	$(".jsplumb-connector").show();
}