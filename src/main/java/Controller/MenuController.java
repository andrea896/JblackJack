package Controller;

import Model.Profile.GameStats;
import Model.Profile.ProfileManager;
import Model.Profile.UserProfile;
import Utility.LoggerUtility;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Controller per il menu principale dell'applicazione. Gestisce la navigazione tra i diversi
 * sottomenu (gioco, profilo, classifica) e le relative animazioni di transizione.
 */
public class MenuController {
    private static final LoggerUtility logger = new LoggerUtility();
    @FXML
    private VBox playMenuBox, profileMenuBox, rankMenuBox;
    @FXML
    private Circle loadAvatarCircle;
    @FXML
    private StackPane transitionPane;
    @FXML
    private Label transitionLabel;
    @FXML
    private Label totalHandsLabel, wonHandsLabel, lostHandsLabel, balanceLabel;
    @FXML
    private TextField loadNameField, newNameField;
    @FXML
    private VBox loadProfileBox, newProfileBox;
    @FXML
    private Circle newAvatarCircle;
    @FXML
    private ComboBox<String> rankingCriteriaComboBox;
    @FXML
    private TableView<UserProfile> rankingTableView;
    @FXML
    private TableColumn<UserProfile, Integer> rankColumn, valueColumn;
    @FXML
    private TableColumn<UserProfile, String> playerNameColumn;
    @FXML
    private RadioButton greenCardRadioButton, redCardRadioButton, blueCardRadioButton;
    @FXML
    private RadioButton onePlayerRadioButton, twoPlayersRadioButton, threePlayersRadioButton;

    private GameManager gameManager;
    private VBox currentMenuBox;
    private boolean isMenuVisible = false;

    private int currentAvatarIndex = 1;
    private List<Image> avatarImages = Arrays.asList(
            new Image("C:\\progetti\\JBlackJack\\src\\main\\resources\\GameMenu\\Images\\avatar1.png"),
            new Image("C:\\progetti\\JBlackJack\\src\\main\\resources\\GameMenu\\Images\\avatar2.png"),
            new Image("C:\\progetti\\JBlackJack\\src\\main\\resources\\GameMenu\\Images\\avatar3.png"),
            new Image("C:\\progetti\\JBlackJack\\src\\main\\resources\\GameMenu\\Images\\avatar4.png")
    );

    /**
     * Inizializza il controller dopo che i componenti FXML sono stati caricati.
     * Imposta la posizione iniziale dei menu fuori dallo schermo.
     */
    @FXML
    public void initialize() {
        playMenuBox.setTranslateX(550);
        profileMenuBox.setTranslateX(550);
        loadProfileBox.setTranslateX(550);
        newProfileBox.setTranslateX(550);
        rankMenuBox.setTranslateX(550);
        gameManager = gameManager.getInstance();
        newAvatarCircle.setFill(new ImagePattern(avatarImages.get(currentAvatarIndex)));
        loadAvatarCircle.setFill(new ImagePattern(avatarImages.get(currentAvatarIndex)));
        // Configura le colonne della TableView
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        valueColumn.setCellValueFactory(cellData -> {
            UserProfile user = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(getValueForCriteria(user, rankingCriteriaComboBox.getValue())).asObject();
        });
        rankingCriteriaComboBox.setOnAction(event -> loadRanking(rankingCriteriaComboBox.getValue()));
    }

    /**
     * Gestisce il clic sul pulsante "Play".
     * Mostra o nasconde il menu di gioco con un'animazione di transizione.
     */
    @FXML
    public void onPlayButtonClick(){
        toggleMenu(playMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Profile".
     * Mostra o nasconde il menu del profilo con un'animazione di transizione.
     */
    @FXML
    public void onProfileButtonClick(){
        toggleMenu(profileMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Rank".
     * Mostra o nasconde il menu della classifica con un'animazione di transizione.
     */
    @FXML
    public void onRankButtonClick(){
        toggleMenu(rankMenuBox);
    }

    private void loadRanking(String criteria) {
        List<UserProfile> profiles = ProfileManager.getInstance().getProfiles();

        // Ordina in base al criterio selezionato
        switch (criteria) {
            case "Hands Lost":
                profiles.sort(Comparator.comparingInt(p -> p.getStats().getHandsLost()));
                break;
            case "Hands Won":
                profiles.sort((p1, p2) -> Integer.compare(p2.getStats().getHandsWon(), p1.getStats().getHandsWon()));
                break;
            case "Total Hands Played":
                profiles.sort((p1, p2) -> Integer.compare(p2.getStats().getTotalHandsPlayed(), p1.getStats().getTotalHandsPlayed()));
                break;
            case "Current Balance":
                profiles.sort((p1, p2) -> Double.compare(p2.getStats().getCurrentBalance(), p1.getStats().getCurrentBalance()));
                break;
        }

        // Converte in ObservableList e aggiorna la TableView
        ObservableList<UserProfile> rankedProfiles = FXCollections.observableArrayList(profiles);
        rankingTableView.setItems(rankedProfiles);

        // Aggiorna la colonna del ranking
        rankColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(rankedProfiles.indexOf(cellData.getValue()) + 1).asObject()
        );
        rankingTableView.refresh();
    }

    private int getValueForCriteria(UserProfile profile, String criteria) {
        if (profile == null || profile.getStats() == null) return 0;
        switch (criteria) {
            case "Hands Lost": return profile.getStats().getHandsLost();
            case "Hands Won": return profile.getStats().getHandsWon();
            case "Total Hands Played": return profile.getStats().getTotalHandsPlayed();
            case "Current Balance": return (int) profile.getStats().getCurrentBalance();
            default: return 0;
        }
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
     * Gestisce il clic sul pulsante "Start Game".
     * Mostra una schermata di transizione con un messaggio di avvio del gioco.
     */
    @FXML
    public void onStartGameButtonClick(){
        if (gameManager.getCurrentProfile() == null) {
            logger.logWarning("Nessun profilo selezionato per iniziare il gioco!");
            showTransitionScreen("Nessun profilo selezionato, accedi al tuo profilo");
            return;
        }
        // Raccogli le opzioni selezionate dall'utente
        int numPlayers = getSelectedNumberOfPlayers(); // Metodo che legge la selezione dai radioButton
        String cardBackDesign = getSelectedCardBackDesign(); // Metodo che legge la selezione dai radioButton o combobox
        showTransitionScreen("STARTING GAME");
        gameManager.startGame(numPlayers, cardBackDesign);

    }

    private int getSelectedNumberOfPlayers() {
        if (onePlayerRadioButton.isSelected()) return 1;
        if (twoPlayersRadioButton.isSelected()) return 2;
        if (threePlayersRadioButton.isSelected()) return 3;
        return 2; // Valore predefinito
    }

    private String getSelectedCardBackDesign() {
        if (blueCardRadioButton.isSelected()) return "Images/cardBack_blue5.png";
        if (redCardRadioButton.isSelected()) return "Images/cardBack_red5.png";
        if (greenCardRadioButton.isSelected()) return "Images/cardBack_green5.png";
        return "Images/cardBack_blue5.png"; // Valore predefinito
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

    private void updateUIFromProfile(String nickname) {
        if (!nickname.isEmpty()) {
            UserProfile currentProfile = gameManager.loadExistingProfile(nickname);
            if (currentProfile != null) {
                loadNameField.setText(currentProfile.getNickname());
                Image avatarImage = new Image(currentProfile.getAvatarPath());
                loadAvatarCircle.setFill(new ImagePattern(avatarImage));
                GameStats stats = currentProfile.getStats();
                totalHandsLabel.setText(String.valueOf(stats.getTotalHandsPlayed()));
                wonHandsLabel.setText(String.valueOf(stats.getHandsWon()));
                lostHandsLabel.setText(String.valueOf(stats.getHandsLost()));
                balanceLabel.setText(String.valueOf(stats.getCurrentBalance()));
            }
        }
        else {
            logger.logWarning("Field del testo vuota");
        }
    }

    /**
     * Gestisce il clic sul pulsante "Change".
     * Esegue azioni personalizzate legate al pulsante di cambio e viene mostrata un'immagine
     * del profilo diversa ad ogni click.
     */
    public void onNewChangeButtonClick() {
        currentAvatarIndex = (currentAvatarIndex + 1) % avatarImages.size();
        newAvatarCircle.setFill(new ImagePattern(avatarImages.get(currentAvatarIndex)));
    }

    public void onCreateProfileClick() {
        logger.logInfo("Aggiorno Nickname e creo il profilo");
        ImagePattern pattern = (ImagePattern) newAvatarCircle.getFill();
        gameManager.createNewProfile(newNameField.getText(), pattern.getImage().getUrl());
        updateUIFromProfile(newNameField.getText());
    }

    public void onLoadProfileClick() {
        loadProfileBox.setVisible(true);
        toggleMenu(loadProfileBox);
        loadNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal)
                updateUIFromProfile(loadNameField.getText());
        });
    }

    public void onNewProfileClick() {
        newProfileBox.setVisible(true);
        toggleMenu(newProfileBox);
    }
}
