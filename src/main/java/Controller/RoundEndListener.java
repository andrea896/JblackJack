package Controller;

/**
 * Interfaccia per la gestione degli eventi di fine round nel BlackJack.
 * Definisce i metodi di callback per le azioni disponibili al termine di ogni mano.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public interface RoundEndListener {
    /**
     * Chiamato quando il giocatore richiede di iniziare un nuovo round.
     */
    void onNewRoundRequested();
    
    /**
     * Chiamato quando il giocatore richiede di uscire dal gioco.
     */
    void onExitRequested();
    
    /**
     * Chiamato quando il giocatore richiede di ricaricare il saldo.
     * 
     * @param amount L'importo da aggiungere al saldo corrente
     */
    void onBalanceReloadRequested(int amount);
}
