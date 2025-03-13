package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Game.GameState;
import Model.Game.Objects.Card;
import Model.Players.Player;
import View.BlackJackViewImpl;

import java.util.Map;

public class ActionController implements BlackjackActionListener {
    protected final GameModel model;
    protected final BlackJackViewImpl view;

    public ActionController(GameModel model, BlackJackViewImpl view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        // Registra listener per le azioni
        view.getControlPanelView().setActionListener(this);
        // Disabilita inizialmente i controlli
        updatePlayerControls();
    }

    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case CARD_DEALT:
                handleCardDealtEvent(event);
                break;

            case PLAYER_HIT:
                // Pre-azione, possibile aggiornamento UI
                handleCardDealtEvent(event);
                break;

            case PLAYER_STAND:
                // Pre-azione, possibile aggiornamento UI
                break;

            case PLAYER_BUSTED:
                handlePlayerBustedEvent(event);
                break;

            case HAND_UPDATED:
                updateHandDisplay(event);
                updatePlayerControls();
                break;

            case HAND_SPLIT:
                handleSplitEvent(event);
                break;

            case DOUBLE_DOWN_EXECUTED:
                handleCardDealtEvent(event);
                view.getControlPanelView().updateControls(false,false,false,false);
                break;

            // Altri eventi...
        }
    }

    private void handleCardDealtEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        Card card = (Card) data.get("card");

        if (player.equals(model.getHumanPlayer())) {
            int handIndex = (int) data.get("handIndex");
            // Carta al giocatore umano
            view.getPlayerHands().animateCardDealt(handIndex, card, player.getHandValue(handIndex));
            //view.getPlayerHands().updateHand(handIndex, player.getHand(handIndex), player.getHandValue(handIndex));

        } else if (player.equals(model.getDealer())) {
            // Carta al dealer
            boolean isHiddenCard = (boolean) data.get("isHiddenCard");
            view.getDealerView().animateCardDealt(card, isHiddenCard, player.getHandValue(0));
            //view.getDealerView().updateHand(player.getHand(0), hideFirstCard ? 0 : player.getHandValue(0), hideFirstCard);

        } else {
            int handIndex = (int) data.get("handIndex");
            // Carta a un giocatore AI
            int playerIndex = model.getPlayers().indexOf(player);
            if (playerIndex >= 0 && playerIndex < view.getAIPlayerViews().size()) {
                view.getAIPlayerViews().get(playerIndex).animateCardDealt(handIndex, card, player.getHandValue(handIndex));
                //view.getAIPlayerViews().get(playerIndex).updateHand(handIndex, player.getHand(handIndex), player.getHandValue(handIndex));
            }
        }

        // Aggiorna i controlli del giocatore
        updatePlayerControls();
    }

    private void handlePlayerBustedEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        int handIndex = (int) data.get("handIndex");

        if (player == model.getHumanPlayer()) {
            view.getPlayerHands().showBusted(handIndex);
            view.updateStatusMessage("Hai sballato!");
        } else {
            int playerIndex = model.getPlayers().indexOf(player);
            if (playerIndex >= 0 && playerIndex < view.getAIPlayerViews().size()) {
                view.getAIPlayerViews().get(playerIndex).showBusted(handIndex);
            }
        }

        updatePlayerControls();
    }

    private void updateHandDisplay(GameEvent event) {
        // Simile a handleCardDealtEvent ma senza animazione
    }

    private void handleSplitEvent(GameEvent event) {
        Map<String, Object> data = event.getData();
        Player player = (Player) data.get("player");
        int handIndex = (int) data.get("handIndex");

        if (player == model.getHumanPlayer()) {
            //view.getPlayerView().animateSplitHands(player.getHand(handIndex), player.getHand(handIndex + 1));
            //view.getPlayerView().updateHand(handIndex, player.getHand(handIndex), player.getHandValue(handIndex));
            //view.getPlayerView().updateHand(handIndex + 1, player.getHand(handIndex + 1), player.getHandValue(handIndex + 1));

            // Aggiorna controlli e scommessa visualizzata
            updatePlayerControls();
        }
    }

    public void updatePlayerControls() {
        // Aggiorna controlli in base allo stato corrente del gioco
        if (model.getGameState() != GameState.PLAYER_TURN) {
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

    @Override
    public void onHitButtonPressed() {
        model.playerHit();
    }

    @Override
    public void onStandButtonPressed() {
        model.playerStand();
    }

    @Override
    public void onDoubleDownPressed() {
        model.doubleDown();
    }

    @Override
    public void onSplitButtonPressed() {
        model.splitHand();
    }

    @Override
    public void onExitButtonPressed() {
        model.playerStand();
    }
}
