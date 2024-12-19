module com.example.jblackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires java.logging;

    opens Controller to javafx.fxml;
    opens Model.Profile to com.google.gson;
    opens Model.GameStats to com.google.gson;
    exports Controller;
}