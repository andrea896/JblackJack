package Model.GameStats;

public class GameStats {
    private int totalHandsPlayed;
    private int handsWon;
    private int handsLost;
    private int currentBalance;

    public GameStats() {
        this.totalHandsPlayed = 0;
        this.handsWon = 0;
        this.handsLost = 0;
        this.currentBalance = 1000;
    }

    public int getTotalHandsPlayed() {
        return totalHandsPlayed;
    }

    public void setTotalHandsPlayed(int totalHandsPlayed) {
        this.totalHandsPlayed = totalHandsPlayed;
    }

    public int getHandsWon() {
        return handsWon;
    }

    public void setHandsWon(int handsWon) {
        this.handsWon = handsWon;
    }

    public int getHandsLost() {
        return handsLost;
    }

    public void setHandsLost(int handsLost) {
        this.handsLost = handsLost;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }
}
