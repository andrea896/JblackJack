package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;
import Model.Game.Objects.Rank;

/**
 * Strategia aggressiva per i giocatori AI nel BlackJack.
 * Questa strategia prende più rischi per massimizzare le potenziali vincite.
 * 
 * Caratteristiche principali:
 * - Pesca carte fino a 17 punti (soglia più alta)
 * - Prende sempre l'assicurazione quando il dealer mostra un Asso
 * - Fa split con Assi e 8 indipendentemente dalla carta del dealer
 * - Fa double down più frequentemente (9, 10, 11) senza considerare la carta del dealer
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class AggressiveStrategy implements PlayerStrategy {
    
    /**
     * Determina se pescare una carta basandosi su una soglia aggressiva.
     * 
     * @param handValue Il valore corrente della mano
     * @return true se il valore è inferiore a 17, false altrimenti
     */
    @Override
    public boolean shouldDraw(int handValue){
        return handValue < 17;
    }

    /**
     * Prende sempre l'assicurazione quando il dealer mostra un Asso.
     * 
     * @param handValue Il valore corrente della mano del giocatore (non utilizzato)
     * @param dealerCard La carta scoperta del dealer
     * @return true se il dealer ha un Asso, false altrimenti
     */
    @Override
    public boolean shouldTakeInsurance(int handValue, Card dealerCard) {
        return dealerCard.isAce(); // Prende l'assicurazione sempre quando il Dealer ha un Asso
    }

    /**
     * Fa split aggressivamente con Assi e 8, indipendentemente dalla carta del dealer.
     * 
     * @param card1 La prima carta della coppia
     * @param card2 La seconda carta della coppia
     * @param dealerCard La carta scoperta del dealer (non utilizzata in questa strategia)
     * @return true se dovrebbe fare split, false altrimenti
     */
    @Override
    public boolean shouldSplitHand(Card card1, Card card2, Card dealerCard) {
        if (card1.getRank() != card2.getRank()) return false; // Può splittare solo coppie
        return card1.getRank() == Rank.EIGHT || card1.getRank() == Rank.ACE; // Splitta sempre 8-8 e A-A
    }

    /**
     * Fa double down aggressivamente con 9, 10, 11 senza considerare la carta del dealer.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @param dealerCard La carta scoperta del dealer (non utilizzata in questa strategia)
     * @return true se dovrebbe fare double down, false altrimenti
     */
    @Override
    public boolean shouldPlayDoubleDown(int handValue, Card dealerCard) {
        return (handValue == 9 || handValue == 10 || handValue == 11); // Double Down con 9, 10, 11
    }

}
