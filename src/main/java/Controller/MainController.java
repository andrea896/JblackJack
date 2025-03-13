package Controller;

import Model.Game.GameEvent;
import Model.Game.GameEventType;
import Model.Game.GameModel;
import View.BlackJackViewImpl;
import java.util.Observable;
import java.util.Observer;

import static Model.Game.GameState.PLAYER_TURN;

public class MainController implements Observer {
    private final GameModel model;
    private final BlackJackViewImpl view;
    private final GameController gameController;
    private final ActionController actionController;
    private final BettingController bettingController;

    public MainController(GameModel model, BlackJackViewImpl view) {
        this.model = model;
        this.view = view;
        // Crea i controller specializzati
        this.gameController = new GameController(model, view);
        this.actionController = new ActionController(model, view);
        this.bettingController = new BettingController(model, view);
        // Registra questo controller come observer del model
        model.getTurnManager().addObserver(this);
        // Inizializza i controller
        initialize();
    }

    private void initialize() {
        // Inizializza i controller
        gameController.initialize();
        actionController.initialize();
        bettingController.initialize();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(model.getTurnManager()) && arg instanceof GameEvent) {
            GameEvent event = (GameEvent) arg;
            dispatchEvent(event);
        }
    }

    private void dispatchEvent(GameEvent event) {
        GameEventType type = event.getType();

        // Smista gli eventi ai controller appropriati
        switch (type) {
            // Eventi del flusso di gioco → GameController
            case GAME_STARTED:
            case ROUND_STARTED:
            case ROUND_ENDED:
            case GAME_STATE_CHANGED:
                actionController.updatePlayerControls();
                break;
            case DEALER_TURN_STARTED:
                gameController.handleEvent(event);
                break;

            // Eventi delle azioni → ActionController
            case CARD_DEALT:
                actionController.handleEvent(event);
                break;
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
            case DEALER_WINS:
            case PUSH:
            case BLACKJACK_ACHIEVED:
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
