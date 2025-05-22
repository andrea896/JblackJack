package Controller;

import java.util.logging.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Classe principale dell'applicazione BlackJack.
 * Estende la classe Application di JavaFX per inizializzare e avviare l'interfaccia grafica.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class JBlackJack extends Application {
    /** Logger per la registrazione degli eventi dell'applicazione */
    static final Logger logger = Logger.getLogger(JBlackJack.class.getName());
    
    /**
     * Metodo di avvio dell'applicazione JavaFX.
     * Carica l'interfaccia FXML principale e configura la finestra dell'applicazione.
     * 
     * @param stage Lo stage principale dell'applicazione
     * @throws IOException Se si verifica un errore nel caricamento del file FXML
     */
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

    /**
     * Metodo main dell'applicazione.
     * Punto di ingresso dell'applicazione che avvia JavaFX.
     * 
     * @param args Argomenti della riga di comando
     */
    public static void main(String[] args) {
        launch();
    }
}