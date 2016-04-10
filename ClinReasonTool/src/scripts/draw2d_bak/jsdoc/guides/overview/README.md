# Overview and Concepts
The purpose of **Draw2D** is to visualize and edit graphs, flow-charts and networks. It almost goes without saying that you should be able to add and delete nodes, move nodes about, create connections between nodes and more. 

Draw2D is high reusability and be customizable by injection edit policies. 
This section introduces the main concepts and components. 

A `draw2d.Canvas` contains a collection of nodes and connections: 


{@img canvas.png Diagram in draw2d}


The `draw2d.shape.node.Node` are the primary components: 

{@img nodes.png nodes in draw2d}


Nodes are linked to each other by a `draw2d.Connection` 

{@img connections.png connections in draw2d}


A `draw2d.Port` is the anchor point of a node to which connections can be attached

{@img ports.png connections in draw2d}


A node may have one or more ports. Each connection has two end-points each of which be anchored to a port. However when the user is dragging out a new connection only one of the end-points is anchored to a port and the other is fixed to the mouse cursor's current position.


The canvas itself places no constraints on the directionality of connections, however it does label the ends of a connection as source and target, but the meanings of these labels are defined by the application


Nodes, ports and connections are all fully skinnable using {@link draw2d.Figure#setCssClass CSS} , user defined SVG and {@link draw2d.shape.basic.Rectangle#setColor style API}. In fact there is a default graphical representation for any of them and draw2d contains a set of pre defined figures. 

