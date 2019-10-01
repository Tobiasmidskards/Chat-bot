import java.net.Socket;
import java.io.*;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    private final Socket socket;
    private final Server server;

    private final ClientHandler client = this;

    private String username;
    private int tick = 120;
    private boolean connected = true;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());

            this.username = inputStream.readUTF();

            if(!validateUsername()){
                send("J_ERR 101: Username not accepted");
                Server.removeClient(this);
                connected = false;
                socket.close();
                return;
            }

            System.out.println("New client connected:\n" + this.username + " " + socket.getRemoteSocketAddress());

            send("J_OK");

            String activeClients = this.username;
            for (ClientHandler client : Server.clientList) {
                if (client.username != this.username) {
                    activeClients += " " + client.username;
                }
            }

            for (ClientHandler client : Server.clientList) {
                client.send("SERVER: ONLINE NOW <" + this.username + ">");
                client.send("        LIST <" + activeClients + ">");
            }

            send("J_OK");

            Thread ticker = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(connected) {
                            Thread.sleep(1000);
                            tick -= 1;
                            System.out.println(tick);
                            if(tick < 0) {
                                Server.removeClient(client);
                                System.out.println("SERVER: Client <" + username + "> went offline");

                                String activeClients = "";
                                for (ClientHandler client : Server.clientList) {
                                    if (client.username != username) {
                                        activeClients += " " + client.username;
                                    }
                                }

                                for (ClientHandler client : Server.clientList) {
                                    client.send("SERVER: Client <" + username + "> went offline");
                                    client.send("        LIST <" + activeClients + ">");
                                }

                                connected = false;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            ticker.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void run() {

        while (connected) {
            try {
                String rx = inputStream.readUTF();

                System.out.println(rx);

                String[] tokens = rx.split("( )|(\\: )");

                if (("quit").equalsIgnoreCase(tokens[0]) && tokens.length == 1) {

                    Server.removeClient(this);
                    this.socket.close();

                    System.out.println("SERVER: Client <" + this.username + "> went offline");

                    String activeClients = "";
                    for (ClientHandler client : Server.clientList) {
                        if (client.username != this.username) {
                            activeClients += " " + client.username;
                        }
                    }

                    for (ClientHandler client : Server.clientList) {
                        client.send("SERVER: Client <" + this.username + "> went offline");
                        client.send("        LIST <" + activeClients + ">");
                    }

                } else if(("data").equalsIgnoreCase(tokens[0]) && (tokens.length == 3)){
                    for (ClientHandler client : Server.clientList) {
                        if (client.username != this.username && tokens[1].equalsIgnoreCase(this.username)) {
                            client.send(this.username + ": " + tokens[2]);
                        }
                    }
                } else if(("imav".equalsIgnoreCase(tokens[0]) && (tokens.length) == 1)) {
                    tick = 120;
                } else {
                    send("J_ERR 201: Unknown command: '" + tokens[0] + "'");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public Socket getSocket() {
        return socket;
    }

    private boolean validateUsername(){
        if (this.username.length() < 13 && Pattern.matches("[a-zA-Z0-9_-]*", this.username)) {
            return true;
        }
        return false;
    }
}
