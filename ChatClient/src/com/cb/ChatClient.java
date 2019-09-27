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
            client.login("");
            while(client.socket.isConnected()){
                client.login("");
            }
        }
    }

    private void login(String username) throws IOException {
        this.serverOut.write("join toby toby".getBytes());
    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            Thread inputListener = new Thread(new ResponseListener(socket));
            inputListener.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
