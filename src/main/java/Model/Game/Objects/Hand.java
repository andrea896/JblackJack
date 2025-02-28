package Model.Game.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una mano di carte nel gioco di BlackJack.
 * Questa classe gestisce tutte le operazioni e lo stato di una singola mano.
 */
public class Hand {
    private List<Card> cards;
    private int bet;
    private boolean doubledDown;
    private boolean insurance;

    /**
     * Costruttore che inizializza una mano vuota senza scommessa.
     */
    public Hand() {
        this.cards = new ArrayList<>();
        this.bet = 0;
        this.doubledDown = false;
        this.insurance = false;
    }

    /**
     * Costruttore che inizializza una mano vuota con una scommessa iniziale.
     *
     * @param bet Importo della scommessa iniziale
     */
    public Hand(int bet) {
        this();
        this.bet = bet;
    }

    /**
     * Aggiunge una carta alla mano.
     *
     * @param card La carta da aggiungere
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Restituisce tutte le carte nella mano.
     *
     * @return Lista delle carte
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Calcola il valore corrente della mano secondo le regole del BlackJack.
     * Gli assi valgono 11 punti, ma possono essere contati come 1 punto se
     * il totale supera 21.
     *
     * @return Il valore della mano
     */
    public int getValue() {
        int total = 0;
        int aceCount = 0;

        for (Card card : cards) {
            total += card.getValue();
            if (card.isAce()) {
                aceCount++;
            }
        }

        // Aggiusta il valore degli assi se necessario
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    /**
     * Verifica se la mano è sballata (valore > 21).
     *
     * @return true se la mano è sballata, false altrimenti
     */
    public boolean isBusted() {
        return getValue() > 21;
    }

    /**
     * Verifica se la mano è un blackjack (21 punti con 2 carte).
     *
     * @return true se la mano è un blackjack, false altrimenti
     */
    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    /**
     * Verifica se la mano può essere divisa.
     * È possibile dividere solo se ci sono due carte dello stesso rango.
     *
     * @return true se la mano può essere divisa, false altrimenti
     */
    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank();
    }

    /**
     * Rimuove e restituisce la seconda carta per uno split.
     * Da utilizzare quando si divide una mano.
     *
     * @return La seconda carta rimossa dalla mano
     */
    public Card splitSecondCard() {
        if (canSplit()) {
            return cards.remove(1);
        }
        return null;
    }

    /**
     * Imposta lo stato della mano come raddoppiata.
     * Raddoppia anche l'importo della scommessa.
     */
    public void doubleDown() {
        doubledDown = true;
        bet *= 2;
    }

    /**
     * Verifica se la mano è stata raddoppiata.
     *
     * @return true se la mano è stata raddoppiata, false altrimenti
     */
    public boolean isDoubledDown() {
        return doubledDown;
    }

    /**
     * Imposta lo stato dell'assicurazione per la mano.
     */
    public void takeInsurance() {
        insurance = true;
    }

    /**
     * Verifica se la mano ha un'assicurazione.
     *
     * @return true se la mano ha un'assicurazione, false altrimenti
     */
    public boolean hasInsurance() {
        return insurance;
    }

    /**
     * Restituisce l'importo della scommessa per questa mano.
     *
     * @return Importo della scommessa
     */
    public int getBet() {
        return bet;
    }

    /**
     * Imposta l'importo della scommessa per questa mano.
     *
     * @param bet Nuovo importo della scommessa
     */
    public void setBet(int bet) {
        this.bet = bet;
    }

    /**
     * Restituisce il numero di carte nella mano.
     *
     * @return Numero di carte
     */
    public int size() {
        return cards.size();
    }

    /**
     * Reimposta la mano, rimuovendo tutte le carte e ripristinando
     * gli stati a false.
     */
    public void clear() {
        cards.clear();
        doubledDown = false;
        insurance = false;
        // Non azzeriamo la scommessa perché potrebbe essere gestita separatamente
    }
}