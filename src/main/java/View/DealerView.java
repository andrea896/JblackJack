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

public class DealerView extends VBox {
    private final HandView handView;

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

        getChildren().addAll(nameLabel, handView);

    }

    public void revealHiddenCard(Card hiddenCard) {
        // Ottieni il contenitore delle carte (handContainer) dall'handView
        HBox handContainer = (HBox) handView.getChildren().get(1);

        // Verifica che ci siano carte nel contenitore
        if (handContainer.getChildren().size() > 1) {
            // La seconda carta è quella nascosta (indice 1)
            ImageView cardView = (ImageView) handContainer.getChildren().get(0);

            // Crea la nuova ImageView per la carta rivelata
            ImageView newCardView = CardImageService.createCardImageView(hiddenCard);

            // Animazione per girare la carta
            RotateTransition rotateOut = new RotateTransition(Duration.millis(1000), cardView);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);

            rotateOut.setOnFinished(e -> {
                // Sostituisci la carta nel contenitore
                handContainer.getChildren().set(0, newCardView);

                // Animazione per mostrare la nuova carta
                RotateTransition rotateIn = new RotateTransition(Duration.millis(150), newCardView);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(-90);
                rotateIn.setToAngle(0);
                rotateIn.play();
            });

            rotateOut.play();
        }
    }

    public void animateCardDealt(Card card, boolean faceDown, int handValue) {
        handView.animateCardDealt(card, handValue, faceDown);
    }
}
