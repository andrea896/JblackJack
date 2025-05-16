package View;

import Model.Game.Objects.Card;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.List;

public class HandView extends VBox {
    private Label valueLabel;
    private Label betLabel;
    private HBox handContainer;
    private Label insuranceLabel;

    public HandView() {
        setAlignment(javafx.geometry.Pos.CENTER);

        valueLabel = new Label();
        valueLabel.getStyleClass().add("hand-value");
        betLabel = new Label("Bet: ");
        betLabel.getStyleClass().add("bet-label");
        betLabel.setAlignment(javafx.geometry.Pos.CENTER);
        insuranceLabel = new Label("Insur: ");
        insuranceLabel.getStyleClass().add("bet-label");
        insuranceLabel.setAlignment(javafx.geometry.Pos.CENTER);

        HBox labelsContainer = new HBox(5);
        labelsContainer.setSpacing(30);
        labelsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        labelsContainer.getChildren().addAll(valueLabel, betLabel, insuranceLabel);
        handContainer = new HBox(60);
        handContainer.setPrefHeight(40);
        handContainer.setSpacing(2);
        handContainer.setPadding(new Insets(10));
        handContainer.setAlignment(Pos.CENTER);

        getChildren().addAll(labelsContainer, handContainer);
    }

    public void setHandlabel(boolean visible){
        betLabel.setVisible(visible);
    }

    public void setInsuranceLabel(boolean visible){
        insuranceLabel.setVisible(visible);
    }

    public void updateHand(List<Card> cards, int handValue) {
        handContainer.getChildren().clear();

        for (Card card : cards) {
            ImageView cardView = CardImageService.createCardImageView(card);
            handContainer.getChildren().add(cardView);
            cardView.toFront();
        }

        valueLabel.setText("value: " + handValue);
    }

    public void animateCardDealt(Card card, int handValue, boolean isHiddenCard) {
        ImageView cardView;
        if (isHiddenCard)
            cardView = CardImageService.getCardBackImageView();
        else
            cardView = CardImageService.createCardImageView(card);

        double dealerX = 15; // Posizione X del dealer nella scena
        double dealerY = 400; // Posizione Y del dealer nella scena

        double startX = dealerX - handContainer.getLayoutX() - handContainer.getBoundsInParent().getMinX();
        double startY = dealerY - handContainer.getLayoutY() - handContainer.getBoundsInParent().getMinY();

        cardView.setTranslateX(startX);
        cardView.setTranslateY(startY);

        cardView.setScaleX(0.7);
        cardView.setScaleY(0.7);

        handContainer.getChildren().add(cardView);

        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(600), cardView);
        moveTransition.setToX(0);
        moveTransition.setToY(0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(600), cardView);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        RotateTransition wobbleTransition = new RotateTransition(Duration.millis(600), cardView);
        wobbleTransition.setFromAngle(-5);
        wobbleTransition.setToAngle(0);
        wobbleTransition.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.2, 1));

        ParallelTransition dealAnimation = new ParallelTransition(
                moveTransition, scaleTransition, wobbleTransition
        );

        valueLabel.setText("value: " + handValue);

        dealAnimation.setInterpolator(Interpolator.EASE_OUT);

        AnimationQueue.queue(dealAnimation);
    }

    public void updateBet(int bet) {
        betLabel.setText("Bet: " + bet);
    }

    public void updateInsurance(int insuranceAmount){
        insuranceLabel.setText("Insur: " + insuranceAmount);
    }

    public void updateHandValue(int handValue){
        valueLabel.setText("value: " + handValue);
    }

    public void showBusted() {
        Label bustedLabel = new Label("BUSTED!");
        bustedLabel.getStyleClass().add("busted-label");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), bustedLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), bustedLabel);
        scaleUp.setFromX(0.5);
        scaleUp.setFromY(0.5);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        // Aggiungi la label e avvia l'animazione
        getChildren().add(bustedLabel);

        ParallelTransition animation = new ParallelTransition(fadeIn, scaleUp);
        AnimationQueue.queue(animation);
    }
}
