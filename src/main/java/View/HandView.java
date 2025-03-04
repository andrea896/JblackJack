package View;

import Model.Game.Objects.Card;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.List;

public class HandView extends HBox{
    private final Label valueLabel;
    private final Label betLabel;

    public HandView() {
        setAlignment(javafx.geometry.Pos.CENTER);
        setSpacing(-30); // Sovrapponi parzialmente le carte

        valueLabel = new Label();
        valueLabel.getStyleClass().add("hand-value");

        betLabel = new Label("€0");
        betLabel.getStyleClass().add("bet-label");
        betLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Posiziona le label in un contenitore separato
        VBox labelsContainer = new VBox(5);
        labelsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        labelsContainer.getChildren().addAll(valueLabel, betLabel);

        getChildren().add(labelsContainer);
    }

    public void updateHand(List<Card> cards, int handValue) {
        // Rimuovi tutte le carte esistenti ma mantieni il container delle label
        getChildren().clear();

        // Aggiungi le nuove carte
        for (Card card : cards) {
            ImageView cardView = CardImageService.createCardImageView(card);
            getChildren().add(cardView);
        }

        // Aggiorna e aggiungi nuovamente il container delle label
        valueLabel.setText(String.valueOf(handValue));
        VBox labelsContainer = new VBox(5);
        labelsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        labelsContainer.getChildren().addAll(valueLabel, betLabel);
        getChildren().add(labelsContainer);
    }

    public void animateCardDealt(Card card) {
        ImageView cardView = CardImageService.createCardImageView(card);

        // Imposta posizione iniziale fuori schermo
        cardView.setTranslateY(-200);

        // Aggiungi la carta alla vista
        getChildren().add(getChildren().size() - 1, cardView); // Aggiungi prima delle label

        // Anima l'entrata della carta
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), cardView);
        transition.setToY(0);
        transition.play();
    }

    public void updateBet(int bet) {
        betLabel.setText("€" + bet);
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
