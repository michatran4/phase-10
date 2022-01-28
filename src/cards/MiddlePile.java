package cards;

import phases.*;

import java.util.LinkedList;

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

    private Card getFirstNormalCard() {
        return cards.get(getIndexOfFirstNormalCard());
    }

    private Card getLastNormalCard() {
        return cards.get(getIndexOfLastNormalCard());
    }

    public boolean addCard(Card toAdd) {
        if (toAdd.toString().equals("SKIP")) {
            throw new IllegalStateException("Shouldn't be putting down skips.");
        }
        if (rule instanceof NumberSet || rule instanceof ColorSet) {
            if (toAdd.toString().equals("WILD")) {
                cards.add(toAdd);
                return true;
            }

            Card example = getFirstNormalCard();
            if (rule instanceof NumberSet) {
                if (example.getNum() == toAdd.getNum()) {
                    cards.add(toAdd);
                    return true;
                }
            }
            if (rule instanceof ColorSet) {
                if (example.getColor() == toAdd.getColor()) {
                    cards.add(toAdd);
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
            int first = getIndexOfFirstNormalCard();
            // this the amount wilds that go before the number. calculate what the first wild's number actually is
            int start = getFirstNormalCard().getNum() - first;

            int last = getIndexOfLastNormalCard();
            // this is the index before any possible wild cards
            int remaining = cards.size() - 1 - last; // find remaining wild cards
            int end = getLastNormalCard().getNum() + remaining;
            // wild and skip card numbers (13 and 14) will never be used to add

            // start and end are inclusive
            if (toAdd.toString().equals("WILD")) {
                if (start != -1) {
                    cards.add(0, toAdd);
                }
                else if (end != 12) {
                    cards.add(toAdd);
                }
                else {
                    throw new IllegalStateException("Should be in bounds.");
                }
                return true;
            }
            int num = toAdd.getNum();
            if (start != 1 && num < start) { // beginning is available
                cards.add(0, toAdd); // TODO beginning vs end matters for gameplay?
            }
            else if (end != 12 && num > end) {
                cards.add(toAdd);
            }
            else {
                return false;
            }
            return true;
        }
        throw new IllegalStateException(); // should've returned by this point
    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public Rule getRule() {
        return rule;
    }
}
