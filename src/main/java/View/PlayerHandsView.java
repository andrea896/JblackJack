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

/**
 * Vista che gestisce la visualizzazione delle mani di un giocatore nel BlackJack.
 * Supporta mani multiple in caso di split e gestisce tutte le animazioni
 * associate alla distribuzione delle carte e alle azioni speciali.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class PlayerHandsView extends VBox {
    private final Label nameLabel;
    private final List<HandView> handViews;

    /**
     * Costruisce la vista delle mani per un giocatore.
     * 
     * @param playerName Il nome del giocatore da visualizzare
     * @param isAI true se è un giocatore AI, false se è il giocatore umano
     */
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
     * Anima l'aggiunta di una carta alla mano specificata.
     * 
     * @param handIndex L'indice della mano a cui aggiungere la carta
     * @param card La carta da aggiungere
     * @param handValue Il nuovo valore totale della mano
     */
    public void animateCardDealt(int handIndex, Card card, int handValue) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).animateCardDealt(card, handValue, false);
    }

    /**
     * Aggiorna la puntata visualizzata per una mano specifica.
     * 
     * @param bet Il nuovo importo della scommessa
     * @param handIndex L'indice della mano da aggiornare
     */
    public void updateBet(int bet, int handIndex) {
        handViews.get(handIndex).updateBet(bet);
    }

    /**
     * Aggiorna l'assicurazione visualizzata per una mano specifica.
     * 
     * @param insuranceAmount Il nuovo importo dell'assicurazione
     * @param handIndex L'indice della mano da aggiornare
     */
    public void updateInsurance(int insuranceAmount, int handIndex) {
        handViews.get(handIndex).updateInsurance(insuranceAmount);
    }

    /**
     * Mostra l'indicatore "BUSTED" su una mano specifica.
     * 
     * @param handIndex L'indice della mano che ha sballato
     */
    public void showBusted(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBusted();
    }

    /**
     * Mostra l'indicatore "BLACKJACK" su una mano specifica.
     * 
     * @param handIndex L'indice della mano con BlackJack
     */
    public void showBlackjack(int handIndex) {
        ensureHandViews(handIndex + 1);
        handViews.get(handIndex).showBlackjack();
    }

    /**
     * Anima la separazione delle mani in caso di split.
     * Sposta una carta dalla prima mano alla seconda e aggiunge nuove carte
     * a entrambe le mani con le appropriate animazioni.
     * 
     * @param newCard1 La nuova carta per la prima mano
     * @param newCard2 La nuova carta per la seconda mano
     * @param handValue1 Il nuovo valore della prima mano
     * @param handValue2 Il nuovo valore della seconda mano
     * @param bet L'importo della scommessa per la nuova mano
     */
    public void animateSplitHands(Card newCard1, Card newCard2, int handValue1, int handValue2, int bet) {
        ensureHandViews(2);

        HandView firstHandView = handViews.get(0);
        HandView secondHandView = handViews.get(1);
        HBox firstHandContainer = firstHandView.getHandContainer();
        HBox secondHandContainer = secondHandView.getHandContainer();

        // Verifica che ci siano almeno due carte nella prima mano
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

            // Animazione per spostare la carta nella seconda mano
            TranslateTransition moveCard = new TranslateTransition(Duration.millis(500), cardToMove);
            moveCard.setToX(secondHandBounds.getMinX() - getLayoutX() + 5);
            moveCard.setToY(secondHandBounds.getMinY() - getLayoutY() + 5);

            // Dopo lo spostamento, aggiungi la carta alla seconda mano
            moveCard.setOnFinished(e -> {
                getChildren().remove(cardToMove);

                secondHandContainer.getChildren().add(cardToMove);
                cardToMove.setTranslateX(0);
                cardToMove.setTranslateY(0);
                firstHandView.animateCardDealt(newCard1, handValue1, false);
                secondHandView.animateCardDealt(newCard2, handValue2, false);

                secondHandView.updateBet(bet);
            });

            AnimationQueue.queue(moveCard);
        }
    }

    /**
     * Assicura che ci siano abbastanza HandView disponibili per il numero richiesto.
     * Crea nuove HandView se necessario.
     * 
     * @param requiredCount Il numero minimo di HandView richieste
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

    /**
     * Resetta la mano principale rimuovendo tutte le carte e i risultati.
     */
    private void resetMainHand(){
        HandView mainHand = handViews.get(0);
        mainHand.reset();
    }

    /**
     * Resetta completamente la vista per un nuovo round.
     * Rimuove tutte le mani aggiuntive e resetta la mano principale.
     */
    public void resetForNewRound() {
        removeAdditionalHands();
        resetMainHand();
    }
}
