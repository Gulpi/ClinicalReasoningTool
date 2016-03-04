//the document to load....in this case a simple JSON Object
/* var jsonDocument = 
     [
	{    "type": "LabelRectangleHyp",    "id": "ca5a0316-89bd-f667-d73a-835a011ff082",    "x": 150,    "y": 200,    "width": 100,    "height": 80, 
	"userData": null,    "cssClass": "draw2d_shape_basic_Rectangle",    "bgColor": "#646464",    "color": "#1B1B1B",    "radius": 2,    "labels": [      
	{        "id": "cf8b5c92-c666-5411-2d46-3feedaf79aca",        "label": "Hypothese",        "locator": "draw2d.layout.locator.CenterLocator"      }    ]  }, 

     ];
*/

/* we create a rectangle & label with the basic settings like size, color,... */
function createRect(name, color, id){
	var rect = new LabelRectangle();
	//rect.setWidth(50);
	//rect.setHeight(22);
	rect.label.text=name;
	//rect.label.setBackgroundColor(color);
	rect.label.setBackgroundColor("#ffffff");
	//rect.bgColor= new draw2d.util.Color(color);
	rect.color= new draw2d.util.Color(color);
	rect.setId(id);
	
	return rect;
}

function createAndAddHyp(name, x, y, id){
	var rect = createRect(name,/*"#B7CDDC"*/ "#cccccc", id/*,"990000"*/); //new draw2d.shape.basic.Rectangle();
	//alert(rect.getId());
	rect.createPort("input");
	rect.createPort("output");
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddHyp(); //we have to re-init this here, because we have created a clone!!!
}

function createAndAddFind(name, x, y, id){
	 var rect = createRect(name,"#cccccc"/*"#99CC99"*/,id);//new draw2d.shape.basic.Rectangle();
	 rect.createPort("output");
	 rect.setBackgroundColor("#ffffff");
	 my_canvas.add(rect, x, y);	 
	 initAddFind();
}

function createAndAddDiagnStep(name, x, y, id){
	var rect = createRect(name,"#cccccc" /*"#F6E3CE"*/, id);
	rect.createPort("input");
	rect.setBackgroundColor("#ffffff");
	my_canvas.add(rect, x, y);	
	initAddDiagnStep();
}

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

/* delete item from concept map*/
function deleteItemFromCM(item){
    var cmd = new draw2d.command.CommandDelete(item);
    my_canvas.getCommandStack().execute(cmd);
}

/* delete item from concept map AND list*/
function deleteItem(item){	
	deleteItemFromCM(item);
	//delete from list:
	var id = "sel"+item.getId().substring(2);	
	$("#"+id).remove();
	
	
}
