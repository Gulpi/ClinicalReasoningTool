
draw2d.EventPane = Class.extend({
	
	init:function( canvas)
	{
	    var log= function(msg){
	        $("#events").prepend($("<div>"+new Date().toTimeString().replace(/.*(\d{2}:\d{2}:\d{2}).*/, "$1")+" - "+msg+"</div>"));
	    };
	    
		canvas.on("figure:add", function(emitter, event){
            log("Figure added");
        });
        
        canvas.on("figure:remove", function(emitter, event){
            log("Figure removed");
        });

        canvas.on("select", function(emitter, event){
            log("Figure selected: "+event);
        });
        
        // use figure.on("dblclick",..) if want determine the related figure...
        
        canvas.on("dblclick", function(emitter, event){
            log("double click: "+JSON.stringify(event));
        });

        canvas.on("click", function(emitter, event){
            log("click: "+JSON.stringify(event));
        });

        canvas.on("contextmenu", function(emitter, event){
            log("Context Menu: "+JSON.stringify(event));
        });
       // alert(log);
        },
    
});