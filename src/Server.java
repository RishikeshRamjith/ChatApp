import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int portNumber = 2222;
    private static final int maxClients = 10;
    private static final ServerThread[] clients = new ServerThread[maxClients];

    public static void main(String args[]) {
        try {
            serverSocket = new ServerSocket(portNumber);

            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("Client accepted: " + clientSocket.getInetAddress());

                int i = 0;
                for (i = 0; i < maxClients; i++) {
                    if (clients[i] == null) {
                        (clients[i] = new ServerThread(clientSocket, clients)).start();
                        break;
                    }
                }

                if (i == maxClients) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
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