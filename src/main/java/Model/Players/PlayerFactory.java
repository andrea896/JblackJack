package Model.Players;

import Model.Players.StrategyPlay.AggressiveStrategy;
import Model.Players.StrategyPlay.PlayerStrategy;

/**
 * Factory per la creazione di diversi tipi di giocatori nel BlackJack.
 * Fornisce metodi statici per creare giocatori umani, AI e il dealer.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class PlayerFactory {

    /**
     * Crea un nuovo giocatore umano.
     * 
     * @param playerName Il nome del giocatore
     * @param initialBalance Il saldo iniziale del giocatore
     * @return Un nuovo oggetto Player per il giocatore umano
     */
    public static Player createHumanPlayer(String playerName, int initialBalance) {
        return new Player(playerName, initialBalance);
    }

    /**
     * Crea un nuovo giocatore AI con una strategia specifica.
     * 
     * @param playerName Il nome del giocatore AI
     * @param strategy La strategia di gioco da utilizzare
     * @return Un nuovo oggetto AIPlayer
     */
    public static Player createAIPlayer(String playerName, PlayerStrategy strategy) {
        return new AIPlayer(playerName, strategy);
    }

    /**
     * Crea un nuovo dealer con strategia aggressiva.
     * Il dealer utilizza sempre una strategia aggressiva per rendere il gioco più interessante.
     * 
     * @return Un nuovo oggetto Dealer
     */
    public static Dealer createDealer() {
        return new Dealer(new AggressiveStrategy());
    }
}
