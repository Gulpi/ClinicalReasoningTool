/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label wehich sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var LabelRectangle= draw2d.shape.basic.Rectangle.extend({
    
    init:function(attr)
    {
    	this._super(attr);
    	this.setWidth(50);
    	this.setHeight(22);

		  // Create any Draw2D figure as decoration for the connection
		  //
		  this.label = new draw2d.shape.basic.Label({text:"I'm a Label", color:"#0d0d0d", fontColor:"#0d0d0d"});
		  this.label.setStroke(0);
		  this.label.setMinWidth(50);
		  this.label.setMinHeight(16);
		  this.label.setWidth(50);
		  this.label.setHeight(16);
		  //this.label.setOutlineColor("#006699")
		  //this.label.setX(0);
		  this.label.setFontSize(8);
		  
		  // add the new decoration to the connection with a position locator.
		  //
		  this.add(this.label, new draw2d.layout.locator.XYRelPortLocator(5,5)); //CenterLocator(this));
		  
		  this.label.installEditor(new draw2d.ui.LabelLMEditor());
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
           switch(key){
           case "red":
               this.setBackgroundColor('#f3546a');
               this.setColor('#f3546a');
               break;
          /* case "green":
               this.setColor('#b9dd69');
               break;
           case "blue":
               this.setColor('#00A8F0');
               break;*/
           case "delete":
               // without undo/redo support
          //     this.getCanvas().remove(this);
               
               // with undo/redo support
               /*var cmd = new draw2d.command.CommandDelete(this);
               this.getCanvas().getCommandStack().execute(cmd);*/
        	   deleteItem(this);
           default:
               break;
           }
        
        },this),
        x:x,
        y:y,
        items: 
        {
            "red":    {name: "Red", icon: "edit"},
            /*"green":  {name: "Green", icon: "cut"},
            "blue":   {name: "Blue", icon: "copy"},
            "sep1":   "---------",*/
            "delete": {name: "Delete", icon: "delete"}
        }
    });
}
});
