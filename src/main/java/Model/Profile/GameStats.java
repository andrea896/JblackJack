package Model.Profile;

/**
 * Classe che rappresenta le statistiche di gioco di un utente.
 * Traccia le performance del giocatore nel tempo, incluse mani giocate, vinte, perse e saldo.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class GameStats {
    private int totalHandsPlayed;
    private int handsWon;
    private int handsLost;
    private int currentBalance;

    /**
     * Costruisce nuove statistiche di gioco con valori iniziali.
     * Il giocatore inizia con 1000 di saldo e nessuna mano giocata.
     */
    public GameStats() {
        this.totalHandsPlayed = 0;
        this.handsWon = 0;
        this.handsLost = 0;
        this.currentBalance = 1000;
    }

    /**
     * Restituisce il numero totale di mani giocate.
     * 
     * @return Il numero totale di mani giocate
     */
    public int getTotalHandsPlayed() {
        return totalHandsPlayed;
    }

    /**
     * Imposta il numero totale di mani giocate.
     * 
     * @param totalHandsPlayed Il nuovo numero totale di mani giocate
     */
    public void setTotalHandsPlayed(int totalHandsPlayed) {
        this.totalHandsPlayed = totalHandsPlayed;
    }

    /**
     * Restituisce il numero di mani vinte.
     * 
     * @return Il numero di mani vinte
     */
    public int getHandsWon() {
        return handsWon;
    }

    /**
     * Imposta il numero di mani vinte.
     * 
     * @param handsWon Il nuovo numero di mani vinte
     */
    public void setHandsWon(int handsWon) {
        this.handsWon = handsWon;
    }

    /**
     * Restituisce il numero di mani perse.
     * 
     * @return Il numero di mani perse
     */
    public int getHandsLost() {
        return handsLost;
    }

    /**
     * Imposta il numero di mani perse.
     * 
     * @param handsLost Il nuovo numero di mani perse
     */
    public void setHandsLost(int handsLost) {
        this.handsLost = handsLost;
    }

    /**
     * Restituisce il saldo corrente del giocatore.
     * 
     * @return Il saldo corrente
     */
    public int getCurrentBalance() {
        return currentBalance;
    }

    /**
     * Imposta il saldo corrente del giocatore.
     * 
     * @param currentBalance Il nuovo saldo corrente
     */
    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }
}
