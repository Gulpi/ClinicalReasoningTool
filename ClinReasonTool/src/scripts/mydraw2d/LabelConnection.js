/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label which sticks in the middle of the connection. Also has a contextmenu.
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var LabelConnection= draw2d.Connection.extend({
    
    init:function(attr)
    {
      this._super(attr);
    
      // Create any Draw2D figure as decoration for the connection
      //
     /* this.label = new draw2d.shape.basic.Label({
          text:"...",
          color:"#0d0d0d",
          fontColor:"#0d0d0d",
          bgColor:"#cccccc",
          fontSize:"8pt"
      });*/
     
      // add the new decoration to the connection with a position locator.
      //
    //  this.add(this.label, new draw2d.layout.locator.ManhattanMidpointLocator());
      
      // Register a label editor with a dialog
      //
     // this.label.installEditor(new draw2d.ui.LabelInplaceEditor());
      
      this.attr({
          router:new draw2d.layout.connection.InteractiveManhattanConnectionRouter(),
          outlineStroke:1,
          outlineColor:"#000000",
          stroke:1,
          color:"#000000",
          radius:2
      });
    },

    /**
     * @method
     * called by the framework if the figure should show the contextmenu.</br>
     * The strategy to show the context menu depends on the plattform. Either loooong press or
     * right click with the mouse.
     * 
     * @param {Number} x the x-coordinate to show the menu
     * @param {Number} y the y-coordinate to show the menu
     * @since 1.1.0
     */
    onContextMenu:function(x,y){
        $.contextMenu({
            selector: 'body', 
            events:
            {  
                hide:function(){ $.contextMenu( 'destroy' ); }
            },
            callback: $.proxy(function(key, options) 
            {
            	if(key=="delete"){
             	   var cmd = new draw2d.command.CommandDelete(this);
                   this.getCanvas().getCommandStack().execute(cmd);
            	   //alert(this.id);
                   delConnection(this.id)
            	}
            	else{
            		chgConnectionWeight(this.id, key);
            		this.setColor(getWeightToColor(key));
            	}
            		//switch(key){
           /*    case "5": //we could also change the thickness of the connections?
                   this.setColor('#009933');
                   chgConnectionWeight(this.id, key);
                   break;
               case "4":
                   this.setColor('#00e64d');
                   chgConnectionWeight(this.id, key);
                   break;

               case "3":
            	   this.setColor(getWeightToColor("3"));
            	   chgConnectionWeight(this.id, key);
            	   break;
            	
               case "delete":                   
            	   //this.getCanvas().remove(this); // without undo/redo support
                   
                   // with undo/redo support:

               default:
                   break;
               }*/
            
            },this),
            x:x,
            y:y,
            items: 
            {
                "5":  {name: "highly related" /*, icon: "edit"*/},
                "4":  {name: "somewhat related", icon: "cut"},
                "3":  {name: "slightly related", icon: "cut"},
                "6":    {name: "speaks against", icon: "cut"},
                /*"blue":   {name: "Blue", icon: "copy"},*/
                "sep1":   "---------",
                "delete": {name: "Delete", icon: "delete"}
            }
        });
   }
});

function getWeightToColor(weight){
	switch(weight){
	case "6": return "#cccccc";
	case "5": return "#009933";
	case "4": return "#00e64d";
	case "3": return "#e6ffee";
	default: 
		return "#000000";
	}
}


