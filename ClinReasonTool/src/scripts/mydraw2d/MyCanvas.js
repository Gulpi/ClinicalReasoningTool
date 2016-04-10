/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label which sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var MyCanvas= draw2d.Canvas.extend({
 
    init:function(attr)
    {
      this._super(attr);
    },

/*onDrop:function( dropTarget, x, y, shiftKey, ctrlKey ){
	alert("drop");
}*/
    
   /* onClick:function(emitter){ //open the select box to change label?
    	//var editor = new draw2d.ui.LabelLMEditor();
    	$("#"+this.id).html("<input class=\"f_text\" id=\"inplaceeditor2\"></input>");
    	//this.label.editor.start(this.label);
        alert(this.id);
    },*/
	
});
