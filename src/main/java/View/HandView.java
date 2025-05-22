package View;

import Model.Game.Objects.Card;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Vista che rappresenta una singola mano di carte nel BlackJack.
 * Gestisce la visualizzazione delle carte, del valore della mano, delle scommesse
 * e delle animazioni associate alla distribuzione delle carte.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class HandView extends VBox {
    private Label valueLabel;
    private Label betLabel;
    private HBox handContainer;
    private Label insuranceLabel;
    private Label resultLabel;
    private HBox labelsContainer;

    /**
     * Costruisce una nuova vista per una mano di carte.
     * Inizializza tutti i componenti grafici e le etichette informative.
     */
    public HandView() {
        setAlignment(javafx.geometry.Pos.CENTER);

        valueLabel = new Label();
        valueLabel.getStyleClass().add("hand-value");
        betLabel = new Label("Bet: ");
        betLabel.getStyleClass().add("bet-label");
        betLabel.setAlignment(javafx.geometry.Pos.CENTER);
        insuranceLabel = new Label("Insur: ");
        insuranceLabel.getStyleClass().add("bet-label");
        insuranceLabel.setAlignment(javafx.geometry.Pos.CENTER);
        resultLabel = new Label();

        labelsContainer = new HBox(5);
        labelsContainer.setSpacing(30);
        labelsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        labelsContainer.getChildren().addAll(valueLabel, betLabel, insuranceLabel);
        handContainer = new HBox(60);
        handContainer.setPrefHeight(40);
        handContainer.setSpacing(2);
        handContainer.setPadding(new Insets(10));
        handContainer.setAlignment(Pos.CENTER);

        getChildren().addAll(labelsContainer, handContainer);
    }

    /**
     * Controlla la visibilità dell'etichetta della scommessa.
     * 
     * @param visible true per mostrare l'etichetta, false per nasconderla
     */
    public void setHandlabel(boolean visible){
        betLabel.setVisible(visible);
    }

    /**
     * Controlla la visibilità dell'etichetta dell'assicurazione.
     * 
     * @param visible true per mostrare l'etichetta, false per nasconderla
     */
    public void setInsuranceLabel(boolean visible){
        insuranceLabel.setVisible(visible);
    }

    /**
     * Anima l'aggiunta di una nuova carta alla mano.
     * La carta appare con un'animazione di movimento, scala e rotazione
     * dal mazzo del dealer alla posizione finale nella mano.
     * 
     * @param card La carta da aggiungere
     * @param handValue Il nuovo valore totale della mano
     * @param isHiddenCard true se la carta deve essere mostrata coperta
     */
    public void animateCardDealt(Card card, int handValue, boolean isHiddenCard) {
        ImageView cardView;
        if (isHiddenCard)
            cardView = CardImageService.getCardBackImageView();
        else
            cardView = CardImageService.createCardImageView(card);

        double dealerX = 15;
        double dealerY = 400;

        double startX = dealerX - handContainer.getLayoutX() - handContainer.getBoundsInParent().getMinX();
        double startY = dealerY - handContainer.getLayoutY() - handContainer.getBoundsInParent().getMinY();

        cardView.setTranslateX(startX);
        cardView.setTranslateY(startY);

        cardView.setScaleX(0.7);
        cardView.setScaleY(0.7);

        handContainer.getChildren().add(cardView);

        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(600), cardView);
        moveTransition.setToX(0);
        moveTransition.setToY(0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(600), cardView);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        RotateTransition wobbleTransition = new RotateTransition(Duration.millis(600), cardView);
        wobbleTransition.setFromAngle(-5);
        wobbleTransition.setToAngle(0);
        wobbleTransition.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.2, 1));

        ParallelTransition dealAnimation = new ParallelTransition(
                moveTransition, scaleTransition, wobbleTransition
        );

        valueLabel.setText("value: " + handValue);

        dealAnimation.setInterpolator(Interpolator.EASE_OUT);

        AnimationQueue.queue(dealAnimation);
    }

    /**
     * Aggiorna l'etichetta della scommessa con un nuovo importo.
     * 
     * @param bet Il nuovo importo della scommessa
     */
    public void updateBet(int bet) {
        betLabel.setText("Bet: " + bet);
    }

    /**
     * Aggiorna l'etichetta dell'assicurazione con un nuovo importo.
     * 
     * @param insuranceAmount Il nuovo importo dell'assicurazione
     */
    public void updateInsurance(int insuranceAmount){
        insuranceLabel.setText("Insur: " + insuranceAmount);
    }

    /**
     * Aggiorna l'etichetta del valore della mano.
     * 
     * @param handValue Il nuovo valore della mano
     */
    public void updateHandValue(int handValue){
        valueLabel.setText("value: " + handValue);
    }

    /**
     * Mostra il risultato "BUSTED!" con animazione.
     */
    public void showBusted() {
        showResult("BUSTED!", "busted-label");
    }

    /**
     * Mostra il risultato "BLACKJACK!" con animazione.
     */
    public void showBlackjack() {
        showResult("BLACKJACK!", "blackjack-label");
    }

    /**
     * Mostra un risultato personalizzato con animazione di fade-in e scala.
     *
     * @param text Il testo da mostrare
     * @param styleClass La classe CSS da applicare alla label
     */
    public void showResult(String text, String styleClass) {
        resultLabel.setText(text);
        resultLabel.getStyleClass().clear();
        resultLabel.getStyleClass().add(styleClass);

        getChildren().add(resultLabel);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), resultLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), resultLabel);
        scaleUp.setFromX(0.5);
        scaleUp.setFromY(0.5);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ParallelTransition animation = new ParallelTransition(fadeIn, scaleUp);
        AnimationQueue.queue(animation);
    }

    /**
     * Resetta completamente la vista della mano per un nuovo round.
     * Rimuove tutte le carte e ripristina le etichette ai valori predefiniti.
     */
    public void reset() {
        handContainer.getChildren().clear();
        getChildren().remove(resultLabel);
        valueLabel.setText("value: ");
        betLabel.setText("Bet: ");
        insuranceLabel.setText("Insur: ");
    }

    /**
     * Restituisce il container delle carte per accesso diretto.
     * 
     * @return Il container HBox che contiene le immagini delle carte
     */
    public HBox getHandContainer() {
        return handContainer;
    }
}
