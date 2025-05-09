package View;

import Controller.BlackjackActionListener;
import Controller.BlackjackBettingListener;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class BlackJackView extends AnchorPane {
    // Componenti principali
    private final DealerView dealerView;
    private final PlayerInfoView playerView;
    private final ControlPanelView controlPanelView;
    private final BettingView bettingView;
    private PlayerHandsView playerHandsView;

    // Elementi dell'interfaccia
    private List<PlayerHandsView> aiHandsViews;
    private final HBox bottomControlsArea;
    private final HBox allHandsArea;

    // Altri elementi UI
    private final Label statusMessageLabel;
    private final Button playAgainButton;

    // Listeners
    private BlackjackActionListener actionListener;
    private BlackjackBettingListener bettingListener;

    public BlackJackView(String cardBackDesign, String playerImagePath, int numberOfPlayers, String playerName, int balance) {
        CardImageService.setCardBackDesign(cardBackDesign);

        setPrefSize(1355, 944);
        getStyleClass().add("blackjack-table");

        // Crea le componenti principali
        dealerView = new DealerView();
        playerView = new PlayerInfoView(playerName, playerImagePath, balance);

        controlPanelView = new ControlPanelView();
        bettingView = new BettingView(10, balance);

        // ===== SEZIONE DEALER =====
        dealerView.setPrefSize(400, 250);
        dealerView.getStyleClass().add("dealer-area");

        // ===== SEZIONE MANI GIOCATORI=====
        playerHandsView = new PlayerHandsView("Your Hands", false);
        aiHandsViews = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++)
            aiHandsViews.add(new PlayerHandsView("AI "+ (i+1), true));

        // Container per tutte le mani
        allHandsArea = new HBox();
        allHandsArea.setPrefSize(1324, 336);
        allHandsArea.getChildren().add(playerHandsView);
        allHandsArea.getChildren().addAll(aiHandsViews);
        allHandsArea.setAlignment(Pos.TOP_CENTER);
        allHandsArea.setSpacing(5);
        allHandsArea.getStyleClass().add("ai-player-area");

        // Container per tutti i controlli inferiori
        bottomControlsArea = new HBox();
        bottomControlsArea.getStyleClass().add("controls-panel");
        bottomControlsArea.setPrefSize(1355, 200);
        bottomControlsArea.getChildren().addAll(bettingView, controlPanelView, playerView);
        bottomControlsArea.setSpacing(20);

        // Elementi aggiuntivi
        statusMessageLabel = new Label();
        statusMessageLabel.getStyleClass().add("status-message");
        statusMessageLabel.setVisible(true);

        playAgainButton = new Button("Play Again");
        playAgainButton.getStyleClass().add("play-again-button");
        playAgainButton.setVisible(true);

        // Posiziona gli elementi nell'AnchorPane
        AnchorPane.setTopAnchor(dealerView, 15.0);
        AnchorPane.setLeftAnchor(dealerView, 465.0);

        AnchorPane.setTopAnchor(allHandsArea, 350.0);
        AnchorPane.setLeftAnchor(allHandsArea, 5.0);

        AnchorPane.setTopAnchor(bottomControlsArea, 700.0);
        AnchorPane.setLeftAnchor(bottomControlsArea, 20.0);  // Margine sinistro
        AnchorPane.setRightAnchor(bottomControlsArea, 20.0); // Margine destro

        // Aggiungi tutti gli elementi al layout
        getChildren().addAll(dealerView, allHandsArea, bottomControlsArea, statusMessageLabel, playAgainButton);

        setupEventHandlers();
    }

    /**
     * Configura gli event handlers per i pulsanti e altri controlli
     */
    private void setupEventHandlers() {
        playAgainButton.setOnAction(e -> {
            if (bettingListener != null) {
                showPlayAgainButton(false);
            }
        });
    }

    public void updateStatusMessage(String message) {
        statusMessageLabel.setText(message);
        statusMessageLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> statusMessageLabel.setVisible(false));
        pause.play();
    }

    public void showGameStartAnimation() {
        updateStatusMessage("Game Started!");
    }

    public void showPlayAgainButton(boolean visible) {
        playAgainButton.setVisible(visible);
    }

    public PlayerInfoView getPlayerView() {
        return playerView;
    }

    public DealerView getDealerView() {
        return dealerView;
    }

    public PlayerHandsView getPlayerHands() {
        return playerHandsView;
    }

    public List<PlayerHandsView> getAIPlayerViews() {
        return aiHandsViews;
    }

    public ControlPanelView getControlPanelView() {
        return controlPanelView;
    }

    public BettingView getBettingView() {
        return bettingView;
    }

    public void setActionListener(BlackjackActionListener listener) {
        this.actionListener = listener;
        controlPanelView.setActionListener(listener);
    }

    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
        bettingView.setBettingListener(listener);
    }

}