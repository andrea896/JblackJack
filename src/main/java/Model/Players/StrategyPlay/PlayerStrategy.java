package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;

public interface PlayerStrategy {

    boolean shouldDraw(int handValue);

    boolean shouldTakeInsurance(int handValue, Card dealerCard);

    boolean shouldSplitHand(Card card1, Card card2, Card dealerCard);

    boolean shouldPlayDoubleDown(int handValue, Card dealerCard);

}
