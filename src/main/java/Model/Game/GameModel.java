package Model.Game;

import Model.Game.Objects.Deck;
import Model.Players.StrategyPlay.AggressiveStrategy;
import Model.Players.StrategyPlay.BalancedStrategy;
import Model.Players.StrategyPlay.ConservativeStrategy;
import Model.Players.Dealer;
import Model.Players.Player;
import Model.Players.PlayerFactory;
import Model.Players.StrategyPlay.PlayerStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Modello principale del gioco BlackJack.
 * Coordina i componenti e gestisce lo stato del gioco.
 */
public class GameModel {
    private Deck deck;
    private Player humanPlayer;
    private List<Player> players;
    private Dealer dealer;
    private Random random;
    private TurnManager turnManager;

    /**
     * Costruttore che inizializza il modello di gioco.
     *
     * @param playerName Nome del giocatore umano
     * @param initialBalance Saldo iniziale del giocatore
     * @param numOfPlayers Numero di giocatori AI
     */
    public GameModel(String playerName, int initialBalance, int numOfPlayers) {
        this.deck = new Deck();
        this.humanPlayer = PlayerFactory.createHumanPlayer(playerName, initialBalance);
        this.players = new ArrayList<>();
        this.dealer = PlayerFactory.createDealer();
        this.random = new Random();
        createAIPlayers(numOfPlayers);
        this.turnManager = new TurnManager(humanPlayer, players, dealer, deck);
    }

    /**
     * Crea i giocatori AI con strategie casuali.
     *
     * @param numOfPlayers Numero di giocatori AI da creare
     */
    private void createAIPlayers(int numOfPlayers) {
        for (int i = 0; i < numOfPlayers; i++) {
            Player aiPlayer = PlayerFactory.createAIPlayer("AI_Player_" + (i + 1), getRandomStrategy());
            players.add(aiPlayer);
        }
    }

    /**
     * Genera una strategia casuale per i giocatori AI.
     *
     * @return Una strategia casuale
     */
    private PlayerStrategy getRandomStrategy() {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0: return new AggressiveStrategy();
            case 1: return new ConservativeStrategy();
            case 2: return new BalancedStrategy();
            default: return new BalancedStrategy();
        }
    }

    /**
     * Avvia una nuova partita con una scommessa iniziale.
     *
     * @param betAmount Importo della scommessa
     * @return true se il gioco è stato avviato con successo, false altrimenti
     */
    public boolean startGame(int betAmount) {
        if (betAmount <= 0 || betAmount > humanPlayer.getBalance())
            return false;

        turnManager.startRound();
        return true;
    }

    /**
     * Gestisce l'azione "Hit" del giocatore umano.
     */
    public void playerHit() {
        turnManager.playerHit();
    }

    /**
     * Gestisce l'azione "Stand" del giocatore umano.
     */
    public void playerStand() {
        turnManager.playerStand();
    }

    /**
     * Gestisce l'azione "Double Down" del giocatore.
     */
    public void doubleDown() {
        turnManager.doubleDown();
    }

    /**
     * Gestisce l'azione "Split" del giocatore.
     */
    public void splitHand() {
        turnManager.splitHand();
    }

    /**
     * Gestisce l'azione "Insurance" del giocatore.
     */
    public void takeInsurance() {
        turnManager.takeInsurance();
    }

    public void declineInsurance() {
        turnManager.declineInsurance();
    }

    /**
     * Restituisce il giocatore umano.
     *
     * @return Giocatore umano
     */
    public Player getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Restituisce il dealer.
     *
     * @return Dealer
     */
    public Dealer getDealer() {
        return dealer;
    }

    /**
     * Restituisce la lista dei giocatori AI.
     *
     * @return Lista dei giocatori AI
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Restituisce lo stato corrente del gioco.
     *
     * @return Stato del gioco
     */
    public GameState getGameState() {
        return turnManager.getGameState();
    }

    /**
     * Restituisce l'indice della mano corrente del giocatore umano.
     * Utile quando il giocatore ha mani multiple dopo uno split.
     *
     * @return Indice della mano corrente
     */
    public int getCurrentHandIndex() {
        return turnManager.getCurrentHandIndex();
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }
}