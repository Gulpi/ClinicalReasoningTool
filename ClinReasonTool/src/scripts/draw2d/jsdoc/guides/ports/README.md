# Ports


A port is an object that is used to establish a connection between a node and 
a connector. The connection port can be placed with a `draw2d.layout.locator.PortLocator` anywhere within a node or at the edge of one.



### Adding a port to a node via external API call
It is possible to add ports during runtime to any kind of node. Below is a simple example
how to do this. The disadvantage of this method is, that the ports are not restored if the
shape is saved via the JSON-Writer/Reader.


     @example small frame      
      
     // create a simple rectangle
     //
     var rect =  new draw2d.shape.basic.Rectangle(50,80);
     
     // a locator is responsible to calculate the position of an
     // port.
     var leftLocator  = new draw2d.layout.locator.InputPortLocator();
     var rightLocator = new draw2d.layout.locator.OutputPortLocator();

     // create a port of the type "hybrid" with the given locator
     // possible port types are: input, output and hybrid
     //
     rect.createPort("hybrid",leftLocator);
     rect.createPort("hybrid",leftLocator);
     rect.createPort("hybrid",rightLocator);

     // and add this to the draw2d canvas
     //
     canvas.add(rect,100,10);
     
     

### Create a new figure and add the ports in the constructor
The prefered way is to create a new shape with all attributes and add the ports in the `init` method. Now the JSON reader creates the shape and everything is setup well.

     @example small frame
     var LEFT_LOCATOR  = new draw2d.layout.locator.InputPortLocator();
     var RIGHT_LOCATOR = new draw2d.layout.locator.OutputPortLocator();
     
         
     var MyShape = draw2d.shape.basic.Rectangle.extend({

        NAME : "MyShape",   // required for JSON I/O

	    init : function()
        {
            this._super(50,80);

            this.createPort("output", RIGHT_LOCATOR);
            this.createPort("input", LEFT_LOCATOR);
            this.createPort("input", LEFT_LOCATOR);
        }
     });
     
     // create my special shape
     //
     var shape =  new MyShape();
     
     // and add this to the draw2d canvas
     //
     canvas.add(shape,100,10);
     
