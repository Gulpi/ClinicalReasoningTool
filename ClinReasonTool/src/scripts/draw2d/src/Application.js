// declare the namespace for this example
var draw2d = {};

/**
 * 
 * The **GraphicalEditor** is responsible for layout and dialog handling.
 * 
 * @author Andreas Herz
 * @extends draw2d.ui.parts.GraphicalEditor
 */
draw2d.Application = Class.extend({
    NAME : "draw2d.Application",

    /**
     * @constructor
     * 
     */
    init : function() 
    {
        this.canvas = new draw2d.Canvas("canvas");
        this.toolbar = new draw2d.Toolbar("toolbar", this.canvas);
        this.properties = new draw2d.EventPane( this.canvas);
    }

});
