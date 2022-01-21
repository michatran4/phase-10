package cards;

import java.util.Map;
import java.util.TreeMap;

/**
 * This represents a player deck, which has sorted cards in a hand for easy identification of sets and runs.
 */
public class PlayerDeck {
    private final Map<Card, Integer> deck;
    public PlayerDeck() {
        deck = new TreeMap<>();
    }
    public void addCard(Card c) {
        if (deck.containsKey(c)) {
            deck.put(c, deck.get(c) + 1);
        }
        else {
            deck.put(c, 1);
        }
    }
    public String toString() {
        return deck.toString();
    }
}
