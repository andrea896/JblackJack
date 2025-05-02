package View;

import Model.Game.Objects.Card;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandsView extends VBox {
    private final Label nameLabel;
    private final List<HandView> handViews;

    public PlayerHandsView(String playerName, boolean isAI) {
        // Impostazioni base del layout
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
        setPrefSize(322, 182);
        getStyleClass().add("ai-player-area");
        // Label per il nome del giocatore
        nameLabel = new Label(playerName);
        nameLabel.setPrefSize(324, 10);
        nameLabel.getStyleClass().add(isAI ? "ai-name" : "player-name");
        nameLabel.setAlignment(Pos.CENTER);
        // Inizializza le mani
        handViews = new ArrayList<>();
        HandView initialHand = new HandView();
        handViews.add(initialHand);
        // Assemblaggio componenti
        getChildren().addAll(nameLabel, initialHand);
    }

    /**
     * Aggiorna una mano con le carte specificate
     */
    public void updateHand(int handIndex, List<Card> cards, int handValue) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).updateHand(cards, handValue);
    }

    /**
     * Anima l'aggiunta di una carta alla mano
     */
    public void animateCardDealt(int handIndex, Card card, int handValue) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).animateCardDealt(card, handValue, false);
    }

    /**
     * Aggiorna la puntata visualizzata
     */
    public void updateBet(int bet, int handIndex) {
        handViews.get(handIndex).updateBet(bet);
    }

    /**
     * Mostra l'indicatore "BUSTED" su una mano
     */
    public void showBusted(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBusted();
    }

    /**
     * Anima la separazione delle mani in caso di split
     */
    public void animateSplitHands(List<Card> firstHand, List<Card> secondHand) {
        ensureHandViews(2);

        // Aggiorna entrambe le mani
        handViews.get(0).updateHand(firstHand, calculateHandValue(firstHand));
        handViews.get(1).updateHand(secondHand, calculateHandValue(secondHand));

        // Anima la separazione
        TranslateTransition leftTransition = new TranslateTransition(Duration.millis(500), handViews.get(0));
        leftTransition.setByX(-50);

        TranslateTransition rightTransition = new TranslateTransition(Duration.millis(500), handViews.get(1));
        rightTransition.setByX(50);

        ParallelTransition splitAnimation = new ParallelTransition(leftTransition, rightTransition);
        splitAnimation.play();
    }

    /**
     * Ottieni il nome del giocatore
     */
    public String getPlayerName() {
        return nameLabel.getText();
    }

    /**
     * Assicura che ci siano abbastanza HandView disponibili
     */
    private void ensureHandViews(int requiredCount) {
        while (handViews.size() < requiredCount) {
            HandView newHand = new HandView();
            handViews.add(newHand);

            // La seconda mano viene aggiunta al container solo quando necessario
            if (handViews.size() == 2) {
                // Non aggiungere automaticamente al container
            } else if (handViews.size() > 2) {
                getChildren().add(newHand);
            }
        }
    }

    /**
     * Calcola il valore di una mano di carte
     */
    private int calculateHandValue(List<Card> cards) {
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
}
