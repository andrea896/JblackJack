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
        configureLogger();
        logger.info("Starting Black Jack");
        FXMLLoader fxmlLoader = new FXMLLoader(JBlackJack.class.getResource("/GameMenu/MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 834.0, 600.0);
        stage.setTitle("Black Jack");
        stage.getIcons().add(new Image(JBlackJack.class.getResourceAsStream("/playing-cards.png")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        // Log di debug aggiuntivi
        System.out.println("Debug: Tentativo di log");
        logger.severe("Log di errore");
        logger.warning("Log di avvertimento");
        logger.info("Starting Black Jack");
        logger.fine("Dettagli aggiuntivi");
    }

    private void configureLogger() {
        // Rimuovi eventuali handler esistenti
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers)
            rootLogger.removeHandler(handler);


        // Aggiungi un handler per la console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());

        // Configura il logger
        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false); // Impedisce la propagazione ai logger padre
    }

    public static void main(String[] args) {
        launch();
    }
}