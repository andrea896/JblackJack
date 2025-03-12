package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import View.BlackJackViewImpl;

public class BettingController implements BlackjackBettingListener{
    protected final GameModel model;
    protected final BlackJackViewImpl view;

    public BettingController(GameModel model, BlackJackViewImpl view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        view.setBettingListener(this);
    }

    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case BET_PLACED:
                int betAmount = (int) event.getData().get("amount");
                if (event.getData().get("player") == model.getHumanPlayer().getName()) {
                    int balance = (int) event.getData().get("balance");
                    view.getPlayerView().updateCurrentBet(betAmount);
                    view.getPlayerView().updateBalance(balance);
                    view.getPlayerHands().updateBet(betAmount);
                }
                else {
                    view.getAIPlayerViews().get((int) event.getData().get("index")).updateBet(betAmount);
                }
                break;

            case INSURANCE_OFFERED:
                view.getBettingView().showInsuranceOption();
                break;

            case INSURANCE_ACCEPTED:
                int insuranceAmount = (int) event.getData().get("amount");
                int currentBalance = (int) event.getData().get("balance");
                view.getBettingView().updateInsuranceDisplay(insuranceAmount);
                view.getPlayerView().updateBalance(currentBalance);
                break;

            case DOUBLE_DOWN_EXECUTED:
                int newBet = (int) event.getData().get("bet") * 2;
                int handIndex = (int) event.getData().get("handIndex");
                //view.getPlayerView().updateBet(newBet, handIndex);
                view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
                break;

            case PLAYER_WINS:
            case BLACKJACK_ACHIEVED:
            case PUSH:
                updatePlayerBalanceFromEvent();
                break;
        }
    }

    private void updatePlayerBalanceFromEvent() {
        int balance = model.getHumanPlayer().getBalance();
        view.getPlayerView().updateBalance(balance);
    }

    @Override
    public void onBetPlaced(int amount) {
        model.getHumanPlayer().placeBet(amount, 0);
        view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
        view.getPlayerView().updateCurrentBet(amount);
        view.getPlayerHands().updateBet(amount);
        model.startGame(amount);
    }

    @Override
    public void onInsuranceAccepted() {
        model.getHumanPlayer().takeInsurance();
    }

    @Override
    public void onInsuranceDeclined() {

    }
}
