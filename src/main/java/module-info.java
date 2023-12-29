module com.minfile.minimalist_file_server {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.minfile.minimalist_file_server to javafx.fxml;
    exports com.minfile.minimalist_file_server;
    exports com.minfile.minimalist_file_server.controller;
    opens com.minfile.minimalist_file_server.controller to javafx.fxml;
}