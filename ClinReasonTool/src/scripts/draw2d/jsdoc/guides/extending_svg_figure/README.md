# Inherit from **draw2d.SVGFigure**.

If you have some existing SVG code or if your prefere a SVG editor you can use this to create your custom figure. Keep in mind that the SVG import
is very limited. No &lt;g&gt; or other grouping elements or gradients are supported.

This code will not work with Draw2D. The reason for this is the **&lt;g&gt;** element. Remove the **&lt;g&gt;** and everything is fine
		<svg width="640" height="480" xmlns="http://www.w3.org/2000/svg">
		 <!-- Created with SVG-edit - http://svg-edit.googlecode.com/ -->
		 <g>
		  <title>Layer 1</title>
		  <rect id="svg_1" height="28" width="27" y="69" x="25" stroke-width="5" stroke="#000000" fill="#FF0000"/>
		 </g>
		</svg>

### Supported elements

 - rect
 - circle
 - ellipse
 - path
 - line
 - polyline
 - polygon
 - image
 - tspan
 - text
 
You can use this <a href="http://code.google.com/p/svg-edit/" target="_blank">Online SVG Editor</a> to paint your shape and export the SVG into Draw2D.


## Inherit from the draw2d.SVGFigure
Use copy SVG from the existing draw2d.shape.analog.OpAmp

    MySVGFigure = draw2d.SVGFigure.extend({
	
	    NAME: "MySVGFigure",
	    
	    init : function()
	    {
	        this._super();
	       
	    },
	
	      
	    getSVG: function(){
	         return '<svg xmlns="http://www.w3.org/2000/svg" version="1.1">'+
	                 '<path d="m8.2627,0l0,35.36035l31.23926,-17.76025l-31.23926,-17.60011l0,0l0,0.00001zm2.27832,27.36719l4.08105,0m-2.10449,-2.20703l0,4.27979m2.26367,-21.35938l-4.15918,0"  stroke="#1B1B1B" fill="none"/>'+
	                 '<line x1="0.53516"  y1="8"  x2="8.21191"  y2="8"  stroke="#010101"/>'+
	                 '<line x1="39.14941" y1="18" x2="45.81055" y2="18" stroke="#010101" />'+
	                 '<line x1="0.53516"  y1="27" x2="8.21191"  y2="27" stroke="#010101" />'+
	                '</svg>';
	    }
	    
    });


## The NAME attribute
The NAME attribute is a Draw2D specific attribute and required if you serialize your data model to JSON. I need this information to restore your
class instances in the JsonReader. Please ensure that the NAME attribute contains the correct class name. We can use reflection if we
are in the Java world. Unfortunately this is not possible in JavaScript. I didn't found any other solution without declaring the class name twice.


## init()
The init method is the constructor for a new object. Well known behaviour in Java. Keep in mind that you **must** call the super method of the parent class.
This is a good moment to set some basic attributes like color, dimension or a selection policy.

## getSVG()
Return a well formated SVG string in this method. And again - keep in mind that the SVGFigure import is currently very limited.


## Live Example

 
    @example
    MySVGFigure = draw2d.SVGFigure.extend({
	
	    NAME: "MySVGFigure",
	    
	    init : function()
	    {
	        this._super();
	       
	    },
	
	      
	    getSVG: function(){
	         return '<svg xmlns="http://www.w3.org/2000/svg" version="1.1">'+
	                 '<path d="m8.2627,0l0,35.36035l31.23926,-17.76025l-31.23926,-17.60011l0,0l0,0.00001zm2.27832,27.36719l4.08105,0m-2.10449,-2.20703l0,4.27979m2.26367,-21.35938l-4.15918,0"  stroke="#1B1B1B" fill="none"/>'+
	                 '<line x1="0.53516"  y1="8"  x2="8.21191"  y2="8"  stroke="#010101"/>'+
	                 '<line x1="39.14941" y1="18" x2="45.81055" y2="18" stroke="#010101" />'+
	                 '<line x1="0.53516"  y1="27" x2="8.21191"  y2="27" stroke="#010101" />'+
	                '</svg>';
	    }
	    
    });
    
	var shape = new MySVGFigure();
	canvas.add( shape,100,100);
