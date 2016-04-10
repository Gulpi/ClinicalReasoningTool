# Initialize Draw2D

A Hello world! application can be written as:

      @example preview small frame
      
      var label =  new draw2d.shape.basic.Label({text:"Double Click on me"});
      
      label.installEditor(new draw2d.ui.LabelEditor({
         // called after the value has been set to the LabelFigure
         onCommit: $.proxy(function(value){
             alert("new value set to:"+value);
         },this),
         // called if the user abort the operation
         onCancel: function(){
         }
      }));
      
      canvas.add(label,50,10);
