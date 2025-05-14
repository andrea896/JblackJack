package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Players.AIPlayer;
import Model.Players.Player;
import View.BlackJackView;

public class BettingController implements BlackjackBettingListener{
    protected final GameModel model;
    protected final BlackJackView view;

    public BettingController(GameModel model, BlackJackView view) {
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
                    view.getPlayerHands().updateBet(betAmount, 0);
                }
                else {
                    view.getAIPlayerViews().get((int) event.getData().get("index")).updateBet(betAmount, 0);
                }
                break;

            case INSURANCE_OFFERED:
                view.getBettingView().showInsuranceOption();
                view.getControlPanelView().updateControls(false, false, false, false);
                break;

            case INSURANCE_ACCEPTED:
                Player player = (Player) event.getData().get("player");
                int insuranceAmount = (int) event.getData().get("amount");
                int currentBalance = (int) event.getData().get("balance");
                int handIndex_ = (int) event.getData().get("handIndex");

                if (player instanceof AIPlayer){
                    int playerIndex = model.getPlayers().indexOf(player);
                    view.getAIPlayerViews().get(playerIndex).updateInsurance(insuranceAmount, handIndex_);
                } else {
                    view.getPlayerHands().updateInsurance(insuranceAmount, handIndex_);
                    view.getPlayerView().updateBalance(currentBalance);
                }
                break;

            case DOUBLE_DOWN_EXECUTED:
                int newBet = (int) event.getData().get("bet") * 2;
                int handIndex = (int) event.getData().get("handIndex");
                view.getPlayerHands().updateBet(newBet, handIndex);
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
        view.getPlayerHands().updateBet(amount, 0);
        model.startGame(amount);
    }

    @Override
    public void onInsuranceAccepted() {
        model.takeInsurance();
    }
}
