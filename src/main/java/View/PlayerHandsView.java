package View;

import Model.Game.Objects.Card;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
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
        // Label per il nome del giocatore
        nameLabel = new Label(playerName);
        nameLabel.setPrefSize(324, 10);
        nameLabel.getStyleClass().add(isAI ? "ai-name" : "player-name");
        nameLabel.setAlignment(Pos.CENTER);
        // Inizializza le mani
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
     * Mostra l'indicatore "BUSTED" su una mano
     */
    public void showBusted(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBusted();
    }

    /**
     * Anima la separazione delle mani in caso di split
     */
    public void animateSplitHands(Card newCard1, Card newCard2, int handValue1, int handValue2, int bet) {
        ensureHandViews(2);

        HandView firstHandView = handViews.get(0);
        HandView secondHandView = handViews.get(1);

        // Ottieni il contenitore delle carte dalla prima mano
        HBox firstHandContainer = (HBox) firstHandView.getChildren().get(1);
        HBox secondHandContainer = (HBox) secondHandView.getChildren().get(1);

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

                // Resetta la traslazione
                cardToMove.setTranslateX(0);
                cardToMove.setTranslateY(0);

                // 5. Ora aggiungi le nuove carte con animazione
                firstHandView.animateCardDealt(newCard1, handValue1, false);

                // Aggiungi un piccolo ritardo prima di animare la seconda carta
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), evt -> {
                    secondHandView.animateCardDealt(newCard2, handValue2, false);
                }));
                delay.play();

                // 6. Aggiorna le scommesse
                secondHandView.updateBet(bet);
            });

            // Avvia l'animazione
            moveCard.play();
        }
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
            getChildren().add(newHand);
        }
    }
}
