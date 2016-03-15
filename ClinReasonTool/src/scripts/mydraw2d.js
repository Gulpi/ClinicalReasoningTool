

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
	  	createAndAddHyp("neue hyp", canvasX, canvasY,"cmddx_-1");	  	
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

		  	createAndAddFind("new find", canvasX, canvasY, "cmprob_-1");	  	
		  	updatePreview();
		  }
	  });	
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
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddHyp(); //we have to re-init this here, because we have created a clone!!!
}

/**
 * create a new problem rectangle and add it to the canvas (including label and ports)
 **/
function createAndAddFind(name, x, y, id){
	 var rect = createRect(name,"#cccccc"/*"#99CC99"*/,id);//new draw2d.shape.basic.Rectangle();
	 rect.createPort("output");
	 rect.setBackgroundColor("#ffffff");
	 my_canvas.add(rect, x, y);	 
	 initAddFind();
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
