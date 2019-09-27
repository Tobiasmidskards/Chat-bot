package com.cb;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 1699);
        if (!client.connect()) {
            System.err.println("Conn failed");
        } else {
            System.out.println("Conn success");
            if (client.login("join toby toby\n")) {
                System.out.println("Login success");
            } else {
                System.out.println("Login failed");
            }
        }
    }

    private boolean login(String creds) throws IOException {
        serverOut.write(creds.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response: " + response);
        if ("ok login".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
