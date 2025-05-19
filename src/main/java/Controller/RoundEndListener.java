package Controller;

public interface RoundEndListener {
    void onNewRoundRequested();
    void onExitRequested();
    void onBalanceReloadRequested(int amount);
}
