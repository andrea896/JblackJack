package View;

import Model.Game.Objects.Card;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlayerView extends GameComponentView {
    private final VBox mainContainer;
    private final ImageView profileImageView;
    private final Label nameLabel;
    private final Label balanceLabel;
    private final List<HandView> handViews;

    public PlayerView(String name, String imagePath) {
        // Inizializza componenti UI
        mainContainer = new VBox(10);
        mainContainer.setAlignment(javafx.geometry.Pos.CENTER);

        nameLabel = new Label(name);
        nameLabel.getStyleClass().add("player-name");

        profileImageView = new ImageView(new Image(imagePath));
        profileImageView.setFitHeight(80);
        profileImageView.setFitWidth(80);
        profileImageView.setPreserveRatio(true);

        // Crea clip circolare
        Circle clip = new Circle(40);
        clip.setCenterX(40);
        clip.setCenterY(40);
        profileImageView.setClip(clip);

        balanceLabel = new Label("€0");
        balanceLabel.getStyleClass().add("balance-label");

        // Inizializza le mani
        handViews = new ArrayList<>();
        HandView initialHand = new HandView();
        handViews.add(initialHand);

        // Assembla il layout
        mainContainer.getChildren().addAll(nameLabel, profileImageView, balanceLabel, initialHand);
        getChildren().add(mainContainer);

        // Aggiunge la label di stato sopra il tutto
        getChildren().add(statusLabel);
        statusLabel.toFront();
    }

    public void updateHand(int handIndex, List<Card> cards, int handValue) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).updateHand(cards, handValue);
    }

    public void animateCardDealt(int handIndex, Card card) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).animateCardDealt(card);
    }

    public void updateBalance(int balance) {
        balanceLabel.setText("€" + balance);
    }

    public void updateBet(int bet, int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).updateBet(bet);
    }

    public void showBusted(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBusted();
    }

    public void animateSplitHands(List<Card> firstHand, List<Card> secondHand) {
        ensureHandViews(2);

        // Rimuovi tutte le mani attuali dal container
        for (HandView handView : handViews) {
            if (mainContainer.getChildren().contains(handView)) {
                mainContainer.getChildren().remove(handView);
            }
        }

        // Crea un container orizzontale per le mani
        HBox handsContainer = new HBox(20);
        handsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        handsContainer.getChildren().addAll(handViews.get(0), handViews.get(1));

        // Aggiungi il container al layout principale
        mainContainer.getChildren().add(handsContainer);

        // Aggiorna e mostra le mani
        handViews.get(0).updateHand(firstHand, getHandValue(firstHand));
        handViews.get(1).updateHand(secondHand, getHandValue(secondHand));

        // Anima la separazione
        TranslateTransition leftTransition = new TranslateTransition(Duration.millis(500), handViews.get(0));
        leftTransition.setByX(-50);

        TranslateTransition rightTransition = new TranslateTransition(Duration.millis(500), handViews.get(1));
        rightTransition.setByX(50);

        ParallelTransition splitAnimation = new ParallelTransition(leftTransition, rightTransition);
        splitAnimation.play();
    }

    private int getHandValue(List<Card> cards) {
        int total = 0;
        int aceCount = 0;

        for (Card card : cards) {
            total += card.getValue();
            if (card.isAce()) {
                aceCount++;
            }
        }

        // Aggiusta il valore degli assi se necessario
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    private void ensureHandViews(int requiredCount) {
        while (handViews.size() < requiredCount) {
            HandView newHand = new HandView();
            handViews.add(newHand);
            // Non aggiungere automaticamente alla UI, lo faremo quando necessario
        }
    }
}
