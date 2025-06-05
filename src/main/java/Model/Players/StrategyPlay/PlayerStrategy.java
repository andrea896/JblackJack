package Model.Players.StrategyPlay;

import Model.Game.Objects.Card;

/**
 * Interfaccia che definisce le strategie di gioco per i giocatori di BlackJack.
 * Implementata dalle diverse strategie concrete (Conservative, Balanced, Aggressive).
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public interface PlayerStrategy {

    /**
     * Determina se il giocatore dovrebbe pescare una carta.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @return true se dovrebbe pescare, false se dovrebbe stare
     */
    boolean shouldDraw(int handValue);

    /**
     * Determina se il giocatore dovrebbe prendere l'assicurazione.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe prendere l'assicurazione, false altrimenti
     */
    boolean shouldTakeInsurance(int handValue, Card dealerCard);

    /**
     * Determina se il giocatore dovrebbe dividere la mano.
     * 
     * @param card1 La prima carta della coppia
     * @param card2 La seconda carta della coppia
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe dividere, false altrimenti
     */
    boolean shouldSplitHand(Card card1, Card card2, Card dealerCard);

    /**
     * Determina se il giocatore dovrebbe fare double down.
     * 
     * @param handValue Il valore corrente della mano del giocatore
     * @param dealerCard La carta scoperta del dealer
     * @return true se dovrebbe fare double down, false altrimenti
     */
    boolean shouldPlayDoubleDown(int handValue, Card dealerCard);

}
