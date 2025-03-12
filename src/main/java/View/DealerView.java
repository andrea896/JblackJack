package View;

import Model.Game.Objects.Card;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class DealerView extends VBox {
    private final HandView handView;
    private final Label valueLabel;

    public DealerView() {
        // Label del dealer
        Label nameLabel = new Label("Dealer");
        nameLabel.getStyleClass().add("dealer-name");
        nameLabel.setPrefSize(400, 20);
        nameLabel.setAlignment(Pos.TOP_CENTER);
        // Mano del dealer
        handView = new HandView();
        handView.setHandlabel(false);
        // Valore della mano
        valueLabel = new Label();
        valueLabel.getStyleClass().add("dealer-value");
        valueLabel.setVisible(false);

        getChildren().addAll(nameLabel, handView, valueLabel);

    }

    public void updateHand(List<Card> cards, int handValue, boolean hideFirstCard) {
        List<Card> displayCards = new ArrayList<>(cards);

        // Se richiesto, sostituisci la prima carta con una carta coperta
        if (hideFirstCard && !cards.isEmpty()) {
            //displayCards.set(0, Card.createFaceDownCard());
        }
        // Aggiorna la mano
        handView.updateHand(displayCards, 0); // Non mostrare il valore nella mano
        // Aggiorna il valore separatamente
        if (handValue > 0 && !hideFirstCard) {
            valueLabel.setText("Valore: " + handValue);
            valueLabel.setVisible(true);
        } else {
            valueLabel.setVisible(false);
        }
    }

    public void revealHiddenCard(Card hiddenCard) {
        // Trova la carta coperta (dovrebbe essere la prima)
        List<Node> children = handView.getChildren();
        if (children.size() > 0 && children.get(0) instanceof ImageView) {
            ImageView cardView = (ImageView) children.get(0);

            // Crea la nuova immagine
            ImageView newCardView = CardImageService.createCardImageView(hiddenCard);

            // Animazione di flip
            RotateTransition rotateOut = new RotateTransition(Duration.millis(150), cardView);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);

            rotateOut.setOnFinished(e -> {
                handView.getChildren().set(0, newCardView);

                RotateTransition rotateIn = new RotateTransition(Duration.millis(150), newCardView);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(-90);
                rotateIn.setToAngle(0);
                rotateIn.play();
            });

            rotateOut.play();
        }
    }

    public void animateCardDealt(Card card, boolean faceDown) {
        //Card displayCard = faceDown ? Card.createFaceDownCard() : card;
        //handView.animateCardDealt(displayCard);
    }
}
