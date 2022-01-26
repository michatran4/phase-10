package turns;

import cards.Card;

import java.util.LinkedList;

public class Turn {
    private final LinkedList<Card> discardCards;
    private LinkedList<Card> droppedCards;
    public Turn(LinkedList<Card> discard) {
        discardCards = new LinkedList<>(discard);
    }
    public Turn(LinkedList<Card> discard, LinkedList<Card> hit) {
        discardCards = new LinkedList<>(discard);
        droppedCards = new LinkedList<>(hit);
    }
    public LinkedList<Card> getDiscardCards() {
        return discardCards;
    }
    public LinkedList<Card> getDroppedCards() {
        return droppedCards;
    }
}
