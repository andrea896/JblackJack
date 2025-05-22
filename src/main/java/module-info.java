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
    // Dipendenze JavaFX Core
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Librerie UI avanzate
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    
    // Gestione JSON e logging
    requires com.google.gson;
    requires java.logging;
    
    // Integrazione desktop per audio
    requires java.desktop;

    // ===== PACKAGE EXPORTS =====
    // Controller layer - Game control and coordination
    exports Controller;
    
    // Model layer - Core game logic
    exports Model.Game;
    exports Model.Game.Objects;
    exports Model.Players;
    exports Model.Players.StrategyPlay;
    exports Model.Profile;
    
    // View layer - User interface components
    exports View;
    
    // Utility layer - Cross-cutting concerns
    exports Utility;

    // ===== PACKAGE OPENS =====
    // Package aperti per JavaFX FXML injection
    opens Controller to javafx.fxml;
    
    // Package aperti per serializzazione JSON
    opens Model.Profile to com.google.gson, javafx.fxml;
    opens Model.Game to com.google.gson;
    opens Model.Game.Objects to com.google.gson;
    opens Model.Players to com.google.gson;
    
    // Package aperti per reflection (se necessario per JavaFX)
    opens View to javafx.fxml;
}
