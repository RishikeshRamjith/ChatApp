import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends Thread {

	private String clientUsername = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;

	private final ServerThread[] clients;
	private int maxClients;
	private SimpleDateFormat sdf;

	public ServerThread(Socket clientSocket, ServerThread[] clients) {
		this.clientSocket = clientSocket;
		this.clients = clients;
		maxClients = clients.length;
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

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
				os.println("Enter your username:");
				line = is.readLine();

				if (line.indexOf("@") == -1) {
					clientUsername = line;
					break;
				}
				else {
					os.println("Username cannot contain '@'");
				}
			}
      
      		os.println("Welcome to the chat room " + clientUsername + "!\n"
      			+ "To logout enter 'EXIT' on a new line\n"
      			+ "To see who is in the chatroom enter 'WHOISON' on a new line");
      
	      	while (true) {
	        	line = is.readLine();

	        	if (line.equalsIgnoreCase("EXIT")) {
	          		break;
	        	}

	        	else if (line.equalsIgnoreCase("WHOISON")) {
	        		synchronized (this) {
			          	for (int i = 0; i < maxClients; i++) {
			            	if (clients[i] != null ) {
			              		os.println(clients[i].clientUsername);
			            	}
			          	}
			        }
	        	}

	        	else {
	        		synchronized (this) {
			          	for (int i = 0; i < maxClients; i++) {
			            	if (clients[i] != null ) {
			              		castMessage(clients[i], "<" + clientUsername + "> " + line);
			            	}
			          	}
			        }
	        	}
	      	}

			synchronized (this) {
				for (int i = 0; i < maxClients; i++) {
					  if (clients[i] != null && clients[i] != this) {
					    	castMessage(clients[i], "<< " + clientUsername + " has left >>");
					  }
				}
			}
			os.println("<< Bye " + clientUsername + " >>");

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
