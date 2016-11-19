


function printBarChart(data, chartName, title, isLarge){
	if(isLarge) $("#chartlarge").html(""); 
	var data1 = [];
	//var isLarge = width>200; //otherwise thumbnail...
	data1[0] = {"key": chartLabelMe , nonStackable: 1 ,"values": data};
	//data1[1] = {key: chartLabelPeer , nonStackable: 1, values: peerdata};
	//data: { x: "1212", y: 52 }
   nv.addGraph({
        generate: function() {
            var chart = nv.models.discreteBarChart()
            	.staggerLabels(false)
            	.showValues(false)
            	.duration(1)
            ;

            chart.yAxis.tickFormat(d3.format(''));
            if(isLarge) chart.yAxis.ticks(10, "%");
            chart.margin({"left":15,"right":5,"top":5,"bottom":15});
           /* if(isLarge)*/{
            	chart.tooltip.enabled(true);
            
	            chart.tooltip.contentGenerator(function(obj) {
	            	 return "<div class=\"ui-tooltip\">"+obj.data.title+"</div>";
	            });
            }
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

/*function printGroupedBarChart(data, peerdata, height, width, chartName, title){
	
	printGroupedBarChart(data, peerdata, height, width, chartName, title, true);
}*/


function printBarLineComboChart(data, peerdata, chartName, title){
	$("#chartlarge").html("");
	var testdata = [];
	testdata[0] = {"key": chartLabelMe ,"values": data};
	testdata[1] = {"key": chartLabelPeer,"values": peerdata};

	testdata[0].type = "bar";
    testdata[0].yAxis = 1;
    testdata[1].type = "line";
    testdata[1].yAxis = 1;
                var chart;
                nv.addGraph(function() {
                    chart = nv.models.multiChart()
                    .margin({"left":15,"right":5,"top":5,"bottom":15})
                    .color(d3.scale.category10().range());

                    chart.yAxis1.tickFormat(d3.format(''));
                    //chart.xAxis.tickFormat('');
                    chart.yAxis1.ticks(10, "%");	
                    chart.tooltip.enabled(true);
                    chart.tooltip.contentGenerator(function(obj) {
                    	if(obj.data)
                    		return "<div class=\"ui-tooltip\">"+obj.data.title+"</div>";
                    });

                chart.yDomain1([0,100]);
                d3.select('#chartlarge')
                    .datum(testdata)
                  .transition().duration(0).call(chart);

                return chart;
                });
}

function printBarLineComboChartTwoLines(data, orgdata, peerdata, chartId, title, showLeg){
	$("#chartlarge").html("");
	var testdata = [];
	testdata[0] = {"key": chartLabelMe ,"values": data};
	testdata[1] = {"key": chartLabelPeer,"values": peerdata};
	testdata[2] = {"key": chartLabelMe,"values": orgdata};

	testdata[0].type = "bar";
    testdata[0].yAxis = 1;
    testdata[1].type = "line";
    testdata[1].yAxis = 1;
    testdata[2].type = "line";
    testdata[2].yAxis = 1;
                var chart;
                nv.addGraph(function() {
                    chart = nv.models.multiChart()
                    .margin({"left":25,"right":5,"top":5,"bottom":15})
                    .color(d3.scale.category10().range())
                    .showLegend(showLeg);

                    chart.yAxis1.tickFormat(d3.format(''));
                    //chart.xAxis.tickFormat('');
                    chart.yAxis1.ticks(10, "%");	
                    chart.tooltip.enabled(true);
                    chart.tooltip.contentGenerator(function(obj) {
                    	if(obj.data)
                    		return "<div class=\"ui-tooltip\">"+obj.data.title+"</div>";
                    });

                chart.yDomain1([0,100]);
                d3.select('#'+chartId)
                    .datum(testdata)
                  .transition().duration(0).call(chart);

                return chart;
                });
}
/**
 * bars are the learner's scores, avg peer score as line
 */
/*function printBarLineComboChartOld(data, peerdata, chartName, title){
	var data1 = [];
	data1[0] = {"key": chartLabelMe , "bar":true ,"values": data};
	data1[1] = {"key": chartLabelPeer , "values": peerdata};

    var testdata = [
                    {
                        "key" : chartLabelMe ,
                        "bar": true,
                        "values" : peerdata
                    },
                    {
                        "key" : chartLabelPeer ,
                        "values" : data
                    }
                ].map(function(series) {
                        series.values = series.values.map(function(d) { return {x: d[0], y: d[1] } });
                        return series;
                    });
	nv.addGraph(function() {
	    var chart = nv.models.linePlusBarChart()
	      .margin({top: 30, right: 60, bottom: 50, left: 70})
	      .x(function(d,i) { return i })
	      .y(function(d) { return d[1] })
	      .color(d3.scale.category10().range());

	      chart.xAxis.tickFormat(function(d) {
	        var dx = data[0].values[d] && data[0].values[d][0] || 0;
	        return d3.time.format('%x')(new Date(dx))
	      });

          chart.y1Axis.tickFormat(d3.format(''));
          chart.y1Axis.ticks(10, "%");	
          chart.y2Axis.tickFormat(d3.format(''));
          chart.y2Axis.ticks(10, "%");	

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

	     // nv.utils.windowResize(chart.update);

	      //return chart;
		 }
	  );

}*/

/*function printLineChart(data, peerdata, height, width, chartName, title){
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

		 // var myData = sinAndCos();   //You need data...

		  d3.select('#'+chartName)    //Select the <svg> element you want to render the chart in.   
		      .datum(data1)         //Populate the <svg> element with chart data...
		      .call(chart);          //Finally, render the chart!

		  //Update the chart when window resizes.
		  nv.utils.windowResize(function() { chart.update() });
		  return chart;
		});
}*/

/*
 * print learner performance and peer performance in one chart -> todo: if we have many cases this becomes complex 
 * and space-consuming, so it would be better to use a combined chart with lines for the peer performance and bars for 
 * the individual performance (or vv).
 */
/*function printGroupedBarChart(data, peerdata, chartName, title){
	$("#chartlarge").html(""); //remove();
	var isLegend = true;
	var data1 = [];//createDataArrs(data, peerdata); //[];
	data1[0] = {key: chartLabelMe , nonStackable: 1 ,values: data};
	data1[1] = {key: chartLabelPeer , nonStackable: 1, values: peerdata};
	
   nv.addGraph({
        generate: function() {
            var chart = nv.models.multiBarChart()
                //.width(width)
                //.height(height)
                .showLegend(isLegend)
                .showControls(false)
                .reduceXTicks(true)
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
        }
    });

}*/


