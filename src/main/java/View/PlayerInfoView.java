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

//    public void updateHand(int handIndex, List<Card> cards, int handValue) {
//        ensureHandViews(handIndex + 1);
//        handViews.get(handIndex).updateHand(cards, handValue);
//    }
//
//    public void animateCardDealt(int handIndex, Card card) {
//        ensureHandViews(handIndex + 1);
//        handViews.get(handIndex).animateCardDealt(card);
//    }
//
//    public void updateBalance(int balance) {
//        balanceLabel.setText("€" + balance);
//    }
//
//    public void updateBet(int bet, int handIndex) {
//        ensureHandViews(handIndex + 1);
//        handViews.get(handIndex).updateBet(bet);
//    }
//
//    public void showBusted(int handIndex) {
//        ensureHandViews(handIndex + 1);
//        handViews.get(handIndex).showBusted();
//    }
//
//    public void animateSplitHands(List<Card> firstHand, List<Card> secondHand) {
//        ensureHandViews(2);
//
//        // Rimuovi tutte le mani attuali dal container
//        for (HandView handView : handViews) {
//            if (mainContainer.getChildren().contains(handView)) {
//                mainContainer.getChildren().remove(handView);
//            }
//        }
//
//        // Crea un container orizzontale per le mani
//        HBox handsContainer = new HBox(20);
//        handsContainer.setAlignment(javafx.geometry.Pos.CENTER);
//        handsContainer.getChildren().addAll(handViews.get(0), handViews.get(1));
//
//        // Aggiungi il container al layout principale
//        mainContainer.getChildren().add(handsContainer);
//
//        // Aggiorna e mostra le mani
//        handViews.get(0).updateHand(firstHand, getHandValue(firstHand));
//        handViews.get(1).updateHand(secondHand, getHandValue(secondHand));
//
//        // Anima la separazione
//        TranslateTransition leftTransition = new TranslateTransition(Duration.millis(500), handViews.get(0));
//        leftTransition.setByX(-50);
//
//        TranslateTransition rightTransition = new TranslateTransition(Duration.millis(500), handViews.get(1));
//        rightTransition.setByX(50);
//
//        ParallelTransition splitAnimation = new ParallelTransition(leftTransition, rightTransition);
//        splitAnimation.play();
//    }
//
//    private int getHandValue(List<Card> cards) {
//        int total = 0;
//        int aceCount = 0;
//
//        for (Card card : cards) {
//            total += card.getValue();
//            if (card.isAce()) {
//                aceCount++;
//            }
//        }
//
//        // Aggiusta il valore degli assi se necessario
//        while (total > 21 && aceCount > 0) {
//            total -= 10;
//            aceCount--;
//        }
//
//        return total;
//    }
//
//    private void ensureHandViews(int requiredCount) {
//        while (handViews.size() < requiredCount) {
//            HandView newHand = new HandView();
//            handViews.add(newHand);
//            // Non aggiungere automaticamente alla UI, lo faremo quando necessario
//        }
//    }

}
