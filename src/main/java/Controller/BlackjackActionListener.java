package Controller;

/**
 * Interfaccia per la gestione delle azioni del giocatore nel BlackJack.
 * Definisce i metodi di callback per le principali azioni di gioco.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public interface BlackjackActionListener {
    
    /**
     * Chiamato quando il giocatore preme il pulsante "Hit" per pescare una carta.
     */
    void onHitButtonPressed();
    
    /**
     * Chiamato quando il giocatore preme il pulsante "Stand" per fermarsi.
     */
    void onStandButtonPressed();
    
    /**
     * Chiamato quando il giocatore preme il pulsante "Double Down" per raddoppiare la puntata.
     */
    void onDoubleDownPressed();
    
    /**
     * Chiamato quando il giocatore preme il pulsante "Split" per dividere la mano.
     */
    void onSplitButtonPressed();
}
