# Required Imports

## jQuery

jQuery 1.8.x or higher. Draw2D has been tested with everything up to 1.10.2.

    <SCRIPT src="./lib/jquery-1.10.2.min.js"></SCRIPT>

jQuery plugins for inplace editing, contextmenu and touch support on the iPad or any other touch enabled devices.

    <SCRIPT src="./lib/jquery.autoresize.js"></SCRIPT>
    <SCRIPT src="./lib/jquery-touch_punch.js"></SCRIPT>
    <SCRIPT src="./lib/jquery.contextmenu.js"></SCRIPT> 

Shifty is an excellent animation solution for scenarios in which jQuery's animate() function is either unavailable or 
insufficient. It's particularly effective for creating `<canvas>` animations.

    <SCRIPT src="./lib/shifty.js"></SCRIPT>

## Raphael.js. 
Unfortunately the latest version of raphael didn't fix some bug. In this case I did it and patch the version in the Draw2D. 
You can't replace the provided raphael version with the common version without lost of functionality. Grep from ``FREEGROUP`` to
find all patches in the raphael.js file

    <SCRIPT src="./lib/raphael.js"></SCRIPT>
    
    
A class to parse color values    

    <SCRIPT src="./lib/rgbcolor.js"></SCRIPT>
   
Convert canvas to PNG images
    
    <SCRIPT src="./lib/canvg.js"></SCRIPT>

## Class.js
Simple JavaScript Inheritance by John Resig

    <SCRIPT src="./lib/Class.js"></SCRIPT>
    
## JSON
This file creates a global JSON object containing two methods: ``stringify`` and ``parse``    
    
    <SCRIPT src="./lib/json2.js"></SCRIPT>
    
## Connection routing
Algorithm for shortes path finding.
    
    <SCRIPT src="./lib/pathfinding-browser.min.js"></SCRIPT>

## Draw2D
And last but not least the draw2d lib itself

    <SCRIPT src="./src/draw2d.js"></SCRIPT>
