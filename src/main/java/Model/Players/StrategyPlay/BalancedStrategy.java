package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

/**
 * Strategia bilanciata per i giocatori AI nel BlackJack.
 * Questa strategia rappresenta un compromesso tra approccio conservativo e aggressivo.
 * 
 * Caratteristiche principali:
 * - Pesca carte fino a 16 punti
 * - Prende l'assicurazione occasionalmente (30% di probabilità)
 * - Fa split in più situazioni rispetto alla strategia conservativa
 * - Fa double down con valori 9, 10, 11 contro carte deboli del dealer
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class BalancedStrategy implements PlayerStrategy{

    /**
     * Determina se pescare una carta basandosi su una soglia bilanciata.
     * 
     * @param handValue Il valore corrente della mano
     * @return true se il valore è inferiore a 16, false altrimenti
     */
    @Override
    public boolean shouldDraw(int handValue) {
        return handValue < 16;
    }

    /**
     * Prende l'assicurazione occasionalmente quando il dealer mostra un Asso.
     * 
     * @param handValue Il valore corrente della mano del giocatore (non utilizzato)
     * @param dealerCard La carta scoperta del dealer
     * @return true con 30% di probabilità se il dealer ha un Asso, false altrimenti
     */
    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return dealerCard.isAce() && Math.random() < 0.3; // Prende l'assicurazione con una probabilità del 30%
    }

    /**
     * Fa split in diverse situazioni, bilanciando rischio e opportunità.
     * 
     * @param card1 La prima carta della coppia
     * @param card2 La seconda carta della coppia
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe fare split, false altrimenti
     */
    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false;
        if (card1.getRank() == Rank.EIGHT || card1.getRank() == Rank.ACE) return true; // Sempre splitta 8-8 e A-A
        if (card1.getRank() == Rank.NINE || card1.getRank() == Rank.SEVEN)
            return dealerCard.getValue() < 7; // Splitta se il Dealer ha una carta debole

        return false;
    }

    /**
     * Fa double down in situazioni favorevoli con una gamma più ampia di valori.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe fare double down, false altrimenti
     */
    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 9 || handValue == 10 || handValue == 11) && dealerCard.getValue() <= 6; // Double Down solo se il Dealer ha una carta bassa
    }
}
