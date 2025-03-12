package Model.Game;

import Model.Game.Objects.Card;
import Model.Game.Objects.Deck;
import Model.Players.AIPlayer;
import Model.Players.Dealer;
import Model.Players.Player;
import Model.Players.StrategyPlay.AggressiveStrategy;
import Model.Players.StrategyPlay.ConservativeStrategy;
import Model.Players.StrategyPlay.PlayerStrategy;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Gestisce il flusso di gioco e i turni nel BlackJack.
 * Estende Observable per notificare gli osservatori dei cambiamenti di stato.
 */
public class TurnManager extends Observable {
    private GameState gameState;
    private int currentHandIndex;
    private Player humanPlayer;
    private List<Player> players;
    private Dealer dealer;
    private Deck deck;
    private ResultCalculator resultCalculator;
    private boolean insurancePaid;
    private BankManager bankManager;
    private int currentBet;
    private Random random;

    /**
     * Costruttore che inizializza il gestore dei turni.
     *
     * @param humanPlayer Giocatore umano
     * @param players Lista di tutti i giocatori AI
     * @param dealer Dealer
     * @param deck Mazzo di carte
     */
    public TurnManager(Player humanPlayer, List<Player> players, Dealer dealer, Deck deck) {
        this.humanPlayer = humanPlayer;
        this.players = players;
        this.dealer = dealer;
        this.deck = deck;
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        this.currentHandIndex = 0;
        this.insurancePaid = false;
        this.bankManager = new BankManager();
        this.resultCalculator = new ResultCalculator(bankManager);
        random = new Random();
    }

    /**
     * Distribuisce le carte iniziali e avvia il gioco.
     */
    public void startRound() {
        // Inizializza il mazzo e le mani
        deck.shuffle();
        humanPlayer.resetHand();
        dealer.resetHand();

        for (Player player : players)
            player.resetHand();

        // Imposta scommesse casuali per i giocatori AI
        int i = 0;
        for (Player player : players)
            if (player instanceof AIPlayer aiPlayer) {
                int aiBet = getAIBetAmount(aiPlayer);
                aiPlayer.placeBet(aiBet, currentHandIndex);
                // Notifica l'evento di scommessa piazzata per l'AI
                notifyObserversWithEvent(GameEventType.BET_PLACED,
                        "player", aiPlayer.getName(),
                        "amount", aiBet,
                        "indexHand", currentHandIndex,
                        "index", i++);
            }

        dealInitialCards();
        // Controlla blackjack naturali
        if (checkForNaturalBlackjacks()) {
            gameState = GameState.GAME_OVER;
        } else {
            // Inizia con il giocatore umano
            currentHandIndex = 0;
            gameState = GameState.PLAYER_TURN;
        }
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
        int maxBet = 300;
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
     * Distribuisce le carte iniziali a tutti i giocatori e al dealer.
     */
    private void dealInitialCards() {
        humanPlayer.addCard(deck.drawCard());
        notifyObserversWithEvent(GameEventType.CARD_DEALT,
                "player", humanPlayer,
                "handIndex",currentHandIndex,
                "card", humanPlayer.getHand(currentHandIndex).get(0));
        humanPlayer.addCard(deck.drawCard());

        notifyObserversWithEvent(GameEventType.CARD_DEALT,
                "player", humanPlayer,
                "handIndex",currentHandIndex,
                "card", humanPlayer.getHand(currentHandIndex).get(1));

        for (Player player : players) {
            if (player != humanPlayer) {
                player.addCard(deck.drawCard());
                player.addCard(deck.drawCard());
            }
        }

        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
    }

    /**
     * Controlla se ci sono blackjack naturali all'inizio del round.
     *
     * @return true se il round termina immediatamente, false altrimenti
     */
    private boolean checkForNaturalBlackjacks() {
        boolean dealerBlackjack = dealer.hasBlackjack(currentHandIndex);
        boolean playerBlackjack = humanPlayer.hasBlackjack(currentHandIndex);

        if (dealerBlackjack || playerBlackjack) {
            dealer.revealHiddenCard();
            if (dealerBlackjack && playerBlackjack) {
                // Pareggio - entrambi hanno blackjack
                bankManager.payPush(humanPlayer, currentHandIndex);
            } else if (playerBlackjack) {
                // Il giocatore vince con blackjack (paga 3:2)
                bankManager.payBlackjack(humanPlayer, currentHandIndex);
            } else if (dealerBlackjack) {
                // Il dealer vince con blackjack
                // Se il giocatore ha preso l'assicurazione, la paga
                if (humanPlayer.hasInsurance()) {
                    bankManager.payInsurance(humanPlayer);
                    insurancePaid = true;
                } else {
                    bankManager.handleLoss(humanPlayer, currentHandIndex);
                }
            }

            return true;
        }

        return false;
    }

    private void handleHandTransition(){
        if (humanPlayer.getHandCount() <= 1 || currentHandIndex >= humanPlayer.getHandCount() - 1) {
            gameState = GameState.AI_PLAYER_TURN;
            notifyObservers();
            playAITurns();
        } else {
            currentHandIndex++;
            notifyObservers();
        }
    }

    /**
     * Gestisce l'azione "Hit" del giocatore umano.
     */
    public void playerHit() {
        if (gameState != GameState.PLAYER_TURN) return;

        if (humanPlayer.isBusted(currentHandIndex) || humanPlayer.getHandValue(currentHandIndex) >= 21) return;

        humanPlayer.addCard(currentHandIndex, deck.drawCard());
        notifyObservers();

        if (humanPlayer.getHandValue(currentHandIndex) >= 21)
            handleHandTransition();
    }

    /**
     * Gestisce l'azione "Stand" del giocatore umano.
     */
    public void playerStand() {
        if (gameState != GameState.PLAYER_TURN) return;
        handleHandTransition();
    }

    /**
     * Gestisce l'azione "Double Down" del giocatore.
     *
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean doubleDown() {
        if (gameState != GameState.PLAYER_TURN) return false;

        boolean success = humanPlayer.canDoubleDown(currentHandIndex) &&
                            bankManager.handleDoubleDown(humanPlayer, currentHandIndex);

        if (success) {
            humanPlayer.addCard(currentHandIndex, deck.drawCard());
            notifyObservers();
            handleHandTransition();
            return true;
        }

        return false;
    }

    /**
     * Gestisce l'azione "Split" del giocatore.
     *
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean splitHand() {
        if (gameState != GameState.PLAYER_TURN) return false;

        boolean success = humanPlayer.canSplit(currentHandIndex) &&
                                bankManager.handleSplit(humanPlayer, currentHandIndex) &&
                                humanPlayer.splitHand(currentHandIndex, deck.drawCard(), deck.drawCard());
        if (success) {
            notifyObservers();
            return true;
        }

        return false;
    }

    /**
     * Gestisce l'azione "Insurance" del giocatore.
     *
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean takeInsurance() {
        if (gameState == GameState.PLAYER_TURN &&
                dealer.getHand(0).get(0).isAce() &&
                !humanPlayer.hasInsurance()) {

            boolean success = humanPlayer.takeInsurance() && bankManager.placeInsurance(humanPlayer);
            if (success) {
                notifyObservers();
                return true;
            }
        }

        return false;
    }

    /**
     * Gestisce i turni dei giocatori AI.
     */
    private void playAITurns() {
        for (Player player : players) {
            if (player instanceof AIPlayer aiPlayer) {
                // Per ciascuna mano del giocatore AI (in caso di split)
                for (int handIndex = 0; handIndex < aiPlayer.getHandCount(); handIndex++) {
                    PlayerStrategy strategy = aiPlayer.getStrategy();
                    boolean continuePlaying = true;

                    while (continuePlaying && aiPlayer.getHandValue(handIndex) < 21) {
                        Card dealerUpCard = dealer.getHand(0).get(0); // La carta visibile del dealer
                        int handValue = aiPlayer.getHandValue(handIndex);

                        // Verifica se fare Split (ha priorità più alta)
                        if (aiPlayer.getHands().get(handIndex).getCards().size() == 2) {
                            Card card1 = aiPlayer.getHands().get(handIndex).getCards().get(0);
                            Card card2 = aiPlayer.getHands().get(handIndex).getCards().get(1);

                            if (strategy.shouldSplitHand(card1, card2, dealerUpCard)) {
                                if (aiPlayer.canSplit(handIndex)) {
                                    aiPlayer.splitHand(handIndex, deck.drawCard(), deck.drawCard());
                                    // Continua a giocare questa mano dopo lo split
                                    continue;
                                }
                            }
                        }

                        // Verifica se fare Double Down (seconda priorità)
                        if (aiPlayer.getHands().get(handIndex).getCards().size() == 2 &&
                                strategy.shouldPlayDoubleDown(handValue, dealerUpCard)) {
                            if (aiPlayer.canDoubleDown(handIndex)) {
                                aiPlayer.doubleDown(handIndex, deck.drawCard());
                                continuePlaying = false; // Dopo Double Down il turno termina
                                continue;
                            }
                        }

                        // Verifica se prendere l'Insurance (solo per la prima mano)
                        if (handIndex == 0 && dealerUpCard.isAce() &&
                                strategy.shouldTakeInsurance(handValue, dealerUpCard) &&
                                aiPlayer.getHand(handIndex).size() <= 2) {
                            aiPlayer.takeInsurance();
                        }

                        // Infine, decide se pescare o stare
                        if (strategy.shouldDraw(handValue)) {
                            aiPlayer.addCard(handIndex, deck.drawCard());

                            // Se sballa, termina il turno per questa mano
                            if (aiPlayer.getHandValue(handIndex) > 21) {
                                continuePlaying = false;
                            }
                        } else {
                            // Decide di stare
                            continuePlaying = false;
                        }
                    }
                }
            }
        }

        // Dopo che tutti i giocatori AI hanno completato i loro turni, passa al dealer
        gameState = GameState.DEALER_TURN;
        notifyObservers();
        playDealerTurn();
    }

    /**
     * Gestisce il turno del dealer.
     */
    private void playDealerTurn() {
        // Prima rivela la carta nascosta del dealer
        dealer.revealHiddenCard();
        notifyObservers(); // Notifica subito per mostrare la carta nascosta
        // Verifica se tutti i giocatori hanno sballato
        boolean allPlayersBusted = isAllPlayersBusted();
        // Se tutti i giocatori hanno sballato, il dealer non pesca e vince automaticamente
        if (!allPlayersBusted) {
            PlayerStrategy strategy = dealer.getStrategy();
            // Il dealer deve giocare secondo le regole standard
            while (strategy.shouldDraw(dealer.getHandValue(0))) {
                dealer.addCard(deck.drawCard());
                notifyObservers(); // Notifica ad ogni carta per animazioni
                // Interrompi se il dealer sballa
                if (dealer.isBusted(0)) {
                    break;
                }
            }
        }
        endRound();
    }

    /**
     * Verifica se tutti i giocatori hanno sballato.
     *
     * @return true se tutti i giocatori hanno sballato, false altrimenti
     */
    private boolean isAllPlayersBusted() {
        // Controlla il giocatore umano
        boolean humanBusted = true;
        for (int i = 0; i < humanPlayer.getHandCount(); i++)
            if (!humanPlayer.isBusted(i)) {
                humanBusted = false;
                break;
            }
        // Se il giocatore umano non ha sballato, almeno un giocatore non ha sballato
        if (!humanBusted) return false;
        // Controlla tutti gli altri giocatori AI
        for (Player player : players)
            if (player != humanPlayer)
                for (int i = 0; i < player.getHandCount(); i++)
                    if (!player.isBusted(i))
                        return false; // Trovato almeno un giocatore che non ha sballato
        // Se arriviamo qui, tutti i giocatori hanno sballato
        return true;
    }

    /**
     * Termina il round e determina i risultati.
     */
    private void endRound() {
        // Gestisci l'assicurazione se il dealer ha un blackjack
        if (dealer.hasBlackjack(currentHandIndex) && !insurancePaid) {
            // Paga l'assicurazione a tutti i giocatori che l'hanno presa
            resultCalculator.processInsuranceOutcomes(humanPlayer, players, dealer, insurancePaid);
            insurancePaid = true;
        } else {
            // Se il dealer non ha blackjack, tutti perdono le assicurazioni
            resultCalculator.clearInsurance(humanPlayer, players);
        }
        // Calcola i risultati per tutti i giocatori
        resultCalculator.calculateResults(humanPlayer, players, dealer);
        // Aggiorna lo stato del gioco
        gameState = GameState.GAME_OVER;
        notifyObservers();
    }

    /**
     * Notifica gli osservatori con un evento di gioco.
     *
     * @param eventType Il tipo di evento da notificare
     * @param keyValuePairs Coppie chiave-valore per i dati dell'evento
     */
    private void notifyObserversWithEvent(GameEventType eventType, Object... keyValuePairs) {
        // Crea l'evento utilizzando il metodo helper di GameEvent
        GameEvent event = GameEvent.create(eventType, keyValuePairs);
        // Imposta l'evento come argomento della notifica
        setChanged();
        // Notifica tutti gli osservatori con l'evento
        notifyObservers(event);
    }

    /**
     * Restituisce lo stato corrente del gioco.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Restituisce l'indice della mano corrente.
     */
    public int getCurrentHandIndex() {
        return currentHandIndex;
    }

    public int getCurrentbet(){
        return currentBet;
    }
}