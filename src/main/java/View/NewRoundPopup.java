package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NewRoundPopup extends VBox {
    private Label titleLabel;
    private Label messageLabel;
    private Button acceptButton;
    private Button declineButton;
    private int insuranceAmount;

    public NewRoundPopup() {
        setPrefSize(400, 200);
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Crea uno sfondo con bordi arrotondati
        Rectangle background = new Rectangle(400, 200);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setFill(Color.rgb(40, 40, 40, 0.9));
        background.setStroke(Color.GOLD);
        background.setStrokeWidth(2);

        // Titolo
        titleLabel = new Label("Assicurazione disponibile");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Messaggio
        messageLabel = new Label("Il dealer ha un asso. Vuoi prendere l'assicurazione?");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        // Pulsanti
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        acceptButton = new Button("Accetta");
        acceptButton.setPrefWidth(100);
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        declineButton = new Button("Rifiuta");
        declineButton.setPrefWidth(100);
        declineButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

        buttonsBox.getChildren().addAll(acceptButton, declineButton);

        // Aggiungi tutto al layout
        getChildren().addAll(titleLabel, messageLabel, buttonsBox);

        // Posiziona il background dietro gli altri elementi
        this.getChildren().add(0, background);

        // Assicurati che il background si adatti al contenuto
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());
    }

    public void setInsuranceAmount(int amount) {
        this.insuranceAmount = amount;
        messageLabel.setText("Il dealer ha un asso come carta scoperta.\nVuoi prendere l'assicurazione per " + amount + " chips?");
    }

    private void hidePopup() {

    }
}
