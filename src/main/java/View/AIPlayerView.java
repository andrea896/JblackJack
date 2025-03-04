package View;

import Model.Game.Objects.Card;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.List;

public class AIPlayerView extends GameComponentView {
    private final Label nameLabel;
    private final ImageView profileImageView;
    private final Label actionLabel;
    private final HandView handView;

    public AIPlayerView(String name, String imagePath) {
        // Layout principale
        VBox mainContainer = new VBox(5);
        mainContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Nome giocatore AI
        nameLabel = new Label(name);
        nameLabel.getStyleClass().add("ai-name");

        // Immagine profilo
        profileImageView = new ImageView(new Image(imagePath));
        profileImageView.setFitHeight(60);
        profileImageView.setFitWidth(60);
        profileImageView.setPreserveRatio(true);

        // Crea clip circolare
        Circle clip = new Circle(30);
        clip.setCenterX(30);
        clip.setCenterY(30);
        profileImageView.setClip(clip);

        // Label per l'azione (Hit, Stand, ecc.)
        actionLabel = new Label();
        actionLabel.getStyleClass().add("action-label");
        actionLabel.setVisible(false);

        // Mano del giocatore AI
        handView = new HandView();

        // Assembla il layout
        mainContainer.getChildren().addAll(nameLabel, profileImageView, actionLabel, handView);
        getChildren().add(mainContainer);

        // Status label sopra tutto
        //getChildren().add(statusLabel);
        //statusLabel.toFront();
    }

    public void updateHand(int handIndex, List<Card> cards, int handValue) {
        // Per semplicità, gestiamo solo la prima mano per i giocatori AI
        if (handIndex == 0) {
            handView.updateHand(cards, handValue);
        }
    }

    public void animateCardDealt(Card card) {
        handView.animateCardDealt(card);
    }

    public void showAction(String action) {
        actionLabel.setText(action);
        actionLabel.setVisible(true);

        // Nascondi dopo un po'
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> actionLabel.setVisible(false));
        pause.play();
    }

    public void showBusted() {
        handView.showBusted();
    }
}
