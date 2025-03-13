package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

public class BalancedStrategy implements PlayerStrategy{

    @Override
    public boolean shouldDraw(int handValue) {
        return handValue < 16; // Gioca più bilanciato, prende meno rischi
    }

    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return dealerCard.isAce() && Math.random() < 0.3; // Prende l'assicurazione con una probabilità del 30%
    }

    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false;
        if (card1.getRank() == Rank.EIGHT || card1.getRank() == Rank.ACE) return true; // Sempre splitta 8-8 e A-A
        if (card1.getRank() == Rank.NINE || card1.getRank() == Rank.SEVEN) {
            return dealerCard.getValue() < 7; // Splitta se il Dealer ha una carta debole
        }
        return false;
    }

    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 9 || handValue == 10 || handValue == 11) && dealerCard.getValue() <= 6; // Double Down solo se il Dealer ha una carta bassa
    }
}
