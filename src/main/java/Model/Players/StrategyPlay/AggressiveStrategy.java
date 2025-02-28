package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

public class AggressiveStrategy implements PlayerStrategy {
    @Override
    public boolean shouldDraw(int handValue){
        return handValue < 17;
    }

    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return dealerCard.isAce(); // Prende l'assicurazione sempre quando il Dealer ha un Asso
    }

    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false; // Può splittare solo coppie
        return card1.getRank() == Rank.EIGHT || card1.getRank() == Rank.ACE; // Splitta sempre 8-8 e A-A
    }

    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 9 || handValue == 10 || handValue == 11); // Double Down con 9, 10, 11
    }

}
