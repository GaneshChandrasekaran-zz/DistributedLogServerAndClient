/**
 * LogServer.java
 * 
 * Version:
 * 1.0
 * 
 * Revisions:
 * None
 * 
 * Program to write a server that serves the client for various types of requests.
 * 
 * The Protocol
 * The log service uses TCP to transmit messages between clients and the server. Messages consist of a
 * series of characters terminated by a newline character. The ?rst character in every message sent by
 * a client to a server is a character that identi?es the type of request being made by the client. The
 * remaining information in the message depends upon the type of request that is being made. The four
 * di?erent request types, and the information included in the message, are summarized in the table below:
 * if I send 0 to the server, it will respond with a ticket number (ie. ABCD1234). I can then
 * use that ticket number to log a message with the message: 1ABCD1234:hello world
 * If the server receives a malformed request, or the request cannot be carried out, the request will simply
 * be ignored by the server.
 * 
 */

/**
 * @author GANESH CHANDRASEKARAN
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * The LogServer has 4 methods that is called depending on the type of request
 * sent by the client
 */
public class LogServer {
	static Socket client = null;
	// Creates a HashMap to store the messages for quick retrieval purposes
	static HashMap<String, List<String>> logMessages = new HashMap<String, List<String>>();
	// Creates a list that stores the history of tickets that have been released
	static List<String> historyTicket = new ArrayList<String>();
	// Creates a list of tickets that has been generated
	static List<String> generatedTicket = new ArrayList<String>();
	// Creates a logger object to perform log operations
	static Logger logger = Logger.getLogger("MyLog");
	static FileHandler fh;
	static File directory = new File(".");

	/**
	 * Generates a new ticket using UUID class defined in java and converts it
	 * into a string
	 * 
	 * @return uid ticket that is issued to a client
	 */
	public static String newTicket() {
		UUID uid = UUID.randomUUID();
		generatedTicket.add(uid.toString());
		return uid.toString();
	}

	/**
	 * Adds a message associated with the corresponding ticket in to the HashMap
	 * 
	 * @param ticketMessage
	 *            ticket with which the message is to be stored
	 * @param message
	 *            message that is to be stored
	 */
	public static void addEntry(String ticketMessage, String message) {
		if (logMessages.containsKey(ticketMessage)) {
			/**
			 * If there already exists a ticket in the hashmap then the messages
			 * are retrievd in a list and then after the new message is added it
			 * is put back into the hashmap
			 */
			List<String> message1 = logMessages.get(ticketMessage);
			message1.add(message);
			logMessages.put(ticketMessage, message1);
			// Logs the entry of new message
			try {
				// This block configures the logger with handler and formatter
				fh = new FileHandler(directory.getCanonicalPath()
						+ "/LogServer.log");
				logger.addHandler(fh);
				// logger.setLevel(Level.ALL);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);
				// The following statement is used to log the ticket and its
				// associated message
				logger.info(ticketMessage + " - " + message);
			} catch (SecurityException e) {
				e.getMessage();
			} catch (IOException e) {
				e.getMessage();
			}
		}

		// If the ticket is not yet released and is a new ticket then the first
		// entry is added in the HashMap

		else if (!historyTicket.contains(ticketMessage)
				&& generatedTicket.contains(ticketMessage)) {
			List<String> message1 = new ArrayList<String>();
			message1.add(message);
			logMessages.put(ticketMessage, message1);

			try {
				// This block configure the logger with handler and formatter
				fh = new FileHandler(directory.getCanonicalPath()
						+ "/LogServer.log");
				logger.addHandler(fh);
				// logger.setLevel(Level.ALL);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);
				// The following statement is used to log the ticket and its
				// message
				logger.info(ticketMessage + " - " + message);
			} catch (SecurityException e) {
				e.getMessage();
			} catch (IOException e) {
				e.getMessage();
			}

		} else {
			System.out.print("");
		}
	}

	/**
	 * The following method retrieves all the messages associated ticket if it
	 * exists in the HashMap
	 * 
	 * @param ticket
	 *            whose messages are to be retrieved
	 * 
	 */
	public static void getEntries(String ticket) throws IOException {
		DataOutputStream toClient = new DataOutputStream(
				client.getOutputStream());
		if (logMessages.containsKey(ticket)) {
			// The messages associated with the ticket are stored in a list of
			// string type
			List<String> messages = logMessages.get(ticket);
			Iterator<String> iter = messages.iterator();
			int count = messages.size();
			String count1 = Integer.toString(count);
			try {
				// This block configure the logger with handler and formatter
				fh = new FileHandler(directory.getCanonicalPath()
						+ "/LogServer.debug");
				logger.addHandler(fh);
				// logger.setLevel(Level.ALL);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);
				// The following statement is used to log message
				logger.info("Received 3 and Delivering " + count
						+ " messages to " + ticket);
			} catch (SecurityException e) {
				e.getMessage();
			} catch (IOException e) {
				e.getMessage();
			}
			// The number of messages is first sent to the client
			toClient.writeBytes(count1 + "\n");
			toClient.flush();
			// All the messages from the list are sent to the client
			while (iter.hasNext()) {
				toClient.writeBytes(iter.next() + "\n");
				toClient.flush();
			}
		} else {
			int count = 0;
			String count1 = Integer.toString(count);
			toClient.writeBytes(count1 + "\n");
		}
	}

	/*
	 * The following main method provides entry point to the program. It listens
	 * on the port 6007 and accpets any connections from the client
	 */
	public static void main(String args[]) {
		try {
			ServerSocket listen = new ServerSocket(6007);
			System.out.println("I am listening on port: "
					+ listen.getLocalPort());
			client = listen.accept();
			while (true) {
				BufferedReader fromClient = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				String ipClient = fromClient.readLine();
				int i = Integer
						.parseInt(Character.toString(ipClient.charAt(0)));
				// Depending on the request type following cases handle the
				// request
				switch (i) {
				case 0:
					DataOutputStream toClient = new DataOutputStream(
							client.getOutputStream());
					String ticket = newTicket();
					try {
						fh = new FileHandler(directory.getCanonicalPath()
								+ "/LogServer.debug");
						logger.addHandler(fh);
						// logger.setLevel(Level.ALL);
						SimpleFormatter formatter = new SimpleFormatter();
						fh.setFormatter(formatter);
						// The following statement is used to log the ticket
						// that is sent to the client
						logger.info("Issuing " + ticket + " to the client ");
					} catch (SecurityException e) {
						e.getMessage();
					} catch (IOException e) {
						e.getMessage();
					}
					// Sends the ticket to the client
					toClient.writeBytes(ticket + "\n");
					break;
				case 1:
					String[] s = ipClient.split(":");
					String ticketMessage = s[0].substring(1);
					String messages = s[1];
					try {
						fh = new FileHandler(directory.getCanonicalPath()
								+ "/LogServer.debug");
						logger.addHandler(fh);
						SimpleFormatter formatter = new SimpleFormatter();
						fh.setFormatter(formatter);
						logger.info("Received 1 from: " + ticketMessage);
					} catch (SecurityException e) {
						e.getMessage();
					} catch (IOException e) {
						e.getMessage();
					}
					addEntry(ticketMessage, messages);
					break;
				case 2:
					String[] s1 = ipClient.split(":");
					String ticketMessage1 = s1[0].substring(1);
					logMessages.remove(ticketMessage1);
					historyTicket.add(ticketMessage1);

					try {
						fh = new FileHandler(directory.getCanonicalPath()
								+ "/LogServer.debug");
						logger.addHandler(fh);
						SimpleFormatter formatter = new SimpleFormatter();
						fh.setFormatter(formatter);
						logger.info("Releasing " + ticketMessage1);
					} catch (SecurityException e) {
						e.getMessage();
					} catch (IOException e) {
						e.getMessage();
					}
					break;
				case 3:
					String[] s2 = ipClient.split(":");
					String ticketMessage2 = s2[0].substring(1);
					getEntries(ticketMessage2);
					break;
				default:
					continue;
				}
			}
		}

		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
