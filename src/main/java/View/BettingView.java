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

    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
    }

    public void showBettingControls(boolean visible) {
        placeBetButton.setVisible(visible);
        betAmountLabel.setVisible(visible);
        betSlider.setVisible(visible);
    }

    public void showInsuranceOption() {
        insuranceBox.setVisible(true);
    }

    public void hideInsuranceOption() {
        insuranceBox.setVisible(false);
    }

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