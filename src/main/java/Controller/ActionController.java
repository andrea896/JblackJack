package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Game.GameState;
import Model.Game.Objects.Card;
import Model.Players.AIPlayer;
import Model.Players.Player;
import View.BlackJackView;
import java.util.Map;

/**
 * Controller che gestisce le azioni di gioco del giocatore nel BlackJack.
 * Coordina l'interazione tra il modello di gioco e la vista per le azioni
 * come Hit, Stand, Double Down e Split.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class ActionController implements BlackjackActionListener {
    protected final GameModel model;
    protected final BlackJackView view;

    /**
     * Costruisce un nuovo ActionController.
     * 
     * @param model Il modello del gioco
     * @param view La vista principale del gioco
     */
    public ActionController(GameModel model, BlackJackView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Inizializza il controller impostando i listener e aggiornando i controlli.
     */
    public void initialize() {
        view.setActionListener(this);
        updatePlayerControls();
    }

    /**
     * Gestisce gli eventi di gioco ricevuti dal TurnManager.
     * 
     * @param event L'evento di gioco da gestire
     */
    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case CARD_DEALT, PLAYER_HIT:
                handleCardDealtEvent(event);
                updatePlayerControls();
                break;

            case PLAYER_STAND, HAND_UPDATED:
                updatePlayerControls();
                break;

            case PLAYER_BUSTED:
                handlePlayerBustedEvent(event);
                break;

            case HAND_SPLIT:
                handleSplitEvent(event);
                updatePlayerControls();
                break;

            case DOUBLE_DOWN_EXECUTED:
                handleDoubleDownEvent(event);
                updatePlayerControls();
                break;
            case BLACKJACK_ACHIEVED:
                handleBlackjackEvent(event);
                updatePlayerControls();
        }
    }

    /**
     * Gestisce l'evento di BlackJack ottenuto da un giocatore.
     * 
     * @param event L'evento contenente i dati del BlackJack
     */
    private void handleBlackjackEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        int handIndex = (int) data.get("handIndex");

        if (player == model.getHumanPlayer()) {
            view.getPlayerHands().showBlackjack(handIndex);
        } else {
            int playerIndex = model.getPlayers().indexOf(player);
            if (playerIndex >= 0 && playerIndex < view.getAIPlayerViews().size()) {
                view.getAIPlayerViews().get(playerIndex).showBlackjack(handIndex);
            }
        }
        updatePlayerControls();
        AudioQueue.queue(AudioManager.SoundEffect.BLACKJACK);
    }

    /**
     * Gestisce l'evento di distribuzione di una carta.
     * 
     * @param event L'evento contenente i dati della carta distribuita
     */
    private void handleCardDealtEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        Card card = (Card) data.get("card");

        if (player.equals(model.getHumanPlayer())) {
            int handIndex = (int) data.get("handIndex");
            view.getPlayerHands().animateCardDealt(handIndex, card, player.getHandValue(handIndex));

        } else if (player.equals(model.getDealer())) {
            boolean isHiddenCard = (boolean) data.get("isHiddenCard");
            view.getDealerView().animateCardDealt(card, isHiddenCard, player.getHandValue(0));

        } else {
            int handIndex = (int) data.get("handIndex");
            int playerIndex = model.getPlayers().indexOf(player);
            if (playerIndex >= 0 && playerIndex < view.getAIPlayerViews().size()) {
                view.getAIPlayerViews().get(playerIndex).animateCardDealt(handIndex, card, player.getHandValue(handIndex));
            }
        }
        AudioQueue.queue(AudioManager.SoundEffect.CARD_DEAL);
    }

    /**
     * Gestisce l'evento di un giocatore che sballa.
     * 
     * @param event L'evento contenente i dati del giocatore che ha sballato
     */
    private void handlePlayerBustedEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        int handIndex = (int) data.get("handIndex");

        if (player.equals(model.getHumanPlayer())) {
            view.getPlayerHands().showBusted(handIndex);
            AudioQueue.queue(AudioManager.SoundEffect.LOSE);
        } else {
            int playerIndex = model.getPlayers().indexOf(player);
            if (playerIndex >= 0 && playerIndex < view.getAIPlayerViews().size()) {
                view.getAIPlayerViews().get(playerIndex).showBusted(handIndex);
            }
        }
    }

    /**
     * Gestisce l'evento di double down eseguito.
     * 
     * @param event L'evento contenente i dati del double down
     */
    private void handleDoubleDownEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        int currentHandIndex = (int) data.get("currentHandIndex");
        Player player = (Player) data.get("player");
        int newBet = (int) data.get("newBet");
        if (player instanceof AIPlayer) {
            int playerIndex = model.getPlayers().indexOf(player);
            view.getAIPlayerViews().get(playerIndex).updateBet(newBet, currentHandIndex);
        }
        else {
            int currentBet = (int) data.get("currentBet");
            view.getPlayerHands().updateBet(newBet, currentHandIndex);
            view.getPlayerView().updateCurrentBet(currentBet);
            view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
        }
        AudioQueue.queue(AudioManager.SoundEffect.DOUBLE_DOWN);
    }

    /**
     * Gestisce l'evento di split della mano.
     * 
     * @param event L'evento contenente i dati dello split
     */
    private void handleSplitEvent(GameEvent event) {
        Player player = (Player) event.getData().get("player");
        Card newCard1 = (Card) event.getData().get("newCard1");
        Card newCard2 = (Card) event.getData().get("newCard2");
        int bet = (int) event.getData().get("bet");
        int handValue1 = (int) event.getData().get("handValue1");
        int handValue2 = (int) event.getData().get("handValue2");
        if (player instanceof AIPlayer) {
            int playerIndex = model.getPlayers().indexOf(player);
            view.getAIPlayerViews().get(playerIndex).animateSplitHands(newCard1, newCard2, handValue1, handValue2, bet);
        }
        else {
            view.getPlayerHands().animateSplitHands(newCard1, newCard2, handValue1, handValue2, bet);
            view.getPlayerView().updateBalance(player.getBalance());
            view.getPlayerView().updateCurrentBet(player.getCurrentBet());
        }
        AudioQueue.queue(AudioManager.SoundEffect.SPLIT);
        AudioQueue.queue(AudioManager.SoundEffect.CARD_DEAL);
        AudioQueue.queue(AudioManager.SoundEffect.CARD_DEAL);
    }

    /**
     * Aggiorna lo stato dei controlli del giocatore basandosi sullo stato del gioco.
     */
    public void updatePlayerControls() {
        if (!model.getGameState().equals(GameState.PLAYER_TURN)) {
            view.getControlPanelView().updateControls(false, false, false, false);
            return;
        }

        int currentHand = model.getCurrentHandIndex();
        Player player = model.getHumanPlayer();

        boolean canHit = !player.isBusted(currentHand) && player.getHandValue(currentHand) < 21;
        boolean canStand = true;
        boolean canDoubleDown = player.canDoubleDown(currentHand);
        boolean canSplit = player.canSplit(currentHand);

        view.getControlPanelView().updateControls(canHit, canStand, canDoubleDown, canSplit);
    }

    /**
     * Gestisce la pressione del pulsante Hit.
     */
    @Override
    public void onHitButtonPressed() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        model.playerHit();
    }

    /**
     * Gestisce la pressione del pulsante Stand.
     */
    @Override
    public void onStandButtonPressed() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        model.playerStand();
    }

    /**
     * Gestisce la pressione del pulsante Double Down.
     */
    @Override
    public void onDoubleDownPressed() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        model.doubleDown();
    }

    /**
     * Gestisce la pressione del pulsante Split.
     */
    @Override
    public void onSplitButtonPressed() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        model.splitHand();
    }
}
