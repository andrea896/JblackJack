package Model.Players;

import Model.Game.Objects.Card;
import Model.Players.StrategyPlay.PlayerStrategy;

public class Dealer extends Player {
    private PlayerStrategy strategy;
    private Card hiddenCard;
    private boolean revealHiddenCard;

    public Dealer(PlayerStrategy strategy) {
        super("Dealer", Integer.MAX_VALUE);
        this.strategy = strategy;
        this.revealHiddenCard = false;
    }

    @Override
    public void resetHand(){
        super.resetHand();
        revealHiddenCard = false;
        hiddenCard = null;
    }

    @Override
    public void addCard(Card card) {
        if (hiddenCard == null) {
            hiddenCard = card;
        } else {
            // Assicurarsi di aggiungere la carta nascosta alla mano se non è già presente
            if (getHand(0).isEmpty() && hiddenCard != null) {
                super.addCard(hiddenCard);
                hiddenCard = null;
            }
            super.addCard(card);
        }
    }

    public void revealHiddenCard(){
        if (hiddenCard != null){
            super.addCard(hiddenCard);
            revealHiddenCard = true;
            hiddenCard = null;
        }
    }

    public PlayerStrategy getStrategy() {
        return strategy;
    }

}
