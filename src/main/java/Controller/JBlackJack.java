package Controller;

import java.util.logging.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class JBlackJack extends Application {
    static final Logger logger = Logger.getLogger(JBlackJack.class.getName());
    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting Black Jack");
        FXMLLoader fxmlLoader = new FXMLLoader(JBlackJack.class.getResource("/GameMenu/MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 834.0, 600.0);
        stage.setTitle("Blackjack");
        stage.getIcons().add(new Image(JBlackJack.class.getResourceAsStream("/playing-cards.png")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}