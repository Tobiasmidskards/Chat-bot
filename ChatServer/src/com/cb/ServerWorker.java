package com.cb;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.regex.Pattern;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private String username = null;
    private OutputStream outputStream;
    private InputStream inputStream;

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
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        outputStream.write("\nWelcome - please login\n".getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null ) {
            String[] tokens = line.split("( )|(\\: )");
            if(tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if("quit".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("data".equalsIgnoreCase(cmd)) {
                    handleMessage(tokens);
                } else {
                    String msg = "unknown command: " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void handleList() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList) {
            worker.send("LIST ");
            for(ServerWorker workers : workerList) {
                if(workers.username != null) {
                    worker.send(workers.username + " ");
                }
            }
            worker.send("\n");
        }
    }

    private void handleMessage(String[] tokens) throws IOException{
        if (tokens.length == 3) {
            List<ServerWorker> workerList = server.getWorkerList();
            String usernameInput = tokens[1];
            String data = tokens[2];

            if (usernameInput.equalsIgnoreCase(username)) {
                for(ServerWorker worker : workerList) {
                    if (!username.equals(worker.getUsername())) {
                        worker.send(username + ": " + data + "\n");
                    }
                }
            } else {
                String msg = "J_ER 002: Wrong username - use your own:\nDATA <<user_name>>: <<free text…>>\n\n";
                outputStream.write(msg.getBytes());
            }
        } else {
            String msg = "J_ER 001: Invalid arguments - see:\nDATA <<user_name>>: <<free text…>>\n\n";
            outputStream.write(msg.getBytes());
        }
    }

    private void handleLogOff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        handleList();

        clientSocket.close();
    }

    public String getUsername() {
        return username;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 2) {
            String username = tokens[1];

            if (username.length() < 13 && Pattern.matches("[a-zA-Z0-9_-]*", username)) {
                String msg = "J_OK\n\n";
                outputStream.write(msg.getBytes());
                this.username = username;
                System.out.println("User logged in successfully " + username + "\n");
                handleList();

            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + username);
            }
        }
    }

    private void send(String onlineMsg) throws IOException {
        if (username != null) {
            outputStream.write(onlineMsg.getBytes());
        }
    }

}
