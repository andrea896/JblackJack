package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class JBlackJack extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JBlackJack.class.getResource("/GameMenu/MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 834.0, 600.0);
        stage.setTitle("Black Jack");
        stage.getIcons().add(new Image(JBlackJack.class.getResourceAsStream("/playing-cards.png")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}