package Model.Game;

import Model.Game.Objects.Deck;
import Model.Players.AIPlayer;
import Model.Players.StrategyPlay.AggressiveStrategy;
import Model.Players.StrategyPlay.BalancedStrategy;
import Model.Players.StrategyPlay.ConservativeStrategy;
import Model.Players.Dealer;
import Model.Players.Player;
import Model.Players.PlayerFactory;
import Model.Players.StrategyPlay.PlayerStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Modello principale del gioco BlackJack.
 * Coordina i componenti e gestisce lo stato del gioco.
 */
public class GameModel extends Observable implements Observer {
    private Deck deck;
    private Player humanPlayer;
    private List<Player> players;
    private Dealer dealer;
    private Random random;
    private int currentBet;
    // Componenti specializzati
    private TurnManager turnManager;

    /**
     * Costruttore che inizializza il modello di gioco.
     *
     * @param playerName Nome del giocatore umano
     * @param initialBalance Saldo iniziale del giocatore
     * @param numDeck Numero di mazzi da utilizzare
     * @param numOfPlayers Numero di giocatori AI da creare
     */
    public GameModel(String playerName, int initialBalance, int numDeck, int numOfPlayers) {
        // Inizializza componenti di base
        this.deck = new Deck(numDeck);
        this.humanPlayer = PlayerFactory.createHumanPlayer(playerName, initialBalance);
        this.players = new ArrayList<>();
        this.dealer = PlayerFactory.createDealer();
        this.random = new Random();
        this.currentBet = 0;

        // Crea giocatori AI
        createAIPlayers(numOfPlayers);

        // Inizializza componenti specializzati
        this.turnManager = new TurnManager(humanPlayer, players, dealer, deck);
        this.turnManager.addObserver(this);
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
        if (betAmount <= 0 || betAmount > humanPlayer.getBalance()) {
            return false;
        }

        // Inizializza il mazzo e le mani
        deck.shuffle();
        humanPlayer.resetHand();
        dealer.resetHand();

        for (Player player : players) {
            player.resetHand();
        }

        currentBet = betAmount;
        humanPlayer.placeBet(betAmount);

        // Imposta scommesse casuali per i giocatori AI
        for (Player player : players) {
            if (player instanceof AIPlayer aiPlayer) {
                int aiBet = getAIBetAmount(aiPlayer);
                aiPlayer.placeBet(aiBet);
            }
        }

        // Avvia il round
        turnManager.startRound();
        return true;
    }

    /**
     * Determina l'importo della scommessa per un giocatore AI.
     *
     * @param aiPlayer Giocatore AI
     * @return Importo della scommessa
     */
    private int getAIBetAmount(AIPlayer aiPlayer) {
        PlayerStrategy strategy = aiPlayer.getStrategy();
        int minBet = 10;
        int maxBet = 100;
        int balance = aiPlayer.getBalance();

        // Limita la scommessa massima al saldo disponibile
        maxBet = Math.min(maxBet, balance);

        if (strategy instanceof AggressiveStrategy) {
            return Math.max(minBet, maxBet / 2 + random.nextInt(maxBet / 2));
        } else if (strategy instanceof ConservativeStrategy) {
            return minBet + random.nextInt(Math.max(1, (maxBet - minBet) / 3));
        } else {
            // BalancedStrategy
            return minBet + random.nextInt(Math.max(1, (maxBet - minBet) / 2));
        }
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

    /**
     * Riceve aggiornamenti dal TurnManager e li inoltra agli osservatori.
     */
    @Override
    public void update(Observable o, Object arg) {
        // Inoltra la notifica ai propri osservatori
        setChanged();
        notifyObservers(arg);
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

    /**
     * Restituisce la scommessa corrente.
     *
     * @return Importo della scommessa corrente
     */
    public int getCurrentBet() {
        return currentBet;
    }
}