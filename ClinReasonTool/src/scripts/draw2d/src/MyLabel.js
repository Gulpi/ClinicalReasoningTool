/**
 * @class example.connection_labeledit.LabelConnection
 * 
 * A simple Connection with a label which sticks in the middle of the connection..
 *
 * @author Andreas Herz
 * @extend draw2d.Connection
 */
var MyLabel= draw2d.shape.basic.Label.extend({ //label for all rects except DDX because we have the extra contextMenu...
    
    init:function(attr)
    {
    	  this._super({text:"I'm a Label", color:"#0d0d0d", fontColor:"#0d0d0d"});
    },
    
    
onDoubleClick:function(emitter){ //open the select box to change label?
    	if(this.parent.isResizeable()){
    		openListForCM(this.parent.x, this.parent.y, this.parent.id); //we could also trigger this from the context menu with an edit button
    	}
    	else alert("expert");
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
        	handleContextMenuRect(this.parent, key);       
        },this),
        x:x,
        y:y,
        items: 
        {
            /*"mnm":    {name: "Must-not-miss"},
            "final":    {name: "Final DDX"},*/
            /*"green":  {name: "Green", icon: "cut"},
            "blue":   {name: "Blue", icon: "copy"},*/
            "sep1":   "---------",
            "delete": {name: "Delete", icon: "delete"}
        }
    });
}
});

var DDXLabel= draw2d.shape.basic.Label.extend({
    
    init:function(attr)
    {
    	  this._super({text:"I'm a Label", color:"#0d0d0d", fontColor:"#0d0d0d"});
    },
    
    
onDoubleClick:function(emitter){ //open the select box to change label?
    	if(this.parent.isResizeable()){
    		openListForCM(this.parent.x, this.parent.y, this.parent.id); //we could also trigger this from the context menu with an edit button
    	}
    	else alert("expert");
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
        	handleContextMenuRect(this.parent, key);       
        },this),
        x:x,
        y:y,
        items: 
        {
            "mnm":    {name: "Must-not-miss"},
            "final":    {name: "Final DDX"},
            /*"green":  {name: "Green", icon: "cut"},
            "blue":   {name: "Blue", icon: "copy"},*/
            "sep1":   "---------",
            "delete": {name: "Delete", icon: "delete"}
        }
    });
}
});

