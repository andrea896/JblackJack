package Model.Game;

import Model.Game.Objects.Card;
import Model.Game.Objects.Deck;
import Model.Game.Objects.Rank;
import Model.Game.Objects.Suit;
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
            notifyObserversWithEvent(GameEventType.GAME_STATE_CHANGED);
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
        //Card humanCard1 = deck.drawCard();
        Card humanCard1 = new Card(Rank.EIGHT, Suit.CLUBS);
        humanPlayer.addCard(humanCard1);
        createCardDealtEvent(humanPlayer, humanCard1, currentHandIndex, false);

        //Card humanCard2 = deck.drawCard();
        Card humanCard2 = new Card(Rank.EIGHT, Suit.HEARTS);
        humanPlayer.addCard(humanCard2);
        createCardDealtEvent(humanPlayer, humanCard2, currentHandIndex, false);

        for (Player player : players) {
            if (player != humanPlayer) {
                Card card1 = deck.drawCard();
                player.addCard(card1);
                createCardDealtEvent(player, card1, 0, false);
                Card card2 = deck.drawCard();
                player.addCard(card2);
                createCardDealtEvent(player, card2, 0, false);
            }
        }
        Card dealerCard1 = deck.drawCard();
        dealer.addCard(dealerCard1);
        createCardDealtEvent(dealer, dealerCard1, 0, true);
        Card dealerCard2 = deck.drawCard();
        dealer.addCard(dealerCard2);
        createCardDealtEvent(dealer, dealerCard2, currentHandIndex, false);
    }

    /**
     * Controlla se ci sono blackjack naturali all'inizio del round.
     *
     * @return true se il round termina immediatamente, false altrimenti
     */
    private boolean checkForNaturalBlackjacks() {
        boolean dealerBlackjack = dealer.hasBlackjack(0);

        // Verifica se qualcuno ha blackjack (dealer o giocatori)
        boolean anyBlackjack = dealerBlackjack;

        // Controlla il giocatore umano
        boolean humanBlackjack = humanPlayer.hasBlackjack(0);
        if (humanBlackjack) {
            anyBlackjack = true;
        }

        // Controlla i giocatori AI
        for (Player player : players) {
            if (player.hasBlackjack(0)) {
                anyBlackjack = true;
                break;
            }
        }

        // Se nessuno ha blackjack, il round continua normalmente
        if (!anyBlackjack) {
            return false;
        }

        // Rivela la carta nascosta del dealer poiché almeno qualcuno ha blackjack
        dealer.revealHiddenCard();
        notifyObserversWithEvent(GameEventType.DEALER_CARD_REVEALED);

        // Gestisci i risultati per il giocatore umano
        processBlackjackResult(humanPlayer, humanBlackjack, dealerBlackjack);

        // Gestisci i risultati per i giocatori AI
        for (Player player : players)
            if (player != humanPlayer)
                processBlackjackResult(player, player.hasBlackjack(0), dealerBlackjack);

        return true;
    }

    /**
     * Elabora i risultati del blackjack naturale per un giocatore.
     *
     * @param player Il giocatore da elaborare
     * @param playerBlackjack Se il giocatore ha blackjack
     * @param dealerBlackjack Se il dealer ha blackjack
     */
    private void processBlackjackResult(Player player, boolean playerBlackjack, boolean dealerBlackjack) {
        if (dealerBlackjack && playerBlackjack) {
            // Pareggio - entrambi hanno blackjack
            bankManager.payPush(player, 0);

            notifyObserversWithEvent(GameEventType.PUSH,
                    "player", player.getName(),
                    "handIndex", 0);

        } else if (playerBlackjack) {
            // Il giocatore vince con blackjack (paga 3:2)
            bankManager.payBlackjack(player, 0);

            notifyObserversWithEvent(GameEventType.BLACKJACK_ACHIEVED,
                    "player", player.getName(),
                    "handIndex", 0);

        } else if (dealerBlackjack) {
            // Il dealer vince con blackjack
            if (player.hasInsurance()) {
                bankManager.payInsurance(player);
                if (player == humanPlayer) {
                    insurancePaid = true;
                }

                notifyObserversWithEvent(GameEventType.WINNINGS_PAID,
                        "player", player.getName(),
                        "type", "insurance",
                        "amount", player.getInsuranceAmount() * 2);
            } else {
                bankManager.handleLoss(player, 0);

                notifyObserversWithEvent(GameEventType.DEALER_WINS,
                        "player", player.getName(),
                        "handIndex", 0);
            }
        }
    }

    private void handleHandTransition(){
        if (humanPlayer.getHandCount() <= 1 || currentHandIndex >= humanPlayer.getHandCount() - 1) {
            gameState = GameState.AI_PLAYER_TURN;
            notifyObserversWithEvent(GameEventType.PLAYER_STAND);
            playAITurns();
        } else {
            currentHandIndex++;
            //notifyObservers();
        }
    }

    /**
     * Gestisce l'azione "Hit" del giocatore umano.
     */
    public void playerHit() {
        if (gameState != GameState.PLAYER_TURN) return;

        if (humanPlayer.isBusted(currentHandIndex) || humanPlayer.getHandValue(currentHandIndex) >= 21) return;

        Card card = deck.drawCard();
        humanPlayer.addCard(currentHandIndex, card);
        createCardDealtEvent(humanPlayer, card, currentHandIndex, false);

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
            Card card = deck.drawCard();
            humanPlayer.addCard(currentHandIndex, card);
            createCardDealtEvent(humanPlayer, card, currentHandIndex, false);
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
                                bankManager.handleSplit(humanPlayer, currentHandIndex);

        if (success) {
            // Ottieni le nuove carte da aggiungere dopo lo split
            Card newCard1 = deck.drawCard();
            Card newCard2 = deck.drawCard();
            humanPlayer.splitHand(currentHandIndex, newCard1, newCard2);
            // Notifica l'evento di split con informazioni sulle carte
            notifyObserversWithEvent(GameEventType.HAND_SPLIT,
                    "player", humanPlayer,
                    "newCard1", newCard1,
                    "newCard2", newCard2,
                    "handValue1", humanPlayer.getHandValue(currentHandIndex),
                    "handValue2", humanPlayer.getHandValue(currentHandIndex + 1),
                    "bet", humanPlayer.getCurrentBet());
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
                dealer.getHand(0).get(1).isAce() &&
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
                                    // Notifica lo split
                                    Card newCard1 = deck.drawCard();
                                    Card newCard2 = deck.drawCard();
                                    aiPlayer.splitHand(handIndex, newCard1, newCard2);
                                    notifyObserversWithEvent(GameEventType.HAND_SPLIT,
                                            "player", aiPlayer,
                                            "newCard1", newCard1,
                                            "newCard2", newCard2);

                                    createCardDealtEvent(player, newCard1, handIndex, false);
                                    createCardDealtEvent(player, newCard2, handIndex, false);

                                    // Notifica l'aggiornamento dei valori delle mani
                                    notifyObserversWithEvent(GameEventType.HAND_UPDATED,
                                            "player", aiPlayer,
                                            "handIndex", handIndex,
                                            "value", aiPlayer.getHandValue(handIndex));

                                    notifyObserversWithEvent(GameEventType.HAND_UPDATED,
                                            "player", aiPlayer,
                                            "handIndex", handIndex + 1,
                                            "value", aiPlayer.getHandValue(handIndex + 1));

                                    continue;
                                }
                            }
                        }

                        // Verifica se fare Double Down (seconda priorità)
                        if (aiPlayer.getHands().get(handIndex).getCards().size() == 2 &&
                                strategy.shouldPlayDoubleDown(handValue, dealerUpCard)) {
                            if (aiPlayer.canDoubleDown(handIndex)) {
                                Card card = deck.drawCard();
                                aiPlayer.doubleDown(handIndex, card);

                                notifyObserversWithEvent(GameEventType.DOUBLE_DOWN_EXECUTED,
                                        "player", aiPlayer,
                                        "handIndex", handIndex,
                                        "newBet", aiPlayer.getHands().get(handIndex).getBet());

                                createCardDealtEvent(aiPlayer, card, handIndex, false);
                                // Notifica l'aggiornamento della mano
                                notifyObserversWithEvent(GameEventType.HAND_UPDATED,
                                        "player", aiPlayer,
                                        "handIndex", handIndex,
                                        "value", aiPlayer.getHandValue(handIndex));

                                continuePlaying = false;
                                continue;
                            }
                        }

                        // Verifica se prendere l'Insurance (solo per la prima mano)
                        if (handIndex == 0 && dealerUpCard.isAce() &&
                                strategy.shouldTakeInsurance(handValue, dealerUpCard) &&
                                aiPlayer.getHand(handIndex).size() <= 2) {
                            aiPlayer.takeInsurance();

                            notifyObserversWithEvent(GameEventType.INSURANCE_ACCEPTED,
                                    "player", aiPlayer,
                                    "amount", aiPlayer.getInsuranceAmount());
                        }

                        // Infine, decide se pescare o stare
                        if (strategy.shouldDraw(handValue)) {
                            // Notifica hit
                            Card card = deck.drawCard();
                            aiPlayer.addCard(handIndex, card);
                            notifyObserversWithEvent(GameEventType.PLAYER_HIT,
                                    "player", aiPlayer,
                                    "card", card,
                                    "handIndex", handIndex);

                            // Se sballa, termina il turno per questa mano
                            if (aiPlayer.getHandValue(handIndex) > 21) {
                                continuePlaying = false;
                                notifyObserversWithEvent(GameEventType.PLAYER_BUSTED,
                                        "player", aiPlayer,
                                        "handIndex", handIndex);
                            }
                        } else {
                            // Decide di stare
                            notifyObserversWithEvent(GameEventType.PLAYER_STAND,
                                    "player", aiPlayer.getName(),
                                    "handIndex", handIndex);
                            continuePlaying = false;
                        }
                    }
                }
            }
        }

        // Dopo che tutti i giocatori AI hanno completato i loro turni, passa al dealer
        gameState = GameState.DEALER_TURN;
        playDealerTurn();
    }

    /**
     * Gestisce il turno del dealer.
     */
    private void playDealerTurn() {
        // Prima rivela la carta nascosta del dealer
        notifyObserversWithEvent(GameEventType.DEALER_TURN_STARTED, "card", dealer.getHiddenCard());
        dealer.revealHiddenCard();
        // Verifica se tutti i giocatori hanno sballato
        boolean allPlayersBusted = isAllPlayersBusted();
        // Se tutti i giocatori hanno sballato, il dealer non pesca e vince automaticamente
        if (!allPlayersBusted) {
            PlayerStrategy strategy = dealer.getStrategy();
            // Il dealer deve giocare secondo le regole standard
            while (strategy.shouldDraw(dealer.getHandValue(0))) {
                Card newCard = deck.drawCard();
                dealer.addCard(0, newCard);
                createCardDealtEvent(dealer, newCard, 0, false);
                // Interrompi se il dealer sballa
                if (dealer.isBusted(0)) {
                    notifyObserversWithEvent(GameEventType.DEALER_BUSTED);
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
        humanPlayer.resetHand();
        dealer.resetHand();

        for (Player player : players)
            player.resetHand();
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
        event = null;
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

    /**
     * Crea un evento per notificare che una carta è stata distribuita
     * @param player Il giocatore che ha ricevuto la carta
     * @param card La carta distribuita
     * @param handIndex L'indice della mano (per supportare split)
     * @param isHiddenCard Indica se la carta è nascosta (solo per il dealer)
     */
    private void createCardDealtEvent(Player player, Card card, int handIndex, boolean isHiddenCard) {
        if (player instanceof Dealer) {
            notifyObserversWithEvent(GameEventType.CARD_DEALT,
                    "player", player,
                    "card", card,
                    "isHiddenCard", isHiddenCard);
        } else {
            // Caso per i giocatori normali
            notifyObserversWithEvent(GameEventType.CARD_DEALT,
                    "player", player,
                    "handIndex", handIndex,
                    "card", card);
        }
    }
}