/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label which sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var LabelRectangle= draw2d.shape.basic.Rectangle.extend({
    
    init:function(attr)
    {
    	//this._super(attr);
    	this._super({width: 65,height: 22 });
    	//this.attr("exp","1")
    	//(name,/*"#B7CDDC"*/ "#cccccc", id/*,"990000"*/);
    	//this.setWidth(55);
    	//this.setId(id);
    	//this.setHeight(22);
    	this.setCssClass("myrect");
    
		  // Create any Draw2D figure as decoration for the connection
		  //
		  this.label = new MyLabel({text:"I'm a Label", color:"#0d0d0d", fontColor:"#0d0d0d"});
		  this.label.setStroke(0);
		  this.label.setMinHeight(16);
		  this.label.setHeight(16);
		  this.label.setFontSize(8);
		  /*this.attr({
			   "userData": "hallo"
			 });*/
		  
		  // add the new decoration to the connection with a position locator.
		  //
		  this.add(this.label, new draw2d.layout.locator.XYRelPortLocator(5,5)); //CenterLocator(this));
		  //this.label.setCssClass("mylabel");
		 //this.label.installEditor(new draw2d.ui.LabelLMEditor());
    },
    
    
   /* onDoubleClick:function(){ //open the select box to change label?
    	if(this.isResizeable()){
    		//alert("non expert")
    		openListForCM(this.x, this.y, this.id); //we could also trigger this from the context menu with an edit button
    	}
    	
    },*/

    onMouseEnter( )
    {
    	xDragStart = this.x;
    	yDragStart = this.y;
    },
    onDragEnd:function(emitter){
    	if(this.isResizeable()){ //we do not save if expert items are moved!
    		handleRectDrop(this);
    	}
    },
onContextMenu:function(x,y){
    $.contextMenu({
        selector: 'body', 
        events:
        {  
            hide:function(){ $.contextMenu( 'destroy' ); }
        },
        callback: $.proxy(function(key, options) 
        {
        	handleContextMenuRect(this, key);        
        },this),
        x:x,
        y:y,
        items: 
        {
        	
           // "mnm":    {name: "Must-not-miss"},
           // "final":    {name: "Final DDX"},
            //"green":  {name: "Green", icon: "cut"},
           // "blue":   {name: "Blue", icon: "copy"},
           // "sep1":   "---------",
            "delete": {name: "Delete", icon: "delete"}
        }
    });
}
});

