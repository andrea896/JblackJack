package Controller;

import Model.Game.GameModel;
import Model.Profile.ProfileManager;
import Model.Profile.UserProfile;
import Utility.LoggerUtility;
import View.BlackJackView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Gestore principale dell'applicazione BlackJack.
 * Coordina l'interazione tra i profili utente, il modello di gioco e la vista.
 * Implementato come singleton per garantire un unico punto di controllo dell'applicazione.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class GameManager {
    private static GameManager instance;
    private ProfileManager profileManager;
    private UserProfile currentProfile;
    private static final LoggerUtility logger = new LoggerUtility();
    private Stage primaryStage;
    private GameModel gameModel;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza il ProfileManager e crea un profilo utente vuoto.
     */
    private GameManager() {
        profileManager = ProfileManager.getInstance();
        currentProfile = new UserProfile();
    }

    /**
     * Restituisce l'istanza singleton del GameManager.
     * 
     * @return L'istanza unica del GameManager
     */
    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    /**
     * Inizializza il GameManager con lo stage principale dell'applicazione.
     * 
     * @param primaryStage Lo stage principale di JavaFX
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Carica un profilo esistente dal database.
     * 
     * @param nickname Il nickname del profilo da caricare
     * @return Il profilo caricato, null se non trovato
     */
    public UserProfile loadExistingProfile(String nickname) {
        if (currentProfile != null && nickname != null && !nickname.trim().isEmpty()) {
            currentProfile = this.profileManager.loadProfile(nickname);
        }

        return currentProfile;
    }

    /**
     * Crea un nuovo profilo utente con nickname e avatar specificati.
     * 
     * @param newNickname Il nickname del nuovo profilo
     * @param avatarPath Il percorso dell'avatar selezionato
     * @return Il nuovo profilo creato, null se la creazione fallisce
     */
    public UserProfile createNewProfile(String newNickname, String avatarPath) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            logger.logWarning("Tentativo di creare un profilo con un nickname vuoto!");
            return null;
        }

        currentProfile = profileManager.createProfile(newNickname, avatarPath);
        logger.logInfo("Profilo creato:" + newNickname);
        return currentProfile;
    }

    /**
     * Restituisce il profilo utente correntemente attivo.
     * 
     * @return Il profilo corrente
     */
    public UserProfile getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Avvia una nuova partita con i parametri specificati.
     * 
     * @param numberOfPlayers Il numero di giocatori AI da includere
     * @param cardBackDesign Il design del dorso delle carte selezionato
     */
    public void startGame(int numberOfPlayers, String cardBackDesign){
        if (currentProfile == null){
            logger.logWarning("Nessun profilo selezionato per iniziare il gioco");
            return;
        }

        int initialBalance = currentProfile.getStats().getCurrentBalance();
        String playerName = currentProfile.getNickname();

        try {
            BorderPane tempRoot = new BorderPane();
            Scene gameScene = new Scene(tempRoot, 1355, 885);
            gameScene.getStylesheets().add(getClass().getResource("/GameView/blackjack.css").toExternalForm());
            gameModel = new GameModel(playerName, initialBalance, numberOfPlayers);
            BlackJackView blackjackView = new BlackJackView(cardBackDesign, currentProfile.getAvatarPath(), numberOfPlayers, currentProfile.getNickname(), currentProfile.getStats().getCurrentBalance());
            gameScene.setRoot(blackjackView);
            MainController mainController = new MainController(gameModel, blackjackView);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.show();

            logger.logInfo("Gioco avviato per il profilo: " + playerName + ", giocatori: " + numberOfPlayers + ", design carte: " + cardBackDesign);
        } catch (Exception e) {
            logger.logError("Errore nell'avvio del gioco: " + e.getMessage(), e);
        }

    }

    /**
     * Aggiorna le statistiche del giocatore al termine di una sessione di gioco.
     * Riproduce anche effetti sonori appropriati basati sui risultati.
     * 
     * @param finalBalance Il saldo finale del giocatore
     * @param totalHands Il numero totale di mani giocate nella sessione
     * @param wonHands Il numero di mani vinte
     * @param lostHands Il numero di mani perse
     */
    public void updatePlayerStats(int finalBalance, int totalHands, int wonHands, int lostHands) {
        if(wonHands < lostHands){
            AudioQueue.queue(AudioManager.SoundEffect.LOSE);
        }
        else{
            AudioQueue.queue(AudioManager.SoundEffect.WIN);
        }
        currentProfile.getStats().setCurrentBalance(finalBalance);

        currentProfile.getStats().setTotalHandsPlayed(
                currentProfile.getStats().getTotalHandsPlayed() + totalHands);

        currentProfile.getStats().setHandsWon(
                currentProfile.getStats().getHandsWon() + wonHands);

        currentProfile.getStats().setHandsLost(
                currentProfile.getStats().getHandsLost() + lostHands);

        profileManager.updateProfile(currentProfile);

        logger.logInfo("Statistiche aggiornate: balance=" + finalBalance +
                ", mani giocate=" + totalHands +
                ", vinte=" + wonHands +
                ", perse=" + lostHands);
    }
}
