Running NetWars.

Upon launch of the server side application, NWServer, a dialog asks for in port and out ports.
The in port is the port the server is listening for clients on.
The out port is the port the server communicates out through.
A typical setting would be in: 5000 out: 5001

The client side application, NetWars, will show a dialog where the first entry is the IP of the server.
If both client and server are running from the same machine you can use the local ip 127.0.0.1.
The next field is the server port number, which we set above to 5000.
The third field is the input port of the client app, which we can set to anything, say 6000.
Finally we can choose a level, which is an integer between 1 and 3. 

