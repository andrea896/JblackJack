package Model.Game.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un mazzo di carte per il gioco di BlackJack.
 * Il mazzo è composto da due mazzi standard da 52 carte (104 carte totali).
 * Gestisce la creazione, mescolamento e distribuzione delle carte.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class Deck {
    private final List<Card> cards;

    /**
     * Costruisce un nuovo mazzo completo e lo mescola.
     * Il mazzo contiene due set completi di 52 carte ciascuno.
     */
    public Deck() {
        cards = new ArrayList<>();
        createDeck();
        shuffle();
    }

    /**
     * Crea un mazzo completo con tutte le combinazioni di ranghi e semi.
     * Vengono creati due mazzi standard per un totale di 104 carte.
     */
    private void createDeck() {
        for (int i = 0; i < 2; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
    }

    /**
     * Mescola le carte nel mazzo in ordine casuale.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Pesca una carta dal mazzo.
     * Se il mazzo è vuoto, viene ricreato automaticamente un nuovo mazzo.
     * 
     * @return La carta pescata dalla cima del mazzo
     */
    public Card drawCard() {
        if (cards.isEmpty()){
            createDeck();
            return cards.remove(0);
        }
        return cards.remove(0);
    }
}
