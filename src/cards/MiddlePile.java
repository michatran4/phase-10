package cards;

import phases.ColorSet;
import phases.NumberRun;
import phases.NumberSet;
import phases.Rule;

import java.util.LinkedList;

/**
 * Middle piles are created once a player advances on to the next phase.
 * They are for cards that are hitting.
 * This class returns if cards can be added to the middle, and adds them if so.
 */
public class MiddlePile {
    private final LinkedList<Card> cards; // linked list should be in order
    private final Rule rule;
    private final boolean DEBUGGING;

    /**
     * Multiple middle piles should be created if a phase has multiple rules,
     * or a count > 1 for the rule.
     *
     * @param dropped cards to be added to the pile
     * @param r       the rule of this pile
     */
    public MiddlePile(LinkedList<Card> dropped, Rule r, boolean b) {
        if (dropped.size() != r.getNumCards()) {
            throw new IllegalArgumentException();
        }
        for (Card card: dropped) {
            if (card == null) {
                throw new IllegalStateException();
            }
        }
        cards = new LinkedList<>(dropped);
        rule = r;
        DEBUGGING = b;
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
     * Add a card to the middle pile.
     *
     * @param toAdd card to add
     * @param add   add the card to pile, or if it's just a peek test
     * @return if the card was added successfully (or addable)
     */
    public boolean addCard(Card toAdd, boolean add) { // TODO test
        if (DEBUGGING) {
            System.out.println("Middle pile: " + this);
            if (!add) System.out.println("Test add");
            System.out.println("adding card: " + toAdd.toString());
        }
        if (toAdd.toString().equals("SKIP")) {
            throw new IllegalStateException("Cannot add skips.");
        }
        if (rule instanceof NumberSet || rule instanceof ColorSet) {
            if (DEBUGGING) {
                System.out.println("Checking " + rule.getClass());
                System.out.println(rule);
            }
            if (toAdd.toString().equals("WILD")) {
                if (add) cards.add(toAdd);
                return true;
            }

            Card example = getFirstNormalCard();
            if (DEBUGGING) {
                System.out.println("EXAMPLE: " + example.toString());
            }
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
            if (DEBUGGING) {
                System.out.println("Failed checks here (1)");
            }
            return false;
        }
        else { // number run requires checking bounds before randomly adding cards
            if (DEBUGGING) {
                System.out.println("Number run checking.");
            }
            if (cards.size() == 12) {
                if (DEBUGGING) {
                    System.out.println("Unavailable pile.");
                }
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
            // TODO allow for cards in the middle to be added
            if (start != 1 && num == start - 1) { // beginning is available
                if (add) cards.add(0, toAdd);
                // TODO beginning vs end matters for gameplay?
            }
            else if (end != 12 && num == end + 1) {
                if (add) cards.add(toAdd);
            }
            else {
                if (DEBUGGING) {
                    System.out.println("Failed checks here (2)");
                }
                return false;
            }
            return true;
        }
    }

    public int getStartBound() {
        if (!(rule instanceof NumberRun))
            throw new UnsupportedOperationException();
        // find the bounds of the pile
        int first = getIndexOfFirstNormalCard();
        // this the amount wilds that go before the number. calculate what the first wild's number actually is
        int start = getFirstNormalCard().getNum() - first;
        if (start < 1) {
            throw new IllegalStateException();
        }
        if (DEBUGGING) {
            System.out.println("First normal index: " + first);
            System.out.println("First normal num: " + getFirstNormalCard().getNum());
            System.out.println("Start num: " +start);
        }
        return start;
    }

    public int getEndBound() {
        if (!(rule instanceof NumberRun))
            throw new UnsupportedOperationException();
        int last = getIndexOfLastNormalCard();
        // this is the index before any possible wild cards
        int remaining = cards.size() - 1 - last; // find remaining wild cards
        int end = getLastNormalCard().getNum() + remaining;
        // wild and skip card numbers (13 and 14) will never be used to add
        if (end > 12) {
            throw new IllegalStateException();
        }
        if (DEBUGGING) {
            System.out.println("Last normal index: " + last);
            System.out.println("Last normal num: " + getLastNormalCard().getNum());
            System.out.println("Remaining: " + remaining);
            System.out.println("End num: " + end);
        }
        return end;
    }

    public Rule getRule() {
        return rule;
    }

    public String toString() {
        if (cards.size() == 0) return "[]";
        StringBuilder output = new StringBuilder("[");
        for (Card c: cards) {
            output.append(c.toString());
            output.append(", ");
        }
        output = new StringBuilder(output.substring(0, output.length() - 2));
        output.append("]");
        return output.toString();
    }
}
