package com.cb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        int port = 1699;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                System.out.println("Waiting connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection: " + clientSocket);
                ServerWorker worker = new ServerWorker(clientSocket);
                worker.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
