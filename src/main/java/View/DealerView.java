package View;

import Model.Game.Objects.Card;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Vista specializzata per visualizzare le carte e lo stato del dealer nel BlackJack.
 * Gestisce la visualizzazione delle carte del dealer, inclusa la logica per
 * nascondere e rivelare la carta coperta.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class DealerView extends VBox {
    private final HandView handView;

    /**
     * Costruisce la vista del dealer con etichetta e area per le carte.
     */
    public DealerView() {
        // Label del dealer
        Label nameLabel = new Label("Dealer");
        nameLabel.getStyleClass().add("dealer-name");
        nameLabel.setPrefSize(400, 20);
        setSpacing(10);
        nameLabel.setAlignment(Pos.TOP_CENTER);
        // Mano del dealer
        handView = new HandView();
        handView.setHandlabel(false);
        handView.setInsuranceLabel(false);

        getChildren().addAll(nameLabel, handView);
    }

    /**
     * Rivela la carta nascosta del dealer con un'animazione di rotazione.
     * Sostituisce la carta coperta con la carta reale e aggiorna il valore della mano.
     * 
     * @param hiddenCard La carta nascosta da rivelare
     * @param handValue Il nuovo valore totale della mano del dealer
     */
    public void revealHiddenCard(Card hiddenCard,int handValue) {
        HBox handContainer = (HBox) handView.getChildren().get(1);
        handView.updateHandValue(handValue);

        if (handContainer.getChildren().size() > 1) {
            ImageView cardView = (ImageView) handContainer.getChildren().get(0);
            ImageView newCardView = CardImageService.createCardImageView(hiddenCard);

            RotateTransition rotateOut = new RotateTransition(Duration.millis(1000), cardView);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);

            rotateOut.setOnFinished(e -> {
                handContainer.getChildren().set(0, newCardView);
                RotateTransition rotateIn = new RotateTransition(Duration.millis(150), newCardView);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(-90);
                rotateIn.setToAngle(0);
                rotateIn.play();
            });

            AnimationQueue.queue(rotateOut);
        }
    }

    /**
     * Anima l'aggiunta di una nuova carta alla mano del dealer.
     * 
     * @param card La carta da aggiungere
     * @param faceDown true se la carta deve essere mostrata coperta, false altrimenti
     * @param handValue Il nuovo valore totale della mano
     */
    public void animateCardDealt(Card card, boolean faceDown, int handValue) {
        handView.animateCardDealt(card, handValue, faceDown);
    }

    /**
     * Resetta la mano del dealer per un nuovo round.
     * Rimuove tutte le carte e prepara la vista per una nuova partita.
     */
    public void resetHandForNewRound(){
        handView.reset();
    }
}
