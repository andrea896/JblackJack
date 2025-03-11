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
        // Configura listener per le scommesse
        view.setBettingListener(this);
    }

    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case BET_PLACED:
                int betAmount = (int) event.getData().get("amount");
                //view.getPlayerView().updateBet(betAmount, 0);
                view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
                break;

            case INSURANCE_OFFERED:
                view.getBettingView().showInsuranceOption();
                break;

            case INSURANCE_ACCEPTED:
                int insuranceAmount = (int) event.getData().get("amount");
                view.getBettingView().updateInsuranceDisplay(insuranceAmount);
                view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
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
        // Aggiorna il saldo dopo una vincita/perdita
        int balance = model.getHumanPlayer().getBalance();
        view.getPlayerView().updateBalance(balance);
    }

    @Override
    public void onBetPlaced(int amount) {
        model.getHumanPlayer().placeBet(amount, 0);
    }

    @Override
    public void onInsuranceAccepted() {
        model.getHumanPlayer().takeInsurance();
    }

    @Override
    public void onInsuranceDeclined() {

    }
}
