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
import java.util.*;
import java.util.stream.IntStream;

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
        deck.shuffle();
        notifyObserversWithEvent(GameEventType.GAME_STARTED);
        int i = 0;
        for (Player player : players)
            if (player instanceof AIPlayer aiPlayer) {
                int aiBet = getAIBetAmount(aiPlayer);
                aiPlayer.placeBet(aiBet, 0);
                // Notifica l'evento di scommessa piazzata per l'AI
                notifyObserversWithEvent(GameEventType.BET_PLACED,
                        "player", aiPlayer.getName(),
                        "amount", aiBet,
                        "indexHand", currentHandIndex,
                        "index", i++);
            }
        currentHandIndex = 0;
        dealInitialCards();
        gameState = GameState.PLAYER_TURN;
        if (gameState == GameState.PLAYER_TURN &&
                dealer.getHand(0).get(0).isAce() &&
                !humanPlayer.hasInsurance()) {
                notifyObserversWithEvent(GameEventType.INSURANCE_OFFERED);
        }
        if (humanPlayer.hasBlackjack(currentHandIndex)) {
            notifyObserversWithEvent(GameEventType.BLACKJACK_ACHIEVED, "player", humanPlayer, "handIndex", currentHandIndex);
            playerStand();
        }
        notifyObserversWithEvent(GameEventType.HAND_UPDATED);
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
        maxBet = Math.min(maxBet, balance);

        if (strategy instanceof AggressiveStrategy) {
            return Math.max(minBet, maxBet / 2 + random.nextInt(maxBet / 2));
        } else if (strategy instanceof ConservativeStrategy) {
            return minBet + random.nextInt(Math.max(1, (maxBet - minBet) / 3));
        } else {
            return minBet + random.nextInt(Math.max(1, (maxBet - minBet) / 2));
        }
    }

    /**
     * Distribuisce le carte iniziali a tutti i giocatori e al dealer.
     */
    private void dealInitialCards() {
        //Card humanCard1 = deck.drawCard();
        Card humanCard1 = new Card(Rank.TEN, Suit.CLUBS);
        humanPlayer.addCard(humanCard1);
        createCardDealtEvent(humanPlayer, humanCard1, currentHandIndex, false);

        //Card humanCard2 = deck.drawCard();
        Card humanCard2 = new Card(Rank.TEN, Suit.HEARTS);
        humanPlayer.addCard(humanCard2);
        createCardDealtEvent(humanPlayer, humanCard2, currentHandIndex, false);

        for (Player player : players) {
                Card card1 = deck.drawCard();
                //Card card1 = new Card(Rank.ACE, Suit.SPADES);
                player.addCard(card1);
                createCardDealtEvent(player, card1, 0, false);
                Card card2 = deck.drawCard();
                //Card card2 = new Card(Rank.ACE, Suit.SPADES);
                player.addCard(card2);
                createCardDealtEvent(player, card2, 0, false);
                if (player.hasBlackjack(currentHandIndex))
                    notifyObserversWithEvent(GameEventType.BLACKJACK_ACHIEVED, "player", player, "handIndex", currentHandIndex);
        }
        Card dealerCard1 = deck.drawCard();
        dealer.addCard(dealerCard1);
        createCardDealtEvent(dealer, dealerCard1, 0, true);
        Card dealerCard2 = deck.drawCard();
        //Card dealerCard2 = new Card(Rank.ACE, Suit.HEARTS);
        dealer.addCard(dealerCard2);
        createCardDealtEvent(dealer, dealerCard2, currentHandIndex, false);
    }

    private void handleHandTransition(){
        if (humanPlayer.getHandCount() <= 1 || currentHandIndex >= humanPlayer.getHandCount() - 1) {
            gameState = GameState.AI_PLAYER_TURN;
            playAITurns();
        } else if (humanPlayer.hasBlackjack(currentHandIndex + 1)) {
            currentHandIndex++;
            notifyObserversWithEvent(GameEventType.BLACKJACK_ACHIEVED, "player", humanPlayer, "handIndex", currentHandIndex);
            playerStand();
        }
        else{
            currentHandIndex++;
        }
    }

    /**
     * Gestisce l'azione "Hit" del giocatore umano.
     */
    public void playerHit() {
        if (gameState != GameState.PLAYER_TURN) return;

        if (humanPlayer.isBusted(currentHandIndex))
            return;

        Card card = deck.drawCard();
        humanPlayer.addCard(currentHandIndex, card);
        createCardDealtEvent(humanPlayer, card, currentHandIndex, false);

        if (humanPlayer.getHandValue(currentHandIndex) > 21) {
            notifyObserversWithEvent(GameEventType.PLAYER_BUSTED, "player", humanPlayer, "handIndex", currentHandIndex);
            handleHandTransition();
        }
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
            notifyObserversWithEvent(GameEventType.DOUBLE_DOWN_EXECUTED,
                    "player", humanPlayer,
                    "currentHandIndex", currentHandIndex,
                    "newBet", humanPlayer.getHands().get(currentHandIndex).getBet(),
                    "currentBet", humanPlayer.getCurrentBet());
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
            Card newCard1 = deck.drawCard();
            Card newCard2 = deck.drawCard();
            int bet = humanPlayer.getCurrentBet();
            humanPlayer.splitHand(currentHandIndex, newCard1, newCard2);
            notifyObserversWithEvent(GameEventType.HAND_SPLIT,
                    "player", humanPlayer,
                    "newCard1", newCard1,
                    "newCard2", newCard2,
                    "handValue1", humanPlayer.getHandValue(currentHandIndex),
                    "handValue2", humanPlayer.getHandValue(currentHandIndex + 1),
                    "bet", bet);

            if (humanPlayer.hasBlackjack(currentHandIndex)) {
                notifyObserversWithEvent(GameEventType.BLACKJACK_ACHIEVED, "player", humanPlayer, "handIndex", currentHandIndex);
                playerStand();
            }
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
                notifyObserversWithEvent(GameEventType.INSURANCE_ACCEPTED,
                        "player", humanPlayer,
                        "amount", humanPlayer.getInsuranceAmount(),
                        "balance", humanPlayer.getBalance(),
                        "handIndex", currentHandIndex);
                return true;
            }
        }

        return false;
    }
    /**
     * notifica gli osservatori l'evento di assicurazione rifiutata
     *
     */
    public void declineInsurance(){
        notifyObserversWithEvent(GameEventType.INSURANCE_DECLINED);
    }

    /**
     * Gestisce i turni dei giocatori AI.
     */
    private void playAITurns() {
        for (Player player : players) {
            if (player instanceof AIPlayer aiPlayer) {
                for (int handIndex = 0; handIndex < aiPlayer.getHandCount(); handIndex++) {
                    PlayerStrategy strategy = aiPlayer.getStrategy();
                    boolean continuePlaying = true;

                    while (continuePlaying && aiPlayer.getHandValue(handIndex) < 21) {
                        Card dealerUpCard = dealer.getHand(0).get(0);
                        int handValue = aiPlayer.getHandValue(handIndex);

                        if (aiPlayer.getHands().get(handIndex).getCards().size() == 2) {
                            Card card1 = aiPlayer.getHands().get(handIndex).getCards().get(0);
                            Card card2 = aiPlayer.getHands().get(handIndex).getCards().get(1);

                            if (strategy.shouldSplitHand(card1, card2, dealerUpCard)) {
                                if (aiPlayer.canSplit(handIndex)) {
                                    Card newCard1 = deck.drawCard();
                                    Card newCard2 = deck.drawCard();
                                    int bet = aiPlayer.getCurrentBet();
                                    aiPlayer.splitHand(handIndex, newCard1, newCard2);
                                    notifyObserversWithEvent(GameEventType.HAND_SPLIT,
                                            "player", aiPlayer,
                                            "newCard1", newCard1,
                                            "newCard2", newCard2,
                                            "handValue1", aiPlayer.getHandValue(handIndex),
                                            "handValue2", aiPlayer.getHandValue(handIndex + 1),
                                            "bet", bet);
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
                                        "currentHandIndex", handIndex,
                                        "newBet", aiPlayer.getHands().get(handIndex).getBet(),
                                        "handValue", aiPlayer.getHandValue(handIndex));

                                createCardDealtEvent(aiPlayer, card, handIndex, false);
                                continuePlaying = false;
                                continue;
                            }
                        }

                        if (handIndex == 0 && dealerUpCard.isAce() &&
                                strategy.shouldTakeInsurance(handValue, dealerUpCard) &&
                                aiPlayer.getHand(handIndex).size() <= 2) {

                            player.takeInsurance();
                            bankManager.placeInsurance(player);
                            notifyObserversWithEvent(GameEventType.INSURANCE_ACCEPTED,
                                    "player", aiPlayer,
                                    "amount", aiPlayer.getInsuranceAmount(),
                                    "balance", aiPlayer.getBalance(),
                                    "handIndex", handIndex);
                        }

                        if (strategy.shouldDraw(handValue)) {
                            Card card = deck.drawCard();
                            aiPlayer.addCard(handIndex, card);
                            createCardDealtEvent(player, card, handIndex, false);

                            if (aiPlayer.getHandValue(handIndex) > 21) {
                                continuePlaying = false;
                                notifyObserversWithEvent(GameEventType.PLAYER_BUSTED,
                                        "player", aiPlayer,
                                        "handIndex", handIndex);
                            }
                        } else {
                            notifyObserversWithEvent(GameEventType.PLAYER_STAND,
                                    "player", aiPlayer.getName(),
                                    "handIndex", handIndex);
                            continuePlaying = false;
                        }
                    }
                }
            }
        }
        gameState = GameState.DEALER_TURN;
        playDealerTurn();
    }

    /**
     * Gestisce il turno del dealer.
     */
    private void playDealerTurn() {
        Card hiddenCard = dealer.getHiddenCard();
        dealer.revealHiddenCard();
        notifyObserversWithEvent(GameEventType.DEALER_TURN_STARTED, "card", hiddenCard, "handValue", dealer.getHandValue(0));
        boolean allPlayersBusted = isAllPlayersBusted();
        if (!allPlayersBusted) {
            PlayerStrategy strategy = dealer.getStrategy();
            while (strategy.shouldDraw(dealer.getHandValue(0))) {
                Card newCard = deck.drawCard();
                dealer.addCard(0, newCard);
                createCardDealtEvent(dealer, newCard, 0, false);
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
        boolean humanBusted = IntStream.range(0, humanPlayer.getHandCount())
                .allMatch(humanPlayer::isBusted);
        
        if (!humanBusted) return false;

        return players.stream()
                .filter(player -> player instanceof AIPlayer)
                .allMatch(player -> IntStream.range(0, player.getHandCount())
                        .allMatch(player::isBusted));
    }

    /**
     * Termina il round e determina i risultati.
     */
    private void endRound() {
        if (dealer.hasBlackjack(0) && !insurancePaid) {
            resultCalculator.processInsuranceOutcomes(humanPlayer, players, insurancePaid);
            insurancePaid = true;
        } else {
            resultCalculator.clearInsurance(humanPlayer, players);
        }

        Map<String, Integer> humanPlayerResults = resultCalculator.calculateResults(humanPlayer, players, dealer);

        notifyObserversWithEvent(GameEventType.ROUND_ENDED,
                "finalBalance", humanPlayerResults.get("finalBalance"),
                "wonHands", humanPlayerResults.get("wonHands"),
                "lostHands", humanPlayerResults.get("lostHands"),
                "totalHands", humanPlayerResults.get("totalHands"));

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
        GameEvent event = GameEvent.create(eventType, keyValuePairs);
        setChanged();
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
     *
     * @return currentHandIndex mano corrente del giocatore
     */
    public int getCurrentHandIndex() {
        return currentHandIndex;
    }

    /**
     * Crea un evento per notificare che una carta è stata distribuita.
     *
     * @param player Il giocatore che ha ricevuto la carta
     * @param card La carta distribuita
     * @param handIndex L'indice della mano
     * @param isHiddenCard Indica se la carta è nascosta
     */
    private void createCardDealtEvent(Player player, Card card, int handIndex, boolean isHiddenCard) {
        if (player instanceof Dealer) {
            notifyObserversWithEvent(GameEventType.CARD_DEALT,
                    "player", player,
                    "card", card,
                    "isHiddenCard", isHiddenCard);
        } else {
            notifyObserversWithEvent(GameEventType.CARD_DEALT,
                    "player", player,
                    "handIndex", handIndex,
                    "card", card);
        }
    }
}