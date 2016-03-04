# Inherit from **draw2d.SetFigure**.

In general images are not the best way to create shapes for draw2d. The obvious defect is the bad zooming with the 
loose of render quality. But I use images in this examples just to show that it is possible.

The SetFigure is a good starting point if your element can be created with basic geometry shapes like circle, rectangle 
or simples path elements.

## Inherit from the draw2d.SetFigure
This is a good starting point for your first custom figure in Draw2D.

    BgColorImage = draw2d.SetFigure.extend({
	
	    NAME: "BgColorImage",
	    
	    init : function()
	    {
	        this._super();
	        this.setBackgroundColor("#f0f0ff");
	    },
	
	    createSet: function(){
	        var set = this._super();
	        
	        return set;
	    }
    });

draw2d is heavily based on John Resig JS Class implementation. Just google **john resig simple JavaScript class inheritance** and will 
you find a lot of further information.

## The NAME attribute
The NAME attribute is a Draw2D specific attribute and required if you serialize your data model to JSON. I need this information to restore your
class instances in the JsonReader. Please ensure that the NAME attribute contains the correct class name. We can use reflection if we
are in the Java world. Unfortunately this is not possible in JavaScript. I didn't found any other solution without declaring the class name twice.


## init()
The init method is the constructor for a new object. Well known behaviour in Java. Keep in mind that you **must** call the super method of the parent class.

This is a good moment to set some basic attributes like color, dimension or a selection policy.

## createSet()
Apparently the **createSet** method is the key function for the SetFigure. You can create a bunch of Raphael shapes and add them to the set. Draw2D calculates the bounding box
and handles the drag&drop, resize, hitTest, delete and all the other required stuff for the SVG management.

But at this point you have just an empty figure with *#f0f0ff* as background color. Now we add some shapes to the set.

    createSet: function(){
        var set = this._super();
        
        set.push( this.canvas.paper.image("HTML5.png",0,0,128,128));
        set.push( this.canvas.paper.circle(20, 20, 10));

        return set;
    }

The **this.canvas.paper** object is always accessible and valid within the **createSet** method. It's a RaphaelJS paper object.
We add an image and a circle to the set. 

## Live Example

 
    @example
    BgColorImage = draw2d.SetFigure.extend({
	
	    NAME: "BgColorImage",
	    
	    init : function()
	    {
	        this._super();
	        
	        this.setBackgroundColor("f0f0ff");
	        this.setStroke(1);
	        this.setDimension(128,128);
	    },
	
	    createSet: function(){
	        var set = this._super();
	        
	        set.push( this.canvas.paper.image("guides/extending_set_figure/HTML5.png",0,0,128,128));
            set.push( this.canvas.paper.circle(20, 20, 10));
	        return set;
	    }


    });
    
	var shape = new BgColorImage();
	canvas.add( shape,100,100);
