package turns;

import cards.Card;

import java.util.LinkedList;

public class Turn {
    private final LinkedList<Card> droppedCards;
    private final Card discardCard;
    private final LinkedList<Card> hitCards;

    public Turn(LinkedList<Card> drop, Card discard, LinkedList<Card> hit) {
        discardCard = discard;
        droppedCards = new LinkedList<>(drop);
        hitCards = new LinkedList<>(hit);
    }

    public LinkedList<Card> getDroppedCards() {
        return droppedCards;
    }

    public Card getDiscardCard() {
        return discardCard;
    }

    public LinkedList<Card> getHitCards() {
        return hitCards;
    }
}
