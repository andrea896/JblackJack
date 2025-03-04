package View;

import Controller.BlackjackBettingListener;
import javafx.geometry.Insets;
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
        setAlignment(javafx.geometry.Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));

        // Label per scommessa
        Label betLabel = new Label("Scommessa:");
        betLabel.getStyleClass().add("bet-title");

        // Slider per selezionare la scommessa
        betSlider = new Slider(minBet, maxBet, minBet);
        betSlider.setShowTickMarks(true);
        betSlider.setShowTickLabels(true);
        betSlider.setMajorTickUnit((maxBet - minBet) / 5);
        betSlider.setBlockIncrement((maxBet - minBet) / 10);

        // Label per visualizzare l'importo selezionato
        betAmountLabel = new Label("€" + minBet);
        betAmountLabel.getStyleClass().add("bet-amount");

        // Bottone per confermare la scommessa
        placeBetButton = new Button("Piazza Scommessa");
        placeBetButton.getStyleClass().add("bet-button");

        // Panel per l'assicurazione (inizialmente nascosto)
        insuranceLabel = new Label("Vuoi prendere l'assicurazione?");
        insuranceLabel.getStyleClass().add("insurance-label");

        Button acceptInsuranceButton = new Button("Sì");
        acceptInsuranceButton.getStyleClass().add("accept-button");

        Button declineInsuranceButton = new Button("No");
        declineInsuranceButton.getStyleClass().add("decline-button");

        insuranceButtons = new HBox(20, acceptInsuranceButton, declineInsuranceButton);
        insuranceButtons.setAlignment(javafx.geometry.Pos.CENTER);

        insuranceBox = new VBox(10, insuranceLabel, insuranceButtons);
        insuranceBox.setAlignment(javafx.geometry.Pos.CENTER);
        insuranceBox.setVisible(false);

        // Setup listeners
        betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            betAmountLabel.setText("€" + (int) newVal.doubleValue());
        });

        placeBetButton.setOnAction(e -> {
            if (bettingListener != null) {
                bettingListener.onBetPlaced((int) betSlider.getValue());
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
        getChildren().addAll(betLabel, betSlider, betAmountLabel, placeBetButton, insuranceBox);
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
        insuranceLabel.setText("Assicurazione piazzata: €" + amount);
        insuranceButtons.setVisible(false);
    }
}
