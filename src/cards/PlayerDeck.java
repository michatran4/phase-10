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

    public Card removeCard(Card card) {
        if (!deck.containsKey(card)) throw new IllegalArgumentException();
        int count = deck.get(card);
        if (count - 1 == 0) {
            deck.remove(card);
        }
        else {
            deck.put(card, count - 1);
        }
        return card;
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

    public int getScore() {
        int sum = 0;
        for (Card c: deck.keySet()) {
            int count = deck.get(c);
            for (int i = 0; i < count; i++) {
                if (c.getNum() <= 9) {
                    sum += 5;
                }
                else if (c.getNum() <= 12) {
                    sum += 10;
                }
                else {
                    sum += 25;
                }
            }
        }
        return sum;
    }

    public Map<Card, Integer> getDeck() { // should only be used for CPUDeck
        return deck;
    }
}
