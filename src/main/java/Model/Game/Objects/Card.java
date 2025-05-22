package Model.Game.Objects;

/**
 * Rappresenta una carta da gioco nel BlackJack.
 * Ogni carta ha un rango (Asso, 2-10, Jack, Queen, King) e un seme (Cuori, Quadri, Fiori, Picche).
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class Card {
    private final Rank rank;
    private final Suit suit;

    /**
     * Costruisce una nuova carta con il rango e seme specificati.
     * 
     * @param rank Il rango della carta
     * @param suit Il seme della carta
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Restituisce il rango della carta.
     * 
     * @return Il rango della carta
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Restituisce il seme della carta.
     * 
     * @return Il seme della carta
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Restituisce il valore numerico della carta nel BlackJack.
     * 
     * @return Il valore della carta (1-11 per l'Asso, 2-10 per le carte numeriche, 10 per le figure)
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * Verifica se la carta è un Asso.
     * 
     * @return true se la carta è un Asso, false altrimenti
     */
    public boolean isAce() {
        return rank.isAce();
    }

    /**
     * Restituisce una rappresentazione stringa della carta.
     * 
     * @return Una stringa nel formato "RANK_of_SUIT"
     */
    @Override
    public String toString() {
        return rank + "_of_" + suit;
    }
}
