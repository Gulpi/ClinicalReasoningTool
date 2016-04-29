function printBarChar(svgHeight, svgWidth, data, chart, title){
	//var svgHeight = 400;
	//var svgWidth = 400;
	var maxValue = 1;
	var barSpacing = 1; // The amount of space you want to keep between the bars
	var padding = {
	    left: 50, right: 0,
	    top: 20, bottom: 20
	};

	//function animateBarsUp() {
	    var maxWidth = svgWidth - padding.left - padding.right;
	    var maxHeight = svgHeight - padding.top - padding.bottom;
	//}

	//Define your conversion functions
	var convert = {    
	    x: d3.scale.ordinal(),
	    y: d3.scale.linear()
	};

	//Define your axis
	var axis = {
	    x: d3.svg.axis().orient('bottom'),
	    y: d3.svg.axis()
	    .orient('left')
	    //.scale(y)
	    .ticks(10, "%")
	};

	// Define the conversion function for the axis points
	axis.x.scale(convert.x);
	axis.y.scale(convert.y);

	//Define the output range of your conversion functions
	convert.y.range([maxHeight, 0]);
	convert.x.rangeRoundBands([0, maxWidth]);

	convert.x.domain(data.map(function (d) {
	        return d.label;
	    })
	);
	convert.y.domain([0, maxValue]);


	//Setup the markup for your SVG
	var svg = d3.select('#'+chart)
	    .attr({
	        width: svgWidth,
	        height: svgHeight
	    });
	svg.append("text")
    .attr("x", (svgWidth / 2))             
    .attr("y", 20 )
    .attr("text-anchor", "middle")  
    .style("font-size", "10px") 
    .style("text-decoration", "none")  
    .text(title);
	
	// The group node that will contain all the other nodes
	// that render your chart
	var chart = svg.append('g')
	    .attr({
	        transform: function (d, i) {
	          return 'translate(' + padding.left + ',' + padding.top + ')';
	        }
	    });
	 
	chart.append('g') // Container for the axis
	.attr({
	    class: 'x axis',
	    transform: 'translate(0,' + maxHeight + ')'
	})
	.call(axis.x); // Insert an axis inside this node

	chart.append('g') // Container for the axis
	.attr({
	    class: 'y axis',
	    height: maxHeight
	})
	.call(axis.y) // Insert an axis inside this node
	.append("text")
    //.attr("transform", "rotate(-90)")
    .attr("y", 6)
    .attr("dy", ".71em")
    .style("text-anchor", "end")
    .text("Performance");
	
	var bars = chart
	.selectAll('g.bar-group')
	.data(data)
	.enter()
	.append('g') // Container for the each bar
	.attr({
	  transform: function (d, i) {
	    return 'translate(' + convert.x(d.label) + ', 0)';
	  },
	  class: 'bar-group'
	});

	bars.append('rect')
	.attr({
	    y: maxHeight,
	    height: 0,
	    width: function(d) {return convert.x.rangeBand(d) - 1;},
	    class: 'bar'
	})
	//.transition()
	//.duration(1500)
	.attr({
	y: function (d, i) {
	    return convert.y(d.value);
	},
	height: function (d, i) {
	    return maxHeight - convert.y(d.value);
	}
	});

}