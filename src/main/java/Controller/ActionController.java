package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Game.GameState;
import Model.Game.Objects.Card;
import Model.Players.Player;
import View.BlackJackView;

import java.util.Map;

public class ActionController implements BlackjackActionListener {
    protected final GameModel model;
    protected final BlackJackView view;

    public ActionController(GameModel model, BlackJackView view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        view.setActionListener(this);
        updatePlayerControls();
    }

    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case CARD_DEALT:
                handleCardDealtEvent(event);
                break;

            case PLAYER_HIT:
                handleCardDealtEvent(event);
                break;

            case PLAYER_STAND:
                updatePlayerControls();
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
                view.getControlPanelView().updateControls(false,false,false,false);
                break;


        }
    }

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
        Player player = (Player) event.getData().get("player");
        Card newCard1 = (Card) event.getData().get("newCard1");
        Card newCard2 = (Card) event.getData().get("newCard2");
        int bet = (int) event.getData().get("bet");
        int handValue1 = (int) event.getData().get("handValue1");
        int handValue2 = (int) event.getData().get("handValue2");
        if (!player.equals(model.getDealer())) {
            view.getPlayerHands().animateSplitHands(newCard1, newCard2, handValue1, handValue2, bet);
            updatePlayerControls();
        }
    }

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

    }
}
