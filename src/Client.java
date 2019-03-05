import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    private static Socket clientSocket = null;
    private static PrintStream os = null;
    private static DataInputStream is = null;

    private static BufferedReader input = null;
    private static boolean isClosed = false; // is client closed by server

    // default port number for application
    private static int portNumber = 2222;
    private static String host = "localhost";

    public static void main(String[] args) {
        // takes host ip from user, otherwise uses localhost as default
        if (args.length < 1) {
            System.out.println("Attempting to connect to default host: " + host);
        }
        else {
            System.out.println("Attempting to connect to host: " + args[0]);
            host = args[0];
        }

        try {
            clientSocket = new Socket(host, portNumber);
            input = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

            // start client in thread to listen to server responses
            new Thread(new Client()).start();

            // client sends user input to server in this thread
            String line;
            while (!isClosed) {
                line = input.readLine().trim();
                os.println(line);
            }
        }
        catch (UnknownHostException u) {
            System.err.println(u);
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
            if (is != null)             is.close();
            if (os != null)             os.close();
            if (clientSocket != null)   clientSocket.close();
        }
        catch (IOException i) {
            System.err.println(i);
        }
    }

    public void run() {
        // listens for responses to know when to close client
        String response;
        try {
            while ((response = is.readLine()) != null) {
                System.out.println(response);
                if (response.indexOf("<< Bye") != -1)
                    break;
            }
            isClosed = true;
        }
        catch (IOException i) {
            System.err.println(i);
        }
    }
}