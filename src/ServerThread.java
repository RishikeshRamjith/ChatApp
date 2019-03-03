import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class ServerThread extends Thread {

	private String clientName = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final ServerThread[] clients;
	private int maxClientsCount;

	public ServerThread(Socket clientSocket, ServerThread[] clients) {
		this.clientSocket = clientSocket;
		this.clients = clients;
		maxClientsCount = clients.length;
	}

  	public void run() {
    	try {

			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
      
      		os.println("Welcome "
          	+ " to our chat room.\nTo leave enter /quit in a new line.");
      
	      	while (true) {
	        	String line = is.readLine();
	        	if (line.startsWith("/quit")) {
	          		break;
	        	}
	      
		        synchronized (this) {
		          	for (int i = 0; i < maxClientsCount; i++) {
		            	if (clients[i] != null ) {
		              		clients[i].os.println("< " + "> " + line);
		            	}
		          	}
		        }
	      	}	
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					  if (clients[i] != null && clients[i] != this) {
					    	clients[i].os.println("*** The user is leaving the chat room !!! ***");
					  }
				}
			}
			os.println("*** Bye ***");

	      	synchronized (this) {
	        	for (int i = 0; i < maxClientsCount; i++) {
	          		if (clients[i] == this) {
	            		clients[i] = null;
	          		}
	        	}
	      	}

			is.close();
			os.close();
			clientSocket.close();
    	}
    	catch (IOException i) {
    		System.err.println(i);
    	}
	}
}
