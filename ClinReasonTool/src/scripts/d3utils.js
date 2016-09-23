


function printBarChart(data, height, width, chartName, title){
	var data1 = [];
	data1[0] = {key: chartLabelMe , nonStackable: 1 ,values: data};
	//data1[1] = {key: chartLabelPeer , nonStackable: 1, values: peerdata};
	//data: { x: "1212", y: 52 }
   nv.addGraph({
        generate: function() {
            var chart = nv.models.discreteBarChart()
            	.staggerLabels(true)
            	.showValues(true)
            	.duration(1)
            ;

            chart.yAxis.tickFormat(d3.format(''));
            chart.yAxis.ticks(10, "%");	
            chart.margin({"left":15,"right":5,"top":5,"bottom":15});
            chart.tooltip.enabled(true);
            chart.tooltip.contentGenerator(function(obj) {
            	// tooltips["1212_1"]
                return tooltips[obj.data.x];
            });
            chart.yDomain([0,100]);

            
            chart.dispatch.on('renderEnd', function(){
                console.log('Render Complete');
            });

            var svg = d3.select('#'+chartName).datum(data1);
            console.log('calling chart');
            svg.transition().duration(0).call(chart);

            return chart;
        }
    });

}

/*
 * print learner performance and peer performance in one chart -> todo: if we have many cases this becomes complex 
 * and space-consuming, so it would be better to use a combined chart with lines for the peer performance and bars for 
 * the individual performance (or vv).
 */
function printGroupedBarChart(data, peerdata, height, width, chartName, title){
	var data1 = [];
	data1[0] = {key: chartLabelMe , nonStackable: 1 ,values: data};
	data1[1] = {key: chartLabelPeer , nonStackable: 1, values: peerdata};
	
   nv.addGraph({
        generate: function() {
            var chart = nv.models.multiBarChart()
                .width(width)
                .height(height)
                .showLegend(true)
                .showControls(false)
                .reduceXTicks(true)
               /* .append("title").text(function(d) { 
                	
                }*/
                //.stacked(true)
                ;

            chart.yAxis.tickFormat(d3.format(''));
            chart.xAxis.tickFormat('');
            chart.yAxis.ticks(10, "%");	
            chart.margin({"left":15,"right":5,"top":5,"bottom":15});
            chart.tooltip.enabled(true);
            chart.tooltip.contentGenerator(function(obj) {
               return "<div class=\"ui-tooltip\">"+obj.data.title+"</div>";
            });
            
            chart.yDomain([0,100]);
            
            chart.dispatch.on('renderEnd', function(){
                console.log('Render Complete');
            });

            var svg = d3.select('#'+chartName).datum(data1);
            console.log('calling chart');
            svg.transition().duration(1).call(chart);

            return chart;
        }/*,
        callback: function(graph) {
            nv.utils.windowResize(function() {
                var width = nv.utils.windowSize().width;
                var height = nv.utils.windowSize().height;
                graph.width(width).height(height);

                d3.select('#'+chartName)
                    .attr('width', width)
                    .attr('height', height)
                    .transition().duration(0)
                    .call(graph);

            });
        }*/
    });

}


/**
 * bars are the learner's scores, avg peer score as line
 */
function printBarLineComboChart(data, peerdata, height, width, chartName, title){
	var data1 = [];
	data1[0] = {"key": chartLabelMe , "bar":true ,"values": data};
	data1[1] = {"key": chartLabelPeer , "values": peerdata};

	nv.addGraph(function() {
	    var chart = nv.models.linePlusBarChart()
	      .margin({top: 30, right: 60, bottom: 50, left: 70})
	      .x(function(d,i) { return i })
	      .y(function(d) { return d[1] })
	      .color(d3.scale.category10().range())
	      ;

	     /* chart.xAxis.tickFormat(function(d) {
	        var dx = data[0].values[d] && data[0].values[d][0] || 0;
	        return d3.time.format('%x')(new Date(dx))
	      });*/

	      /*chart.y1Axis
	          .tickFormat(d3.format(',f'));

	      chart.y2Axis
	          .tickFormat(function(d) { return '$' + d3.format(',f')(d) });*/
          chart.y1Axis.tickFormat(d3.format(''));
          chart.y1Axis.ticks(10, "%");	
          chart.margin({"left":15,"right":5,"top":5,"bottom":15});
          chart.tooltip.enabled(false)
          chart.yDomain([0,100]);

	      //chart.bars.forceY([0]);
          chart.dispatch.on('renderEnd', function(){
              console.log('Render Complete');
          });

          var svg = d3.select('#'+chartName)
          .datum(data1)
          .transition()
	      .duration(0)
	      .call(chart);
          console.log('calling chart');
          //svg.transition().duration(0).call(chart);

          return chart;
	    /*  d3.select('#chart svg')
	        .datum(data)
	        .transition()
	        .duration(0)
	        .call(chart);*/

	     // nv.utils.windowResize(chart.update);

	      //return chart;
		 }
	  );

}

function printLineChart(data, peerdata, height, width, chartName, title){
	var data1 = [];
	data1[0] = {key: chartLabelMe ,values: data};
	data1[1] = {key: chartLabelPeer , values: peerdata};
	
	nv.addGraph(function() {
		  var chart = nv.models.lineChart()
		                .margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
		                //.useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline!
		                .transitionDuration(0)  //how fast do you want the lines to transition?
		                .showLegend(true)       //Show the legend, allowing users to turn on/off line series.
		                .showYAxis(true)        //Show the y-axis
		                .showXAxis(true)        //Show the x-axis
		  ;

		  chart.xAxis     //Chart x-axis settings
		      .axisLabel('VPs')
		      .tickFormat(d3.format(',r'));

          chart.yAxis.tickFormat(d3.format(''));
          chart.yAxis.ticks(10, "%");	

		  /* Done setting the chart up? Time to render it!*/
		 // var myData = sinAndCos();   //You need data...

		  d3.select('#'+chartName)    //Select the <svg> element you want to render the chart in.   
		      .datum(data1)         //Populate the <svg> element with chart data...
		      .call(chart);          //Finally, render the chart!

		  //Update the chart when window resizes.
		  nv.utils.windowResize(function() { chart.update() });
		  return chart;
		});
}



/*function printBarChart(svgHeight, svgWidth, data, chart, title){
	
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
    .attr("transform", "rotate(-90)")
    .attr("y", 6)
    .attr("dy", ".71em")
    .style("text-anchor", "end")
    //.text("Performance");
	
	var bars = chart
	.selectAll('g.bar-group')
	.data(data)
	.enter()
	.append('g') // Container for the each bar
	.attr({
	  transform: function (d, i) {
	    return 'translate(' + convert.x(d.x) + ', 0)';
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
		    return convert.y(d.y);
		},
		height: function (d, i) {
		    return maxHeight - convert.y(d.y);
		}
	});
	
	//printGroupedBarChart();
}*/

/* exemplary charts for instructors*/
/*function testPrint(){
	var ddxitemdata = [];
	var arr =["Asthma", "COPD", "Pneumonia", "acute Bronchitis", "Lung cancer"];
	var arr2=[0.85, 0.82, 0.76, 0.58, 0.49];
	for(i=0; i<5; i++){
		ddxitemdata.push({ label: arr[i], value:  arr2[i]});
	}
	//printBarChar(150, 250, ddxitemdata, "ddxitemschart","Most selected differentials ");

	var probitemdata = [];
	var arr =["Cough", "Smoking", "Wheezing", "Hemoptysis", "Dyspnea"];
	var arr2=[0.92, 0.87, 0.76, 0.75, 0.57];
	for(i=0; i<5; i++){
		probitemdata.push({ label: arr[i], value:  arr2[i]});
	}
	printBarChar(150, 250, probitemdata, "probitemschart","Most selected problems ");

	
	var overalldata = [];
	var arr =["VP xy ", "VP ab", "VP cd", "VP xx", "VP yy"];
	var arr2=[0.76, 0.77, 0.65, 0.87, 0.57];
	for(i=0; i<5; i++){
		overalldata.push({ label: arr[i], value:  arr2[i]});
	}
	printBarChar(150, 250, overalldata, "overallchart","Overall Score");
}*/


/* examplary charts for learner*/ 
/*function testPrint2(width){
	var overallproblemData = [];
	var arr =["VP xy ", "VP ab", "VP cd", "VP xx", "VP yy"];
	var arr2=[0.76, 0.77, 0.65, 0.87, 0.57];
	for(i=0; i<5; i++){
		overallproblemData.push({ label: arr[i], value:  arr2[i]});
	}
	printBarChar(150, width, overallproblemData, "overallProbchart","Overall Problems Scores");

	var overallDDXData = [];
	var arr =["VP xy ", "VP ab", "VP cd", "VP xx", "VP yy"];
	var arr2=[0.57, 0.50, 0.65, 0.87, 0.89];
	for(i=0; i<5; i++){
		overallDDXData.push({ label: arr[i], value:  arr2[i]});
	}
	printBarChar(150, width, overallDDXData, "overallDDXchart","Overall DDX Scores");
}*/

