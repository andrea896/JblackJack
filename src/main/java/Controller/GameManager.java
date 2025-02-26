package Controller;

import Model.Profile.GameStats;
import Model.Profile.ProfileManager;
import Model.Profile.UserProfile;
import Utility.LoggerUtility;

public class GameManager {
    private static GameManager instance;
    private ProfileManager profileManager;
    private UserProfile currentProfile;
    private static final LoggerUtility logger = new LoggerUtility();

    private GameManager() {
        profileManager = ProfileManager.getInstance();
        currentProfile = new UserProfile();
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    public UserProfile loadExistingProfile(String nickname) {
        if (currentProfile != null && nickname != null && !nickname.trim().isEmpty())
            currentProfile = this.profileManager.loadProfile(nickname);
        return currentProfile;
    }

    public UserProfile createNewProfile(String newNickname, String avatarPath) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            logger.logWarning("Tentativo di creare un profilo con un nickname vuoto!");
            return null;
        }

        currentProfile = profileManager.createProfile(newNickname, avatarPath);
        logger.logInfo("Profilo creato:" + newNickname);
        return currentProfile;
    }

    public UserProfile getCurrentProfile() {
        return currentProfile;
    }

    public void updateGameStats(boolean won, int betAmount){

    }
}
