import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

// Reads and handles input from each client connected to server
public class ServerThread extends Thread {

	private String username = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;

	private final ServerThread[] clients;
	private int maxClients;
	private SimpleDateFormat sdf; // to timestamp messages sent

	public ServerThread(Socket clientSocket, ServerThread[] clients) {
		this.clientSocket = clientSocket;
		this.clients = clients;
		maxClients = clients.length;
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

	// sends a message to specified client with timestamp
	public void castMessage(ServerThread client, String msg) {
		String response = sdf.format(new Date()) + ": " + msg;
		client.os.println(response);
	}

  	public void run() {
    	try {

			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());

			String line;

			while (true) {
				os.println("Do you want to Log in (L) or Sign up (S)");
				String password;

				line = is.readLine();

				// sign up
				if (line.equalsIgnoreCase("S")) {
					// gets username from client
					os.println("Enter your username:");
					line = is.readLine();

					if (line.indexOf("@") == -1) {
						username = line;

						os.println("Enter your password:");
						password = (is.readLine());

						// TODO: user validation here
						break;
					}
					else {
						os.println("Username cannot contain '@'");
					}
				}
				//login 
				if else (line.equalsIgnoreCase("L")) {
					os.println("Enter your username:");
					line = is.readLine();

					// TODO: validate username exists

					os.println("Enter your password:");
					password = (is.readLine());

					// TODO: validation

					break;
				}
				
			}
      
      		os.println("Welcome to NetChatter, " + username + "!\n"
      			+ "To logout enter 'EXIT' on a new line\n"
      			+ "To see who is in the chatroom enter 'WHOISHERE' on a new line\n"
      			+ "To send a private message type '@username' followed by your message");
      
	      	while (true) {
	        	line = is.readLine();

	        	// user exits the chat
	        	if (line.equalsIgnoreCase("EXIT")) {
	          		break;
	        	}

	        	// user checks to see who is in the chat
	        	else if (line.equalsIgnoreCase("WHOISHERE")) {
	        		synchronized (this) {
			          	for (int i = 0; i < maxClients; i++) {
			            	if (clients[i] != null ) {
			              		os.println(clients[i].username);
			            	}
			          	}
			        }
	        	}	

	        	// user attempts to send a private message to client connected to server
	        	else if (line.startsWith("@")) {
	        		int split = line.indexOf(" ");
	        		String receiver, msg;
	        		boolean sent = false;

	        		if (split != -1) {
	        			receiver = line.substring(1, split); // gets target username
	        			msg = line.substring(split);

	        			if (receiver.equals(username)) {
	        				os.println("Why are you talking to yourself?"); // just in case
	        			}
	        			else {
	        				synchronized (this) {
					          	for (int i = 0; i < maxClients; i++) {
					            	if (clients[i] != null && clients[i].username.equals(receiver)) {
					              		castMessage(clients[i], "<" + username + "> " + msg);
					              		sent = true;
					              		break;
					            	}
					          	}
					        }
					        if (!sent)	os.println("User " + receiver + " not found");
	        			}
	        		}
	        		else {
	        			os.println("Incorrect message format: "
	        				+ "please include a space between the targeted user and your message");
	        		}
	        	}

	        	// broadcasts message to all clients (to be included?)
	        	else {
	        		synchronized (this) {
			          	for (int i = 0; i < maxClients; i++) {
			            	if (clients[i] != null ) {
			              		castMessage(clients[i], "<" + username + "> " + line);
			            	}
			          	}
			        }
	        	}
	      	}

	      	// notifies all clients connected that the user has left
			synchronized (this) {
				for (int i = 0; i < maxClients; i++) {
					  if (clients[i] != null && clients[i] != this) {
					    	castMessage(clients[i], "<< " + username + " has left >>");
					  }
				}
			}
			os.println("<< Bye " + username + " >>");

			// frees up a slot in the server client pool
	      	synchronized (this) {
	        	for (int i = 0; i < maxClients; i++) {
	          		if (clients[i] == this) {
	            		clients[i] = null;
	          		}
	        	}
	      	}
    	}
    	catch (IOException i) {
    		System.err.println(i);
    	}
    	finally {
    		close();
    	}
	}

	public void close() {
		try {
			if (is != null) 			is.close();
    		if (os != null) 			os.close();
    		if (clientSocket != null)	clientSocket.close();
		}
		catch (IOException i) {
    		System.err.println(i);
    	}
	}
}
