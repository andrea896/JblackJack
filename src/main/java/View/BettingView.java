package View;

import Controller.BlackjackBettingListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BettingView extends VBox {
    private final Slider betSlider;
    private final Label betAmountLabel;
    private final Button placeBetButton;
    private final VBox insuranceBox;
    private final Label insuranceLabel;
    private final HBox insuranceButtons;
    private BlackjackBettingListener bettingListener;

    public BettingView(int minBet, int maxBet) {
        setAlignment(Pos.CENTER);
        setSpacing(10); // Spaziatura ridotta
        setPadding(new Insets(10));
        setPrefWidth(400); // Larghezza preferita per la zona scommesse

        // Label per scommessa
        Label betLabel = new Label("SCOMMESSA:");
        betLabel.getStyleClass().add("bet-title");

        // Label per visualizzare l'importo selezionato
        betAmountLabel = new Label(" " + minBet);
        betAmountLabel.getStyleClass().add("bet-amount");

        // Slider per selezionare la scommessa (orizzontale)
        betSlider = new Slider(minBet, maxBet, minBet);
        betSlider.setShowTickMarks(true);
        betSlider.setShowTickLabels(true);
        betSlider.setMajorTickUnit((maxBet - minBet) / 4);
        betSlider.setBlockIncrement((maxBet - minBet) / 10);
        betSlider.setPrefWidth(160); // Larghezza fissa per lo slider

        // Bottone per confermare la scommessa
        placeBetButton = new Button("PLACE BET");
        placeBetButton.getStyleClass().add("bet-button");

        // Panel per l'assicurazione (inizialmente nascosto)
        insuranceLabel = new Label("DO YOU WANT TO TAKE INSURANCE?");
        insuranceLabel.getStyleClass().add("insurance-label");

        Button acceptInsuranceButton = new Button("YES");
        acceptInsuranceButton.getStyleClass().add("accept-button");

        Button declineInsuranceButton = new Button("NO");
        declineInsuranceButton.getStyleClass().add("decline-button");

        insuranceButtons = new HBox(10, acceptInsuranceButton, declineInsuranceButton);
        insuranceButtons.setAlignment(Pos.CENTER);

        insuranceBox = new VBox(5, insuranceLabel, insuranceButtons); // Spaziatura ridotta
        insuranceBox.setAlignment(Pos.CENTER);
        insuranceBox.setVisible(false);
        insuranceBox.getStyleClass().add("game-component");

        // Setup listeners
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

        // Assembla il layout
        getChildren().addAll(betLabel, betAmountLabel, betSlider, placeBetButton, insuranceBox);
    }

    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
    }

    public int getBetAmount() {
        return (int) betSlider.getValue();
    }

    public void showBettingControls(boolean visible) {
        betSlider.setVisible(visible);
        betAmountLabel.setVisible(visible);
        placeBetButton.setVisible(visible);
    }

    public void showInsuranceOption() {
        insuranceBox.setVisible(true);
    }

    public void hideInsuranceOption() {
        insuranceBox.setVisible(false);
    }

    public void updateInsuranceDisplay(int amount) {
        insuranceLabel.setText("INSURANCE: " + amount);
        insuranceButtons.setVisible(false);
    }

    public void setMaxBetSlider(int maxBet) {
        betSlider.setVisible(true);
        betSlider.setShowTickMarks(true);
        betSlider.setShowTickLabels(true);
        betSlider.setMax(maxBet);
        betSlider.setMajorTickUnit((maxBet - 10) / 4);
        betSlider.setBlockIncrement((maxBet - 10) / 10);
        betSlider.setPrefWidth(160); // Larghezza fissa per lo slider
    }
}