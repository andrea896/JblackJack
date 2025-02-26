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
    exports Model.Profile;
    exports Controller;
    opens Model.Profile to com.google.gson, javafx.fxml;
}