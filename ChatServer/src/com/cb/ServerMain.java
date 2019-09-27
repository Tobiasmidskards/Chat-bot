package com.cb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        int port = 1699;
        Server server = new Server(port);
        server.start();
    }
}
