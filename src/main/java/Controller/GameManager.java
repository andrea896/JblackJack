package Controller;

import Model.GameStats.GameStats;
import Model.Profile.UserProfile;

public class GameManager {
        private static GameManager instance;
        private ProfileManager profileManager;
        private UserProfile currentProfile;

    private GameManager() {
        profileManager = new ProfileManager();
        currentProfile = new UserProfile();
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    public void updateNickname(String newNickname) {
        if (currentProfile != null && newNickname != null && !newNickname.trim().isEmpty()) {
            currentProfile.setNickname(newNickname);
            saveCurrentProfile();
        }
    }

    public UserProfile getCurrentProfile() {
        return currentProfile;
    }

    public void saveCurrentProfile() {
        currentProfile = this.profileManager.loadOrCreateProfile(currentProfile.getNickname());
    }

    public void updateGameStats(boolean won, int betAmount){
        if (currentProfile != null){
            GameStats stats = currentProfile.getStats();
            stats.setTotalHandsPlayed(stats.getTotalHandsPlayed() + 1);

            if (won){
                stats.setHandsWon(stats.getHandsWon() + 1);
                stats.setHandsLost(stats.getHandsLost() + betAmount);
            } else {
                stats.setHandsWon(stats.getHandsWon() + 1);
                stats.setHandsLost(stats.getCurrentBalance() - betAmount);
            }
            saveCurrentProfile();
        }
    }
}
