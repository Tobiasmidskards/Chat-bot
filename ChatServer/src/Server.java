import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static Socket socket = null;
    private static ServerSocket server = null;
    public static ArrayList<ClientHandler> clientList = new ArrayList<>();
    final static private int port = 1699;

    public Server(int port) {
        try {
            server = new ServerSocket(port);

            if (server != null) {
                System.out.println("Server running on port: " + port);
            } else {
                System.err.println("J_ERR 001: Failed to open socket on port: " + port);
            }

            System.out.println("\nWaiting for clients..");

            while (true) {
                socket = server.accept();
                ClientHandler client = new ClientHandler(socket, this);
                Thread t = new Thread(client);

                clientList.add(client);

                System.out.println(clientList);

                t.start();

            }

        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        Server server = new Server(port);
    }

    public static List<ClientHandler> getClientList(){
        return clientList;
    }

    public static void removeClient(ClientHandler client) {
        clientList.remove(client);
    }

} 