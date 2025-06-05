package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

/**
 * Strategia conservativa per i giocatori AI nel BlackJack.
 * Questa strategia prende pochi rischi e tende a giocare in modo sicuro.
 * 
 * Caratteristiche principali:
 * - Pesca carte solo con valori bassi (minore 15)
 * - Non prende mai l'assicurazione
 * - Fa split solo con Assi e 8 contro carte deboli del dealer
 * - Fa double down solo in situazioni molto favorevoli
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class ConservativeStrategy implements PlayerStrategy {
    
    /**
     * Determina se pescare una carta basandosi su una soglia conservativa.
     * 
     * @param handValue Il valore corrente della mano
     * @return true se il valore è inferiore a 15, false altrimenti
     */
    @Override
    public boolean shouldDraw(int handValue) {
        return handValue < 15;
    }

    /**
     * Non prende mai l'assicurazione, considerandola una scommessa sfavorevole.
     * 
     * @param handValue Il valore corrente della mano del giocatore (non utilizzato)
     * @param dealerCard La carta scoperta del dealer (non utilizzata)
     * @return sempre false
     */
    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return false;
    }

    /**
     * Fa split solo con Assi e 8, e solo quando il dealer ha una carta debole.
     * 
     * @param card1 La prima carta della coppia
     * @param card2 La seconda carta della coppia
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe fare split, false altrimenti
     */
    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false;
        if (card1.getRank() == Rank.ACE || card1.getRank() == Rank.EIGHT)
            return dealerCard.getValue() < 7;

        return false;
    }

    /**
     * Fa double down solo in situazioni molto favorevoli.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe fare double down, false altrimenti
     */
    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 10 || handValue == 11) && dealerCard.getValue() <= 6;
    }
}
