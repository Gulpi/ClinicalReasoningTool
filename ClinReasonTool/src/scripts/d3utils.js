


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

function drawSimpleMultipleDonutChart(element, percent, percent2, width, height, text_y, duration, transition, startAng, endAng, inFormat) {
	var myWidthCheck  = width!=null; 
	width = (width!=null && typeof width !== 'undefined') ? width : d3.select(element).attr("width");
	height = (height!=null && typeof height !== 'undefined') ? height : d3.select(element).attr("height");
	if (!width) {
		width = d3.select(element).node().getBoundingClientRect().width;
	}
	if (!height) {
		height = d3.select(element).node().getBoundingClientRect().height;
	}
	text_y = typeof text_y !== 'undefined' ? text_y : "-.10em";
	percent = parseInt(percent);
	if (percent < 0) {
		return;
	}
	var has_percent2 = (percent2>=0);
	percent2 = parseInt(percent2);
	if (percent2 < 0) {
		percent2 = 0;
		//return;
	}
	
	
	
	var dataset = {
	    lower: d3utils_calcPercent(100),
	    upper: d3utils_calcPercent(100-percent)
	  };
	var dataset2 = {
			    lower: d3utils_calcPercent(100),
			    upper: d3utils_calcPercent(100-percent2)
			  };
	
	var radius = Math.min(width, height) / 2;
	var radius2 = Math.min(width, height) / 4;
	var transformX = width / 2;
	var transformY = height / 2;
	
	var my_startAng = 1;
	var my_endAng = -1;
	if (startAng) {
		my_startAng = startAng;
	}
	if (endAng) {
		my_endAng = endAng;
	}

	var pie = d3.layout.pie().sort(null)
		.startAngle(my_startAng * Math.PI)
		.endAngle(my_endAng * Math.PI);
	
	var isPercentFormat = false;
	if (!inFormat) {
		inFormat = ".0%";
	}
	if (inFormat && inFormat.indexOf("%") != -1) {
		isPercentFormat = true;
	}
	var format = d3.format(inFormat);
	var arc_width = 20;
	
	
	
	  var tmp = d3.select(element).attr("radius");
	  if (tmp) {
		  radius = parseInt(tmp);
	  }
	  tmp = d3.select(element).attr("radius2");
	  if (tmp) {
		  radius2 = parseInt(tmp);
	  }
	  tmp = d3.select(element).attr("arc_width");
	  if (tmp) {
		  arc_width = parseInt(tmp);
	  }
	  
	  if (!has_percent2) {
			arc_width = 25;
		}
	  
	var arc = d3.svg.arc()
	    .innerRadius(radius - arc_width)
	    .outerRadius(radius);
	
	var arc2 = d3.svg.arc()
    .innerRadius(radius2 - arc_width)
    .outerRadius(radius2);

	var svg = d3.select(element).append("svg")
	    .attr("width", width)
	    .attr("height", height)
	    .append("g")
	    .attr("transform", "translate(" + transformX + "," + transformY + ")");

	var path = svg.selectAll("path")
	    .data(pie(dataset.lower))
	    .enter().append("path")
	    .attr("class", function(d, i) { return "color" + i })
	    .attr("d", arc)
	    .each(function(d) { this._current = d; });
	
	var path2 = null;
	if (has_percent2) {
		path2 = svg.selectAll("g").append("g")
    	.attr("transform", "translate(" + transformX + "," + transformY + ")")
		.data(pie(dataset2.lower))
		.enter().append("path")
		.attr("class", function(d, i) { return "color2" + i })
		.attr("d", arc2)
		.each(function(d) { this._current = d; }); // store the initial values
	}
	
	var text = svg.append("text")
	    .attr("text-anchor", "middle")
	    .attr("dy", text_y);
	
	var text2 = null;
	
	if (has_percent2) {
		var tmp2 = d3.select(element).attr("legend_dy");
		tmp = d3.select(element).attr("legend");
		if (tmp && tmp != "") {
			if (!tmp2 || tmp2=="") {
				tmp2 = "-.6em";
			}
			text2 = svg.append("text").attr("text-anchor", "middle")
		    .attr("dy", tmp2)
			.attr("class", "legend");
			text2.text( tmp );
		}
	}
	
	//text.text( format(percent / 100) );
		
	
		var progress = 0;
		var timeout = setTimeout(function () {
		  clearTimeout(timeout);
		  path = path.data(pie(dataset.upper)); // update the data
		  path.transition().duration(duration).attrTween("d", function (a) {
		    // Store the displayed angles in _current.
		    // Then, interpolate from _current to the new angles.
		    // During the transition, _current is updated in-place by d3.interpolate.
		    var i  = d3.interpolate(this._current, a);
		    var i2 = d3.interpolate(progress, percent)
		    this._current = i(0);
		    return function(t) {
		    	 d3.select(element).select(function() { return this.parentNode; }).selectAll(".you").html(format(i2(t) / (isPercentFormat ? 100 : 1)));
		      //text.text( format(i2(t) / 100) );
		      return arc(i(t));
		    };
		  }); // redraw the arcs
		}, 200);
		
		progress2 = 0;
		var timeout2 = setTimeout(function () {
		  clearTimeout(timeout2);
		  if (path2) {
			  path2 = path2.data(pie(dataset2.upper)); // update the data
			  path2.transition().duration(duration).attrTween("d", function (a) {
				  // Store the displayed angles in _current.
		    // Then, interpolate from _current to the new angles.
		    // During the transition, _current is updated in-place by d3.interpolate.
				  var i2  = d3.interpolate(this._current, a);
				  var i22 = d3.interpolate(progress2, percent2)
				  this._current = i2(0);
				  return function(t) {
					  text.text( format(i22(t) / (isPercentFormat ? 100 : 1)) );
					  return arc2(i2(t));
				  };
			  }); // redraw the arcs
		  }
		}, 200);
		
}

function validateAnd2PercentScore(in_score) {
	var result = 0;
	if (!in_score || in_score == "") {
		return 0;
	}
	
	try {
		result = parseFloat(in_score);
		if (result<0) {
			result = 0;
		}
		else if (result>1) {
			result = 1;
		}
	}
	catch(x) {
	}
	
	return result * 100;
}

function validateAnd2PercentScore100(in_score) {
	var result = 0;
	if (!in_score || in_score == "") {
		return 0;
	}
	
	try {
		result = parseFloat(in_score);
		
		if (result<0) {
			result = 0;
		}
		else if (result>100) {
			result = 100;
		}
	}
	catch(x) {
	}
	
	return result;
}

function d3utils_calcPercent(percent) {
	return [percent, 100-percent];
	};


function simpleDonutHelper(in_id,in_my,in_peers, func_correct) {
	// alert(in_id + ":" + in_my + ","+in_peers)
	var my_val = validateAnd2PercentScore(in_my);
	var peers_val = validateAnd2PercentScore(in_peers);
	if (func_correct) {
		my_val = func_correct(my_val);
		peers_val = func_correct(peers_val);
	}
	
	$(in_id + ' .donut').attr('data',my_val);
	$(in_id + ' .donut').attr('data2',peers_val);
	drawSimpleMultipleDonutChart(
		in_id + ' .donut',
		my_val,peers_val,
		null,
		null,
		".7em",100,100,1,-1,"01d");
}
