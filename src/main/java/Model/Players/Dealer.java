package Model.Players;

import Model.Game.Objects.Card;
import Model.Players.StrategyPlay.PlayerStrategy;

/**
 * Rappresenta il dealer nel gioco di BlackJack.
 * Estende la classe Player con comportamenti specifici del dealer,
 * come la gestione della carta nascosta.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class Dealer extends Player {
    private PlayerStrategy strategy;
    private Card hiddenCard;

    /**
     * Costruisce un nuovo dealer con una strategia specifica.
     * Il dealer ha un saldo infinito (Integer.MAX_VALUE).
     * 
     * @param strategy La strategia di gioco del dealer
     */
    public Dealer(PlayerStrategy strategy) {
        super("Dealer", Integer.MAX_VALUE);
        this.strategy = strategy;
    }

    /**
     * Reimposta la mano del dealer rimuovendo anche la carta nascosta.
     */
    @Override
    public void resetHand(){
        super.resetHand();
        hiddenCard = null;
    }

    /**
     * Aggiunge una carta al dealer.
     * La prima carta viene nascosta (hole card), le successive sono visibili.
     * 
     * @param card La carta da aggiungere
     */
    @Override
    public void addCard(Card card) {
        if (hiddenCard == null) {
            hiddenCard = card;
        } else {
            super.addCard(card);
        }
    }

    /**
     * Rivela la carta nascosta del dealer aggiungendola alla mano visibile.
     * Questo metodo viene chiamato all'inizio del turno del dealer.
     */
    public void revealHiddenCard(){
        if (hiddenCard != null){
            super.addCard(hiddenCard);
            hiddenCard = null;
        }
    }

    /**
     * Restituisce la carta nascosta del dealer.
     * 
     * @return La carta nascosta, null se non presente
     */
    public Card getHiddenCard() {
        return hiddenCard;
    }

    /**
     * Restituisce la strategia di gioco del dealer.
     * 
     * @return La strategia di gioco
     */
    public PlayerStrategy getStrategy() {
        return strategy;
    }

}
