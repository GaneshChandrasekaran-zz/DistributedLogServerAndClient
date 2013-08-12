Overview

Most operating systems provide the ability for both system and user processes to log messages in a log
file. The messages in a log file can be used by a system administrator to monitor the activities of a
system, to look for potential problems, and to troubleshoot problems when they occur. In most systems
the log files are stored on a per machine basis, which means an administrator has to potentially look
through log files on dozens of machines when trying to track down a problem. Another solution to this
problem is to provide one central logging facility that is used by all the machines on a network. In this
assignment you will write both the client and server code to implement a distributed system log facility.

System Organization

The distributed log system consists of a server, typically a single server, and several clients that use this
server to record log messages. Before a client can log a message on a log server it must obtain a ticket.
Tickets are used by the server to authenticate the clients and to provide the clients with the ability
to separate their log messages into groups. For example, a distributed course registration system may
want to separate the log messages that deal with problems with course data, from network problems.
The course registration system could do this by obtaining two tickets from the log server: one would
be used to log course data problems, whereas the other could be used to record network related messages.
The log service also provides a client with the ability to retrieve all of the messages associated with a
given ticket. This capability might be used by an administrator to read log messages remotely. The
server maintains the log messages for a given ticket until the ticket is released by a client. When a ticket
is released, all of the messages associated with the ticket are discarded and the ticket is considered
invalid (i.e. clients can no longer log messages using the ticket).

The Protocol

The log service uses TCP to transmit messages between clients and the server. Messages consist of a
series of characters terminated by a newline character. The first character in every message sent by
a client to a server is a character that identifies the type of request being made by the client. The
remaining information in the message depends upon the type of request that is being made. The four
different request types, and the information included in the message, are summarized in the table below:

Request 		First Character 	Additional Information		Response	
New Ticket 		0 			none 				ticket
Log a Message 		1	 		ticket:message 			none
Release a Ticket 	2 ticket 		none
Get Messages 		3	 		ticket 				count followed by message (each
										message is on a new line)

For example, if I send 0 to the server, it will respond with a ticket number (ie. ABCD1234). I can then
use that ticket number to log a message with the message: 1ABCD1234:hello world
If the server receives a malformed request, or the request cannot be carried out, the request will simply
be ignored by the server.