package Model.Game.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        createDeck();
        shuffle();
    }

    private void createDeck() {
        for (int i = 0; i < 2; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()){
            createDeck();
            return cards.remove(0);
        }
        return cards.remove(0);
    }
}
