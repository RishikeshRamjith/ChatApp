import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

// Reads and handles input from each client connected to server
public class ServerThread extends Thread {

	private static String admin = null;
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
			Login.loadUsers();
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
						if (!Login.UsernameTaken(username)) {
							os.println("Enter your password:");
							password = (is.readLine());
							Login.RegisterNewUser(username,password);
							break;
						}
						else {
							os.println("<< Username taken, try again >>");
							continue;
						}
					}
					else {
						os.println("<< Username cannot contain '@' >>");
					}
				}
				//login
				else if (line.equalsIgnoreCase("L")) {
					os.println("Enter your username:");
					line = is.readLine();
					os.println("Enter your password:");
					password = (is.readLine());
					if (Login.ValidateLogin(line,password)) {
						username = line;
						break;
					}
					else {
						os.println("<< Username/password was incorrect, try again >>");
						continue;
					}
				}
			}

      		os.println("\n<< Welcome to NetChatter, " + username + "! >>\n"
      			+ "To logout enter 'EXIT' on a new line\n"
      			+ "To see who is in the chatroom enter 'WHOISHERE' on a new line\n"
      			+ "To send a private message type '@username' followed by your message\n"
      			+ "Admins are allowed to broadcast to all users at once\n");

      		// if user comes into empty chat room, they are automatically admin
      		if (admin == null) {
      			admin = username;
      			castMessage(this, "<< You have been made admin >>");
      		}
      		else {
      			castMessage(this, "<< " + admin + " is the admin >>");
      		}

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
			              		os.println("> " + clients[i].username);
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
	        			msg = line.substring(split).trim();

	        			if (receiver.equals(username)) {
	        				os.println("<< Why are you talking to yourself? >>"); // just in case
	        			}
	        			else {
	        				synchronized (this) {
					          	for (int i = 0; i < maxClients; i++) {
					            	if (clients[i] != null && clients[i].username.equals(receiver)) {
					              		castMessage(clients[i], "<" + username + "> " + msg);
					              		sent = true;
					              		castMessage(this, "<< Message sent >>");
					              		break;
					            	}
					          	}
					        }
					        if (!sent)	os.println("<< User " + receiver + " not found >>");
	        			}
	        		}
	        		else {
	        			os.println("<< Incorrect message format: "
	        				+ "please include a space between the targeted user and your message >>");
	        		}
	        	}

	        	// admin broadcasts message to all clients
	        	else {
	        		if (username.equals(admin)) {
	        			synchronized (this) {
				          	for (int i = 0; i < maxClients; i++) {
				            	if (clients[i] != null && clients[i] != this) {
				              		castMessage(clients[i], "<" + username + "> " + line);
				            	}
				          	}
				          	castMessage(this, "<< Message sent >>");
				        }
	        		}
	        		else {
	        			os.println("<< You cannot broadcast as you are not admin >>");
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
	      	
    	}
    	catch (IOException i) {
    		System.err.println(i);
    	}
    	finally {
    		disconnectClient();
    		close();
    	}
	}

	// frees up a slot in the server client pool and elects new admin if
	// admin has left
	private void disconnectClient() {
		synchronized (this) {
        	for (int i = 0; i < maxClients; i++) {
          		if (clients[i] == this) {
            		clients[i] = null;

            		if (username.equals(admin)) {
            			admin = null;
            			for (int j = 0; j < maxClients; j++) {
							if (clients[j] != null) {
								if (admin == null) {
									admin = clients[j].username;
									castMessage(clients[j], "<< You have been made admin >>");
								}
								else {
									castMessage(clients[j], "<< " + admin + " is the admin >>");
								}
							}
						}
            		}
          		}
        	}
      	}
      	System.out.println("Client disconnected " + clientSocket.getInetAddress()
                + ":" + clientSocket.getPort());
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
