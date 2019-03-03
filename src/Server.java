import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClients = 10;
    private static final ServerThread[] clients = new ServerThread[maxClients];

    public static void main(String args[]) {

        int portNumber = 2222;

        try {
            serverSocket = new ServerSocket(portNumber);
        }
        catch (IOException i) {
            System.err.println(i);
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
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
            catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}