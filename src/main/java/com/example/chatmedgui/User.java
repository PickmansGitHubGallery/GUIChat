package com.example.chatmedgui;

import java.io.*;
import java.net.*;

class User {
    private String username;
    private Socket clientSocket;

    public User(String username, Socket clientSocket) {
        this.username = username;
        this.clientSocket = clientSocket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}