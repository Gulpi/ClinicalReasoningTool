function printGroupedBarChart(data, peerdata, height, width, chartName, title){
	var data1 = [];
	data1[0] = {key: "stud" , nonStackable: 1 ,values: peerdata};
	data1[1] = {key: "peer" , nonStackable: 1, values: data};

	
   nv.addGraph({
        generate: function() {
            var chart = nv.models.multiBarChart()
                .width(width)
                .height(height)
                .showLegend(true)
                .showControls(false)
                .reduceXTicks(false)
                //.stacked(true)
                ;

            chart.yAxis.tickFormat(d3.format(''));
            chart.yAxis.ticks(10, "%");	
            chart.margin({"left":15,"right":5,"top":5,"bottom":15});
            chart.tooltip.enabled(false)
            chart.yDomain([0,100]);
            
            chart.dispatch.on('renderEnd', function(){
                console.log('Render Complete');
            });

            var svg = d3.select('#'+chartName).datum(data1);
            console.log('calling chart');
            svg.transition().duration(0).call(chart);

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
