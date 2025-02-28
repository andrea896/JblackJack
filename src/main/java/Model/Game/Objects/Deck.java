package Model.Game.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck(int numDeck) {
        cards = new ArrayList<>();
        createDeck(numDeck);
        shuffle();
    }

    private void createDeck(int numDecks) {
        for (int i = 0; i < numDecks; i++) { // Creazione di più mazzi
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
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public int remainingCards() {
        return cards.size();
    }
}
