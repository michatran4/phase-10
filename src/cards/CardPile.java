package cards;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

/**
 * This represents a card pile acting like a stack, which is the discard pile or the draw pile.
 */
public class CardPile {
    private final Stack<Card> pile;
    private final boolean drawDeck;

    public CardPile(boolean draw) {
        pile = new Stack<>();
        drawDeck = draw;
    }

    // TODO use with the GUI
    public boolean isDrawDeck() {
        return drawDeck;
    }

    public void add(Card c) {
        pile.push(c);
    }

    public void shuffle() {
        Collections.shuffle(pile);
    }

    public Card peek() {
        if (!drawDeck) {
            if (pile.isEmpty()) {
                throw new IllegalStateException();
            }
            return pile.peek();
        }
        throw new IllegalCallerException("Only the discard pile should be peeked at.");
    }

    public Card pop() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        if (!drawDeck) {
            if (pile.peek().toString().equals("SKIP")) {
                System.out.println("not allowed");
                return null;
            }
        }
        return pile.pop();
    }

    public Stack<Card> getPile() {
        return pile;
    }

    public void clear() {
        pile.clear();
    }

    public void addAll(Collection<Card> stack) {
        pile.addAll(stack);
    }

    public boolean isEmpty() {
        return pile.isEmpty();
    }
}
