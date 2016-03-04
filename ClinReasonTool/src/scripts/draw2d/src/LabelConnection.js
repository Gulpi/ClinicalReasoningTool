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
      this.label = new draw2d.shape.basic.Label({
          text:"...",
          color:"#0d0d0d",
          fontColor:"#0d0d0d",
          bgColor:"#f0f0f0",
          fontSize:"8pt"
      });
     
      // add the new decoration to the connection with a position locator.
      //
      this.add(this.label, new draw2d.layout.locator.ManhattanMidpointLocator());
      
      // Register a label editor with a dialog
      //
      this.label.installEditor(new draw2d.ui.LabelInplaceEditor());
      
      this.attr({
          router:new draw2d.layout.connection.InteractiveManhattanConnectionRouter(),
          outlineStroke:1,
          outlineColor:"#303030",
          stroke:2,
          color:"#00a8f0",
          radius:4
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
               switch(key){
               case "red":
                   this.setColor('#f3546a');
                   break;
               case "green":
                   this.setColor('#b9dd69');
                   break;
               case "blue":
                   this.setColor('#00A8F0');
                   break;
               case "delete":
                   // without undo/redo support
              //     this.getCanvas().remove(this);
                   
                   // with undo/redo support
                   var cmd = new draw2d.command.CommandDelete(this);
                   this.getCanvas().getCommandStack().execute(cmd);
               default:
                   break;
               }
            
            },this),
            x:x,
            y:y,
            items: 
            {
                "red":    {name: "Red", icon: "edit"},
                "green":  {name: "Green", icon: "cut"},
                "blue":   {name: "Blue", icon: "copy"},
                "sep1":   "---------",
                "delete": {name: "Delete", icon: "delete"}
            }
        });
   }
});
