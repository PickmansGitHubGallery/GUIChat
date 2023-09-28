package com.example.chatmedgui;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private static String sessionID = "";
    private static String brugerNavn = " ";
    static Scanner input = new Scanner(System.in);


    private LinkedBlockingQueue<String> messageToServer = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> messageFromServer = new LinkedBlockingQueue<>();

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    BufferedReader stdIn;

    private TextArea chatArea;  // Added TextArea for displaying chat messages
    private TextField inputField;  // Added TextField for user input
    private Button sendButton;

    private void chooseUserName(PrintWriter out, BufferedReader in) throws IOException {
        //Gui beder om indtastning
        String navn = tjekLovligtBrugernavn(tjekBrugerInput());
        //sender brugernavn til server
        out.println("100" + sessionID + navn);
        //venter på svar fra severen
        String serverMessage = in.readLine();
        while (serverMessage != null) {
            //hvis brugernavnet er accepteret
            if (serverMessage.substring(0, 3).equals("999")) {
                brugerNavn = navn;
                break;
            }
            //Hvis brugernavnet ikke er ledigt
            if (serverMessage.substring(0, 3).equals("000")) {
                System.out.println("Brugernavn er taget prøv igen");
                serverMessage = null;
                //metoden kaldes igen for at vælge nyt brugernavn
                chooseUserName(out, in);
            }
        }

    }

    private static String tjekBrugerInput() {

        System.out.println("Opret brugernavn");
        System.out.println("Brugernavn må ikke indholde mellemrum eller @");
        String brugernavnInput = input.nextLine();
        return brugernavnInput;
    }

    private static String tjekLovligtBrugernavn(String brugernavn) {

        while (brugernavn.contains(" ") || brugernavn.contains("@")) {
            System.out.println("Der må ikke være mellemrum eller @ i dit brugernavn.");
            System.out.println("Prøv igen");
            brugernavn = tjekBrugerInput();
        }
        return brugernavn;
    }

    private static boolean sendUsernameToServer(PrintWriter out, String username) {
        if (out != null && username != null && !username.isEmpty()) {
            out.println(username);
            return true;
        } else {
            System.out.println("prøv igen");
            return false;

        }
    }

    public static void sendMessage(PrintWriter out, String message) {

        if (message.equalsIgnoreCase("exit")) {
            out.println("400" + sessionID);
        }
        else {
            if (message.length() == 0) {
                message = null;
            }
            else if (message.charAt(0) == '@') {
                out.println("300" + sessionID + message.substring(1));
                message = null;
            }
            else if (message.equalsIgnoreCase("!Brugere")) {
                out.println("500" + sessionID);
            }
            else if (message != null && !message.trim().isEmpty()) {
                out.println("200" + sessionID + message);
                message = null;
            }
        }
    }
    public void setUpButtonSend() {
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            messageToServer.add(message);
            inputField.setText("");
        });
    }
    public void addMessageToChat(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatArea.appendText(message + "\n");
            }
        });
    }
        public void disconnect() {
        out.println(400+sessionID);
    }
    Client(TextArea chatArea, TextField inputField,Button sendButton) throws IOException {
        String serverAddress = "10.200.130.36";
        int serverPort = 1992;
        this.chatArea = chatArea;
        this.inputField = inputField;
        this.sendButton = sendButton;


        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);
            String testMessage;
            while ((testMessage = in.readLine()) != null) {
                if (sessionID.isEmpty() && testMessage.substring(0, 3).equals("999")) {
                    sessionID = testMessage.substring(3, 7);
                    System.out.println("Received sessionID: " + sessionID);
                    break;
                }
            }
            chooseUserName(out, in);
            setUpButtonSend();

            Thread writeToServer = new Thread() {
                public void run() {
                    try {
                        String message = "";
                        while(!message.equals("exit")) {
                            message = messageToServer.take();
                            if (message != null && !message.trim().isEmpty()) {
                                sendMessage(out, message);
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            writeToServer.start();
            Thread listenToServer = new Thread(() -> {
                while (true) {
                    String inputFromServer = null;
                    try {
                        inputFromServer = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    messageFromServer.add(inputFromServer);
                }
                });
            listenToServer.start();

            Thread writeToGUI = new Thread(() -> {
                while(true){
                    try {
                        String inputFromServerQueue = messageFromServer.take();
                        addMessageToChat(inputFromServerQueue);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                });
            writeToGUI.start();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


        }


}


