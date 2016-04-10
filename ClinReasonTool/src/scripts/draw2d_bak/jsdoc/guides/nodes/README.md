# Nodes

Nodes are graphical objects that can be arranged in a diagram page. These nodes can 
be added to the diagram page either programmatically, via JSON or during run time or by [dragging them from a palette](#!/example/buildin_commandstack)

The nodes can be one of the predefined figures provided or by inherit an existing one and override the {@link draw2d.Figure#createShapeElement createShapeElement} method.

You can also design these shapes using a <a href="https://www.draw2d.org/designer/" target="_blank">symbol designer</a> utility (source code included in the download package). 


### Adding a node via API call

     @example small frame
     
     // create a simple rectangle
     //
     var rect =  new draw2d.shape.basic.Rectangle();
     
     // and add this to the draw2d canvas
     //
     canvas.add(rect,100,10);
     
     


### Adding a node via JSON import

     @example small frame
     // the json document to import. a JSON document can contain more than one node
     // and connections
     var json = [
     {
        "type": "draw2d.shape.basic.Rectangle",
        "x": 100,
        "y": 10,
        "width": 50,
        "height": 50,
        "radius": 2
      }
     ]
     // unmarshal the JSON document into the canvas
     // (load)
     var reader = new draw2d.io.json.Reader();
     reader.unmarshal(canvas, json);


### Composing nodes

Figures can be composed via a parent-child relationship. Every figure has a rectangular bounds inside which it, and its children, paint. A `draw2d.layout.locator.Locator` must be used to place children. 

     @example small frame
     // create a simple rectangle
     //
     var rect =  new draw2d.shape.basic.Rectangle(100,80);
     
     // add a child to the rectangle
     rect.add(new draw2d.shape.basic.Label("Label"), new draw2d.layout.locator.CenterLocator(rect));
     
     // and add this to the draw2d canvas
     //
     canvas.add(rect,100,10);

