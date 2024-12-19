package Controller;

import Model.GameStats.GameStats;
import Model.Profile.UserProfile;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Controller per il menu principale dell'applicazione. Gestisce la navigazione tra i diversi
 * sottomenu (gioco, profilo, classifica) e le relative animazioni di transizione.
 */
public class MenuController {
    @FXML
    private VBox playMenuBox, profileMenuBox, rankMenuBox;
    @FXML
    private Circle avatarCircle;
    @FXML
    private StackPane transitionPane;
    @FXML
    private Label transitionLabel;
    @FXML
    private Label totalHandsLabel, wonHandsLabel, lostHandsLabel, balanceLabel;
    @FXML
    private TextField nameField;

    private GameManager gameManager;
    private VBox currentMenuBox;
    private boolean isMenuVisible = false;

    /**
     * Inizializza il controller dopo che i componenti FXML sono stati caricati.
     * Imposta la posizione iniziale dei menu fuori dallo schermo.
     */
    @FXML
    public void initialize() {
        playMenuBox.setTranslateX(550);
        profileMenuBox.setTranslateX(550);
        rankMenuBox.setTranslateX(550);
        gameManager.getInstance();
    }

    /**
     * Gestisce il clic sul pulsante "Play".
     * Mostra o nasconde il menu di gioco con un'animazione di transizione.
     */
    @FXML
    public void onPlayButtonClick(){
        System.out.println("Personalizzazione Avviata");
        toggleMenu(playMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Profile".
     * Mostra o nasconde il menu del profilo con un'animazione di transizione.
     */
    @FXML
    public void onProfileButtonClick(){
        System.out.println("Profilo Avviato");
        System.out.println("Profile Avviato");
        toggleMenu(profileMenuBox);
        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateNickname();
            }
        });
        updateUIFromProfile();
    }

    /**
     * Gestisce il clic sul pulsante "Rank".
     * Mostra o nasconde il menu della classifica con un'animazione di transizione.
     */
    @FXML
    public void onRankButtonClick(){
        System.out.println("Rank Avviato");
        toggleMenu(rankMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Exit".
     * Chiude l'applicazione.
     */
    @FXML
    public void onExitButtonClick(){
        System.exit(0);
    }

    /**
     * Gestisce il clic sul pulsante "Change".
     * Esegue azioni personalizzate legate al pulsante di cambio.
     */
    @FXML
    public void onChangeButtonClick(){
        System.out.println("Change Avviato");
    }

    /**
     * Gestisce il clic sul pulsante "Start Game".
     * Mostra una schermata di transizione con un messaggio di avvio del gioco.
     */
    @FXML
    public void onStartGameButtonClick(){
        System.out.println("Game Avviato");
        showTransitionScreen("STARTING GAME");
    }

    /**
     * Mostra o nasconde un menu specifico con un'animazione di transizione.
     * Se un altro menu è visibile, lo nasconde prima di mostrare il nuovo menu.
     *
     * @param menuBox Il VBox del menu da mostrare o nascondere.
     */
    private void toggleMenu(VBox menuBox) {
        if (currentMenuBox != null && currentMenuBox != menuBox)
            hideMenu(currentMenuBox);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), menuBox);

        if (isMenuVisible && currentMenuBox == menuBox) {
            tt.setToX(550);
            tt.setOnFinished(event -> menuBox.setVisible(false));
            isMenuVisible = false;
        } else {
            menuBox.setVisible(true);
            tt.setToX(0);
            isMenuVisible = true;
        }
        tt.play();
        currentMenuBox = menuBox;
    }

    /**
     * Nasconde un menu specifico con un'animazione di transizione.
     *
     * @param menuBox Il VBox del menu da nascondere.
     */
    private void hideMenu(VBox menuBox) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), menuBox);
        tt.setToX(550);
        tt.setOnFinished(event -> menuBox.setVisible(false));
        tt.play();
    }

    /**
     * Mostra una schermata di transizione con un titolo specificato.
     * La schermata si dissolve gradualmente, mostra il titolo e si dissolve di nuovo.
     *
     * @param title Il testo da mostrare durante la transizione.
     */
    private void showTransitionScreen(String title) {
        transitionLabel.setText(title);
        transitionPane.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), transitionPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), transitionPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));

        fadeOut.setOnFinished(event -> {
            transitionPane.setVisible(false);
        });

        fadeIn.setOnFinished(event -> fadeOut.play());
        fadeIn.play();
    }

    private void updateNickname() {
        String nickname = nameField.getText();
        if (!nickname.isEmpty())
            gameManager.updateNickname(nickname);
    }

    private void updateUIFromProfile() {
        UserProfile currentProfile = gameManager.getCurrentProfile();
        if (currentProfile != null) {
            nameField.setText(currentProfile.getNickname());
            GameStats stats = currentProfile.getStats();
            totalHandsLabel.setText(String.valueOf(stats.getTotalHandsPlayed()));
            wonHandsLabel.setText(String.valueOf(stats.getHandsWon()));
            lostHandsLabel.setText(String.valueOf(stats.getHandsLost()));
            balanceLabel.setText(String.valueOf(stats.getCurrentBalance()));
        }
    }
}
