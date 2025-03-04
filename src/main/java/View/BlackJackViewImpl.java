package View;

import Controller.BlackjackActionListener;
import Controller.BlackjackBettingListener;
import Model.Game.Objects.Card;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class BlackJackViewImpl extends BorderPane implements BlackjJackView {
    // Componenti principali
    private final PlayerView playerView;
    private final DealerView dealerView;
    private final List<AIPlayerView> aiPlayerViews;
    private final ControlPanelView controlPanelView;
    private final BettingView bettingView;
    private Stage primaryStage;


    // Altri elementi UI
    private final Label statusMessageLabel;
    private final Button playAgainButton;

    // Listeners
    private BlackjackActionListener actionListener;
    private BlackjackBettingListener bettingListener;

    public BlackJackViewImpl(Stage primaryStage, String cardBackDesign, String playerImagePath, int numberOfPlayers) {
        // Inizializza il servizio per le immagini
        //CardImageService.initialize(cardBackDesign);
        this.primaryStage = primaryStage;
        // Crea i componenti
        playerView = new PlayerView("Tu", playerImagePath);
        dealerView = new DealerView();

        aiPlayerViews = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            aiPlayerViews.add(new AIPlayerView("AI " + (i + 1), "C:\\progetti\\JBlackJack\\src\\main\\resources\\GameMenu\\Images\\avatar4.png"));
        }

        controlPanelView = new ControlPanelView();
        bettingView = new BettingView(10, 200);

        statusMessageLabel = new Label();
        statusMessageLabel.getStyleClass().add("status-message");
        statusMessageLabel.setVisible(false);

        playAgainButton = new Button("Gioca Ancora");
        playAgainButton.getStyleClass().add("play-again-button");
        playAgainButton.setVisible(false);
        playAgainButton.setOnAction(e -> {
            if (bettingListener != null) {
                showPlayAgainButton(false);
                bettingView.showBettingControls(true);
            }
        });

        // Configura il layout
        setupLayout();
    }

    private void setupLayout() {
        // Layout per il dealer (top)
        setTop(dealerView);
        BorderPane.setAlignment(dealerView, javafx.geometry.Pos.CENTER);
        BorderPane.setMargin(dealerView, new Insets(20));

        // Layout per i giocatori (center)
        HBox playersBox = new HBox(40);
        playersBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Dividi i giocatori AI a sinistra e destra del giocatore umano
        int leftCount = aiPlayerViews.size() / 2;
        int rightCount = aiPlayerViews.size() - leftCount;

        // Aggiungi AI a sinistra
        for (int i = 0; i < leftCount; i++) {
            playersBox.getChildren().add(aiPlayerViews.get(i));
        }

        // Aggiungi giocatore umano al centro
        playersBox.getChildren().add(playerView);

        // Aggiungi AI a destra
        for (int i = 0; i < rightCount; i++) {
            playersBox.getChildren().add(aiPlayerViews.get(leftCount + i));
        }

        setCenter(playersBox);

        // Layout per controlli e scommesse (bottom)
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER);
        bottomBox.getChildren().addAll(controlPanelView, statusMessageLabel, playAgainButton, bettingView);
        setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20));
    }

    // Implementazione dell'interfaccia BlackjackView

    @Override
    public void updateStatusMessage(String message) {
        statusMessageLabel.setText(message);
        statusMessageLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> statusMessageLabel.setVisible(false));
        pause.play();
    }

    @Override
    public void showGameStartAnimation() {
        // Implementazione di un'animazione di inizio gioco
        updateStatusMessage("Partita iniziata!");
    }

    @Override
    public void showPlayAgainButton(boolean visible) {
        playAgainButton.setVisible(visible);
    }

    @Override
    public PlayerView getPlayerView() {
        return playerView;
    }

    @Override
    public DealerView getDealerView() {
        return dealerView;
    }

    @Override
    public List<AIPlayerView> getAIPlayerViews() {
        return aiPlayerViews;
    }

    @Override
    public ControlPanelView getControlPanelView() {
        return controlPanelView;
    }

    @Override
    public BettingView getBettingView() {
        return bettingView;
    }

    @Override
    public void setActionListener(BlackjackActionListener listener) {
        this.actionListener = listener;
        controlPanelView.setActionListener(listener);
    }

    @Override
    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
        bettingView.setBettingListener(listener);
    }

    @Override
    public int getBetAmount() {
        return bettingView.getBetAmount();
    }
}
