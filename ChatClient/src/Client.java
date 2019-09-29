import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

enum State {
    MENU,
    CONNECTED,
    CLOSED
}

public class Client {
    private static BufferedReader inputStream = null;
    private static ServerHandler serverHandler = null;

    private static State state = State.MENU;

    public static void main(String args[]) {

        inputStream = new BufferedReader(new InputStreamReader(System.in));

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                while (serverHandler.connected) {
                    try {
                        String line = inputStream.readLine();
                        if (line.equalsIgnoreCase("QUIT")) {
                            serverHandler.send("QUIT");
                            serverHandler.close();
                            System.out.println("Closing connection..");

                        } else {
                            serverHandler.send(line);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (serverHandler.connected) {
                    try {
                        serverHandler.read();
                    } catch (IOException e) {

                    }

                }

            }
        });

        while (state == State.MENU) {
            try {
                String line = inputStream.readLine();

                String[] tokens = line.split("( )|(\\:)|(\\, )");

                if ("QUIT".equalsIgnoreCase(tokens[0])) {
                    state = state.CLOSED;
                    System.out.println("Closing connection..");

                } else if ("JOIN".equalsIgnoreCase(tokens[0])) {

                    if (handleLogin(tokens)) {
                        serverHandler = new ServerHandler(tokens[1], tokens[2], tokens[3]);
                        sender.start();
                        reader.start();
                        state = State.CONNECTED;

                    } else {
                        System.out.println("J_ERR: 003: Wrong format");
                        System.out.println("       USE -> 'JOIN <<user_name>>, <<server_ip>>:<<server_port>>'");
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static boolean handleLogin(String[] tokens){
        if(tokens.length == 4) {
            String username = tokens[1];
            if (username.length() < 13 && Pattern.matches("[a-zA-Z0-9_-]*", username)) {
                return true;
            } else {
                System.err.println("J_ERR 002: Login failed");
            }
        } else {
            System.err.println("J_ERR 003: Invalid arguments");
        }
        return false;
    }
}