package View;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class PlayerInfoView extends VBox {
    private final ImageView profileImageView;
    private final Label nameLabel;
    private final Label balanceLabel;
    private final Label currentBetLabel;

    public PlayerInfoView(String name, String imagePath, int balance) {
        // Impostazioni base del layout
        setAlignment(Pos.TOP_CENTER);
        setSpacing(5);
        setPrefSize(350, 182);
        getStyleClass().add("player-info");

        // Nome giocatore
        nameLabel = new Label(name);
        nameLabel.setPrefSize(350, 46);
        nameLabel.setAlignment(Pos.TOP_CENTER);
        nameLabel.getStyleClass().add("player-name");

        // Immagine profilo
        profileImageView = new ImageView(new Image(imagePath));
        profileImageView.setFitWidth(86);
        profileImageView.setFitHeight(86);
        profileImageView.setPreserveRatio(true);

        // Crea clip circolare
        Circle clip = new Circle(43);
        clip.setCenterX(44);
        clip.setCenterY(44);
        profileImageView.setClip(clip);

        // Label per saldo
        balanceLabel = new Label("Balance: " + balance);
        balanceLabel.setPrefSize(341, 39);
        balanceLabel.setAlignment(Pos.TOP_CENTER);
        balanceLabel.getStyleClass().add("balance-label");

        // Label per puntata corrente
        currentBetLabel = new Label("Current Bet: ");
        currentBetLabel.setPrefSize(341, 39);
        currentBetLabel.setAlignment(Pos.TOP_CENTER);
        currentBetLabel.getStyleClass().add("bet-label");

        // Assemblaggio componenti
        getChildren().addAll(nameLabel, profileImageView, balanceLabel, currentBetLabel);
    }

    /**
     * Aggiorna il saldo visualizzato
     */
    public void updateBalance(int balance) {
        balanceLabel.setText("Balance: " + balance);
    }

    /**
     * Aggiorna la puntata corrente visualizzata
     */
    public void updateCurrentBet(int bet) {
        currentBetLabel.setText("Current Bet: " + bet);
    }

    /**
     * Restituisce il nome del giocatore
     */
    public String getPlayerName() {
        return nameLabel.getText();
    }
}
