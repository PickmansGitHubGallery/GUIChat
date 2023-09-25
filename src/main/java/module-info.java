module com.example.chatmedgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chatmedgui to javafx.fxml;
    exports com.example.chatmedgui;
}