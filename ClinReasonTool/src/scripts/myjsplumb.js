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
	        droppable:false,
	        draggable:false //remove if boxes shall be draggable again!!!
	    });
	}
	for(var i=0; i<item_arr.length;i++){
		var itemId = item_arr[i];
		var item = $("#"+itemId)
		addToGroup(itemId, item);
		createEndpointsForItems(itemId, endpoint);

	}
	for(var i=0; i<exp_arr.length;i++){
		var itemId = exp_arr[i];
		var item = $("#"+itemId)
		addToGroup(itemId, item);
	}
	instance.addToGroup("sum_group", $("#summStText"));
}

function createEndpointsForItems(itemId, endpointtmp){
	var eps = instance.getEndpoints(itemId);
	if(eps!=undefined) return; ///do not create endpoints again if they are already there, e.g. for boxes added by learner & expert
	
	if(itemId.startsWith("fdg") || itemId.startsWith("tst")){
		//endpoint.id="hallo_"+itemId;
		var ep = instance.addEndpoint(itemId, { anchor:"RightMiddle" }, endpointtmp);
		ep.id = ep_right_prefix + itemId;
	}
	else if(itemId.startsWith("ddx")){
		var ep = instance.addEndpoint(itemId, { anchor:"LeftMiddle" }, endpointtmp);
		ep.id = ep_left_prefix + itemId;
		var ep2 = instance.addEndpoint(itemId, { anchor:"RightMiddle" }, endpointtmp);
		ep2.id = ep_right_prefix + itemId;
	}
	else{
		var ep = instance.addEndpoint(itemId, { anchor:"LeftMiddle" }, endpointtmp);
		ep.id = ep_left_prefix + itemId;
	}
}

/*
 * called after an item has been added (and after clicking the hidden button)
 */
function updateItemCallback(data, items, boxId){ 
	 var status = data.status; // Can be "begin", "complete" or "success".
	 //var boxes = new Array(fdg_box, ddx_box, tst_box, mng_box, sum_box/*, pat_box*/ );
	if(isCallbackStatusSuccess(data)){
    	 var arr = $("."+items);
    	 if(arr!=null){
    		 for(var i=0; i<arr.length;i++){
    			// var o = arr[i];
    			var id = arr[i].id;
				var item = $("#"+id);
				 
				instance.draggable(jsPlumb.getSelector("#"+id));
				 $( "#"+id).draggable({
				        containment:"parent"
				  });
			     $( "#"+id).draggable({
			   	  stop: function( event, ui ) {
			   		  handleRectDrop(ui);
			   	  }
			   });
				addToGroup(id, item); //4.
				initBoxHeights();
				var pos = $(item).position();
				var ep = instance.getEndpoints(id);
				createEndpointsForItems(id, endpoint);
    		 }
    	 }
	}   
}


/*
 * attach an item to a group
 */
function addToGroup(itemId, item){ //fdg_1571
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


/*
 * On load we draw the connections between the items.
 * TODO create a javascript object 
 */
function initConnections(){	
	var conns = '';
	if($("#jsonConns").html()!=null && $("#jsonConns").html()!='' && $("#jsonConns").html()!=undefined)
		conns = jQuery.parseJSON($("#jsonConns").html());
	
	if(conns!=''){
		for(j=0; j<conns.length;j++){
			createConnection(conns[j].id, conns[j].sourceid, conns[j].targetid, conns[j].l, conns[j].e,  conns[j].weight_e,  conns[j].weight_l, conns[j].start_ep, conns[j].target_ep);
		}
	}
}

function reInitExpConnections(){
	var conns = '';
	if($("#jsonConns").html()!=null && $("#jsonConns").html()!='' && $("#jsonConns").html()!=undefined)
		conns = jQuery.parseJSON($("#jsonConns").html());
	if(conns!=''){
		for(j=0; j<conns.length;j++){
			if(conns[j].e=="1")
				createExpConnection(conns[j].id, conns[j].sourceid, conns[j].targetid, conns[j].l, conns[j].e,  conns[j].weight_e,  conns[j].weight_l, conns[j].start_ep, conns[j].target_ep);
		}
	}


}


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

/**init box heights on loading
 * 
 */
/*function initBoxHeights(){
	initBoxHeight("fdg_box", "fdgs");
	initBoxHeight("ddx_box", "ddxs");
	initBoxHeight("tst_box", "tests");
	initBoxHeight("mng_box", "mngs");
	sameHeightForNeigborBoxes();
	//instance.repaintEverything();

}
function initExpBoxHeights(){
	initBoxHeight("fdg_box", "expfdgs");
	initBoxHeight("ddx_box", "expddxs");
	initBoxHeight("tst_box", "exptests");
	initBoxHeight("mng_box", "expmngs");
	sameHeightForNeigborBoxes();
	//instance.repaintEverything();
}

function sameHeightForNeigborBoxes(){
	//make neighbor boxes the same height:
	if($("#fdg_box").height()>$("#ddx_box").height()) $("#ddx_box").height($("#fdg_box").height());
	if($("#ddx_box").height()>$("#fdg_box").height()) $("#fdg_box").height($("#ddx_box").height());
	if($("#tst_box").height()>$("#mng_box").height()) $("#mng_box").height($("#mng_box").height());
	if($("#mng_box").height()>$("#tst_box").height()) $("#tst_box").height($("#mng_box").height());
}*/

/**
 * automatically enlarge a box, depending on the number of items in it.
 * @param boxId
 * @param itemCls
 */
/*function initBoxHeight(boxId, itemCls){
	var storedHeight = -1;
	if(boxId=="fdg_box" || boxId=="ddx_box") storedHeight = getBoxFdgDDXHeight();
	if(boxId=="tst_box" || boxId=="mng_box") storedHeight = getBoxTstMngHeight();
	
	var itemArr = $("."+itemCls);
	if(itemArr.length==0) return;
	var maxY = 0; //item that is the farthest below and determines the height of the box
	for(i=0; i<itemArr.length; i++){
		var myY = $(itemArr[i]).position().top + 30;
		if(myY > maxY) maxY = myY;
	}
	if(maxY>400) maxY=400;

	if(maxY>=200 && $("#"+boxId).height()< maxY && maxY > storedHeight){
		$("#"+boxId).height(maxY);
		setBoxHeight(boxId, maxY);
		//return;
	}	
	else if(storedHeight > $("#"+boxId).height()) 
		$("#"+boxId).height(storedHeight);
	
	sendNewHeightToHostSystem();

}*/

function initBoxHeights(){
	var storedHeightRow1 = getBoxFdgDDXHeight();
	var storedHeightRow2 = getBoxTstMngHeight();
	//if(boxId=="fdg_box" || boxId=="ddx_box") storedHeight = getBoxFdgDDXHeight();
	//if(boxId=="tst_box" || boxId=="mng_box") storedHeight = getBoxTstMngHeight();
	var maxYRow1 = 210; //height of fdg & ddx containers
	var maxYRow2 = 210; //height of tst & mng containers
	
	var itemArr2 = $(".row1");
	if(itemArr2.length==0) return;
	for(i=0; i<itemArr2.length; i++){
		var myY = $(itemArr2[i]).position().top + 30;
		if(myY > maxYRow1) maxYRow1 = myY;
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
	//if(maxY>400) maxY=400;
	//if(maxYRow1>storedHeightRow1){
		$("#fdg_box").height(storedHeightRow1);
		$("#ddx_box").height(storedHeightRow1);
		//setBoxHeight("fdg_box", maxYRow1);
	//}
	
	//if(maxYRow2>storedHeightRow2){
		$("#tst_box").height(maxYRow2);
		$("#mng_box").height(maxYRow2);
		//setBoxHeight("mng_box", maxYRow2);
	//}
	
	
	
	/*if(maxY>=200 && $("#"+boxId).height()< maxY && maxY > storedHeight){
		$("#"+boxId).height(maxY);
		setBoxHeight(boxId, maxY);
		//return;
	}	
	else if(storedHeight > $("#"+boxId).height()) 
		$("#"+boxId).height(storedHeight);*/
	
	sendNewHeightToHostSystem();

}

function sendNewHeightToHostSystem(){
	var rowsHeight = $("#fdg_box").height() + $("#mng_box").height();
	var minHeight = 520;
	//if(rowsHeight>520){
		var newFrameHeight = 730;
		if((rowsHeight-520)>0) newFrameHeight += rowsHeight-520;
		//alert(newFrameHeight);
		postFrameHeight(newFrameHeight);
	//}
		
	
}


