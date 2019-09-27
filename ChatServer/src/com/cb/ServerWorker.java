package com.cb;

import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private String login = null;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        outputStream.write("\nJ_OK\n".getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null ) {
            String[] tokens = line.split(" ");
            if(tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if("quit".equalsIgnoreCase(cmd)) {
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException, InterruptedIOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if (login.length() < 12) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully" + login);
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

}
