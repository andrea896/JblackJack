package Model.Players;

import Model.Game.Objects.Card;
import Model.Players.StrategyPlay.PlayerStrategy;

public class Dealer extends Player {
    private PlayerStrategy strategy;
    private Card hiddenCard;

    public Dealer(PlayerStrategy strategy) {
        super("Dealer", Integer.MAX_VALUE);
        this.strategy = strategy;
    }

    @Override
    public void resetHand(){
        super.resetHand();
        hiddenCard = null;
    }

    @Override
    public void addCard(Card card) {
        if (hiddenCard == null) {
            hiddenCard = card;
        } else {
            super.addCard(card);
        }
    }

    public void revealHiddenCard(){
        if (hiddenCard != null){
            super.addCard(hiddenCard);
            hiddenCard = null;
        }
    }

    public Card getHiddenCard() {
        return hiddenCard;
    }

    public PlayerStrategy getStrategy() {
        return strategy;
    }

}
