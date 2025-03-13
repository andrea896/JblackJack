package View;

import Model.Game.Objects.Card;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
        //setSpacing(-30); // Sovrapponi parzialmente le carte

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
        betLabel.setVisible(false);
    }

    public void updateHand(List<Card> cards, int handValue) {
        // Rimuovi tutte le carte esistenti ma mantieni il container delle label
        handContainer.getChildren().clear();

        // Aggiungi le nuove carte
        for (Card card : cards) {
            ImageView cardView = CardImageService.createCardImageView(card);
            handContainer.getChildren().add(cardView);
            cardView.toFront();
        }

        // Aggiorna e aggiungi nuovamente il container delle label
        valueLabel.setText("value: " + handValue);
        // Debug
        System.out.println("updateHand - contenitore contiene ora " + handContainer.getChildren().size() + " nodi");
    }

    public void animateCardDealt(Card card, int handValue, boolean isHiddenCard) {
        ImageView cardView;

        if (isHiddenCard)
            cardView = CardImageService.getCardBackImageView();
        else
            cardView = CardImageService.createCardImageView(card);

        // Imposta posizione iniziale fuori schermo
        cardView.setTranslateY(-200);
        // Aggiungi la carta alla vista
        handContainer.getChildren().add(cardView);
        // Anima l'entrata della carta
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), cardView);
        transition.setToY(0);
        transition.play();
        valueLabel.setText("value: " + handValue);
        System.out.println("updateHand - contenitore contiene ora " + handContainer.getChildren().size() + " nodi");
    }

    public void updateBet(int bet) {
        betLabel.setText("Bet: " + bet);
    }

    public void showBusted() {
        Label bustedLabel = new Label("BUSTED!");
        bustedLabel.getStyleClass().add("busted-label");

        // Effetti di animazione
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
        animation.play();
    }
}
