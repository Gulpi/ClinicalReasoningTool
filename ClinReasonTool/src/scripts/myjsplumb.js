var dynamicAnchors = [ "Left",  "Right" ];
var patDefX = 0;
var patDefY = 0; //{ left:0px;top:0px; }
var fdgDefX = 240;
var fdgDefY = 0;
var ddxDefX = 0;
var ddxDefY = 210;
var tstDefX = 240;
var tstDefY = 210;
var mngDefX = 240;
var mngDefY = 420;
var sumDefX = 0;
var sumDefY = 420;



/*
 * TODO not very elegant, but the "" vs non "" is important and seems to be difficult to do when getting the items/ids from 
 * an array....
 * all groups are created and all items attached to a group
 */
function initGroups(){
	var groups = new Array("fdg_group", "ddx_group","tst_group", "mng_group", "sum_group", "pat_group");
	var boxes = new Array(fdg_box, ddx_box, tst_box,mng_box, sum_box, pat_box );
	var classes = new Array("fdgs", "ddxs", "fdgs","fdgs", "fdgs", "fdgs" );

	for(var i=0; i<boxes.length;i++){
		instance.addGroup({
	        el:boxes[i],
	        id:groups[i],
	        constrain:true,
	        droppable:false
	    });
	}
	for(var i=0; i<item_arr.length;i++){
		var itemId = item_arr[i];
		var item = $("#"+itemId)
		addToGroup(itemId, item);
		instance.addEndpoint(itemId, { anchor:dynamicAnchors }, endpoint);
		var ep = instance.getEndpoints(itemId);
		//ep.addClass("ddxs");
		//alert(ep);
	}
	for(var i=0; i<exp_arr.length;i++){
		var itemId = exp_arr[i];
		var item = $("#"+itemId)
		addToGroup(itemId, item);
		instance.addEndpoint(itemId, { anchor:dynamicAnchors }, endpoint);
		var ep = instance.getEndpoints(itemId);
		//ep.addClass("ddxs");
		//alert(ep);
	}
	instance.addToGroup("sum_group", $("#summStText"));
	//var ep = instance.getEndpoints();
	//var ep2 = jsPlumb.getEndpoints();
}

/*
 * called after an item has been added (and after clicking the hidden button)
 */
function updateItemCallback(){ 
	//re-init fdgs:
	for(var i=0; i<item_arr.length;i++){
		var itemId = item_arr[i];
		var item = $("#"+itemId);
		addToGroup(itemId, item);
		var ep = instance.getEndpoints(itemId);
		if(ep==undefined){ //item has no endpoints yet, so we add them
			instance.addEndpoint(itemId, { anchor:dynamicAnchors }, endpoint);
		}
		//var endpoints = jsPlumb.getEndpoints(item);
		//var ep = instance.getEndpoints();
		//alert("hallo");
	}
	item_arr = new Array();
    instance.draggable(jsPlumb.getSelector(".drag-drop .itembox"));		
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
 */
function initConnections(){	
	var conns = jQuery.parseJSON($("#jsonConns").html());
	if(conns!=''){
		for(j=0; j<conns.length;j++){
			createConnection(conns[j].id, conns[j].sourceid, conns[j].targetid, conns[j].l, conns[j].e,  conns[j].weight_e,  conns[j].weight_l);
		}
	}
}




/*
 * init positions of all containers....
 */
function initContainerPos(){
	initOneContainerPos("fdg", "fdg_box");
	initOneContainerPos("pat", "pat_box");
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
	initOneContainerCollapsed("pat", "pat_box");
	initOneContainerCollapsed("ddx", "ddx_box");
	initOneContainerCollapsed("tst", "tst_box");
	initOneContainerCollapsed("mng", "mng_box");
	initOneContainerCollapsed("sum", "sum_box");
}

function initOneContainerCollapsed(type, box){ //type="fdg"
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


