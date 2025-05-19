package View;

import Controller.RoundEndListener;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Pannello scorrevole che appare dalla destra per chiedere all'utente
 * se vuole giocare un altro round o uscire dal gioco.
 */
public class EndRoundPanel extends VBox {

    private RoundEndListener listener;

    /**
     * Crea un nuovo pannello di fine round con saldo sufficiente.
     */
    public EndRoundPanel() {
        getStyleClass().add("end-round-panel");

        setPrefWidth(400);
        setPrefHeight(300);
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setTranslateX(420);

        setupNormalView();
    }

    /**
     * Configura la vista normale (quando il saldo è sufficiente).
     */
    private void setupNormalView() {
        getChildren().clear();

        Label titleLabel = new Label("Round Ended");
        titleLabel.getStyleClass().add("end-round-title");

        Label questionLabel = new Label("Do you want to play another round?");
        questionLabel.getStyleClass().add("end-round-message");

        Button playAgainButton = new Button("New Round");
        playAgainButton.getStyleClass().addAll("action-button", "play-again-button");
        playAgainButton.setOnAction(e -> {
            if (listener != null) {
                listener.onNewRoundRequested();
                slideOut();
            }
        });

        Button exitButton = new Button("Exit Game");
        exitButton.getStyleClass().addAll("action-button", "exit-button");
        exitButton.setOnAction(e -> {
            if (listener != null) {
                listener.onExitRequested();
            }
        });

        getChildren().addAll(titleLabel, questionLabel, playAgainButton, exitButton);
    }

    /**
     * Configura la vista per saldo insufficiente.
     *
     * @param currentBalance Il saldo attuale
     * @param minimumBet La scommessa minima
     */
    private void setupInsufficientFundsView(int currentBalance, int minimumBet) {
        getChildren().clear();

        String titleText;
        String messageText;

        titleText = "You're Bankrupt!";
        messageText = "You don't have enough funds to continue playing.";

        Label titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("end-round-title");

        Label messageLabel = new Label(messageText);
        messageLabel.getStyleClass().add("end-round-message");

        Button reloadButton = new Button("Reload 1000 Chips");
        reloadButton.getStyleClass().addAll("action-button", "reload-button");
        reloadButton.setOnAction(e -> {
            if (listener != null) {
                listener.onBalanceReloadRequested(1000);
                slideOut();
            }
        });

        Button exitButton = new Button("Exit Game");
        exitButton.getStyleClass().addAll("action-button", "exit-button");
        exitButton.setOnAction(e -> {
            if (listener != null) {
                listener.onExitRequested();
            }
        });

        getChildren().addAll(titleLabel, messageLabel, reloadButton, exitButton);
    }

    /**
     * Imposta il listener per gli eventi di fine round.
     *
     * @param listener L'oggetto listener
     */
    public void setEndRoundListener(RoundEndListener listener) {
        this.listener = listener;
    }

    /**
     * Mostra il pannello per un nuovo round.
     */
    public void showForNewRound() {
        setupNormalView();
        slideIn();
    }

    /**
     * Mostra il pannello per saldo insufficiente.
     *
     * @param currentBalance Il saldo attuale
     * @param minimumBet La scommessa minima
     */
    public void showForInsufficientFunds(int currentBalance, int minimumBet) {
        setupInsufficientFundsView(currentBalance, minimumBet);
        slideIn();
    }

    /**
     * Anima il pannello per entrare nello schermo da destra.
     */
    private void slideIn() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), this);
        slideIn.setToX(-470);
        AnimationQueue.queue(slideIn);
    }

    /**
     * Anima il pannello per uscire dallo schermo verso destra.
     */
    private void slideOut() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), this);
        slideOut.setToX(420);
        slideOut.play();
    }
}
