package Controller;

import Model.Game.GameEvent;
import Model.Game.GameEventType;
import Model.Game.GameModel;
import Model.Game.Objects.Card;
import View.BlackJackView;
import javafx.application.Platform;
import java.util.Observable;
import java.util.Observer;

public class MainController implements Observer {
    private final GameModel model;
    private final BlackJackView view;
    private final GameController gameController;
    private final ActionController actionController;
    private final BettingController bettingController;

    public MainController(GameModel model, BlackJackView view) {
        this.model = model;
        this.view = view;

        this.gameController = new GameController(model, view);
        this.actionController = new ActionController(model, view);
        this.bettingController = new BettingController(model, view);
        model.getTurnManager().addObserver(this);
        initialize();
    }

    private void initialize() {
        gameController.initialize();
        actionController.initialize();
        bettingController.initialize();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(model.getTurnManager()) && arg instanceof GameEvent) {
            GameEvent event = (GameEvent) arg;

            Platform.runLater(() -> {
                dispatchEvent(event);
            });
        }
    }

    private void dispatchEvent(GameEvent event) {
        GameManager gameManager = GameManager.getInstance();
        GameEventType type = event.getType();
        int bet;

        switch (type) {
            // Eventi del flusso di gioco → GameController
            case GAME_STARTED:
            case ROUND_STARTED:
            case ROUND_ENDED:
            case GAME_STATE_CHANGED:
                actionController.updatePlayerControls();
                break;
            case DEALER_TURN_STARTED:
                Card hiddenCard = (Card) event.getData().get("card");
                gameController.handleEvent(event);
                view.getDealerView().revealHiddenCard(hiddenCard);
                break;

            // Eventi delle azioni → ActionController
            case CARD_DEALT:
            case HAND_UPDATED:
            case PLAYER_HIT:
            case PLAYER_STAND:
            case PLAYER_BUSTED:
            case HAND_SPLIT:
            case DOUBLE_DOWN_EXECUTED:
                actionController.handleEvent(event);
                break;

            // Eventi delle scommesse → BettingController
            case BET_PLACED:
            case INSURANCE_OFFERED:
            case INSURANCE_ACCEPTED:
            case INSURANCE_DECLINED:
            case WINNINGS_PAID:
                bettingController.handleEvent(event);
                break;

            // Eventi dei risultati (possono interessare più controller)
            case PLAYER_WINS:
                bet = (int) event.getData().get("bet");
                gameManager.updatePlayerStats(true, false, bet);
                gameController.handleEvent(event);
                bettingController.handleEvent(event);
                break;
            case DEALER_WINS:
                bet = (int) event.getData().get("bet");
                gameManager.updatePlayerStats(false, false, bet);
                gameController.handleEvent(event);
                bettingController.handleEvent(event);
                break;
            case PUSH:
            case BLACKJACK_ACHIEVED:
                //bet = (int) event.getData().get("bet");
                //bet *= (int) 1.5;
                //gameManager.updatePlayerStats(true, false, bet);
                gameController.handleEvent(event);
                bettingController.handleEvent(event);
                break;

            // Altri eventi - inviare a tutti i controller
            default:
                //gameController.handleEvent(event);
                //actionController.handleEvent(event);
                //bettingController.handleEvent(event);
        }
    }
}
