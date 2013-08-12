/**
 * LogClient.java
 * 
 * Version:
 * 1.0
 * 
 * Revisions:
 * None
 * 
 * Program to write a client that implements the methods decalred in LogService interface
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
 *
 * @author GANESH CHANDRASEKARAN
 */

//Imports java library files
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class LogClient overrides the methods defined in LogService interface. It
 * initiates communication between the client and the server. The client can
 * send requests with the 1st character being 0, 1, 2 or 3. All other messages
 * will be ignored by the server.
 */
public class LogClient implements LogService {

	// Creates a client socket through which the data transfer takes place

	private Socket sock = null;
	private static String ticket = null;

	public void open(String host, int port) throws UnknownHostException,
			IOException {
		sock = new Socket(host, port);
		System.out.println("Connected to " + host);
		while (sock.isConnected()) {
			Scanner sc = new Scanner(System.in);
			String request = sc.nextLine();
			// Parses the first character to check for the type of request
			int i = Integer.parseInt(Character.toString(request.charAt(0)));

			switch (i) {
			// Requests for a new ticket
			case 0:
				ticket = newTicket();
				break;
			// Logs messages associated with the corressponding ticket
			case 1:
				String[] s = request.split(":");
				ticket = s[0].substring(1);
				String message = s[1];
				addEntry(ticket, message);
				break;
			// Releases the ticket and deletes all the associated messages from
			// the temporary memory
			case 2:
				String[] s1 = request.split(":");
				ticket = s1[0].substring(1);
				releaseTicket(ticket);
				break;
			// Retrieves all the messages associated with the specified ticket
			case 3:
				String[] s2 = request.split(":");
				ticket = s2[0].substring(1);
				List<String> log = new ArrayList<String>();
				log = getEntries(ticket);
				break;
			default:
				continue;
			}
		}
	}

	/**
	 * Close the connection with the server
	 */
	public void close() {
		try {
			sock.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Obtain a new ticket.
	 * 
	 * @return the ticket returned by the server
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public String newTicket() throws IOException {
		// Creates an output stream to be sent to the server
		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		// Passes the string to the server
		toServer.writeBytes("0\n");
		// Immediately sends the stream to the server
		toServer.flush();
		// Reads the input stream from the server and stores it in a buffer
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		// Coverts the stream into a string which is the ticket issued by the
		// server
		String getTicket = fromServer.readLine();
		System.out.println(getTicket);
		return getTicket;
	}

	/**
	 * Add an entry to the log identified by the specified ticket
	 * 
	 * @param ticket
	 *            the ticket of the log to be written to
	 * @param message
	 *            the message to be written to the log
	 */
	public void addEntry(String ticket, String message) {
		try {
			DataOutputStream toServer = new DataOutputStream(
					sock.getOutputStream());
			toServer.writeBytes("1" + ticket + ":" + message + "\n");
			toServer.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Get a list of all the entries that have been written to the log
	 * identified by the given ticket.
	 * 
	 * @param ticket
	 *            the ticket that identifies the log
	 * 
	 * @return a list containing all of the entries written to the log
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public List<String> getEntries(String ticket) throws IOException {
		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		toServer.writeBytes("3" + ticket + "\n");
		toServer.flush();
		// Creates a linked list of String type to store the messages retrieved
		// from the server
		LinkedList<String> messages = new LinkedList<String>();
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		String receiveCount = fromServer.readLine();
		int count = Integer.parseInt(receiveCount);
		if (count > 0) {
			// Adds the messages to the list
			for (int i = 0; i < count; i++) {
				messages.add(fromServer.readLine());
			}
			System.out.println("Displaying the " + count + " sent messages: ");
			/*
			 * Displays the stored messages from the list in the order it was
			 * sent. Iterator is used to iterate through the list.
			 */
			Iterator iter = messages.iterator();
			while (iter.hasNext())
				System.out.println(iter.next());
		} else {
			System.out.print("");
		}

		return messages;
	}

	/**
	 * Release the specified ticket. The entries associated with the ticket will
	 * no longer be available
	 * 
	 * @param ticket
	 *            the ticket to be released
	 */
	public void releaseTicket(String ticket) {
		try {
			DataOutputStream toServer = new DataOutputStream(
					sock.getOutputStream());
			toServer.writeBytes("2" + ticket + "\n");
			toServer.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
