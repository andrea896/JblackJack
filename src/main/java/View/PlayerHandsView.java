package View;

import Model.Game.Objects.Card;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandsView extends VBox {
    private final Label nameLabel;
    private final List<HandView> handViews;

    public PlayerHandsView(String playerName, boolean isAI) {
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
        setPrefSize(322, 182);
        getStyleClass().add("ai-player-area");

        nameLabel = new Label(playerName);
        nameLabel.setPrefSize(324, 10);
        nameLabel.getStyleClass().add(isAI ? "ai-name" : "player-name");
        nameLabel.setAlignment(Pos.CENTER);

        handViews = new ArrayList<>();
        HandView initialHand = new HandView();
        handViews.add(initialHand);


        getChildren().addAll(nameLabel, initialHand);
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
     * Aggiorna l'assicurazione visualizzata
     */
    public void updateInsurance(int insuranceAmount, int handIndex) {
        handViews.get(handIndex).updateInsurance(insuranceAmount);
    }

    /**
     * Mostra l'indicatore "BUSTED" su una mano
     */
    public void showBusted(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBusted();
    }

    /**
     * Mostra l'indicatore "BLACKJACK" su una mano
     */
    public void showBlackjack(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBlackjack();
    }

    /**
     * Anima la separazione delle mani in caso di split
     */
    public void animateSplitHands(Card newCard1, Card newCard2, int handValue1, int handValue2, int bet) {
        ensureHandViews(2);

        HandView firstHandView = handViews.get(0);
        HandView secondHandView = handViews.get(1);
        HBox firstHandContainer = firstHandView.getHandContainer();
        HBox secondHandContainer = secondHandView.getHandContainer();

        // 2. Verifica che ci siano almeno due carte nella prima mano
        if (firstHandContainer.getChildren().size() >= 2) {
            ImageView cardToMove = (ImageView) firstHandContainer.getChildren().get(1);

            firstHandContainer.getChildren().remove(cardToMove);

            Bounds cardBounds = cardToMove.localToScene(cardToMove.getBoundsInLocal());
            Bounds secondHandBounds = secondHandContainer.localToScene(secondHandContainer.getBoundsInLocal());

            // Aggiungi la carta al pannello principale per l'animazione
            getChildren().add(cardToMove);

            // Posiziona la carta nella sua posizione originale
            cardToMove.setTranslateX(cardBounds.getMinX() - getLayoutX());
            cardToMove.setTranslateY(cardBounds.getMinY() - getLayoutY());

            // 3. Animazione per spostare la carta nella seconda mano
            TranslateTransition moveCard = new TranslateTransition(Duration.millis(500), cardToMove);
            moveCard.setToX(secondHandBounds.getMinX() - getLayoutX() + 5);
            moveCard.setToY(secondHandBounds.getMinY() - getLayoutY() + 5);

            // 4. Dopo lo spostamento, aggiungi la carta alla seconda mano
            moveCard.setOnFinished(e -> {
                getChildren().remove(cardToMove);

                secondHandContainer.getChildren().add(cardToMove);
                cardToMove.setTranslateX(0);
                cardToMove.setTranslateY(0);
                firstHandView.animateCardDealt(newCard1, handValue1, false);
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), evt -> {
                    secondHandView.animateCardDealt(newCard2, handValue2, false);
                }));
                delay.play();

                secondHandView.updateBet(bet);
            });

            moveCard.play();
        }
    }

    /**
     * Assicura che ci siano abbastanza HandView disponibili
     */
    private void ensureHandViews(int requiredCount) {
        while (handViews.size() < requiredCount) {
            HandView newHand = new HandView();
            handViews.add(newHand);
            getChildren().add(newHand);
        }
    }

    /**
     * Rimuove tutte le mani aggiuntive create durante uno split
     * e mantiene solo la mano principale.
     */
    private void removeAdditionalHands() {
        for (int i = handViews.size() - 1; i > 0; i--) {
            HandView handView = handViews.remove(i);
            getChildren().remove(handView);
        }

    }

    private void resetMainHand(){
        HandView mainHand = handViews.get(0);
        mainHand.reset();
    }

    public void resetForNewRound() {
        removeAdditionalHands();
        resetMainHand();
    }
}
