var dynamicAnchors = [ "Left",  "Right" ];

var ep_right_prefix = "1_"; //e.g. 1_fdg_12345
var ep_left_prefix = "2_";	
var ep_top_prefix = "3_";
var ep_bottom_prefix = "4_";

var groups = new Array("fdg_group", "ddx_group","tst_group", "mng_group", "sum_group");

/*
 * TODO not very elegant, but the "" vs non "" is important and seems to be difficult to do when getting the items/ids from 
 * an array....
 * all groups are created and all items attached to a group
 */
function initGroups(){
	//var boxes = new Array(fdg_box, ddx_box, tst_box, mng_box, sum_box /*, pat_box*/ );
	var boxes = new Array("fdg_box", "ddx_box", "tst_box", "mng_box", "sum_box" /*, pat_box*/ );

	for(var i=0; i<boxes.length;i++){
		instance.addGroup({
	        el: document.getElementById(boxes[i]),
	        id:groups[i],
	        constrain:true,
	        dropOverride:true,
	        droppable:false,
	        draggable:false //remove if boxes shall be draggable again!!!
	    });
	}
	initElems("");
	/*for(var i=0; i<item_arr.length;i++){
		var itemId = item_arr[i];
		var item = $("#"+itemId)[0];
		addToGroup(itemId, item);
		//this makes each node a target for connections, independent from the endpoint:
		instance.makeTarget(item,{
			isTarget:true,
			maxConnections:10,			
			anchor: ["Perimeter", { shape:"Rectangle" }]
			//paintStyle:{ fill:"red" },
		});
		createEndpointsForItems(itemId);
	}
	for(var i=0; i<exp_arr.length;i++){
		var itemId = exp_arr[i];
		var item = $("#"+itemId)[0];
		addToGroup(itemId, item);
	}*/
}

function initElems(selector){
	for(var i=0; i<item_arr.length;i++){
		var itemId = item_arr[i];
		if(selector==""|| itemId.startsWith(selector)){
			var item = $("#"+itemId)[0];
			addToGroup(itemId, item);
			//this makes each node a target for connections, independent from the endpoint:
			instance.makeTarget(item,{
				isTarget:true,
				maxConnections:10,			
				anchor: ["Perimeter", { shape:"Rectangle" }]
				//paintStyle:{ fill:"red" },
			});
			createEndpointsForItems(itemId);
		}
	}
	for(var i=0; i<exp_arr.length;i++){
		var itemId = exp_arr[i];
		if(selector==""|| itemId.startsWith(selector)){
			var item = $("#"+itemId)[0];
			addToGroup(itemId, item);
		}
		//createExpEndpointsForItems(itemId);
	}
	initDraggables();
} 


/*all boxes have 2 endpoints (left and right) */
function createEndpointsForItems(itemId){	
	var eps = instance.getEndpoints(itemId);
	if(eps!=undefined && eps.length>0) return; //do not create endpoints again if they are already there, e.g. for boxes added by learner & expert	
	//var source = $("#"+itemId)[0];
	//var ep1Connected = isEPConnected(itemId);
	//if(ep1Connected){
		var ep = instance.addEndpoint(itemId, { anchor:"LeftMiddle" }, endpointCL);
		ep.id = ep_left_prefix + itemId;
		ep.maxConnections = 10;

		var ep2 = instance.addEndpoint(itemId, { anchor:"RightMiddle" }, endpointCR);
		ep2.id = ep_right_prefix + itemId;
	    ep2.maxConnections = 10;

	/*}
	else{	
		var ep = instance.addEndpoint(itemId, { anchor:"LeftMiddle" }, endpointR);
		ep.id = ep_left_prefix + itemId;
		var ep2 = instance.addEndpoint(itemId, { anchor:"RightMiddle" }, endpointL);
		ep2.id = ep_right_prefix + itemId;
	}*/
}


function createExpEndpointsForItems(itemId){	
	var eps = instance.getEndpoints(itemId);
	if(eps!=undefined && eps.length>0) return; ///do not create endpoints again if they are already there, e.g. for boxes added by learner & expert	
	var ep = instance.addEndpoint(itemId, { anchor:"LeftMiddle" }, expendpoint);
	ep.id = ep_left_prefix + itemId;
	var ep2 = instance.addEndpoint(itemId, { anchor:"RightMiddle" }, expendpoint);
	ep2.id = ep_right_prefix + itemId;
}
/*
 * called after an item has been added (and after clicking the hidden button)
 */
function updateItemCallback(data, items, boxId){ 
	 var status = data.status; // Can be "begin", "complete" or "success".
	if(isCallbackStatusSuccess(data)){
		initElems(items);
    	/* var arr = $("."+items);
    	 if(arr!=null){
    		 for(var i=0; i<arr.length;i++){
    			var id = arr[i].id;
				var item = $("#"+id)[0]; //jsPlumb.getSelector("#"+id); //$("#"+id)[0];
				//var item = jsPlumb.getSelector("#"+id);
				
				//var sel = jsPlumb.getSelector("#"+id); //for new items there is only one element which has no class "jtk-draggable".
				//if(sel!=null && sel.length==2) $("#"+id).remove(); //these are the duplicates
				
				//else if(!instance.hasClass(sel[0], "jtk-draggable")){
					instance.draggable(jsPlumb.getSelector("#"+id));
					 $( "#"+id).draggable({
					        containment:"parent",
				        	start: function(event, ui) {             
				                 $('.ui-tooltip').remove();
				             }, 
					  });
				     $( "#"+id).draggable({
			    	 start: function(event, ui) {             
			                 $('.ui-tooltip').remove();
			             }, 
				   	  stop: function( event, ui ) {
				   		  handleRectDrop(ui);
				   	  }
				   });
					addToGroup(id, item); //4.
					instance.makeTarget(item,{
						isTarget:true,
						maxConnections:10,			
						anchor: ["Perimeter", { shape:"Rectangle" }]
						//paintStyle:{ fill:"red" },
					});
					createEndpointsForItems(id);
				//}
				
							
    		 }*/
    		 //initBoxHeights();	
    	 //}
		 initBoxHeights();
	}   
}


/*
 * attach an item to a group and make it a target
 */
function addToGroup(itemId, item){ 
	if(itemId.indexOf("fdg")>=0){
		instance.addToGroup("fdg_group", item);
		return;
	}
	if(itemId.indexOf("ddx")>=0){
		instance.addToGroup("ddx_group", item);
		return;
	}
	if(itemId.indexOf("tst")>=0){
		instance.addToGroup("tst_group", item);
		return;
	}
	if(itemId.indexOf("mng")>=0){
		instance.addToGroup("mng_group", item);
	}
	
}

/*
 * an item is moved, so we store the new position
 */
function handleRectDrop(ui){
	//if((xDragStart>-1 && xDragStart>-1) && rect.x >= xDragStart+10 || rect.x <= xDragStart-10 || rect.y >= yDragStart+10 || rect.y <= yDragStart-10){
		//var position = ui.position;
		var x = ui.position.left;
		var y = ui.position.top;
		var id = $(ui.helper).attr("id");
		  //after drag&drop we have to re-hide the connections if they are turned off:
		initCnxDisplay();	  
		sendAjaxCM(id, doNothing, "moveItem", name, x, y);
}




function toggleContainerCollapse(elem){
    var g = elem.getAttribute("group"), collapsed = instance.hasClass(elem, "collapsed");
    instance[collapsed ? "removeClass" : "addClass"](elem, "collapsed");
    instance[collapsed ? "expandGroup" : "collapseGroup"](g);
    toggleStoredContainerCollapsed(elem.id);
}

var conns = '';

/** we parse the JSON string of connections and store the connections in the conn variable 
 * We do this quite at the beginning to be able to access the conn variable when initializing the endpoints
**/
function parseConns(){
	if($("#jsonConns").html()!=null && $("#jsonConns").html()!='' && $("#jsonConns").html()!=undefined)
		conns = jQuery.parseJSON($("#jsonConns").html());
}
/**
 * On load we draw the connections between the items.
 **/
function initConnections(){	
	
	/*if($("#jsonConns").html()!=null && $("#jsonConns").html()!='' && $("#jsonConns").html()!=undefined)
		conns = jQuery.parseJSON($("#jsonConns").html());*/
	
	if(conns!=''){
		for(j=0; j<conns.length;j++){
			createConnection(conns[j].id, conns[j].sourceid, conns[j].targetid, conns[j].l, conns[j].e,  conns[j].weight_e,  conns[j].weight_l, conns[j].start_ep, conns[j].target_ep, conns[j].target_x, conns[j].target_y);
		}
	}
}

//function reInitConnections(){
//	parseConns();
//	initConnections();
	/*var expConns = '';
	if($("#jsonConns").html()!=null && $("#jsonConns").html()!='' && $("#jsonConns").html()!=undefined)
		expConns = jQuery.parseJSON($("#jsonConns").html());
	if(expConns!=''){
		for(j=0; j<expConns.length;j++){
			if(expConns[j].e=="1" && expConns[j].l=="0")
				createExpConnection(expConns[j].id, expConns[j].sourceid, expConns[j].targetid,  expConns[j].weight_e,  expConns[j].weight_l, expConns[j].start_ep, expConns[j].target_ep, expConns[j].target_x, expConns[j].target_y);
		}
	}*/


//}


/*
 * init positions of all containers....
 */
function initContainerPos(){
	initOneContainerPos("fdg", "fdg_box");
	//initOneContainerPos("pat", "pat_box");
	initOneContainerPos("ddx", "ddx_box");
	initOneContainerPos("tst", "tst_box");
	initOneContainerPos("mng", "mng_box");
	initOneContainerPos("sum", "sum_box");
}

function initOneContainerPos(type, box){
	var x = getContainerX(type); 
	var y = getContainerY(type); 
	$("#"+box).offset({ top: getContainerY(type), left: getContainerX(type) });
}

/*
 * init collapse status of all containers....
 */
function initContainerCollapsed(){
	initOneContainerCollapsed("fdg", "fdg_box");
	//initOneContainerCollapsed("pat", "pat_box");
	initOneContainerCollapsed("ddx", "ddx_box");
	initOneContainerCollapsed("tst", "tst_box");
	initOneContainerCollapsed("mng", "mng_box");
	initOneContainerCollapsed("sum", "sum_box");
}
/**
 * 
 * @param type e.g. "fdg"
 * @param box e.g. "fdg_box"
 */
function initOneContainerCollapsed(type, box){ 
	var isCollapsed = getContainerCollapsed(type); 
	if(!isCollapsed || isCollapsed=="false") return; //nothing to do...
	$("#"+box).addClass("collapsed");
	$("#fdg_box").addClass("jsplumb-group-collapsed");
}

function deleteEndpoints(id){
	var ep = instance.getEndpoints(id);
	if(ep!=undefined && ep.length>0){
		for(var i=0; i<ep.length;i++){
			instance.deleteEndpoint(ep[i]);
		}
	}
}

function initBoxHeights(){
	var storedHeightRow1 = getBoxFdgDDXHeight();
	var storedHeightRow2 = getBoxTstMngHeight();
	var maxYRow1 = 210; //height of fdg & ddx containers
	var maxYRow2 = 210; //height of tst & mng containers
	
	var itemArr2 = $(".row1");
	if(itemArr2.length==0) return;
	var isLastElemExpItem = false;
	for(i=0; i<itemArr2.length; i++){
		var myY = $(itemArr2[i]).position().top + 30;
		if(myY > maxYRow1){
			maxYRow1 = myY;
			if($(itemArr2[i]).hasClass("expbox")){
				isLastElemExpItem = true;
			}
			else isLastElemExpItem = false;
		}
	}
	//if the last element (lowest) is an expert item, we have to add some more pixels to the box, because
	//otherwise the lowest expBox lays over the submit button!
	if(isLastElemExpItem==true){
		maxYRow1 += 30; 
	}
	var itemArr3 = $(".row2");
	if(itemArr3.length==0) return;
	for(i=0; i<itemArr3.length; i++){
		var myY = $(itemArr3[i]).position().top + 30;
		if(myY > maxYRow2) maxYRow2 = myY;
	}	
	if(maxYRow1>storedHeightRow1){
		storedHeightRow1 = maxYRow1;
		setBoxHeight("fdg_box", maxYRow1);
	}
	
	if(maxYRow2>storedHeightRow2){
		storedHeightRow2 = maxYRow2;
		setBoxHeight("mng_box", maxYRow2);
	}
	$("#fdg_box").height(storedHeightRow1);
	$("#ddx_box").height(storedHeightRow1);

	$("#tst_box").height(maxYRow2);
	$("#mng_box").height(maxYRow2);
	
	sendNewHeightToHostSystem();

}

function sendNewHeightToHostSystem(){
	var rowsHeight = $("#fdg_box").height() + $("#mng_box").height();
	var minHeight = 520;
	var newFrameHeight = 730;
	if((rowsHeight-520)>0) newFrameHeight += rowsHeight-520;
	postFrameHeight(newFrameHeight);			
}

/**
 * we display the connection hint close to the first element we see. The arrow should point onto the endpoint of 
 * this element. Whether the box shall be displayed or not is handled server-side (only then the display box has the  
 * css class cnxhint_true).
 * @deprecated
 */
function checkDisplayCnxHint(){
	var item = $(".itembox")[0];	
	if(item===undefined) return;//important, otherwise we cause trouble for the page initialization!
	var posLeft = item.offsetLeft + item.offsetParent.offsetLeft + item.offsetWidth + 20;
	var posTop = item.offsetTop  + item.offsetParent.offsetTop + item.offsetHeight;
	
	$(".cnxhint_true").css({top: posTop+'px', left: posLeft+'px'});
	$(".cnxhint_true").hide();
	$(".cnxhint_true").toggle("drop", {direction: "right"},1000);
}

function initDraggables(){
    instance.draggable(jsPlumb.getSelector(".drag-drop .itembox"));        
	//item was moved -> trigger ajax call to update position
    $( ".drag-drop .itembox" ).draggable({
    	  stop: function( event, ui ) {
    		  handleRectDrop(ui);
    	  },	
        start: function(event, ui) {             
            // $(ui.helper).find('.tooltip').hide(); 
             $('.ui-tooltip').remove();
         }, 
    
    });
}

