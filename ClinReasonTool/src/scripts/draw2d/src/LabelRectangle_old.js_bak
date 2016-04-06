/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label wehich sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var LabelRectangleHyp= draw2d.shape.basic.Rectangle.extend({
    
    init:function(width, height, label)
    {
    	//alert("lable= " + label);
        if(width==undefined) width=80;
        if(height==undefined) height=80;
    	//this._super(width, height, label);
       // this.width = 100;
    
      // Create any Draw2D figure as decoration for the connection
      //
        var label = new draw2d.shape.basic.Label();
        label.text = "hallo";
        //this.add(label);
       // this.add(new draw2d.shape.basic.Label({text:"Test Label"}), new draw2d.layout.locator.TopLocator());
        //this.add(label, new draw2d.layout.locator.CenterLocator(this));
      //if(label==null || label=="" || label=="undefined"){
    	 // this.label = new draw2d.shape.basic.Label("hallo");
     // }
    	 
      //else this.label = new draw2d.shape.basic.Label("hallo2");
      //this.label.setColor("#dddddd");
      //this.label.setFontColor("#0d0d0d");
      this.setBackgroundColor("#dddddd"); /* color for dragged hyps */
      this.createPort("input");
      this.createPort("output");
      // add the new decoration to the connection with a position locator.
      //
      //this.addFigure(this.label, new draw2d.layout.locator.CenterLocator(this));
      
      this.label.installEditor(new draw2d.ui.LabelInplaceEditor());
    },


/**
 * @method 
 * Return an objects with all important attributes for XML or JSON serialization
 * 
 * @returns {Object}
 */
getPersistentAttributes : function()
{
    var memento = this._super();
    memento.type="LabelRectangleHyp";
    // add all decorations to the memento 
    //
    memento.labels = [];
    this.children.each(function(i,e){
        memento.labels.push({
            id:e.figure.getId(),
            label:e.figure.getText(),
            locator:e.locator.NAME
        });
    });

    return memento;
},
setPersistentAttributes : function(memento)
{
    this._super(memento);
    //memento.type="HYPO";
    // remove all decorations created in the constructor of this element
    //
    this.resetChildren();
    
    // and restore all children of the JSON document instead.
    //
    $.each(memento.labels, $.proxy(function(i,e){
        var label = new draw2d.shape.basic.Label(e.label);
        var locator =  eval("new "+e.locator+"()");
        locator.setParent(this);
        this.addFigure(label, locator);
    },this));
}
});

/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label which sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var LabelRectangleFind= draw2d.shape.basic.Rectangle.extend({
    
    init:function(width, height, label)
    {
      if(width==undefined) width=80;
      if(height==undefined) height=80;
      this._super(width, height, label);
      
      // Create any Draw2D figure as decoration for the connection
      //
      if(label==null || label=="" || label=="undefined"){
    	  this.label = new draw2d.shape.basic.Label(findLabel);
      }
      else this.label = new draw2d.shape.basic.Label(label); 
      //this.label.setColor("#0d0d0d");
      this.label.setFontColor("#006699");
      this.createPort("output");
      this.label.setColor("#B7CDDC"); 
      this.setBackgroundColor("#B7CDDC"); /* color for dragged hyps */
      // add the new decoration to the connection with a position locator.
      //
      this.addFigure(this.label, new draw2d.layout.locator.CenterLocator(this));
      
      this.label.installEditor(new draw2d.ui.LabelInplaceEditor());
    },

    /**
     * @method 
     * Return an objects with all important attributes for XML or JSON serialization
     * 
     * @returns {Object}
     */
    getPersistentAttributes : function()
    {
        var memento = this._super();
        memento.type="LabelRectangleFind";
        // add all decorations to the memento 
        //
        memento.labels = [];
        this.children.each(function(i,e){
            memento.labels.push({
                id:e.figure.getId(),
                label:e.figure.getText(),
                locator:e.locator.NAME
            });
        });

        return memento;
    },
    /**
     * @method 
     * Read all attributes from the serialized properties and transfer them into the shape.
     * 
     * @param {Object} memento
     * @returns 
     */
    setPersistentAttributes : function(memento)
    {
        this._super(memento);
       // memento.type="FIND";
        // remove all decorations created in the constructor of this element
        //
        this.resetChildren();
        
        // and restore all children of the JSON document instead.
        //
        $.each(memento.labels, $.proxy(function(i,e){
            var label = new draw2d.shape.basic.Label(e.label);
            var locator =  eval("new "+e.locator+"()");
            locator.setParent(this);
            this.addFigure(label, locator);
        },this));
    }
});

/**
 * @class MyConnection
 * 
 * A simple Connection with a label wehich sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var MyConnection= draw2d.Connection.extend({
    
    init:function()
    {
      this._super();
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
                   this.setColor("ff0000");
                   break;
               case "green":
                   this.setColor("00ff00");
                   break;
               case "blue":
                   this.setColor("0000ff");
                   break;
               /*case "delete":
                   // without undo/redo support
              //     this.getCanvas().removeFigure(this);
                   
                   // with undo/redo support
                   var cmd = new draw2d.command.CommandDelete(this);
                   this.getCanvas().getCommandStack().execute(cmd);*/
               default:
                   break;
               }
            
            },this),
            x:x,
            y:y,
            items: 
            {
                "red":    {name: "Red", icon: "red"},
                "green":  {name: "Green", icon: "green"},
                "blue":   {name: "Blue", icon: "blue"},
                /*"sep1":   "---------",
                "delete": {name: "Delete", icon: "delete"},*/
            }
        });
   }


});



