package Controller;

import Model.Game.GameModel;
import Model.Profile.ProfileManager;
import Model.Profile.UserProfile;
import Utility.LoggerUtility;
import View.BlackJackViewImpl;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameManager {
    private static GameManager instance;
    private ProfileManager profileManager;
    private UserProfile currentProfile;
    private static final LoggerUtility logger = new LoggerUtility();
    private Stage primaryStage;
    private GameModel gameModel;

    private GameManager() {
        profileManager = ProfileManager.getInstance();
        currentProfile = new UserProfile();
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public UserProfile loadExistingProfile(String nickname) {
        if (currentProfile != null && nickname != null && !nickname.trim().isEmpty()) {
            currentProfile = this.profileManager.loadProfile(nickname);
        }

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

    public void startGame(int numberOfPlayers, String cardBackDesign){
        if (currentProfile == null){
            logger.logWarning("Nessun profilo selezionato per iniziare il gioco");
            return;
        }

        int initialBalance = currentProfile.getStats().getCurrentBalance();
        String playerName = currentProfile.getNickname();

        try {
            BorderPane tempRoot = new BorderPane();
            Scene gameScene = new Scene(tempRoot, 1355, 944);
            gameScene.getStylesheets().add(getClass().getResource("/GameView/blackjack.css").toExternalForm());
            gameModel = new GameModel(playerName, initialBalance, numberOfPlayers);
            BlackJackViewImpl blackjackView = new BlackJackViewImpl(cardBackDesign, currentProfile.getAvatarPath(), numberOfPlayers, currentProfile.getNickname(), currentProfile.getStats().getCurrentBalance());
            gameScene.setRoot(blackjackView);
            MainController mainController = new MainController(gameModel, blackjackView);
            gameModel.addObserver(mainController);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.show();

            logger.logInfo("Gioco avviato per il profilo: " + playerName + ", giocatori: " + numberOfPlayers + ", design carte: " + cardBackDesign);
        } catch (Exception e) {
            logger.logError("Errore nell'avvio del gioco: " + e.getMessage(), e);
        }

    }

    public void updatePlayerStats(boolean hasWon, boolean isPush, int betAmount) {
        if (currentProfile == null) {
            logger.logWarning("Impossibile aggiornare le statistiche: profilo non trovato");
            return;
        }

        currentProfile.getStats().setTotalHandsPlayed(currentProfile.getStats().getTotalHandsPlayed() + 1);

        if (hasWon) {
            currentProfile.getStats().setHandsWon(currentProfile.getStats().getHandsWon() + 1);
            if (!isPush)
                currentProfile.getStats().setCurrentBalance(currentProfile.getStats().getCurrentBalance() + betAmount);
        } else if (!isPush) {
            currentProfile.getStats().setHandsLost(currentProfile.getStats().getHandsLost() + 1);
            currentProfile.getStats().setCurrentBalance(currentProfile.getStats().getCurrentBalance() - betAmount);
        }

        profileManager.updateProfile(currentProfile);
    }
}
