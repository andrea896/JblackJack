package Controller;

import Model.Game.GameEvent;
import Model.Game.GameEventType;
import Model.Game.GameModel;
import Model.Game.Objects.Card;
import View.BlackJackView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

public class MainController implements Observer, RoundEndListener {
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
        view.setRoundEndListener(this);
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
        GameManager gameManager = GameManager.getInstance();
        GameEventType type = event.getType();
        int bet;

        switch (type) {
            case GAME_STARTED:
            case ROUND_STARTED:
            case ROUND_ENDED:
                int finalBalance = (int) event.getData().get("finalBalance");
                int wonHands = (int) event.getData().get("wonHands");
                int lostHands = (int) event.getData().get("lostHands");
                int totalHands = (int) event.getData().get("totalHands");

                gameManager.updatePlayerStats(finalBalance, totalHands, wonHands, lostHands);
                view.getPlayerView().updateBalance(finalBalance);
                int minimumBet = 10;
                view.showEndRoundPanel(finalBalance, minimumBet);
                break;
            case GAME_STATE_CHANGED:
                actionController.updatePlayerControls();
                break;
            case DEALER_TURN_STARTED:
                Card hiddenCard = (Card) event.getData().get("card");
                int handValue = (int) event.getData().get("handValue");
                view.getDealerView().revealHiddenCard(hiddenCard, handValue);
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
            case INSURANCE_ACCEPTED:
            case INSURANCE_DECLINED:
            case INSURANCE_OFFERED:
            case WINNINGS_PAID:
                actionController.updatePlayerControls();
                bettingController.handleEvent(event);
                break;

            case PLAYER_WINS:
                bettingController.handleEvent(event);
                break;

            case DEALER_WINS:
                bet = (int) event.getData().get("bet");
                //gameManager.updatePlayerStats(false, false, bet);
                gameController.handleEvent(event);
                bettingController.handleEvent(event);
                break;
            case PUSH:
            case BLACKJACK_ACHIEVED:
                actionController.handleEvent(event);
                gameController.handleEvent(event);
                bettingController.handleEvent(event);
                break;

            default:
                break;
        }
    }

    @Override
    public void onNewRoundRequested() {
        view.resetViewForNewRound();
        view.getBettingView().setMaxBetSlider(model.getHumanPlayer().getBalance());
        view.getBettingView().showBettingControls(true);
    }

    @Override
    public void onExitRequested() {
        navigateToMainMenu();
    }

    @Override
    public void onBalanceReloadRequested(int amount) {
        int currentBalance = model.getHumanPlayer().getBalance();
        int newBalance = currentBalance + amount;

        model.getHumanPlayer().setBalance(newBalance);
        view.getPlayerView().updateBalance(newBalance);
        GameManager.getInstance().updatePlayerStats(newBalance, 0, 0, 0);
        onNewRoundRequested();
    }

    private void navigateToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameMenu/MenuView.fxml"));
            Parent root = loader.load();
            Scene menuScene = new Scene(root);
            Stage primaryStage = (Stage) view.getScene().getWindow();
            primaryStage.setScene(menuScene);
        } catch (Exception e) {
            System.err.println("Errore durante la navigazione al menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
