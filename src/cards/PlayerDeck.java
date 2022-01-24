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

    public Card removeCard() { // TODO
        //Card c = deck.get(new Card(c))
        return null;
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public String toString() {
        StringBuilder output = new StringBuilder("[");
        for (Card c: deck.keySet()) {
            for (int i = 0; i < deck.get(c); i++) {
                output.append(c.toString());
                output.append(", ");
            }
        }
        output = new StringBuilder(output.substring(0, output.length() - 2));
        output.append("]");
        return output.toString();
    }
}
