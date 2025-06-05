package Controller;

/**
 * Interfaccia per la gestione delle azioni di scommessa nel BlackJack.
 * Definisce i metodi di callback per le operazioni relative alle puntate e assicurazioni.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public interface BlackjackBettingListener {
    
    /**
     * Chiamato quando il giocatore piazza una scommessa.
     * 
     * @param amount L'importo della scommessa piazzata
     */
    void onBetPlaced(int amount);
    
    /**
     * Chiamato quando il giocatore accetta l'assicurazione.
     */
    void onInsuranceAccepted();
    
    /**
     * Chiamato quando il giocatore rifiuta l'assicurazione.
     */
    void onInsuranceDeclined();
}
