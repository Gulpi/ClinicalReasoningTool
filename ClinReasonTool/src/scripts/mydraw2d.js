

/******************** concept map *******************************/
/**
 * read the json strings and place the rectangles and connection on the canvas.
 */
function initConceptMap(){
	var ddxs = ''; 
	var probs = '';
	var conns = '';
	if(jsonDDXMap!=null && jsonDDXMap!='') ddxs = jQuery.parseJSON(jsonDDXMap);
	if(jsonProbMap!=null && jsonProbMap!='') probs = jQuery.parseJSON(jsonProbMap);
	if(jsonConnsMap!='') conns =  jQuery.parseJSON(jsonConnsMap);
	if(ddxs!=''){
		for(i=0; i<ddxs.length;i++){
			createAndAddHyp(ddxs[i].label, ddxs[i].x, ddxs[i].y, ddxs[i].id);
		}
	}
	if(probs!=''){
		for(i=0; i<probs.length;i++){
			createAndAddFind(probs[i].label, probs[i].x, probs[i].y, probs[i].id);
		}
	}
	if(conns!=''){
		for(i=0; i<conns.length;i++){
			createConnection(conns[i].id, conns[i].sourceid, conns[i].targetid);
		}
	}
}
/**
 * called when initializing the concept map on load.
 * @param id
 * @param sourceId
 * @param targetId
 */
function createConnection(id, sourceId, targetId){
	var lc = new LabelConnection();
	lc.setId(id);
	var sourceRect  = my_canvas.getFigure(sourceId); //source: Problem
	var targetRect  = my_canvas.getFigure(targetId); //target: DDX
	if(sourceRect!=null && targetRect!=null){
		lc.setSource(sourceRect.getOutputPort(0));
		lc.setTarget(targetRect.getInputPort(0));
		my_canvas.add(lc);	
	}
}
/**
 * a connection is added to the canvas, we save the start- and endpoint and label.
 */
function addConnection(sourcePort, targetPort){
	sendAjax(sourcePort.getParent().id, doNothing, "addConnection", targetPort.getParent().id);
}

/*function addConnectionCallback(){}*/
/**
 * init the drag&drop rectangle for adding a new hypothesis
 */
function initAddHyp(){
  $( "#addhyp" ).draggable({ //add a new hypothesis
	  start: function( event, ui ) {
		 $("#addhyp").clone().prependTo($("#hypcontainer")); 
	  } , 
	  stop: function( event, ui ) {	
		  this.remove();	  
		  var canvasX = ui.offset.left- my_canvas.html.offset().left;
 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
	  	createAndAddHyp("", canvasX, canvasY,"cmddx_-1");	  	
	  	updatePreview();
	  }
  });	
}

/**
 * init the drag&drop rectangle for adding a new problem
 */
function initAddFind(){
	$( "#addfind" ).draggable({ //add a new hypothesis
	  start: function( event, ui ) {
			 $("#addfind").clone().prependTo($("#findcontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
	 		 //sendAjaxCM(initAddFindCallback, canvasX, canvasY); //(id, callback, type, name, x, y)
	 		  createAndAddFindCM("prob", canvasX, canvasY, "cmprob_-1");	 
	 		  //open list:
	 		 // alert(canvasX);
	 		 //openListForCMNewItem(canvasX,canvasY, "prob");
		  	updatePreview();
		  }
	  });	
}

/**
 * open the list for a new item, that has been dragged to the canvas
 * @param x
 * @param y
 * @param type
 */
function createAndAddFindCM(type,x,y,id){
	//positioning stuff:
	//selectedId = clickedId; //TODO: get the element last clicked from the canvas?
	canvasX = $("#canvas").offset().left;
	canvasY = $("#canvas").offset().top;
	xOfDialog = canvasX + x; 
	yOfDialog = canvasY + y;
	var rect = createRect(name,"#cccccc"/*"#99CC99"*/,id);//new draw2d.shape.basic.Rectangle();
	rect.createPort("output", new draw2d.layout.locator.RightLocator());
	designPort(rect.getOutputPort(0));
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	 
	var dialogName = "dialogCMNewProb";
	//alert(xOfDialog);
	//getting type of item the user has clicked on: 
	if(type=="ddx") 
		dialogName = "dialogCMNewDDX";
		
	//display:
	 $("#"+dialogName ).show();
	 $("#"+dialogName ).offset({ top: yOfDialog, left: xOfDialog });
}

/**
 * we submit the new problem and add it the same way as if coming from list. 
 * Therefore, we have to remove the temporarily created rectangle.
 * @param id
 * @param name
 */
function newProblemCM(id, name){
	//$("cm_prob_sel").val(""); //empty the select list
	var rect  = my_canvas.getFigure("cmprob_-1");
	var x = rect.x;
	var y = rect.y;
	//$("#dialogCMProb").hide();
	deleteItemFromCM(rect); //removes the item from the cm		
	sendAjaxCM(id, addProblemCallBack, "addProblem", name, x, y);
}

/**
 * create a new problem rectangle and add it to the canvas (including label and ports)
 **/
function createAndAddFind(name, x, y, id){
	 var rect = createRect(name,"#cccccc"/*"#99CC99"*/,id);//new draw2d.shape.basic.Rectangle();
	 rect.createPort("output", new draw2d.layout.locator.RightLocator());
	 designPort(rect.getOutputPort(0));
	 rect.setBackgroundColor("#ffffff");
	 my_canvas.add(rect, x, y);	 
	 //initAddFind();
}
/**
 * init the drag&drop rectangle for adding a new management item
 */
function initAddMng(){
	 $( "#addmng" ).draggable({ //add a new hypothesis
		  start: function( event, ui ) {
			 $("#addmng").clone().prependTo($("#mngcontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
		  	  createAndAddMng("new mng", canvasX, canvasY, "cmmng_-1");	  	
		      updatePreview();
		  }
	 });	
}

/**
 * init the drag&drop rectangle for adding a new diagnostic step
 */
function initAddDiagnStep(){
	 $( "#adddiagnstep" ).draggable({ //add a new hypothesis
		  start: function( event, ui ) {
			 $("#adddiagnstep").clone().prependTo($("#dscontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
			  createAndAddDiagnStep("new test", canvasX, canvasY,"cmds_-1");	  	
		  	updatePreview();
		  }
	}); 
}



/** we create a rectangle & label with the basic settings like size, color,... **/
function createRect(name, color, id){
	var rect = new LabelRectangle();
	rect.label.text=name;
	rect.label.setBackgroundColor("#ffffff");
	rect.color= new draw2d.util.Color(color);
	rect.setId(id);
	
	return rect;
}


/**
 * create a new hypothesis rectangle and add it to the canvas (including label and ports)
 **/
function createAndAddHyp(name, x, y, id){
	var rect = createRect(name,/*"#B7CDDC"*/ "#cccccc", id/*,"990000"*/); //new draw2d.shape.basic.Rectangle();
	//alert(rect.getId());
	rect.createPort("input");
	rect.createPort("output");
	//rect.getInputPort(0).setCssClass("ports");
	designPort(rect.getInputPort(0)); 
	designPort(rect.getOutputPort(0));
	if(name==""){ //then it is a new hyp from drag&drop and we have to attach an editor to select a label:
		var editor = new draw2d.ui.LabelLMEditor();
		rect.label.installEditor(editor);
		editor.start(rect.label);
	}
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddHyp(); //we have to re-init this here, because we have created a clone!!!
}
/* for some reason the setCssClass method is not working properly...*/
function designPort(port){
	port.setBackgroundColor("#cccccc");
	port.setRadius(3);
}



/**
 * create a new diagnostic step rectangle and add it to the canvas (including label and ports)
 **/
function createAndAddDiagnStep(name, x, y, id){
	var rect = createRect(name,"#cccccc" /*"#F6E3CE"*/, id);
	rect.createPort("input");
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddDiagnStep();
}

/**
 * create a new management item rectangle and add it to the canvas (including label and ports)
 **/
function createAndAddMng(name, x, y, id){
	var rect = createRect(name,"#cccccc" /*"#FFFF99"*/, id);
	rect.createPort("input");
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddMng();
}



function updatePreview(){
	/*var writer = new draw2d.io.png.Writer();	
	writer.marshal(my_canvas, function(png){
	    $("#preview_img").attr("src",png);
	    $("#preview_img").attr("width","50px");
	});*/
}

/*function displayJSON(){
    var writer = new draw2d.io.json.Writer();
    writer.marshal(app.view,function(json){
        $("#json").val(JSON.stringify(json, null, 2));
    });
}*/

function doSave(){
	alert("save");
}

/** delete item from concept map (called when deleting an item from the lists)
 * 
**/
function deleteItemFromCM(item){
    var cmd = new draw2d.command.CommandDelete(item);
    my_canvas.getCommandStack().execute(cmd);
}

/** delete item from concept map AND list (except if connection)
 * 
 * */
function deleteItem(id){	
	//deleteItemFromCM(item);
	if(id.startsWith("cmcnx")) //then we delete connection: 
	{
		sendAjax(id, doNothing, "delConnection", "");
	}
	else{//delete from list (except connections):
		var id = "sel"+item.getId().substring(2);	
		$("#"+id).remove();
	}
}
/**
 * we show the div tag with the list and lay it over the rectangle the user has clicked on. 
 * this is a workaround, because I was not able to control the events of the canvas and its childs.
 */
function openListForCM(x,y, clickedId){
	//positioning stuff:
	//selectedId = clickedId; //TODO: get the element last clicked from the canvas?
	canvasX = $("#canvas").offset().left;
	canvasY = $("#canvas").offset().top;
	xOfDialog = canvasX + x; 
	yOfDialog = canvasY + y;
	var dialogName = "dialogCMProb";
	//getting type of item the user has clicked on: 
	if(clickedId.startsWith("cmddx")) 
			dialogName = "dialogCMDDX";
		
	//display:
	$("#"+dialogName ).show();
	$("#"+dialogName ).offset({ top: yOfDialog, left: xOfDialog });
}
/**
 * We would have to update here the 
 * - figure: label, itemid(?), we do not have to change the id of the rectangle 
 * - list: change problem 
 * - connection
 * -> ajax: get problem with of the rectangles id and update the associated Problem object,
 * All ids can stay the same, so we do not have to update connections.
 * @param value
 * @param label
 */
function editProblemCM(newValue, label){
	//update rectangle with selectedId
	var selFigure = my_canvas.getSelection().getPrimary();
	selFigure.label.setText(label);	
	alert(selFigure.id.substring(7));
	//update list and trigger ajax call for saving:
	editProblem(selFigure.id.substring(7), newValue, label);
}
