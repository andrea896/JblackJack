package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

public class ConservativeStrategy implements PlayerStrategy {
    @Override
    public boolean shouldDraw(int handValue) {
        return handValue < 15; // Conservativo, prende meno rischi
    }

    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return false; // Non prende mai l'assicurazione, considerata una scommessa sfavorevole
    }

    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false; // Può splittare solo coppie
        if (card1.getRank() == Rank.ACE || card1.getRank() == Rank.EIGHT)
            return dealerCard.getValue() < 7; // Splitta solo se il Dealer ha una carta debole

        return false;
    }

    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 10 || handValue == 11) && dealerCard.getValue() <= 6; // Double Down solo se il Dealer ha una carta bassa
    }
}
