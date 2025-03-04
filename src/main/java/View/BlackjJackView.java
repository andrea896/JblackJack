package View;

import Controller.BlackjackActionListener;
import Controller.BlackjackBettingListener;
import java.util.List;

public interface BlackjJackView {
    // Metodi per l'aggiornamento dello stato del gioco
    void updateStatusMessage(String message);
    void showGameStartAnimation();
    void showPlayAgainButton(boolean visible);

    // Metodi per accedere ai componenti view
    PlayerView getPlayerView();
    DealerView getDealerView();
    List<AIPlayerView> getAIPlayerViews();
    ControlPanelView getControlPanelView();
    BettingView getBettingView();

    // Metodi per impostare i listener
    void setActionListener(BlackjackActionListener listener);
    void setBettingListener(BlackjackBettingListener listener);

    // Utilità
    int getBetAmount();
}
