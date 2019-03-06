import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int portNumber = 12069;
    private static final int maxClients = 10; // maximum number of clients allowed to connect to server at once
    private static final ServerThread[] clients = new ServerThread[maxClients];

    public static void main(String args[]) {
        // takes desired port number, otherwise default
        if (args.length == 1) {
            System.out.println("Attempting to start server on port: " + args[0]);
            portNumber = args[0];
        }
        else if (args.length == 0) {
            System.out.println("Attempting to start server on default port: " + portNumber);
        }
        else {
            throw new IllegalArgumentException("Too many arguments. "
                + "Should be in form: java Server <port-no>");
        }
        
        try {
            serverSocket = new ServerSocket(portNumber);

            while (true) {
                clientSocket = serverSocket.accept(); // listen for clients

                System.out.println("Client accepted " + clientSocket.getInetAddress()
                    + ":" + clientSocket.getPort());

                // run ServerThread for newly connected client in a new thread and start thread
                int i = 0;
                Login.loadUsers();
                for (i = 0; i < maxClients; i++) {
                    if (clients[i] == null) {
                        (clients[i] = new ServerThread(clientSocket, clients)).start();
                        break;
                    }
                }

                // if client pool is full, close socket
                if (i == maxClients) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Please try later.");
                    os.close();
                    clientSocket.close();
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

    public static void close() {
        try {
            if (serverSocket != null) serverSocket.close();
        }
        catch (IOException i) {
            System.err.println(i);
        }
    }
}
