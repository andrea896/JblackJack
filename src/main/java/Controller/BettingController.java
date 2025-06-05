package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Players.AIPlayer;
import Model.Players.Player;
import View.BlackJackView;

/**
 * Controller che gestisce le scommesse e le assicurazioni nel BlackJack.
 * Coordina l'interazione tra il modello di gioco e la vista per tutte
 * le operazioni relative alle puntate.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class BettingController implements BlackjackBettingListener {
    /** Modello del gioco */
    protected final GameModel model;
    
    /** Vista principale del gioco */
    protected final BlackJackView view;

    /**
     * Costruisce un nuovo BettingController.
     * 
     * @param model Il modello del gioco
     * @param view La vista principale del gioco
     */
    public BettingController(GameModel model, BlackJackView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Inizializza il controller impostando i listener delle scommesse.
     */
    public void initialize() {
        view.setBettingListener(this);
    }

    /**
     * Gestisce gli eventi di gioco relativi alle scommesse.
     * 
     * @param event L'evento di gioco da gestire
     */
    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case BET_PLACED:
                int betAmount = (int) event.getData().get("amount");
                if (event.getData().get("player").equals(model.getHumanPlayer().getName())) {
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

            case INSURANCE_DECLINED:
                view.getBettingView().hideInsuranceOption();
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
        }
    }

    /**
     * Gestisce il piazzamento di una scommessa da parte del giocatore umano.
     * 
     * @param amount L'importo della scommessa
     */
    @Override
    public void onBetPlaced(int amount) {
        model.getHumanPlayer().placeBet(amount, 0);
        view.getPlayerView().updateBalance(model.getHumanPlayer().getBalance());
        view.getPlayerView().updateCurrentBet(amount);
        view.getPlayerHands().updateBet(amount, 0);
        AudioQueue.queue(AudioManager.SoundEffect.CHIP_STACK);
        model.startRound(amount);
    }

    /**
     * Gestisce l'accettazione dell'assicurazione da parte del giocatore.
     */
    @Override
    public void onInsuranceAccepted() {
        AudioQueue.queue(AudioManager.SoundEffect.BUTTON_CLICK);
        AudioQueue.queue(AudioManager.SoundEffect.CHIP_PLACE);
        model.takeInsurance();
    }

    /**
     * Gestisce il rifiuto dell'assicurazione da parte del giocatore.
     */
    @Override
    public void onInsuranceDeclined() {
        AudioQueue.queue(AudioManager.SoundEffect.BUTTON_CLICK);
        model.declineInsurance();
    }
}
