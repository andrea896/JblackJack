package Controller;

import Model.Profile.GameStats;
import Model.Profile.ProfileManager;
import Model.Profile.UserProfile;
import Utility.LoggerUtility;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private int currentAvatarIndex = 0;

    private List<String> avatarPaths = Arrays.asList(
            "/GameMenu/Images/avatar1.png",
            "/GameMenu/Images/avatar2.png",
            "/GameMenu/Images/avatar3.png",
            "/GameMenu/Images/avatar4.png"
    );

    private List<Image> avatarImages = avatarPaths.stream()
            .map(path -> new Image(getClass().getResourceAsStream(path)))
            .collect(Collectors.toList());

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
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        valueColumn.setCellValueFactory(cellData -> {
            UserProfile user = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(getValueForCriteria(user, rankingCriteriaComboBox.getValue())).asObject();
        });
        rankingCriteriaComboBox.setOnAction(event -> loadRanking(rankingCriteriaComboBox.getValue()));
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.MENU_MUSIC);
    }

    /**
     * Gestisce il clic sul pulsante "Play".
     * Mostra o nasconde il menu di gioco con un'animazione di transizione.
     */
    @FXML
    public void onPlayButtonClick(){
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        toggleMenu(playMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Profile".
     * Mostra o nasconde il menu del profilo con un'animazione di transizione.
     */
    @FXML
    public void onProfileButtonClick(){
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        toggleMenu(profileMenuBox);
    }

    /**
     * Gestisce il clic sul pulsante "Rank".
     * Mostra o nasconde il menu della classifica con un'animazione di transizione.
     */
    @FXML
    public void onRankButtonClick() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        toggleMenu(rankMenuBox);
    }

    /**
     * Carica e ordina la classifica dei profili utente secondo il criterio specificato.
     * Recupera tutti i profili dal ProfileManager, li ordina in base al criterio selezionato
     * e aggiorna la TableView con i risultati ordinati.
     *
     * @param criteria Il criterio di ordinamento da applicare. Deve essere uno dei valori
     *                 supportati: "Hands Lost", "Hands Won", "Total Hands Played", "Current Balance".
     *                 Se il criterio non è riconosciuto, la lista non viene ordinata.
     * @since 1.0
     */
    private void loadRanking(String criteria) {
        List<UserProfile> profiles = ProfileManager.getInstance().getProfiles();

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

        ObservableList<UserProfile> rankedProfiles = FXCollections.observableArrayList(profiles);
        rankingTableView.setItems(rankedProfiles);

        rankColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(rankedProfiles.indexOf(cellData.getValue()) + 1).asObject()
        );
        rankingTableView.refresh();
    }

    /**
     * Estrae il valore numerico corrispondente al criterio specificato dalle statistiche del profilo.
     * Questo metodo helper viene utilizzato per ottenere valori comparabili dalle statistiche
     * di un profilo utente in base al criterio di ranking selezionato.
     *
     * @param profile Il profilo utente da cui estrarre il valore. Può essere null.
     * @param criteria Il criterio per determinare quale valore estrarre.
     *                 Deve corrispondere a uno dei criteri supportati dal sistema di ranking.
     *
     * @return Il valore numerico corrispondente al criterio, oppure 0 se il profilo/criterio
     *         non è valido o se si verifica un errore nell'estrazione del dato.
     *
     * @since 1.0
     */
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
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        System.exit(0);
    }

    /**
     * Gestisce il clic sul pulsante "Start Game".
     * Mostra una schermata di transizione con un messaggio di avvio del gioco.
     * Viene subito caricata la nuova Scene di gioco e mostrata
     */
    @FXML
    public void onStartGameButtonClick(){
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        if (gameManager.getCurrentProfile() == null) {
            logger.logWarning("Nessun profilo selezionato per iniziare il gioco!");
            showTransitionScreen("Nessun profilo selezionato, accedi al tuo profilo");
            return;
        }
        int numPlayers = getSelectedNumberOfPlayers();
        String cardBackDesign = getSelectedCardBackDesign();
        showTransitionScreen("STARTING GAME");
        Stage currentStage = (Stage) loadNameField.getScene().getWindow();
        gameManager.init(currentStage);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3.5),
                ae -> gameManager.startGame(numPlayers, cardBackDesign)
        ));
        timeline.play();
    }

    /**
     * Determina il numero di giocatori AI selezionato dall'utente tramite i radio button.
     * Verifica quale radio button è attualmente selezionato e restituisce il numero
     * corrispondente di giocatori AI da includere nella partita.
     *
     * @return Il numero di giocatori AI selezionato:
     *         <ul>
     *         <li>1 se è selezionato il radio button "Un giocatore"</li>
     *         <li>2 se è selezionato il radio button "Due giocatori"</li>
     *         <li>3 se è selezionato il radio button "Tre giocatori"</li>
     *         <li>2 come valore di default se nessun radio button è selezionato</li>
     *         </ul>
     * @since 1.0
     */
    private int getSelectedNumberOfPlayers() {
        if (onePlayerRadioButton.isSelected()) return 1;
        if (twoPlayersRadioButton.isSelected()) return 2;
        if (threePlayersRadioButton.isSelected()) return 3;
        return 2;
    }

    /**
     * Determina il design del dorso delle carte selezionato dall'utente.
     * Verifica quale radio button per il design delle carte è attualmente selezionato
     * e restituisce il percorso dell'immagine corrispondente.
     *
     * @return Il percorso dell'immagine del dorso delle carte selezionato:
     *         <ul>
     *         <li>"Images/cardBack_blue5.png" per il design blu</li>
     *         <li>"Images/cardBack_red5.png" per il design rosso</li>
     *         <li>"Images/cardBack_green5.png" per il design verde</li>
     *         <li>"Images/cardBack_blue5.png" come design di default se nessun radio button è selezionato</li>
     *         </ul>
     * @since 1.0
     */
    private String getSelectedCardBackDesign() {
        if (blueCardRadioButton.isSelected()) return "Images/cardBack_blue5.png";
        if (redCardRadioButton.isSelected()) return "Images/cardBack_red5.png";
        if (greenCardRadioButton.isSelected()) return "Images/cardBack_green5.png";
        return "Images/cardBack_blue5.png";
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

    /**
     * Aggiorna l'interfaccia utente con i dati del profilo specificato.
     * Carica il profilo dal database tramite il nickname e popola tutti i campi
     * dell'interfaccia con le informazioni del profilo (avatar, statistiche, saldo).
     *
     * @param nickname Il nickname del profilo da caricare e visualizzare.
     *
     * @throws IllegalArgumentException se il nickname è null
     * @throws RuntimeException se l'avatar del profilo non può essere caricato
     *
     * @since 1.0
     */
    private void updateUIFromProfile(String nickname) {
        if (!nickname.isEmpty()) {
            UserProfile currentProfile = gameManager.loadExistingProfile(nickname);
            if (currentProfile != null) {
                loadNameField.setText(currentProfile.getNickname());
                Image avatarImage = new Image(getClass().getResourceAsStream(currentProfile.getAvatarPath()));
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
    @FXML
    public void onNewChangeButtonClick() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        currentAvatarIndex = (currentAvatarIndex + 1) % avatarImages.size();
        newAvatarCircle.setFill(new ImagePattern(avatarImages.get(currentAvatarIndex)));
    }

    /**
     * Gestisce l'evento di click sul pulsante "Crea Profilo".
     * Crea un nuovo profilo utente utilizzando il nickname inserito e l'avatar
     * attualmente selezionato, quindi aggiorna l'interfaccia con i dati del nuovo profilo.
     *
     * @since 1.0
     */
    @FXML
    public void onCreateProfileClick() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        logger.logInfo("Aggiorno Nickname e creo il profilo");
        String avatarPath = avatarPaths.get(currentAvatarIndex);
        gameManager.createNewProfile(newNameField.getText(), avatarPath);
        updateUIFromProfile(newNameField.getText());
    }

    /**
     * Gestisce l'evento di click sul pulsante "Carica Profilo".
     * Mostra la sezione per il caricamento di un profilo esistente e configura
     * il listener per l'aggiornamento automatico dell'UI quando l'utente inserisce un nickname.
     *
     * @since 1.0
     */
    @FXML
    public void onLoadProfileClick() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        loadProfileBox.setVisible(true);
        toggleMenu(loadProfileBox);
        loadNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal)
                updateUIFromProfile(loadNameField.getText());
        });
    }

    /**
     * Gestisce l'evento di click sul pulsante "Nuovo Profilo".
     * Mostra la sezione per la creazione di un nuovo profilo utente,
     * permettendo all'utente di inserire nickname e selezionare un avatar.
     *
     * @since 1.0
     */
    @FXML
    public void onNewProfileClick() {
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.BUTTON_CLICK);
        newProfileBox.setVisible(true);
        toggleMenu(newProfileBox);
    }
}
