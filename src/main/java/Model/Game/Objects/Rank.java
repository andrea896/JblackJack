package Model.Game.Objects;

/**
 * Enumerazione che rappresenta i ranghi delle carte da gioco nel BlackJack.
 * Ogni rango ha un valore associato utilizzato per il calcolo del punteggio.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public enum Rank {

    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    ACE(11);

    private final int value;

    /**
     * Costruttore per inizializzare il rango con il suo valore.
     * 
     * @param value Il valore numerico del rango
     */
    Rank(int value) {
        this.value = value;
    }

    /**
     * Restituisce il valore numerico del rango.
     * 
     * @return Il valore del rango
     */
    public int getValue() {
        return value;
    }

    /**
     * Verifica se il rango è un Asso.
     * 
     * @return true se il rango è ACE, false altrimenti
     */
    public boolean isAce() {
        return this.equals(ACE);
    }

    /**
     * Restituisce la rappresentazione stringa del rango.
     * 
     * @return Il nome del rango
     */
    @Override
    public String toString() {
        return name();
    }
}
