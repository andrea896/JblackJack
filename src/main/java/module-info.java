/**
 * Modulo principale dell'applicazione JBlackJack.<br>
 * 
 * Questo modulo implementa un gioco completo di BlackJack con interfaccia grafica JavaFX,<br>
 * sistema audio, gestione profili utente, giocatori AI con strategie diverse,<br>
 * e un sistema completo di scommesse e statistiche.
 * 
 * <h2>Caratteristiche principali:</h2>
 * <ul>
 *   <li>Interfaccia grafica moderna realizzata con JavaFX</li>
 *   <li>Sistema audio completo con effetti sonori</li>
 *   <li>Gestione profili utente con persistenza JSON</li>
 *   <li>Giocatori AI con strategie configurabili</li>
 *   <li>Sistema di logging</li>
 *   <li>Animazioni e transizioni fluide</li>
 * </ul>
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
module com.example.jblackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;

    requires com.google.gson;
    requires java.logging;

    requires java.desktop;

    exports Controller;

    exports Model.Game;
    exports Model.Game.Objects;
    exports Model.Players;
    exports Model.Players.StrategyPlay;
    exports Model.Profile;

    exports View;

    exports Utility;

    opens Controller to javafx.fxml;

    opens Model.Profile to com.google.gson, javafx.fxml;
    opens Model.Game to com.google.gson;
    opens Model.Game.Objects to com.google.gson;
    opens Model.Players to com.google.gson;
    
    opens View to javafx.fxml;
}
