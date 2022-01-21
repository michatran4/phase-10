package cards;

import java.util.Collections;
import java.util.Stack;

/**
 * This represents a card pile acting like a stack, which is the discard pile or the original pile.
 */
public class CardPile {
    private final Stack<Card> pile;
    private final boolean drawDeck;
    public CardPile(boolean draw) {
        pile = new Stack<>();
        drawDeck = draw;
    }

    public boolean isDrawDeck() {
        return drawDeck;
    }

    public void addCard(Card c) {
        pile.push(c);
    }

    public void shuffle() {
        Collections.shuffle(pile);
    }

    public Card pop() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        if (!drawDeck) {
            if (pile.peek().toString().equals("SKIP")) { // TODO wild card?
                System.out.println("not allowed");
                return null;
            }
        }
        return pile.pop();
    }

    public boolean isEmpty() {
        return pile.isEmpty();
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Card c: pile) {
            output.append(c.toString());
            output.append("\n");
        }
        return output.toString();
    }
}
