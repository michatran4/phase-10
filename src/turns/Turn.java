package turns;

import cards.Card;

import java.util.LinkedList;

public class Turn {
    private final LinkedList<Card> droppedCards;
    private final Card discardCard;
    private final LinkedList<Card> hitCards;

    public Turn(LinkedList<Card> drop, Card discard, LinkedList<Card> hit) {
        for (Card c: drop) {
            if (c.toString().equals("SKIP")) {
                // TODO might not throw for the GUI, check with earlier logic
                throw new IllegalArgumentException();
            }
        }
        for (Card c: hit) {
            if (c.toString().equals("SKIP")) {
                throw new IllegalArgumentException();
            }
        }
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

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Dropped cards: ");
        if (droppedCards.size() == 0) {
            output.append("None");
        }
        for (int i = 0; i < droppedCards.size(); i++) {
            if (i != 0) output.append(", ");
            output.append(droppedCards.get(i).toString());
        }
        output.append("\n");

        output.append("Hit cards: ");
        if (hitCards.size() == 0) {
            output.append("None");
        }
        for (int i = 0; i < hitCards.size(); i++) {
            if (i != 0) output.append(", ");
            output.append(hitCards.get(i).toString());
        }
        output.append("\n");

        output.append("Discard card: ");
        if (discardCard != null) { // valid because phase plays don't need it
            output.append(discardCard);
        }
        else {
            output.append("None");
        }
        return output.toString();
    }
}
