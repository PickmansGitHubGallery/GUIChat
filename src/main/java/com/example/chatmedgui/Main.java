package com.example.chatmedgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    Stage window;
    public static void main(String[] args) throws IOException {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        this.window.setTitle("Client");        //Grid
        GridPane gridPane = new GridPane();
        Client client = new Client(gridPane);


        Scene scene = new Scene(gridPane,600,400);
        this.window.setScene(scene);
        this.window.show();

    }
}
