package cards;

import phases.*;

import java.util.LinkedList;

/**
 * Middle piles are created once a player advances on to the next phase.
 * They are for cards that are hitting.
 * This class returns if cards can be added to the middle, and adds them if so.
 */
public class MiddlePile {
    private final LinkedList<Card> cards; // linked list should be in order
    private final Rule rule;

    /**
     * Multiple middle piles should be created if a phase has multiple rules,
     * or a count > 1 for the rule.
     *
     * @param dropped cards to be added to the pile
     * @param r       the rule of this pile
     */
    public MiddlePile(LinkedList<Card> dropped, Rule r) {
        if (dropped.size() != r.getNumCards()) {
            throw new IllegalArgumentException();
        }
        cards = new LinkedList<>(dropped);
        rule = r;
    }

    private int getIndexOfFirstNormalCard() {
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (c.toString().equals("SKIP")) {
                throw new IllegalStateException("Skips should not exist in a middle pile."); // just double-checking
            }
            if (!c.toString().equals("WILD")) {
                return i;
            }
        }
        throw new IllegalStateException("A stack must contain a non wild card.");
    }

    private int getIndexOfLastNormalCard() {
        for (int i = cards.size() - 1; i > -1; i--) {
            Card c = cards.get(i);
            if (c.toString().equals("SKIP")) {
                throw new IllegalStateException("Skips should not exist in a middle pile."); // just double-checking
            }
            if (!c.toString().equals("WILD")) {
                return i;
            }
        }
        throw new IllegalStateException("A stack must contain a non wild card.");
    }

    public Card getFirstNormalCard() {
        return cards.get(getIndexOfFirstNormalCard());
    }

    public Card getLastNormalCard() {
        return cards.get(getIndexOfLastNormalCard());
    }

    /**
     * Test add to a middle pile that is a number run.
     * Sets have an infinite amount of cards that can be added, while number
     * runs have bounds. Therefore, number runs need to be checked.
     * @param add the cards that are to be added, to set up the test case
     * @param toAdd the card duplicate to be added
     * @return if the card can be added
     */
    public boolean testAddNumberRun(LinkedList<Card> add, Card toAdd) { // TODO test
        if (!(rule instanceof NumberRun)) {
            throw new IllegalStateException("Method was called incorrectly.");
        }

        LinkedList<Card> test = new LinkedList<>(cards);
        // TODO add modified number run logic from addCard method
        // add variable should not be considered because test is a duplicate?
        return true;
    }

    /**
     * Add a card to the middle pile.
     * @param toAdd card to add
     * @param add add the card to pile, or if it's just a peek test
     * @return if the card was added successfully (or addable)
     */
    public boolean addCard(Card toAdd, boolean add) { // TODO test
        if (toAdd.toString().equals("SKIP")) {
            throw new IllegalStateException("Shouldn't be putting down skips.");
        }
        if (rule instanceof NumberSet || rule instanceof ColorSet) {
            if (toAdd.toString().equals("WILD")) {
                if (add) cards.add(toAdd);
                return true;
            }

            Card example = getFirstNormalCard();
            if (rule instanceof NumberSet) {
                if (example.getNum() == toAdd.getNum()) {
                    if (add) cards.add(toAdd);
                    return true;
                }
            }
            if (rule instanceof ColorSet) {
                if (example.getColor().equals(toAdd.getColor())) {
                    if (add) cards.add(toAdd);
                    return true;
                }
            }
        }
        else { // number run requires checking bounds before randomly adding cards
            if (cards.size() == 12) {
                System.out.println("Unavailable pile."); // TODO remove print
                return false;
            }
            // find the bounds of the pile
            int start = getStartBound();
            int end = getEndBound();

            // start and end are inclusive
            if (toAdd.toString().equals("WILD")) {
                if (start != 1) {
                    if (add) {
                        cards.add(0, toAdd);
                    }
                }
                else if (end != 12) {
                    if (add) {
                        cards.add(toAdd);
                    }
                }
                else {
                    throw new IllegalStateException("Should be in bounds.");
                }
                return true;
            }
            int num = toAdd.getNum();
            if (start != 1 && num == start - 1) { // beginning is available
                if (add) cards.add(0, toAdd); // TODO beginning vs end matters for gameplay?
            }
            else if (end != 12 && num == end + 1) {
                if (add) cards.add(toAdd);
            }
            else {
                return false;
            }
            return true;
        }
        throw new IllegalStateException(); // should've returned by this point
    }

    public int getStartBound() {
        if (!(rule instanceof NumberRun)) throw new UnsupportedOperationException();
        // find the bounds of the pile
        int first = getIndexOfFirstNormalCard();
        // this the amount wilds that go before the number. calculate what the first wild's number actually is
        int start = getFirstNormalCard().getNum() - first;
        if (start < 1) {
            throw new IllegalStateException();
        }
        return start;
    }

    public int getEndBound() {
        if (!(rule instanceof NumberRun)) throw new UnsupportedOperationException();
        int last = getIndexOfLastNormalCard();
        // this is the index before any possible wild cards
        int remaining = cards.size() - 1 - last; // find remaining wild cards
        int end = getLastNormalCard().getNum() + remaining;
        // wild and skip card numbers (13 and 14) will never be used to add
        if (end > 12) {
            throw new IllegalStateException();
        }
        return last;
    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public Rule getRule() {
        return rule;
    }
}
