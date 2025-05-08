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
    private final Label valueLabel;
    private final Label betLabel;
    private final HBox handContainer;

    public HandView() {
        setAlignment(javafx.geometry.Pos.CENTER);

        valueLabel = new Label();
        valueLabel.getStyleClass().add("hand-value");
        betLabel = new Label("Bet: ");
        betLabel.getStyleClass().add("bet-label");
        betLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Posiziona le label in un contenitore separato
        HBox labelsContainer = new HBox(5);
        labelsContainer.setSpacing(30);
        labelsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        labelsContainer.getChildren().addAll(valueLabel, betLabel);
        handContainer = new HBox(60);
        handContainer.setPrefHeight(40);
        handContainer.setSpacing(2); // Aggiungi spazio tra le carte
        handContainer.setPadding(new Insets(10));
        handContainer.setAlignment(Pos.CENTER);// Aggiungi padding attorno alle carte

        getChildren().addAll(labelsContainer, handContainer);
    }

    public void setHandlabel(boolean visible){
        betLabel.setVisible(visible);
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


        // Calcola il punto di partenza (posizione del dealer)
        double dealerX = 15; // Posizione X del dealer nella scena
        double dealerY = 400; // Posizione Y del dealer nella scena

        // Converti in coordinate relative al contenitore
        double startX = dealerX - handContainer.getLayoutX() - handContainer.getBoundsInParent().getMinX();
        double startY = dealerY - handContainer.getLayoutY() - handContainer.getBoundsInParent().getMinY();

        // Posiziona la carta inizialmente presso il dealer
        cardView.setTranslateX(startX);
        cardView.setTranslateY(startY);

        // Inizialmente la carta è più piccola (come se fosse più lontana)
        cardView.setScaleX(0.7);
        cardView.setScaleY(0.7);

        // Aggiungi la carta al contenitore
        handContainer.getChildren().add(cardView);

        // Crea l'animazione di movimento
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(600), cardView);
        moveTransition.setToX(0);
        moveTransition.setToY(0);

        // Animazione per ingrandire la carta mentre si avvicina
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(600), cardView);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        // Opzionale: aggiungi un leggero effetto di oscillazione
        RotateTransition wobbleTransition = new RotateTransition(Duration.millis(600), cardView);
        wobbleTransition.setFromAngle(-5);
        wobbleTransition.setToAngle(0);
        wobbleTransition.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.2, 1));

        // Combina tutte le animazioni
        ParallelTransition dealAnimation = new ParallelTransition(
                moveTransition, scaleTransition, wobbleTransition
        );

        // Imposta cosa accade quando l'animazione termina
        valueLabel.setText("value: " + handValue);

        // Animazione più fluida con un interpolatore personalizzato
        dealAnimation.setInterpolator(Interpolator.EASE_OUT);

        // Avvia l'animazione
        AnimationQueue.queue(dealAnimation);
    }

    public void updateBet(int bet) {
        betLabel.setText("Bet: " + bet);
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
