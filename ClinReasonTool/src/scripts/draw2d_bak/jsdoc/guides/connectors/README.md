# Connectors

Connections are created and added just like any other figure. Unlike normal figures, you must not set the bounds of a connection. Instead, 
you must provide the source and target port and let the connection router calculate the connection's points. The connection 
then determines its own bounding box.

A connection has a simple router by default - one that can connect the source and target anchors. But additional routers are 
available and can be set on the connection. Some routers can handle constraints for the connection. Note that when setting 
a routing constraint on a connection, you must first set the router which will use that constraint.

