package View;

import Controller.BlackjackBettingListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Vista per la gestione delle scommesse e delle assicurazioni nel BlackJack.
 * Fornisce controlli per piazzare scommesse tramite slider e gestire l'assicurazione
 * quando il dealer mostra un Asso.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class BettingView extends VBox {
    private final Slider betSlider;
    private final Label betAmountLabel;
    private final Button placeBetButton;
    private final VBox insuranceBox;
    private final Label insuranceLabel;
    private final HBox insuranceButtons;
    private BlackjackBettingListener bettingListener;

    /**
     * Costruisce la vista delle scommesse con i limiti di puntata specificati.
     * 
     * @param minBet La scommessa minima consentita
     * @param maxBet La scommessa massima consentita
     */
    public BettingView(int minBet, int maxBet) {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(400);

        Label betLabel = new Label("Bet: ");
        betLabel.getStyleClass().add("bet-title");

        betAmountLabel = new Label(" " + minBet);
        betAmountLabel.getStyleClass().add("bet-amount");

        betSlider = new Slider(minBet, maxBet, minBet);
        betSlider.setShowTickMarks(true);
        betSlider.setShowTickLabels(true);
        betSlider.setMajorTickUnit((maxBet - minBet) / 4);
        betSlider.setBlockIncrement((maxBet - minBet) / 10);
        betSlider.setPrefWidth(160);

        placeBetButton = new Button("PLACE BET");
        placeBetButton.getStyleClass().add("bet-button");

        insuranceLabel = new Label("DO YOU WANT TO TAKE INSURANCE?");
        insuranceLabel.getStyleClass().add("insurance-label");

        Button acceptInsuranceButton = new Button("YES");
        acceptInsuranceButton.getStyleClass().add("accept-button");

        Button declineInsuranceButton = new Button("NO");
        declineInsuranceButton.getStyleClass().add("decline-button");

        insuranceButtons = new HBox(10, acceptInsuranceButton, declineInsuranceButton);
        insuranceButtons.setAlignment(Pos.CENTER);

        insuranceBox = new VBox(5, insuranceLabel, insuranceButtons);
        insuranceBox.setAlignment(Pos.TOP_CENTER);
        insuranceBox.setVisible(false);
        insuranceBox.getStyleClass().add("game-component");

        betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            betAmountLabel.setText(" " + (int) newVal.doubleValue());
        });

        placeBetButton.setOnAction(e -> {
            if (bettingListener != null) {
                bettingListener.onBetPlaced((int) betSlider.getValue());
                showBettingControls(false);
            }
        });

        acceptInsuranceButton.setOnAction(e -> {
            if (bettingListener != null) {
                bettingListener.onInsuranceAccepted();
                hideInsuranceOption();
            }
        });

        declineInsuranceButton.setOnAction(e -> {
            if (bettingListener != null) {
                bettingListener.onInsuranceDeclined();
                hideInsuranceOption();
            }
        });

        getChildren().addAll(betLabel, betAmountLabel, betSlider, placeBetButton, insuranceBox);
    }

    /**
     * Imposta il listener per gli eventi di scommessa.
     * 
     * @param listener Il listener che gestirà gli eventi di scommessa
     */
    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
    }

    /**
     * Mostra o nasconde i controlli per le scommesse.
     * 
     * @param visible true per mostrare i controlli, false per nasconderli
     */
    public void showBettingControls(boolean visible) {
        placeBetButton.setVisible(visible);
        betAmountLabel.setVisible(visible);
        betSlider.setVisible(visible);
    }

    /**
     * Mostra l'opzione di assicurazione quando il dealer ha un Asso.
     */
    public void showInsuranceOption() {
        insuranceBox.setVisible(true);
    }

    /**
     * Nasconde l'opzione di assicurazione.
     */
    public void hideInsuranceOption() {
        insuranceBox.setVisible(false);
    }

    /**
     * Aggiorna il valore massimo dello slider delle scommesse.
     * Utile per adattare i limiti al saldo corrente del giocatore.
     * 
     * @param maxBet Il nuovo valore massimo per le scommesse
     */
    public void setMaxBetSlider(int maxBet) {
        betSlider.setVisible(true);
        betSlider.setShowTickMarks(true);
        betSlider.setShowTickLabels(true);
        betSlider.setMax(maxBet);
        betSlider.setMajorTickUnit((maxBet - 10) / 4);
        betSlider.setBlockIncrement((maxBet - 10) / 10);
        betSlider.setPrefWidth(160);
    }
}
