package com.example.chatmedgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {
    private Client client; // Declare a Client instance

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        primaryStage.setTitle("Chat Application");

        // Create the chat area (a TextArea)
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        // Create the input field
        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");

        // Create the send button
        Button sendButton = new Button("Send");

        // Create a layout for the input bar
        HBox inputBar = new HBox(inputField, sendButton);
        inputBar.setHgrow(inputField, Priority.ALWAYS);

        // Create the main layout
        BorderPane layout = new BorderPane();
        layout.setCenter(chatArea);
        layout.setBottom(inputBar);

        // Create a scene and set it in the stage
        Scene scene = new Scene(layout, 800, 800);
        primaryStage.setScene(scene);
        String css = this.getClass().getResource("/styles/styles.css").toExternalForm();

        scene.getStylesheets().add(css);
        chatArea.getStyleClass().add("chat-message");

        sendButton.getStyleClass().add("button-send");

        client = new Client(chatArea, inputField, sendButton);

        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendButton.fire(); // Simulate a button click
                event.consume(); // Consume the event to prevent further processing
            }
        });

        primaryStage.setOnCloseRequest(this::onWindowClose);


        primaryStage.show();
    }
    private void onWindowClose(WindowEvent event) {
        // Call the disconnect method in the Client
        client.disconnect();
    }
}