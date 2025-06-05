package Model.Players;

import Model.Players.StrategyPlay.PlayerStrategy;

/**
 * Rappresenta un giocatore controllato dalla CPU nel BlackJack.
 * Estende la classe Player aggiungendo una strategia di gioco specifica.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class AIPlayer extends Player {
    private PlayerStrategy strategy;

    /**
     * Costruisce un nuovo giocatore AI con una strategia specifica.
     * Il giocatore AI inizia sempre con un saldo di 2000.
     * 
     * @param name Il nome del giocatore AI
     * @param strategy La strategia di gioco da utilizzare
     */
    public AIPlayer(String name, PlayerStrategy strategy) {
        super(name, 2000);
        this.strategy = strategy;
    }

    /**
     * Restituisce la strategia di gioco corrente dell'AI.
     * 
     * @return La strategia di gioco
     */
    public PlayerStrategy getStrategy() {
        return strategy;
    }

}
